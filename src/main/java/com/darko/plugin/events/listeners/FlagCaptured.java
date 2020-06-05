package com.darko.plugin.events.listeners;

import com.darko.plugin.events.events.FlagCapturedEvent;
import com.darko.plugin.gameclasses.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;


public class FlagCaptured implements Listener {

    @EventHandler
    public void onFlagCapture(FlagCapturedEvent e) {

        Game game = GameManager.getActiveGame();
        Team teamWhoCaptured = e.getTeamWhoCapturedTheFlag();
        Team teamWhosFlagHasBeenCaptured = e.getTeamWhosFlagHasBeenCaptured();
        Flag flag = game.getFlagFromTeam(teamWhosFlagHasBeenCaptured);
        Participant carrier = flag.getCarrier();
        ItemStack flagItem = new ItemStack(flag.getBlock().getType());
        Location flagLocation = flag.getBlock().getLocation();

        Bukkit.getWorld(flagLocation.getWorld().getName()).getBlockAt(flagLocation).setType(Material.AIR);

        for (Entity ent : flagLocation.getNearbyEntities(game.getFlagRadius(), game.getFlagRadius(), game.getFlagRadius())) {
            if (ent instanceof Player) {
                Bukkit.getWorld(ent.getLocation().getWorld().getName()).strikeLightningEffect(ent.getLocation());
                Bukkit.getWorld(flagLocation.getWorld().getName()).strikeLightningEffect(flagLocation.clone().set(flagLocation.clone().getBlockX() + 0.5, flagLocation.clone().getBlockY(), flagLocation.clone().getBlockZ() + 0.5));
            }
        }

        carrier.getPlayer().getInventory().setHelmet(flagItem);

        System.out.println(teamWhoCaptured.getName() + "(" + carrier.getPlayer().getName() + ") captured " + teamWhosFlagHasBeenCaptured.getName() + "'s flag!");
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', teamWhoCaptured.getDisplayName() + ChatColor.GOLD + " captured " + teamWhosFlagHasBeenCaptured.getDisplayName() + ChatColor.GOLD + "'s flag!"), "", 5, 40, 5);
        }

    }

}
