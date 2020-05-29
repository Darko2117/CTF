package com.darko.plugin.commands;

import com.darko.plugin.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class CTFCommandTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.hasPermission("ctf.admin")) {
            if (args.length == 1) {

                List<String> completions = new ArrayList<>();
                for (String s : getCTFCommands()) {
                    if (s.startsWith(args[0])) {
                        completions.add(s);
                    }
                }
                return completions;

            } else if (args.length == 2 && args[0].equalsIgnoreCase("kit")) {

                List<String> completions = new ArrayList<>();
                for (String s : getKitCommands()) {
                    if (s.startsWith(args[1])) {
                        completions.add(s);
                    }
                }
                return completions;

            } else if (args.length == 2 && args[0].equalsIgnoreCase("team")) {

                List<String> completions = new ArrayList<>();
                for (String s : getTeamCommands()) {
                    if (s.startsWith(args[1])) {
                        completions.add(s);
                    }
                }
                return completions;

            } else if (args.length == 3 && args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("delete")) {

                return getKitNames();

            } else if (args.length == 3 && args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("seticon")) {

                return getKitNames();

            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("delete")) {

                return getTeamNames();

            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("addkit")) {

                return getTeamNames();

            } else if (args.length == 4 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("addkit")) {

                return getKitNames();

            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("removekit")) {

                return getTeamNames();

            } else if (args.length == 4 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("removekit")) {

                return getKitNames();

            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("addspawnlocation")) {

                return getTeamNames();
            } else if (args.length == 2 && args[0].equalsIgnoreCase("setdefaultkit")) {

                return getKitNames();
            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("setdisplayname")) {

                return getTeamNames();
                //} else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("setbaselocation")) {

                //return getTeamNames();
            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("setcolor")) {

                return getTeamNames();
            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("setflaglocation")) {

                return getTeamNames();
            }
        }
        return null;
    }


    public static List<String> getCTFCommands() {

        List<String> commands = new ArrayList<>();

        commands.add("start");
        commands.add("stop");
        commands.add("reload");
        commands.add("configreload");
        commands.add("kit");
        commands.add("team");
        commands.add("setdefaultkit");
        commands.add("setflaglocation");
        commands.add("setflagradius");
        commands.add("setflagparticlecount");
        commands.add("setsecondsneededforcapture");
        commands.add("setpointsneededtowin");

        return commands;
    }

    public static List<String> getKitCommands() {

        List<String> commands = new ArrayList<>();

        commands.add("create");
        commands.add("delete");
        commands.add("seticon");

        return commands;

    }

    public static List<String> getTeamCommands() {

        List<String> commands = new ArrayList<>();

        commands.add("create");
        commands.add("delete");
        commands.add("addkit");
        commands.add("removekit");
        commands.add("addspawnlocation");
        commands.add("setdisplayname");
        //commands.add("setbaselocation");
        commands.add("setcolor");
        commands.add("setflaglocation");

        return commands;
    }

    public static List<String> getTeamNames() {

        List<String> completions = new ArrayList<>();

        for (String s : Main.getInstance().getConfig().getKeys(true)) {
            if (s.startsWith("Teams.")) {
                s = s.substring(6);
                if (s.contains(".")) {
                    s = s.substring(0, s.indexOf("."));
                }
                if (!completions.contains(s)) completions.add(s);
            }
        }

        return completions;
    }

    public static List<String> getKitNames() {

        List<String> completions = new ArrayList<>();

        for (String s : Main.getInstance().getConfig().getKeys(true)) {
            if (s.startsWith("Kits.")) {
                s = s.substring(5);
                if (s.contains(".")) {
                    s = s.substring(0, s.indexOf("."));
                }
                if (!completions.contains(s)) completions.add(s);
            }
        }

        return completions;
    }

    public static void invalidUsageMessageCTFCommands(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Invalid usage. Possible commands:");
        for (String s : getCTFCommands()) {
            sender.sendMessage(ChatColor.RED + "/ctf " + s);
        }
    }

    public static void invalidUsageMessageKitCommands(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Invalid usage. Possible commands:");
        for (String s : getKitCommands()) {
            if (s.equals("create") || s.equals("delete") || s.equals("seticon")) {
                sender.sendMessage(ChatColor.RED + "/ctf kit " + s + " [name]");
            }
        }
    }

    public static void invalidUsageMessageTeamCommands(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Invalid usage. Possible commands:");
        for (String s : getTeamCommands()) {
            if (s.equals("addkit") || s.equals("removekit")) {
                sender.sendMessage(ChatColor.RED + "/ctf team " + s + " [team-name] [kit-name]");
            } else if (s.equals("setdisplayname")) {
                sender.sendMessage(ChatColor.RED + "/ctf team " + s + " [team-name] [display-name]");
            } else if (s.equals("setbaselocation")) {
                sender.sendMessage(ChatColor.RED + "/ctf team " + s + " [team-name] [display-name]");
            } else {
                sender.sendMessage(ChatColor.RED + "/ctf team " + s);
            }
        }
    }


}