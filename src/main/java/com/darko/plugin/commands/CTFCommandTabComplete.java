package com.darko.plugin.commands;

import com.darko.plugin.Main;
import com.darko.plugin.commands.command_enums.CTFCommands;
import com.darko.plugin.commands.command_enums.KitCommands;
import com.darko.plugin.commands.command_enums.TeamCommands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CTFCommandTabComplete implements TabCompleter {
    
    private final static List<String> VALID_SUB_COMMANDS_THREE_ARGS = Arrays.asList("delete", "seticon", "addkit", "removekit", "addspawnlocation", "setdisplayname", "setcolor", "setflaglocation", "addpotioneffect");
    private final static List<String> VALID_SUB_COMMANDS_FOUR_ARGS = Arrays.asList("addkit", "removekit", "addpotioneffect");

    @Override
    public List<String> onTabComplete(CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!sender.hasPermission("ctf.admin")) {
            return null;
        }
        int argLength = args.length;
        String firstArg = argLength > 0 ? args[0].toLowerCase() : "";
        String secondArg = argLength > 1 ? args[1].toLowerCase() : "";
        String thirdArg = argLength > 2 ? args[2] : "";
        String fourthArg = argLength > 3 ? args[3] : "";

        if (argLength == 1) {
            return getStartsWithList(getCTFCommands(), firstArg);
        }
        
        if (argLength == 2) {
            return switch (firstArg) {
                case "kit" -> getStartsWithList(getKitCommands(), secondArg);
                case "team" -> getStartsWithList(getTeamCommands(), secondArg);
                case "setdefaultkit" -> getStartsWithList(getKitNames(), secondArg);
                default -> List.of();
            };
        }
        
        if (argLength >= 3 && VALID_SUB_COMMANDS_THREE_ARGS.contains(secondArg)) {
            return getStartsWithList(firstArg.equals("kit") ? getKitNames() : getTeamNames(), thirdArg);
        }
        
        if (argLength == 4 && VALID_SUB_COMMANDS_FOUR_ARGS.contains(secondArg)) {
            if (secondArg.equals("addpotioneffect") && firstArg.equals("kit")) {
                List<String> effects = Stream.of(PotionEffectType.values())
                        .map(PotionEffectType::getName)
                        .collect(Collectors.toList());
                return getStartsWithList(effects, fourthArg);
            }
            return getStartsWithList(getKitNames(), fourthArg);
        }

        return List.of();
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
        return Arrays.stream(CTFCommands.values()).map(CTFCommands::getCommand).collect(Collectors.toList());
    }

    public static List<String> getKitCommands() {
        return Arrays.stream(KitCommands.values()).map(KitCommands::getCommand).collect(Collectors.toList());
    }

    public static List<String> getTeamCommands() {
        return Arrays.stream(TeamCommands.values()).map(TeamCommands::getCommand).collect(Collectors.toList());
    }

    public static List<String> getTeamNames() {
        return Main.getInstance().getConfig().getKeys(true).stream()
                .filter(s -> s.startsWith("Teams."))
                .map(s -> {
                    s = s.substring(6);
                    if (s.contains(".")) {
                        s = s.substring(0, s.indexOf("."));
                    }
                    return s;
                })
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<String> getKitNames() {
        return Main.getInstance().getConfig().getKeys(true).stream()
                .filter(s -> s.startsWith("Kits."))
                .map(s -> s.substring(5))
                .map(s -> s.contains(".") ? s.substring(0, s.indexOf(".")) : s)
                .distinct()
                .collect(Collectors.toList());
    }

    public static void invalidUsageMessageCTFCommands(CommandSender sender) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component message = miniMessage.deserialize("<red>Invalid usage. Possible commands:</red>");
        for (String command : getCTFCommands()) {
            message = message
                    .append(Component.newline())
                    .append(miniMessage.deserialize("<yellow>/ctf <command></yellow>", Placeholder.parsed("command", command)));
        }
        sender.sendMessage(message);
    }

    public static void invalidUsageMessageKitCommands(CommandSender sender) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component message = miniMessage.deserialize("<red>Invalid usage. Possible commands:</red>");
        for (String command : getKitCommands()) {
            message = message
                    .append(Component.newline())
                    .append(miniMessage.deserialize("<yellow>/ctf kit <command> [name]</yellow>", Placeholder.parsed("command", command)));
        }
        sender.sendMessage(message);
    }

    public static void invalidUsageMessageTeamCommands(CommandSender sender) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component message = miniMessage.deserialize("<red>Invalid usage. Possible commands:</red>");
        for (String command : getTeamCommands()) {
            message = message.append(Component.newline());
            message = switch (command) {
                case "addkit", "removekit" -> message
                        .append(miniMessage.deserialize("<yellow>/ctf team <command> [team-name] [kit-name]</yellow>", Placeholder.parsed("command", command)));
                case "setdisplayname", "setbaselocation" -> message
                        .append(miniMessage.deserialize("<yellow>/ctf team <command> [team-name] [display-name]</yellow>", Placeholder.parsed("command", command)));
                default ->
                        message.append(miniMessage.deserialize("<yellow>/ctf team <command></yellow>", Placeholder.parsed("command", command)));
            };
        }
        sender.sendMessage(message);
    }
}