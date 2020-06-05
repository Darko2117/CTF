package com.darko.plugin.commands;

import com.darko.plugin.Main;
import com.darko.plugin.Methods;
import com.darko.plugin.other.Serialization;
import com.darko.plugin.events.events.GameStartEvent;
import com.darko.plugin.gameclasses.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CTFCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Boolean wrongCommand = true;

        if (args.length > 0) {
            for (String s : CTFCommandTabComplete.getCTFCommands()) {
                if (args[0].equalsIgnoreCase(s)) {
                    wrongCommand = false;
                    break;
                }
            }
        }

        if (wrongCommand) {
            CTFCommandTabComplete.invalidUsageMessageCTFCommands(sender);
            return false;
        }

        if (args[0].equalsIgnoreCase("start")) Start(sender);
        if (args[0].equalsIgnoreCase("stop")) Stop();
        if (args[0].equalsIgnoreCase("reload")) Reload();
        if (args[0].equalsIgnoreCase("configreload")) ConfigReload(sender);
        if (args[0].equalsIgnoreCase("kit")) Kit(sender, args);
        if (args[0].equalsIgnoreCase("team")) Team(sender, args);
        if (args[0].equalsIgnoreCase("setdefaultkit")) SetDefaultKit(sender, args);
        if (args[0].equalsIgnoreCase("setflagdepositlocation")) SetFlagDepositLocation(sender, args);
        if (args[0].equalsIgnoreCase("setflagradius")) SetFlagRadius(sender, args);
        if (args[0].equalsIgnoreCase("setflagparticlecount")) SetFlagParticleCount(sender, args);
        if (args[0].equalsIgnoreCase("setsecondsneededforcapture")) SetSecondsNeededForCapture(sender, args);
        if (args[0].equalsIgnoreCase("setpointsneededtowin")) SetPointsNeededToWin(sender, args);
        if (args[0].equalsIgnoreCase("setfinalrespawnpoint")) SetFinalRespawnPoint(sender, args);
        if (args[0].equalsIgnoreCase("setdeathtimer")) SetDeathTimer(sender, args);
        if (args[0].equalsIgnoreCase("setwinnerpermission")) SetWinnerPermission(sender, args);

        return false;
    }

    private void Start(CommandSender sender) {

        GameStartEvent event = new GameStartEvent(sender);
        Bukkit.getPluginManager().callEvent(event);

    }

    private void SetDefaultKit(CommandSender sender, String[] args) {

        String kitName = args[1];

        Main.getInstance().getConfig().set("DefaultKit", kitName);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.AQUA + kitName + ChatColor.GREEN + " set as the default kit.");

    }

    private void Stop() {

        GameManager.setActiveGame(null);

    }

    private void Reload() {

    }

    private void ConfigReload(CommandSender sender) {

        Main.getInstance().reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Config reloaded!");

    }

    private void Team(CommandSender sender, String[] args) {

        if (args.length < 3) {
            CTFCommandTabComplete.invalidUsageMessageTeamCommands(sender);
            return;
        }

        if (!CTFCommandTabComplete.getTeamCommands().contains(args[1])) {
            CTFCommandTabComplete.invalidUsageMessageTeamCommands(sender);
            return;
        }

        if (args[1].equalsIgnoreCase("create")) teamCreate(sender, args);
        if (args[1].equalsIgnoreCase("delete")) teamDelete(sender, args);
        if (args[1].equalsIgnoreCase("addkit")) teamAddKit(sender, args);
        if (args[1].equalsIgnoreCase("removekit")) teamRemoveKit(sender, args);
        if (args[1].equalsIgnoreCase("addspawnlocation")) teamAddSpawnLocation(sender, args);
        if (args[1].equalsIgnoreCase("setdisplayname")) teamSetDisplayName(sender, args);
        //if (args[1].equalsIgnoreCase("setbaselocation")) teamSetBaseLocation(sender, args);
        if (args[1].equalsIgnoreCase("setflaglocation")) teamSetFlagLocation(sender, args);
        if (args[1].equalsIgnoreCase("setcolor")) teamSetColor(sender, args);

    }

    private void teamCreate(CommandSender sender, String[] args) {

        String teamName = args[2];
        Main.getInstance().getConfig().createSection("Teams." + teamName);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Team " + ChatColor.AQUA + teamName + ChatColor.GREEN + " created.");

    }

    private void teamDelete(CommandSender sender, String[] args) {

        String teamName = args[2];

        if (Main.getInstance().getConfig().contains("Teams." + teamName)) {

            Main.getInstance().getConfig().set("Teams." + teamName, null);
            Main.getInstance().saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Team " + ChatColor.AQUA + teamName + ChatColor.GREEN + " deleted.");

        } else {
            sender.sendMessage(ChatColor.RED + "Team " + ChatColor.AQUA + teamName + ChatColor.RED + " does not exist.");
        }

    }

    private void teamAddKit(CommandSender sender, String[] args) {

        String teamName = args[2];
        String kitName = args[3];

        if (Main.getInstance().getConfig().contains("Kits." + kitName)) {

            List<String> kits = Main.getInstance().getConfig().getStringList("Teams." + teamName + ".Kits");
            kits.add(kitName);
//            Game game = new Game();
//
//            if (Main.getInstance().getConfig().contains("Teams." + teamName + ".Kits")) {
//
//                for (String s : Main.getInstance().getConfig().getStringList("Teams." + teamName + ".Kits")) {
//
//                    if (game.getKitByName(s) != null) {
//                        kits.add(s);
//                    }
//
//                }
//
//            }
//
//            if (game.getKitByName(kitName) != null) {
//                kits.add(kitName);
//            } else {
//                sender.sendMessage(ChatColor.RED + "The kit " + ChatColor.AQUA + kitName + ChatColor.RED + " does not exist.");
//            }

            Main.getInstance().getConfig().set("Teams." + teamName + ".Kits", kits);
            Main.getInstance().saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.AQUA + kitName + ChatColor.GREEN + " added to the team " + ChatColor.AQUA + teamName + ChatColor.GREEN + ". Kits available for that team are: " + ChatColor.AQUA + kits);

        }
    }

    private void teamRemoveKit(CommandSender sender, String[] args) {

        String teamName = args[2];
        String kitName = args[3];

        if (Main.getInstance().getConfig().contains("Kits." + kitName)) {

            List<String> kits = Main.getInstance().getConfig().getStringList("Teams." + teamName + ".Kits");
            kits.remove(kitName);
            Main.getInstance().getConfig().set("Teams." + teamName + ".Kits", kits);
            Main.getInstance().saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.AQUA + kitName + ChatColor.GREEN + " removed from the team " + ChatColor.AQUA + teamName + ChatColor.GREEN + ". Kits available for that team are: " + ChatColor.AQUA + kits);

        }
    }

    private void teamAddSpawnLocation(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be performed by a player.");
            return;
        }

        String location = Methods.WriteLocationToString(((Player) sender).getLocation());
        String teamName = args[2];

