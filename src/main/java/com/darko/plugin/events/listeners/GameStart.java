package com.darko.plugin.events.listeners;

import com.darko.plugin.Main;
import com.darko.plugin.Methods;
import com.darko.plugin.events.events.GameStartEvent;
import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.gameclasses.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GameStart implements Listener {

    @EventHandler
    public void onGameStart(GameStartEvent e) {

        MakeGameObject();

        LoadAvailableKits();

        LoadTeams();

        LoadFlags();

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

        e.getStarter().sendMessage(ChatColor.GREEN + "Event started!");

    }


    private void MakeGameObject() {

        Game game = new Game();
        GameManager.setActiveGame(game);

    }

    private void LoadAvailableKits() {

        Game game = GameManager.getActiveGame();

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

            for(String pot : potionEffectsString){

                StringBuilder reading = new StringBuilder(pot);

                String kitName = reading.substring(0, reading.indexOf("|"));
                reading.delete(0, reading.indexOf("|") + 1);

                PotionEffectType type = PotionEffectType.getByName(reading.substring(0, reading.indexOf("|")));
                reading.delete(0, reading.indexOf("|") + 1);

                Integer amplifier = Integer.valueOf(reading.substring(0, reading.indexOf("|")));
                reading.delete(0, reading.indexOf("|") + 1);

                PotionEffect effect = new PotionEffect(type, 20, amplifier);

                kit.addPotionEffect(effect);

            }
            game.addAvailableKit(kit);
        }

    }

    private void LoadTeams() {

        Game game = GameManager.getActiveGame();

        for (String s : CTFCommandTabComplete.getTeamNames()) {

            Team team = new Team();

            team.setName(s);
            team.setCanRespawn(true);

            if (Main.getInstance().getConfig().contains("Teams." + s + ".SpawnLocations")) {
//                List<Location> locations = new ArrayList<>();
//
//                try {
//                    for (Location l : (List<Location>) Main.getInstance().getConfig().get("Teams." + s + ".SpawnLocations")) {
//                        team.addSpawnLocation(l);
//                    }
//                }catch (IllegalArgumentException e){System.out.println("It fucked up");}
                List<String> locationsString = Main.getInstance().getConfig().getStringList("Teams." + s + ".SpawnLocations");

                for(String s1 : locationsString){
                    team.addSpawnLocation(Methods.GetLocationFromString(s1));
                }
            }

            if (Main.getInstance().getConfig().contains("Teams." + s + ".Kits")) {
                for(Kit k : game.getKits()){
                    if(Main.getInstance().getConfig().getStringList("Teams." + s + ".Kits").contains(k.getName())){
                        team.addAvailableKits(k);
                    }
                }

                Integer numberOfKits = team.getAvailableKits().size();
                Integer numberOfSlotsInTheUI;

                if (numberOfKits <= 9) numberOfSlotsInTheUI = 9;
                else if (numberOfKits <= 18) numberOfSlotsInTheUI = 18;
                else if (numberOfKits <= 27) numberOfSlotsInTheUI = 27;
                else if (numberOfKits <= 36) numberOfSlotsInTheUI = 36;
                else if (numberOfKits <= 45) numberOfSlotsInTheUI = 45;
                else numberOfSlotsInTheUI = 54;

                Inventory UI = Bukkit.createInventory(null, numberOfSlotsInTheUI, "" + ChatColor.BLACK + ChatColor.BOLD + "Choose a class");

                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.ITALIC + "Left click to select the kit");

                Integer i = 0;
                for(Kit k : team.getAvailableKits()){
                    ItemStack icon = k.getIcon();
                    icon.setLore(lore);
                    UI.setItem(i, icon);
                    i++;
                }

                team.setKitSelectInventory(UI);

            }

            if (Main.getInstance().getConfig().contains("Teams." + s + ".DisplayName")) {
                team.setDisplayName(Main.getInstance().getConfig().getString("Teams." + s + ".DisplayName"));
            }

//            if (Main.getInstance().getConfig().contains("Teams." + s + ".BaseLocation")) {
//                team.setBaseLocation(Methods.GetLocationFromString(Main.getInstance().getConfig().getString("Teams." + s + ".BaseLocation")));
//            }

            if(Main.getInstance().getConfig().contains("Teams." + s + ".Flag")){
                team.setFlag(Methods.GetBlockFromString(Main.getInstance().getConfig().getString("Teams." + s + ".Flag")));
            }

            if (Main.getInstance().getConfig().contains("Teams." + s + ".Color")) {
                team.setColor(ChatColor.getByChar(Main.getInstance().getConfig().getString("Teams." + s + ".Color")));
            }

            GameManager.getActiveGame().addTeam(team);

        }

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (!p.hasPermission("ctf.bypass") || p.isOp()) {

                Boolean teamFound = false;

                for (PermissionAttachmentInfo pe : p.getEffectivePermissions()) {

                    if (pe.getPermission().contains("ctf.team.")) {

                        teamFound = true;

                        String teamString = pe.getPermission().substring(9);

                        for (Team t : GameManager.getActiveGame().getTeams()) {

                            if (teamString.equalsIgnoreCase(t.getName())) {

                                Participant participant = new Participant();
                                participant.setPlayer(p);
                                participant.setTeam(t);
                                t.addTeamMember(participant);

                            }

                        }
                    }

                }

                if (!teamFound) {

                    Team leastPlayersTeam = null;

                    for (Team t : GameManager.getActiveGame().getTeams()) {

                        if (leastPlayersTeam == null) leastPlayersTeam = t;
                        if (t.getTeamMembers().size() < leastPlayersTeam.getTeamMembers().size()) leastPlayersTeam = t;

                    }

                    Participant participant = new Participant();
                    participant.setPlayer(p);
                    participant.setTeam(leastPlayersTeam);
                    leastPlayersTeam.addTeamMember(participant);

                    Main.getInstance().getLogger().info(p.getName() + " doesn't have a team permission set. They were put in " + leastPlayersTeam.getName());

                }

            }
        }

        for (Team t : GameManager.getActiveGame().getTeams()) {

            org.bukkit.scoreboard.Team t1 = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(t.getName());
            if (t1 == null) {
                t1 = Bukkit.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(t.getName());
            }
            for (Participant p : t.getTeamMembers()) {
                t1.addEntry(p.getPlayer().getName());
            }
            t1.setColor(t.getColor());
            t1.setAllowFriendlyFire(false);

        }

    }

    private void LoadFlags() {

        Game game = GameManager.getActiveGame();

//        Block flag = Methods.GetBlockFromString(Main.getInstance().getConfig().getString("Flag"));
//        game.setFlag(flag);

        for(Team t : game.getTeams()){

            Flag flag = new Flag();

            Block flagBlock = Methods.GetBlockFromString(Main.getInstance().getConfig().getString("Teams." + t.getName() + ".Flag"));

            Bukkit.getWorld(flagBlock.getLocation().getWorld().getName()).getBlockAt(flagBlock.getLocation()).setType(flagBlock.getType());
            Bukkit.getWorld(flagBlock.getLocation().getWorld().getName()).getBlockAt(flagBlock.getLocation()).setBlockData(flagBlock.getBlockData());

            flag.setBlock(flagBlock);
            flag.setTeam(t);
            game.addFlag(flag);
        }

        Integer radius = Main.getInstance().getConfig().getInt("FlagRadius");
        game.setFlagRadius(radius);

        Integer count = Main.getInstance().getConfig().getInt("FlagParticleCount");
        game.setFlagParticleCount(count);

        Integer seconds = Main.getInstance().getConfig().getInt("FlagSecondsNeededForCapture");
        game.setSecondsNeededForCapture(seconds);

        Integer points = Main.getInstance().getConfig().getInt("PointsNeededToWin");
        game.setPointsNeededToWin(points);

        Location depositLocation = Methods.GetLocationFromString(Main.getInstance().getConfig().getString("FlagDepositLocation"));
        game.setFlagDepositLocation(depositLocation);

        Location finalRespawnLocation = Methods.GetLocationFromString(Main.getInstance().getConfig().getString("FinalRespawnPoint"));
        game.setFinalRespawnLocation(finalRespawnLocation);

        Integer deathTimer = Main.getInstance().getConfig().getInt("DeathTimer");
        game.setDeathTimer(deathTimer);

        String winnerPermission = Main.getInstance().getConfig().getString("WinnerPermission");
        game.setWinnerPermission(winnerPermission);

    }

    private void SendTimedTitle(Integer secondsOnTheTimer, Integer secondsUntilShowing) {

        new BukkitRunnable() {
            @Override
            public void run() {

                String seconds;
                if (secondsOnTheTimer != 1) seconds = "SECONDS";
                else seconds = "SECOND";

                for (Player p : Bukkit.getOnlinePlayers()) {

                    String teamOfPlayer = null;

                    for (Team t : GameManager.getActiveGame().getTeams()) {
                        for (Participant par : t.getTeamMembers()) {
                            if (par.getPlayer() == p) {
                                teamOfPlayer = "Your team is: " + ChatColor.translateAlternateColorCodes('&', t.getDisplayName());
                            }
                        }
                    }

                    p.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "GAME STARTING IN " + ChatColor.AQUA + "" + ChatColor.BOLD + secondsOnTheTimer + ChatColor.GOLD + "" + ChatColor.BOLD + " " + seconds, teamOfPlayer, 10, 60, 10);

                }
            }
        }.runTaskLater(Main.getInstance(), secondsUntilShowing * 20);

    }

    private void StartGame() {
        for (Participant p : GameManager.getActiveGame().getParticipants()) {
            Methods.TeleportParticipantToOneOfTheSpawnLocations(p);
            Methods.OpenKitSelectionUI(p);
        }
    }


}
