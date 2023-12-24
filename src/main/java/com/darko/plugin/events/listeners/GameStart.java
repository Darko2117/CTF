package com.darko.plugin.events.listeners;

import com.darko.plugin.Main;
import com.darko.plugin.Methods;
import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.events.events.GameStartEvent;
import com.darko.plugin.gameclasses.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameStart implements Listener {

    @EventHandler
    public void onGameStart(GameStartEvent gameStartEvent) {
        Game game = makeGameObject();
        loadAvailableKits(game);
        LoadTeams(game);
        loadFlags(game);

        SendTimedTitle(60, 0);
        SendTimedTitle(30, 30);
        SendTimedTitle(10, 50);
        SendTimedTitle(5, 55);
        SendTimedTitle(4, 56);
        SendTimedTitle(3, 57);
        SendTimedTitle(2, 58);
        SendTimedTitle(1, 59);

        new BukkitRunnable() {
            @Override
            public void run() {
                StartGame();
            }
        }.runTaskLater(Main.getInstance(), 60 * 20);

        gameStartEvent.getStarter().sendMessage(MiniMessage.miniMessage().deserialize("<green>Event started!</green>"));
    }


    private Game makeGameObject() {
        Game game = new Game();
        GameManager.setActiveGame(game);
        return game;
    }

    private void loadAvailableKits(Game game) {
        for (String s : CTFCommandTabComplete.getKitNames()) {

            Kit kit = new Kit();

            kit.setName(s);

            String inventory = Main.getInstance().getConfig().getString("Kits." + s + ".Inventory");
            kit.setInventory(inventory);

            List<String> availableAbilities = Main.getInstance().getConfig().getStringList("Kits." + s + ".AvailableAbilities");
            kit.setAvailableAbilities(availableAbilities);

            ItemStack icon = null;
            if (Main.getInstance().getConfig().getItemStack("Kits." + s + ".Icon") != null) {
                icon = Main.getInstance().getConfig().getItemStack("Kits." + s + ".Icon");
            }
            kit.setIcon(icon);

            List<String> potionEffectsString = Main.getInstance().getConfig().getStringList("Kits." + s + ".PotionEffects");

            for (String pot : potionEffectsString) {

                StringBuilder reading = new StringBuilder(pot);

//                String kitName = reading.substring(0, reading.indexOf("|"));
                reading.delete(0, reading.indexOf("|") + 1);

                PotionEffectType type = PotionEffectType.getByName(reading.substring(0, reading.indexOf("|")));
                if (type == null) {
                    Main.getInstance().getLogger().warning("Received null for PotionEffectType from config");
                    continue;
                }
                reading.delete(0, reading.indexOf("|") + 1);

                int amplifier = Integer.parseInt(reading.substring(0, reading.indexOf("|")));
                reading.delete(0, reading.indexOf("|") + 1);

                PotionEffect effect = new PotionEffect(type, 20, amplifier);

                kit.addPotionEffect(effect);

            }
            game.addAvailableKit(kit);
        }

    }

    private void LoadTeams(Game game) {
        for (String s : CTFCommandTabComplete.getTeamNames()) {

            Team team = new Team();

            team.setName(s);
            team.setCanRespawn(true);

            if (Main.getInstance().getConfig().contains("Teams." + s + ".SpawnLocations")) {
                List<String> locationsString = Main.getInstance().getConfig().getStringList("Teams." + s + ".SpawnLocations");

                for (String s1 : locationsString) {
                    Optional<Location> location = Methods.getLocationFromString(s1);
                    if (location.isEmpty())
                        continue;
                    team.addSpawnLocation(location.get());
                }
            }

            MiniMessage miniMessage = MiniMessage.miniMessage();
            if (Main.getInstance().getConfig().contains("Teams." + s + ".Kits")) {
                for (Kit k : game.getKits()) {
                    if (Main.getInstance().getConfig().getStringList("Teams." + s + ".Kits").contains(k.getName())) {
                        team.addAvailableKits(k);
                    }
                }

                int numberOfSlotsInTheUI = getNumberOfSlotsInTheUI(team);

                Inventory UI = Bukkit.createInventory(null,
                        numberOfSlotsInTheUI,
                        miniMessage.deserialize("<black><bold>Choose a class</bold></black>")
                );

                List<Component> lore = new ArrayList<>();
                lore.add(miniMessage.deserialize("<i>Left click to select the kit"));

                int i = 0;
                for (Kit k : team.getAvailableKits()) {
                    ItemStack icon = k.getIcon();
                    icon.lore(lore);
                    UI.setItem(i, icon);
                    i++;
                }

                team.setKitSelectInventory(UI);

            }

            if (Main.getInstance().getConfig().contains("Teams." + s + ".DisplayName")) {
                String string = Main.getInstance().getConfig().getString("Teams." + s + ".DisplayName");
                if (string == null)
                    return;
                team.setDisplayName(miniMessage.deserialize(string));
            }

            if (Main.getInstance().getConfig().contains("Teams." + s + ".Color")) {
                String string = Main.getInstance().getConfig().getString("Teams." + s + ".Color");
                if (string == null) {
                    return;
                }
                TextColor textColor = TextColor.fromCSSHexString(string);
                if (textColor == null) {
                    Main.getInstance().getLogger().severe("No valid color to set team color with");
                    return;
                }
                team.setColor(NamedTextColor.nearestTo(textColor));
            }

            game.addTeam(team);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("ctf.bypass") && !p.isOp()) {
                continue;
            }

            AtomicBoolean teamFound = new AtomicBoolean(false);
            p.getEffectivePermissions().stream()
                    .filter(permissionAttachmentInfo -> permissionAttachmentInfo.getPermission().startsWith("ctf.team."))
                    .map(permissionAttachmentInfo -> permissionAttachmentInfo.getPermission().substring(9))
                    .findFirst()
                    .flatMap(teamName -> game.getTeams().stream()
                            .filter(team -> team.getName().equalsIgnoreCase(teamName))
                            .findAny())
                    .ifPresent(team -> {
                        Participant participant = new Participant();
                        participant.setPlayer(p);
                        participant.setTeam(team);
                        team.addTeamMember(participant);
                        teamFound.set(true);
                    });

            if (teamFound.get()) {
                continue;
            }

            Optional<Team> min = game.getTeams().stream().min(Comparator.comparingInt(t -> t.getTeamMembers().size()));
            if (min.isEmpty()) {
                Main.getInstance().getLogger().severe("No teams found to assign players to");
                continue;
            }
            Team smallestTeam = min.get();
            Participant participant = new Participant();
            participant.setPlayer(p);
            participant.setTeam(smallestTeam);
            smallestTeam.addTeamMember(participant);
            Main.getInstance().getLogger().info(p.getName() + " doesn't have a team permission set. They were put in " + smallestTeam.getName());
        }

        for (Team t : game.getTeams()) {
            org.bukkit.scoreboard.Team teamScoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(t.getName());
            if (teamScoreboard == null) {
                teamScoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(t.getName());
            }
            for (Participant p : t.getTeamMembers()) {
                teamScoreboard.addEntry(p.getPlayer().getName());
            }
            teamScoreboard.color(t.getColor());
            teamScoreboard.setAllowFriendlyFire(false);
        }

    }

    private int getNumberOfSlotsInTheUI(Team team) {
        int numberOfKits = team.getAvailableKits().size();
        int numberOfSlotsInTheUI;

        if (numberOfKits <= 9)
            numberOfSlotsInTheUI = 9;
        else if (numberOfKits <= 18)
            numberOfSlotsInTheUI = 18;
        else if (numberOfKits <= 27)
            numberOfSlotsInTheUI = 27;
        else if (numberOfKits <= 36)
            numberOfSlotsInTheUI = 36;
        else if (numberOfKits <= 45)
            numberOfSlotsInTheUI = 45;
        else
            numberOfSlotsInTheUI = 54;
        return numberOfSlotsInTheUI;
    }

    private void loadFlags(Game game) {
        for (Team team : game.getTeams()) {
            Flag flag = new Flag();
            Optional<Block> optionalBlock = Methods.getBlockFromString(Main.getInstance().getConfig().getString("Teams." + team.getName() + ".Flag"));
            if (optionalBlock.isEmpty()) {
                Main.getInstance().getLogger().warning("Received empty block for flag location for team " + team.getName() + ". Skipping team.");
                continue;
            }

            Block flagBlock = optionalBlock.get();

            flagBlock.getLocation().getWorld().getBlockAt(flagBlock.getLocation()).setType(flagBlock.getType());
            flagBlock.getLocation().getWorld().getBlockAt(flagBlock.getLocation()).setBlockData(flagBlock.getBlockData());

            flag.setBlock(flagBlock);
            flag.setTeam(team);
            game.addFlag(flag);
        }

        Integer radius = Main.getInstance().getConfig().getInt("FlagRadius");
        game.setFlagRadius(radius);

        Integer count = Main.getInstance().getConfig().getInt("FlagParticleCount");
        game.setFlagParticleCount(count);

        Integer seconds = Main.getInstance().getConfig().getInt("FlagSecondsNeededForCapture");
        game.setSecondsNeededForCapture(seconds);

        Optional<Location> flagDepositLocation = Methods.getLocationFromString(Main.getInstance().getConfig().getString("FlagDepositLocation"));
        if (flagDepositLocation.isEmpty()) {
            System.out.println("ERROR unable to get location from string for FlagDepositLocation");
            return;
        }
        game.setFlagDepositLocation(flagDepositLocation.get());

        Optional<Location> finalRespawnPoint = Methods.getLocationFromString(Main.getInstance().getConfig().getString("FinalRespawnPoint"));
        if (finalRespawnPoint.isEmpty()) {
            System.out.println("ERROR unable to get location from string for FinalRespawnPoint");
            return;
        }
        game.setFinalRespawnLocation(finalRespawnPoint.get());

        Integer deathTimer = Main.getInstance().getConfig().getInt("DeathTimer");
        game.setDeathTimer(deathTimer);

        String winnerPermission = Main.getInstance().getConfig().getString("WinnerPermission");
        game.setWinnerPermission(winnerPermission);
    }

    /**
     * Sends a timed title to all online players.
     *
     * @param secondsOnTheTimer   the number of seconds on the timer
     * @param secondsUntilShowing the number of seconds until the title is shown
     */
    private void SendTimedTitle(Integer secondsOnTheTimer, Integer secondsUntilShowing) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String seconds = secondsOnTheTimer != 1 ? "SECONDS" : "SECOND";
                MiniMessage miniMessage = MiniMessage.miniMessage();
                String msgWithTeam = "<gold><b>GAME STARTING IN <aqua><b><second_on_timer><gold><b> <seconds> Your team is: <team_of_player>";
                String msgNoTeam = "<gold><b>GAME STARTING IN <aqua><b><second_on_timer><gold><b> <seconds> Your don't have a team yet!";

                Bukkit.getOnlinePlayers().forEach(player -> {
                    String msg;
                    Optional<Component> teamOfPlayer = getTeamDisplayName(player);
                    TagResolver resolver = TagResolver.resolver(
                            Placeholder.parsed("second_on_timer", String.valueOf(secondsOnTheTimer)),
                            Placeholder.parsed("seconds", seconds)
                    );
                    if (teamOfPlayer.isPresent()) {
                        TagResolver.resolver(resolver, Placeholder.component("team_of_player", teamOfPlayer.get()));
                        msg = msgWithTeam;
                    } else {
                        msg = msgNoTeam;
                    }
                    Title title = Title.title(
                            miniMessage.deserialize(msg, resolver),
                            Component.empty(),
                            Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500)));
                    player.showTitle(title);
                });
            }
        }.runTaskLater(Main.getInstance(), secondsUntilShowing * 20);

    }

    /**
     * Returns the display name of the team that the player belongs to.
     *
     * @param player the player to get the team's display name for
     * @return an optional containing the team's display name, or empty if the player is not in any team
     */
    private static Optional<Component> getTeamDisplayName(Player player) {
        Optional<Game> optionalGame = GameManager.getActiveGame();
        return optionalGame.flatMap(game -> game.getTeams()
                .stream()
                .filter(team -> team.getTeamMembers()
                        .stream()
                        .anyMatch(member -> member.getPlayer().equals(player)))
                .map(Team::getDisplayName)
                .findFirst());
    }

    private void StartGame() {
        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            Main.getInstance().getLogger().severe("No game to start");
            return;
        }
        optionalGame.get().getParticipants().forEach(participant -> {
            Methods.teleportParticipantToOneOfTheSpawnLocations(participant);
            Methods.openKitSelectionUI(participant);
        });
    }


}
