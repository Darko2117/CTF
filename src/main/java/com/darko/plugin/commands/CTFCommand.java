package com.darko.plugin.commands;

import com.darko.plugin.Main;
import com.darko.plugin.Methods;
import com.darko.plugin.commands.command_enums.CTFCommands;
import com.darko.plugin.commands.command_enums.KitCommands;
import com.darko.plugin.commands.command_enums.TeamCommands;
import com.darko.plugin.events.events.GameStartEvent;
import com.darko.plugin.gameclasses.GameManager;
import com.darko.plugin.gameclasses.Kit;
import com.darko.plugin.other.Serialization;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CTFCommand implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            CTFCommandTabComplete.invalidUsageMessageCTFCommands(sender);
            return true;
        }
        Optional<CTFCommands> any = Arrays.stream(CTFCommands.values())
                .filter(managedCommand -> managedCommand.getCommand().equalsIgnoreCase(args[0]))
                .findAny();
        CTFCommands command;
        if (any.isPresent()) {
            command = any.get();
        } else {
            CTFCommandTabComplete.invalidUsageMessageCTFCommands(sender);
            return true;
        }

        switch (command) {
            case START -> start(sender);
            case STOP -> stop();
            case RELOAD, CONFIG_RELOAD -> configReload(sender);
            case KIT -> kit(sender, args);
            case TEAM -> team(sender, args);
            case SET_DEFAULT_KIT -> setDefaultKit(sender, args);
            case SET_FLAG_RADIUS -> setFlagRadius(sender, args);
            case SET_FLAG_COUNT -> setFlagParticleCount(sender, args);
            case SET_SECONDS -> setSecondsNeededForCapture(sender, args);
            case SET_RESPAWN_POINT -> setFinalRespawnPoint(sender);
            case SET_DEATH_TIMER -> setDeathTimer(sender, args);
            case SET_WINNER -> setWinnerPermission(sender, args);
        }
        return true;
    }

    private void start(CommandSender sender) {
        GameStartEvent event = new GameStartEvent(sender);
        Bukkit.getPluginManager().callEvent(event);
    }

    private void stop() {
        GameManager.setActiveGame(null);
    }

    private void configReload(CommandSender sender) {
        Main.getInstance().reloadConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Config reloaded!</green>"));
    }

    private void team(CommandSender sender, String[] args) {
        if (args.length < 3) {
            CTFCommandTabComplete.invalidUsageMessageTeamCommands(sender);
            return;
        }

        Optional<TeamCommands> any = Arrays.stream(TeamCommands.values())
                .filter(teamCommand -> teamCommand.getCommand().equalsIgnoreCase(args[1]))
                .findAny();
        if (any.isEmpty()) {
            CTFCommandTabComplete.invalidUsageMessageTeamCommands(sender);
            return;
        }

        switch (any.get()) {
            case CREATE -> teamCreate(sender, args);
            case DELETE -> teamDelete(sender, args);
            case ADD_KIT -> teamAddKit(sender, args);
            case REMOVE_KIT -> teamRemoveKit(sender, args);
            case ADD_SPAWN_LOCATION -> teamAddSpawnLocation(sender, args);
            case SET_DISPLAY_NAME -> teamSetDisplayName(sender, args);
            case SET_COLOR -> teamSetColor(sender, args);
            case SET_FLAG_LOCATION -> teamSetFlagLocation(sender, args);
        }
    }

    private void teamCreate(CommandSender sender, String[] args) {
        String teamName = args[2];
        Main.getInstance().getConfig().createSection("Teams." + teamName);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Team <aqua><team_name></aqua> created.</green>",
                Placeholder.parsed("team_name", teamName)));
    }

    private void teamDelete(CommandSender sender, String[] args) {
        String teamName = args[2];

        if (Main.getInstance().getConfig().contains("Teams." + teamName)) {
            Main.getInstance().getConfig().set("Teams." + teamName, null);
            Main.getInstance().saveConfig();
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Team <aqua><team_name></aqua> deleted.</green>",
                    Placeholder.parsed("team_name", teamName)));
        } else {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<red>Team <aqua><team_name></aqua> does not exist.</red>",
                    Placeholder.parsed("team_name", teamName)));
        }
    }

    private void teamAddKit(CommandSender sender, String[] args) {
        String teamName = args[2];
        String kitName = args[3];

        List<String> kits = Main.getInstance().getConfig().getStringList("Teams." + teamName + ".Kits");
        kits.add(kitName);
        Main.getInstance().getConfig().set("Teams." + teamName + ".Kits", kits);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Kit <aqua><kit_name></aqua> added to the team <aqua><team_name></aqua>. Kits available for that team are: <aqua><kits></aqua></green>",
                Placeholder.parsed("kit_name", kitName),
                Placeholder.parsed("team_name", teamName),
                Placeholder.parsed("kits", String.join(", ", kits))));
    }

    private void teamRemoveKit(CommandSender sender, String[] args) {
        String teamName = args[2];
        String kitName = args[3];

        List<String> kits = Main.getInstance().getConfig().getStringList("Teams." + teamName + ".Kits");
        kits.remove(kitName);
        Main.getInstance().getConfig().set("Teams." + teamName + ".Kits", kits);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Kit <aqua><kit_name></aqua> removed from the team <aqua><team_name></aqua>. Kits available for that team are: <aqua><kits></aqua></green>",
                Placeholder.parsed("kit_name", kitName),
                Placeholder.parsed("team_name", teamName),
                Placeholder.parsed("kits", String.join(", ", kits))));

    }

    private void teamAddSpawnLocation(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>This command can only be performed by a player.</red>"));
            return;
        }

        String location = Methods.writeLocationToString(((Player) sender).getLocation());
        String teamName = args[2];

        List<String> locations = Main.getInstance().getConfig().getStringList("Teams." + teamName + ".SpawnLocations");
        locations.add(location);

        Main.getInstance().getConfig().set("Teams." + teamName + ".SpawnLocations", locations);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Spawn location added for team <aqua><team_name></aqua>.</green>",
                Placeholder.parsed("team_name", teamName)));

    }

    private void teamSetDisplayName(CommandSender sender, String[] args) {
        String teamName = args[2];
        String displayName = args[3];

        Main.getInstance().getConfig().set("Teams." + teamName + ".DisplayName", displayName);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Display name for team <aqua><team_name></aqua> set to <display_name></green>",
                Placeholder.parsed("team_name", teamName),
                Placeholder.parsed("display_name", displayName)));
    }

    private void teamSetFlagLocation(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>This command can only be performed by a player.</red>"));
            return;
        }

        String teamName = args[2];
        Location senderLocation = ((Player) sender).getLocation();

        if (senderLocation.getY() % 1 == 0) senderLocation.setY(senderLocation.getY() - 1);

        Block block = senderLocation.getWorld().getBlockAt(senderLocation);

        String blockString = Methods.writeBlockToString(block);

        Main.getInstance().getConfig().set("Teams." + teamName + ".Flag", blockString);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Flag for the team <aqua><team_name></aqua> saved <block_string>.</green>",
                Placeholder.parsed("team_name", teamName),
                Placeholder.parsed("block_string", blockString)));

    }

    private void teamSetColor(CommandSender sender, String[] args) {
        String teamName = args[2];
        String color = args[3];

        Main.getInstance().getConfig().set("Teams." + teamName + ".Color", color);
        Main.getInstance().saveConfig();
        String miniMessageColor = "<" + color + ">";
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Color for the team <aqua><team_name></aqua> set to " + miniMessageColor + "this<reset><green>.</green>",
                Placeholder.parsed("team_name", teamName)));
    }

    private void kit(CommandSender sender, String[] args) {
        if (args.length < 3) {
            CTFCommandTabComplete.invalidUsageMessageKitCommands(sender);
            return;
        }

        Optional<KitCommands> any = Arrays.stream(KitCommands.values())
                .filter(kitCommands -> kitCommands.getCommand().equalsIgnoreCase(args[1]))
                .findAny();
        if (any.isEmpty()) {
            CTFCommandTabComplete.invalidUsageMessageKitCommands(sender);
            return;
        }

        switch (any.get()) {
            case CREATE -> kitCreate(sender, args);
            case DELETE -> kitDelete(sender, args);
            case SET_ICON -> kitSetIcon(sender, args);
            case ADD_POTION_EFFECT -> kitAddPotionEffect(sender, args);
        }
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
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Kit <aqua><kit_name></aqua> created.</green>",
                Placeholder.parsed("kit_name", kit.getName())));
    }

    private void kitDelete(CommandSender sender, String[] args) {
        String kitName = args[2];

        if (Main.getInstance().getConfig().contains("Kits." + kitName)) {
            Main.getInstance().getConfig().set("Kits." + kitName, null);
            Main.getInstance().saveConfig();
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Kit <aqua><kit_name></aqua> deleted.</green>",
                    Placeholder.parsed("kit_name", kitName)));
        } else {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<red>Kit <aqua><kit_name></aqua> does not exist.</red>",
                    Placeholder.parsed("kit_name", kitName)));
        }
    }

    private void kitSetIcon(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>This command can only be performed by a player.</red>"));
            return;
        }

        String kitName = args[2];
        Main.getInstance().getConfig().set("Kits." + kitName + ".Icon", ((Player) sender).getInventory().getItemInMainHand());
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Icon for the kit <aqua><kit_name></aqua> changed.</green>",
                Placeholder.parsed("kit_name", kitName)));
    }

    private void kitAddPotionEffect(CommandSender sender, String[] args) {
        String amplifier = String.valueOf(Integer.parseInt(args[4]) - 1);
        String kit = args[2];
        String type = args[3];

        List<String> effects = Main.getInstance().getConfig().getStringList("Kits." + kit + ".PotionEffects");

        String effect = kit + "|" + type + "|" + amplifier + "|";
        effects.add(effect);

        Main.getInstance().getConfig().set("Kits." + kit + ".PotionEffects", effects);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Added a potion effect <aqua><effect></aqua>.</green>",
                Placeholder.parsed("effect", effect)));
    }

    private void setFlagRadius(CommandSender sender, String[] args) {
        int radius = Integer.parseInt(args[1]);

        Main.getInstance().getConfig().set("FlagRadius", radius);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Flag radius set to <aqua><radius></aqua>.</green>",
                Placeholder.parsed("radius", String.valueOf(radius))));
    }

    private void setFlagParticleCount(CommandSender sender, String[] args) {
        int count = Integer.parseInt(args[1]);

        Main.getInstance().getConfig().set("FlagParticleCount", count);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Flag particle count set to <aqua><count></aqua>.</green>",
                Placeholder.parsed("count", String.valueOf(count))));
    }

    private void setSecondsNeededForCapture(CommandSender sender, String[] args) {
        int seconds = Integer.parseInt(args[1]);

        Main.getInstance().getConfig().set("FlagSecondsNeededForCapture", seconds);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Seconds needed for capture set to <aqua><seconds></aqua>.</green>",
                Placeholder.parsed("seconds", String.valueOf(seconds))));

    }

    private void setFinalRespawnPoint(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>This command can only be performed by a player.</red>"));
            return;
        }

        String location = Methods.writeLocationToString(((Player) sender).getLocation());

        Main.getInstance().getConfig().set("FinalRespawnPoint", location);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Final respawn location saved!</green>"));
    }

    private void setDeathTimer(CommandSender sender, String[] args) {
        int seconds = Integer.parseInt(args[1]);

        Main.getInstance().getConfig().set("DeathTimer", seconds);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Death timer set to <aqua><seconds></aqua>.</green>",
                Placeholder.parsed("seconds", String.valueOf(seconds))));
    }

    private void setWinnerPermission(CommandSender sender, String[] args) {
        String permission = args[1];

        Main.getInstance().getConfig().set("WinnerPermission", permission);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Winner permission set to <aqua><permission></aqua>.</green>",
                Placeholder.parsed("permission", permission)));
    }


    private void setDefaultKit(CommandSender sender, String[] args) {
        String kitName = args[1];

        Main.getInstance().getConfig().set("DefaultKit", kitName);
        Main.getInstance().saveConfig();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Kit <aqua><kit_name></aqua> set as the default kit.</green>",
                Placeholder.parsed("kit_name", kitName)));
    }

}
