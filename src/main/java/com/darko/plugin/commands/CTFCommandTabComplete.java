package com.darko.plugin.commands;

import com.darko.plugin.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CTFCommandTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.hasPermission("ctf.admin")) {
            if (args.length == 1) {

                return getStartsWithList(getCTFCommands(), args[0]);

            } else if (args.length == 2 && args[0].equalsIgnoreCase("kit")) {

                return getStartsWithList(getKitCommands(), args[1]);

            } else if (args.length == 2 && args[0].equalsIgnoreCase("team")) {

                return getStartsWithList(getTeamCommands(), args[1]);

            } else if (args.length == 3 && args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("delete")) {

                return getStartsWithList(getKitNames(), args[2]);

            } else if (args.length == 3 && args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("seticon")) {

                return getStartsWithList(getKitNames(), args[2]);

            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("delete")) {

                return getStartsWithList(getTeamNames(), args[2]);

            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("addkit")) {

                return getStartsWithList(getTeamNames(), args[2]);

            } else if (args.length == 4 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("addkit")) {

                return getStartsWithList(getKitNames(), args[3]);

            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("removekit")) {

                return getStartsWithList(getTeamNames(), args[2]);

            } else if (args.length == 4 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("removekit")) {

                return getStartsWithList(getKitNames(), args[3]);

            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("addspawnlocation")) {

                return getStartsWithList(getTeamNames(), args[2]);

            } else if (args.length == 2 && args[0].equalsIgnoreCase("setdefaultkit")) {

                return getStartsWithList(getKitNames(), args[1]);

            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("setdisplayname")) {

                return getStartsWithList(getTeamNames(), args[2]);

            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("setcolor")) {

                return getStartsWithList(getTeamNames(), args[2]);

            } else if (args.length == 3 && args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("setflaglocation")) {

                return getStartsWithList(getTeamNames(), args[2]);

            } else if (args.length == 3 && args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("addpotioneffect")) {

                return getStartsWithList(getKitNames(), args[2]);

            } else if (args.length == 4 && args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("addpotioneffect")) {

                List<String> effects = new ArrayList<>();

                for (PotionEffectType p : PotionEffectType.values()) {
                    effects.add(p.getName());
                }

                return getStartsWithList(effects, args[3]);

            }
        }
        return null;
    }

    public static List<String> getStartsWithList(List<String> list, String startsWith){

        List<String> completions = new ArrayList<>();
        for(String s : list){
            if(s.toLowerCase().startsWith(startsWith.toLowerCase())){
                completions.add(s);
            }
        }
        return completions;

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
        commands.add("setflagdepositlocation");
        commands.add("setflagradius");
        commands.add("setflagparticlecount");
        commands.add("setsecondsneededforcapture");
        //commands.add("setpointsneededtowin");
        commands.add("setfinalrespawnpoint");
        commands.add("setdeathtimer");
        commands.add("setwinnerpermission");

        return commands;

    }

    public static List<String> getKitCommands() {

        List<String> commands = new ArrayList<>();

        commands.add("create");
        commands.add("delete");
        commands.add("seticon");
        commands.add("addpotioneffect");

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