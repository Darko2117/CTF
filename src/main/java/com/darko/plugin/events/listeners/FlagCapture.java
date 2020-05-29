package com.darko.plugin.events.listeners;

import com.darko.plugin.events.events.FlagCapturedEvent;
import com.darko.plugin.gameclasses.Game;
import com.darko.plugin.gameclasses.GameManager;
import com.darko.plugin.gameclasses.Participant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;


public class FlagCapture implements Listener {

    @EventHandler
    public void onFlagCapture(FlagCapturedEvent e) {

        Game game = GameManager.getActiveGame();
        Location flagLocation = game.getFlag().getLocation();

        Participant carrier = e.getCarrier();
        game.setFlagCarrier(carrier);

        Block flag = game.getFlag();
        ItemStack flagItem = new ItemStack(flag.getType());

        Bukkit.getWorld(flagLocation.getWorld().getName()).getBlockAt(flagLocation).setType(Material.AIR);

        for (Entity ent : flagLocation.getNearbyEntities(game.getFlagRadius(), game.getFlagRadius(), game.getFlagRadius())) {
            if (ent instanceof Player) {
                Bukkit.getWorld(ent.getLocation().getWorld().getName()).strikeLightningEffect(ent.getLocation());
                Bukkit.getWorld(flagLocation.getWorld().getName()).strikeLightningEffect(flagLocation.clone().set(flagLocation.clone().getBlockX() + 0.5, flagLocation.clone().getBlockY(), flagLocation.clone().getBlockZ() + 0.5));
            }
        }

        carrier.getPlayer().getInventory().setHelmet(flagItem);

        System.out.println(carrier.getPlayer().getName() + " captured the flag!");


    }

}
