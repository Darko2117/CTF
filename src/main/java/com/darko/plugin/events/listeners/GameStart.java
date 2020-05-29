package com.darko.plugin.events.listeners;

import com.darko.plugin.Main;
import com.darko.plugin.Methods;
import com.darko.plugin.events.events.GameStartEvent;
import com.darko.plugin.other.Serialization;
import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.gameclasses.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Score;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameStart implements Listener {

    @EventHandler
    public void onGameStart(GameStartEvent e) {

        MakeGameObject();

        LoadAvailableKits();

        LoadTeams();

        LoadFlag();

        SendTimedTitle(60, 0);
//        SendTimedTitle(30, 30);
//        SendTimedTitle(10, 50);
//        SendTimedTitle(5, 55);
//        SendTimedTitle(4, 56);
//        SendTimedTitle(3, 57);
//        SendTimedTitle(2, 58);
//        SendTimedTitle(1, 59);

        new BukkitRunnable() {
            @Override
            public void run() {
                StartGame();
            }
        }.runTaskLater(Main.getInstance(), 1 * 20);

        e.getStarter().sendMessage(ChatColor.GREEN + "Event started!");

    }


    private void MakeGameObject() {

        Game game = new Game();
        GameManager.setActiveGame(game);

    }

    private void LoadAvailableKits() {

        Game game = GameManager.getActiveGame();

        for (String s : CTFCommandTabComplete.getKitNames()) {
            game.addAvailableKit(game.getKitByName(s));
        }

    }

    private void LoadTeams() {

        for (String s : CTFCommandTabComplete.getTeamNames()) {

            Team team = new Team();

            team.setName(s);

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

                List<Kit> availableKits = new ArrayList<>();

                for (String s1 : Main.getInstance().getConfig().getStringList("Teams." + s + ".Kits")) {

                    Kit kit = new Kit();

                    kit.setName(s1);

                    String inventory = Main.getInstance().getConfig().getString("Kits." + s1 + ".Inventory");
                    kit.setInventory(inventory);

                    List<String> availableAbilities = Main.getInstance().getConfig().getStringList("Kits." + s1 + ".AvailableAbilities");
                    kit.setAvailableAbilities(availableAbilities);

                    ItemStack icon = null;
                    if (Main.getInstance().getConfig().getItemStack("Kits." + s1 + ".Icon") != null) {
                        icon = Main.getInstance().getConfig().getItemStack("Kits." + s1 + ".Icon");
                    }
                    kit.setIcon(icon);
                    availableKits.add(kit);

                }
                team.setAvailableKits(availableKits);
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

    private void LoadFlag() {

        Game game = GameManager.getActiveGame();

        Block flag = Methods.GetBlockFromString(Main.getInstance().getConfig().getString("Flag"));
        game.setFlag(flag);
        Bukkit.getWorld(flag.getLocation().getWorld().getName()).getBlockAt(flag.getLocation()).setType(flag.getType());
        Bukkit.getWorld(flag.getLocation().getWorld().getName()).getBlockAt(flag.getLocation()).setBlockData(flag.getBlockData());

        Integer radius = Main.getInstance().getConfig().getInt("FlagRadius");
        game.setFlagRadius(radius);

        Integer count = Main.getInstance().getConfig().getInt("FlagParticleCount");
        game.setFlagParticleCount(count);

        Integer seconds = Main.getInstance().getConfig().getInt("FlagSecondsNeededForCapture");
        game.setSecondsNeededForCapture(seconds);

        Integer points = Main.getInstance().getConfig().getInt("PointsNeededToWin");
        game.setPointsNeededToWin(points);

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

    private void LoadKitForParticipant(Participant p) {

        Boolean kitFound = false;

        for (PermissionAttachmentInfo pe : p.getPlayer().getEffectivePermissions()) {

            if (pe.getPermission().contains("ctf.kit.")) {

                kitFound = true;

                String kitString = pe.getPermission().substring(8);

                for (String s : CTFCommandTabComplete.getKitNames()) {

                    if (s.equalsIgnoreCase(kitString)) {

                        Kit kit = GameManager.getActiveGame().getKitByName(kitString);

                        GivePlayerKitInventory(p.getPlayer(), kit);
                        p.setKit(kit);

                    }
                }
            }
        }

        if (!kitFound) {

            String defaultKit = Main.getInstance().getConfig().getString("DefaultKit");

            for (String s : CTFCommandTabComplete.getKitNames()) {

                if (s.equalsIgnoreCase(defaultKit)) {

                    Kit kit = GameManager.getActiveGame().getKitByName(defaultKit);

                    GivePlayerKitInventory(p.getPlayer(), kit);
                    p.setKit(kit);

                }
            }

            Main.getInstance().getLogger().info(p.getPlayer().getName() + " doesn't have a kit permission set. They got the default kit: " + defaultKit);

        }

    }

    public static void TeleportParticipant(Participant p) {

        List<Location> locations = p.getTeam().getSpawnLocations();
        Random r = new Random();
        Location teleportSpot = locations.get(r.nextInt(locations.size()));
        p.getPlayer().teleport(teleportSpot);

    }

    private void StartGame() {
        for (Participant p : GameManager.getActiveGame().getParticipants()) {
            LoadKitForParticipant(p);
            TeleportParticipant(p);
            Methods.OpenKitSelectionUI(p);
        }
    }


    public static void GivePlayerKitInventory(Player player, Kit kit) {

        PlayerInventory inventory = player.getInventory();

        ItemStack[] contents = new ItemStack[41];
        try {
            contents = Serialization.itemStackArrayFromBase64(kit.getInventory());
        } catch (IOException e) {
            System.out.println(e);
        }
        inventory.setContents(contents);

        player.updateInventory();
    }



}