//        for(Team t : GameManager.getActiveGame().getTeams()){
//
//            if(t.getName().equalsIgnoreCase(teamName)){
//                t.addSpawnLocation(location);
//            }
//
//        }

        if (Main.getInstance().getConfig().contains("Teams." + teamName)) {

            List<String> locations = new ArrayList<>();

            if (Main.getInstance().getConfig().contains("Teams." + teamName + ".SpawnLocations")) {
                locations = Main.getInstance().getConfig().getStringList("Teams." + teamName + ".SpawnLocations");
            }

            locations.add(location);

            Main.getInstance().getConfig().set("Teams." + teamName + ".SpawnLocations", locations);
            Main.getInstance().saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Spawn location added for team " + ChatColor.AQUA + teamName + ChatColor.GREEN + ".");

        }
    }

    private void teamSetDisplayName(CommandSender sender, String[] args) {

        String teamName = args[2];
        String displayName = args[3];

        Main.getInstance().getConfig().set("Teams." + teamName + ".DisplayName", displayName);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GREEN + "Display name for team " + ChatColor.AQUA + teamName + ChatColor.GREEN + " set to " + displayName));

    }

    private void teamSetBaseLocation(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be performed by a player.");
            return;
        }

        String teamName = args[2];
        String location = Methods.WriteLocationToString(((Player) sender).getLocation());

        Main.getInstance().getConfig().set("Teams." + teamName + ".BaseLocation", location);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Base location for team " + ChatColor.AQUA + teamName + ChatColor.GREEN + " saved.");

    }

    private void teamSetFlagLocation(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be performed by a player.");
            return;
        }

        String teamName = args[2];
        Location senderLocation = ((Player) sender).getLocation();

        if (senderLocation.getY() % 1 == 0) senderLocation.setY(senderLocation.getY() - 1);

        Block block = Bukkit.getWorld(senderLocation.getWorld().getName()).getBlockAt(senderLocation);

        String blockString = Methods.WriteBlockToString(block);

        Main.getInstance().getConfig().set("Teams." + teamName + ".Flag", blockString);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Flag for the team " + ChatColor.AQUA + teamName + ChatColor.GREEN + " saved " + ChatColor.AQUA + blockString);

    }

    private void teamSetColor(CommandSender sender, String[] args) {

        String teamName = args[2];
        String color = args[3];

        Main.getInstance().getConfig().set("Teams." + teamName + ".Color", color);
        Main.getInstance().saveConfig();
        String color1 = "&" + color;
        sender.sendMessage(ChatColor.GREEN + "Color for the team " + ChatColor.AQUA + teamName + ChatColor.GREEN + " set to " + ChatColor.translateAlternateColorCodes('&', color1) + "this" + ChatColor.GREEN + ".");

    }

    private void Kit(CommandSender sender, String[] args) {

        if (args.length < 3) {
            CTFCommandTabComplete.invalidUsageMessageKitCommands(sender);
            return;
        }

        if (!CTFCommandTabComplete.getKitCommands().contains(args[1])) {
            CTFCommandTabComplete.invalidUsageMessageKitCommands(sender);
            return;
        }

        if (args[1].equalsIgnoreCase("create")) kitCreate(sender, args);
        if (args[1].equalsIgnoreCase("delete")) kitDelete(sender, args);
        if (args[1].equalsIgnoreCase("seticon")) kitSetIcon(sender, args);
        if (args[1].equalsIgnoreCase("addpotioneffect")) kitAddPotionEffect(sender, args);

    }

    private void kitCreate(CommandSender sender, String[] args) {

        Kit kit = new Kit();

        kit.setName(args[2]);
        kit.setInventory(Serialization.playerInventoryToBase64(((Player) sender).getInventory())[0]);

        Main.getInstance().getConfig().set("Kits." + kit.getName(), null);
        Main.getInstance().getConfig().set("Kits." + kit.getName() + ".Inventory", kit.getInventory());
        Main.getInstance().getConfig().set("Kits." + kit.getName() + ".AvailableAbilities", kit.getAvailableAbilities());
        Main.getInstance().getConfig().set("Kits." + kit.getName() + ".Icon", "not set");
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.AQUA + kit.getName() + ChatColor.GREEN + " created.");

    }

    private void kitDelete(CommandSender sender, String[] args) {

        String kitName = args[2];

        if (Main.getInstance().getConfig().contains("Kits." + kitName)) {

            Main.getInstance().getConfig().set("Kits." + kitName, null);
            Main.getInstance().saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.AQUA + kitName + ChatColor.GREEN + " deleted.");

        } else {
            sender.sendMessage(ChatColor.RED + "Kit " + ChatColor.AQUA + kitName + ChatColor.RED + " does not exist.");
        }
    }

    private void kitSetIcon(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be performed by a player.");
            return;
        }

        String kitName = args[2];

        if (Main.getInstance().getConfig().contains("Kits." + kitName)) {

            Main.getInstance().getConfig().set("Kits." + kitName + ".Icon", ((Player) sender).getInventory().getItemInMainHand());
            Main.getInstance().saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Icon for the kit " + ChatColor.AQUA + kitName + ChatColor.GREEN + " changed.");

        } else {
            sender.sendMessage(ChatColor.RED + "Kit " + ChatColor.AQUA + kitName + ChatColor.RED + " does not exist.");
        }
    }

    private void kitAddPotionEffect(CommandSender sender, String[] args) {

        String amplifier = String.valueOf(Integer.parseInt(args[4]) - 1);
        String kit = args[2];
        String type = args[3];

        List<String> effects = new ArrayList<>();
        if (Main.getInstance().getConfig().getStringList("Kits." + kit + ".PotionEffects") != null) {
            effects = Main.getInstance().getConfig().getStringList("Kits." + kit + ".PotionEffects");
        }

        String effect = kit + "|" + type + "|" + amplifier + "|";
        effects.add(effect);

        Main.getInstance().getConfig().set("Kits." + kit + ".PotionEffects", effects);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Added a potion effect " + ChatColor.AQUA + effect + ChatColor.GREEN + ".");

    }

    private void SetFlagDepositLocation(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be performed by a player.");
            return;
        }

        String location = Methods.WriteLocationToString(((Player) sender).getLocation());

        Main.getInstance().getConfig().set("FlagDepositLocation", location);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Flag deposit location saved! ");

    }

    private void SetFlagRadius(CommandSender sender, String[] args) {

        Integer radius = Integer.parseInt(args[1]);

        Main.getInstance().getConfig().set("FlagRadius", radius);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Flag radius set to " + ChatColor.AQUA + radius + ChatColor.GREEN + ".");

    }

    private void SetFlagParticleCount(CommandSender sender, String[] args) {

        Integer count = Integer.parseInt(args[1]);

        Main.getInstance().getConfig().set("FlagParticleCount", count);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Flag particle count set to " + ChatColor.AQUA + count + ChatColor.GREEN + ".");

    }

    private void SetSecondsNeededForCapture(CommandSender sender, String[] args) {

        Integer seconds = Integer.parseInt(args[1]);

        Main.getInstance().getConfig().set("FlagSecondsNeededForCapture", seconds);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Seconds needed for capture set to " + ChatColor.AQUA + seconds + ChatColor.GREEN + ".");

    }

    private void SetPointsNeededToWin(CommandSender sender, String[] args) {

        Integer points = Integer.parseInt(args[1]);

        Main.getInstance().getConfig().set("PointsNeededToWin", points);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Points needed for a win set to " + ChatColor.AQUA + points + ChatColor.GREEN + ".");

    }

    private void SetFinalRespawnPoint(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be performed by a player.");
            return;
        }

        String location = Methods.WriteLocationToString(((Player) sender).getLocation());

        Main.getInstance().getConfig().set("FinalRespawnPoint", location);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Final respawn location saved! ");

    }

    private void SetDeathTimer(CommandSender sender, String[] args) {

        Integer seconds = Integer.parseInt(args[1]);

        Main.getInstance().getConfig().set("DeathTimer", seconds);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Death timer set to " + ChatColor.AQUA + seconds + ChatColor.GREEN + ".");

    }

    private void SetWinnerPermission(CommandSender sender, String[] args) {

        String permission = args[1];

        Main.getInstance().getConfig().set("WinnerPermission", permission);
        Main.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Winner permission set to " + ChatColor.AQUA + permission + ChatColor.GREEN + ".");

    }
}
