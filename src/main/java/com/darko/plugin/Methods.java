package com.darko.plugin;

import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.gameclasses.Game;
import com.darko.plugin.gameclasses.GameManager;
import com.darko.plugin.gameclasses.Participant;
import com.darko.plugin.gameclasses.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Methods {

    public static Inventory UI;

    public static HashMap<ItemStack, String> iconsAndKits = new HashMap<>();

    public static void RespawnFlagEliminated() {

        Game game = GameManager.getActiveGame();

        Block flag = GetBlockFromString(Main.getInstance().getConfig().getString("Flag"));

        Bukkit.getWorld(flag.getLocation().getWorld().getName()).getBlockAt(flag.getLocation()).setType(flag.getType());
        Bukkit.getWorld(flag.getLocation().getWorld().getName()).getBlockAt(flag.getLocation()).setBlockData(flag.getBlockData());
        Bukkit.getWorld(flag.getLocation().getWorld().getName()).strikeLightningEffect(flag.getLocation());

        game.setFlagCarrier(null);

        for (Player p : Bukkit.getOnlinePlayers()) {

            p.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "FLAG CARRIER ELIMINATED", "THE FLAG CAN BE CAPTURED AGAIN", 5, 40, 5);

        }
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

        if (UI == null) {

            Integer numberOfKits = CTFCommandTabComplete.getKitNames().size();
            Integer numberOfSlotsInTheUI;

            if (numberOfKits <= 9) numberOfSlotsInTheUI = 9;
            else if (numberOfKits <= 18) numberOfSlotsInTheUI = 18;
            else if (numberOfKits <= 27) numberOfSlotsInTheUI = 27;
            else if (numberOfKits <= 36) numberOfSlotsInTheUI = 36;
            else if (numberOfKits <= 45) numberOfSlotsInTheUI = 45;
            else numberOfSlotsInTheUI = 54;

            UI = Bukkit.createInventory(null, numberOfSlotsInTheUI, "" + ChatColor.BLACK + ChatColor.BOLD + "Choose a class");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.ITALIC + "Left click to select the kit");
            lore.add(ChatColor.ITALIC + "Right click to see the items and the abilities");

            for (String kitName : CTFCommandTabComplete.getKitNames()) {
                ItemStack tempItem = Main.getInstance().getConfig().getItemStack("Kits." + kitName + ".Icon");
                tempItem.setLore(lore);
                iconsAndKits.put(tempItem, kitName);
            }

            Integer i = 0;
            for(Map.Entry<ItemStack, String> entry : iconsAndKits.entrySet()){
                UI.setItem(i, entry.getKey());
                i++;
            }

        }

        participant.getPlayer().openInventory(UI);

    }

    public static String WriteLocationToString(Location location){

        String world = location.getWorld().getName();
        String X = String.valueOf(location.getX());
        String Y = String.valueOf(location.getY());
        String Z = String.valueOf(location.getZ());
        String pitch = String.valueOf(location.getPitch());
        String yaw = String.valueOf(location.getYaw());

        return world + "|" + X + "|" + Y + "|" + Z + "|" + pitch + "|" + yaw + "|";

    }

    public static Location GetLocationFromString(String string){

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
}
