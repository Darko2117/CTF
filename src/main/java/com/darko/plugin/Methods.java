package com.darko.plugin;

import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.gameclasses.*;
import com.darko.plugin.other.Serialization;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.IOException;
import java.util.*;

public class Methods {

    public static void RespawnFlag(Team teamWhosFlagShouldBeRespawned) {

        Game game = GameManager.getActiveGame();

        Team team = teamWhosFlagShouldBeRespawned;

        Block flagBlock = Methods.GetBlockFromString(Main.getInstance().getConfig().getString("Teams." + team.getName() + ".Flag"));

//        Block flag = GetBlockFromString(Main.getInstance().getConfig().getString("Flag"));
//
        Bukkit.getWorld(flagBlock.getLocation().getWorld().getName()).getBlockAt(flagBlock.getLocation()).setType(flagBlock.getType());
        Bukkit.getWorld(flagBlock.getLocation().getWorld().getName()).getBlockAt(flagBlock.getLocation()).setBlockData(flagBlock.getBlockData());
        Bukkit.getWorld(flagBlock.getLocation().getWorld().getName()).strikeLightningEffect(flagBlock.getLocation());

        game.getFlagFromTeam(team).setCarrier(null);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Player carrying " + ChatColor.translateAlternateColorCodes('&', team.getDisplayName()) + ChatColor.GOLD + "" + ChatColor.BOLD + "'s flag was eliminated! That team's flag will respawn in their base!");
        }
//
//        game.setFlagCarrier(null);
//
//        for (Player p : Bukkit.getOnlinePlayers()) {
//
//            p.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "FLAG CARRIER ELIMINATED", "THE FLAG CAN BE CAPTURED AGAIN", 5, 40, 5);
//
//        }
    }

    public static void RespawnFlagReturned(Team team) {

        Game game = GameManager.getActiveGame();

        Block flag = GetBlockFromString(Main.getInstance().getConfig().getString("Flag"));

        Bukkit.getWorld(flag.getLocation().getWorld().getName()).getBlockAt(flag.getLocation()).setType(flag.getType());
        Bukkit.getWorld(flag.getLocation().getWorld().getName()).getBlockAt(flag.getLocation()).setBlockData(flag.getBlockData());
        Bukkit.getWorld(flag.getLocation().getWorld().getName()).strikeLightningEffect(flag.getLocation());

        game.setFlagCarrier(null);

        String score = "";

        score += "THEY HAVE " + team.getPoints() + "/" + game.getPointsNeededToWin() + " POINTS NEEDED FOR A WIN";

        for (Player p : Bukkit.getOnlinePlayers()) {

            p.sendTitle(ChatColor.translateAlternateColorCodes('&', team.getDisplayName()) + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " CAPTURED THE FLAG", score, 5, 40, 5);

        }
    }

    public static void OpenKitSelectionUI(Participant participant) {

        participant.getPlayer().openInventory(participant.getTeam().getKitSelectInventory());

    }

    public static String WriteLocationToString(Location location) {

        String world = location.getWorld().getName();
        String X = String.valueOf(location.getX());
        String Y = String.valueOf(location.getY());
        String Z = String.valueOf(location.getZ());
        String pitch = String.valueOf(location.getPitch());
        String yaw = String.valueOf(location.getYaw());

        return world + "|" + X + "|" + Y + "|" + Z + "|" + pitch + "|" + yaw + "|";

    }

    public static Location GetLocationFromString(String string) {

        StringBuilder string1 = new StringBuilder(string);

        String worldName = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);

        String X = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);

        String Y = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);

        String Z = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);

        String pitch = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);

        String yaw = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);

        Location location = Bukkit.getServer().getWorld(worldName).getSpawnLocation();

        location.set(Double.parseDouble(X), Double.parseDouble(Y), Double.parseDouble(Z));
        location.setPitch(Float.parseFloat(pitch));
        location.setYaw(Float.parseFloat(yaw));

        return location;

    }

    public static String WriteBlockToString(Block block) {

        String blockString = "";

        //Location
        blockString += block.getLocation().getWorld().getName() + "|";
        blockString += block.getLocation().getX() + "|";
        blockString += block.getLocation().getY() + "|";
        blockString += block.getLocation().getZ() + "|";
        blockString += block.getType() + "|";
        blockString += block.getBlockData().getAsString() + "|";

        return blockString;
    }

    public static Block GetBlockFromString(String string) {

        StringBuilder string1 = new StringBuilder(string);

        String worldName = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);

        String X = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);
        X = X.substring(0, X.length() - 2);

        String Y = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);
        Y = Y.substring(0, Y.length() - 2);

        String Z = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);
        Z = Z.substring(0, Z.length() - 2);

        String Type = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);

        BlockData blockData = Bukkit.getServer().createBlockData(string1.substring(0, string1.indexOf("|")));
        string1.delete(0, string1.indexOf("|") + 1);

        Block block = Bukkit.getWorld(worldName).getBlockAt(Integer.parseInt(X), Integer.parseInt(Y), Integer.parseInt(Z));

        block.setType(Material.getMaterial(Type));
        block.setBlockData(blockData);

        return block;
    }

    public static void TeleportParticipantToOneOfTheSpawnLocations(Participant p) {

        List<Location> locations = p.getTeam().getSpawnLocations();
        Random r = new Random();
        Location teleportSpot = locations.get(r.nextInt(locations.size()));
        p.getPlayer().teleport(teleportSpot);

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

    public static void LoadKitForParticipant(Participant p) {

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

}
