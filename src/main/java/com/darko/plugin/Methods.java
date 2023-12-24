package com.darko.plugin;

import com.darko.plugin.gameclasses.*;
import com.darko.plugin.other.Serialization;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Methods {

    /**
     * Respawns the flag of the given team in their base.
     *
     * @param team the team whose flag needs to be respawned
     */
    public static void respawnFlag(Team team) {

        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            return;
        }
        Game game = optionalGame.get();

        Optional<Block> optionalBlock = Methods.getBlockFromString(Main.getInstance().getConfig().getString("Teams." + team.getName() + ".Flag"));
        if (optionalBlock.isEmpty()) {
            Main.getInstance().getLogger().severe("Unable to get block from string in respawnFlag");
            return;
        }

        Block flagBlock = optionalBlock.get();

        flagBlock.getLocation().getWorld().getBlockAt(flagBlock.getLocation()).setType(flagBlock.getType());
        flagBlock.getLocation().getWorld().getBlockAt(flagBlock.getLocation()).setBlockData(flagBlock.getBlockData());
        flagBlock.getLocation().getWorld().strikeLightningEffect(flagBlock.getLocation());

        game.getFlagFromTeam(team).ifPresent(flag -> flag.setCarrier(null));

        MiniMessage miniMessage = MiniMessage.miniMessage();
        String msg = "<gold><bold>Player carrying <team>'s flag was eliminated! That team's flag will respawn in their base!";
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(miniMessage.deserialize(msg, Placeholder.component("team", team.getDisplayName())));
        }
    }

    public static void openKitSelectionUI(Participant participant) {
        participant.getPlayer().openInventory(participant.getTeam().getKitSelectInventory());
    }

    public static String writeLocationToString(Location location) {
        String world = location.getWorld().getName();
        String X = String.valueOf(location.getX());
        String Y = String.valueOf(location.getY());
        String Z = String.valueOf(location.getZ());
        String pitch = String.valueOf(location.getPitch());
        String yaw = String.valueOf(location.getYaw());

        return world + "|" + X + "|" + Y + "|" + Z + "|" + pitch + "|" + yaw + "|";

    }

    public static Optional<Location> getLocationFromString(String string) {
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

        World world = Bukkit.getServer().getWorld(worldName);
        if (world == null)
            return Optional.empty();
        Location location = world.getSpawnLocation();

        location.set(Double.parseDouble(X), Double.parseDouble(Y), Double.parseDouble(Z));
        location.setPitch(Float.parseFloat(pitch));
        location.setYaw(Float.parseFloat(yaw));

        return Optional.of(location);
    }

    public static String writeBlockToString(Block block) {

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

    public static Optional<Block> getBlockFromString(String string) {
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

        String type = string1.substring(0, string1.indexOf("|"));
        string1.delete(0, string1.indexOf("|") + 1);

        BlockData blockData = Bukkit.getServer().createBlockData(string1.substring(0, string1.indexOf("|")));
        string1.delete(0, string1.indexOf("|") + 1);
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return Optional.empty();
        }
        Block block = world.getBlockAt(Integer.parseInt(X), Integer.parseInt(Y), Integer.parseInt(Z));

        Material material = Material.getMaterial(type);
        if (material == null)
            return Optional.empty();

        block.setType(material);
        block.setBlockData(blockData);

        return Optional.of(block);
    }

    public static void teleportParticipantToOneOfTheSpawnLocations(Participant participant) {
        List<Location> locations = participant.getTeam().getSpawnLocations();
        Random random = new Random();
        Location location = locations.get(random.nextInt(locations.size()));
        new BukkitRunnable() {
            @Override
            public void run() {
                participant.getPlayer().teleportAsync(location);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public static void givePlayerKitInventory(Player player, Kit kit) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = new ItemStack[41];
        try {
            contents = Serialization.itemStackArrayFromBase64(kit.getInventory());
        } catch (IOException e) {
            Main.getInstance().getLogger().throwing(Methods.class.getName(), "givePlayerKitInventory", e);
        }
        inventory.setContents(contents);
        //noinspection UnstableApiUsage
        player.updateInventory();
    }

}
