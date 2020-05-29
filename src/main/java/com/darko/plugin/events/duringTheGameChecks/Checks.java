package com.darko.plugin.events.duringTheGameChecks;

import com.darko.plugin.Methods;
import com.darko.plugin.events.listeners.GameStart;
import com.darko.plugin.gameclasses.Game;
import com.darko.plugin.gameclasses.GameManager;
import com.darko.plugin.gameclasses.Kit;
import com.darko.plugin.gameclasses.Participant;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class Checks implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (GameManager.getActiveGame() != null) {
            Game game = GameManager.getActiveGame();
            for (Participant p : game.getParticipants()) {
                if (p.getPlayer().equals(e.getPlayer())) {
                    Player player = e.getPlayer();
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerClickItemForDropping(InventoryClickEvent e) {
        if (GameManager.getActiveGame() != null) {
            Game game = GameManager.getActiveGame();
            for (Participant p : game.getParticipants()) {
                if (p.getPlayer().equals(e.getWhoClicked())) {
                    if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (GameManager.getActiveGame() != null) {
            Game game = GameManager.getActiveGame();
            for (Participant p : game.getParticipants()) {
                if (p.getPlayer().equals(e.getEntity())) {
                    e.getDrops().clear();
                    if (game.getFlagCarrier() != null && game.getFlagCarrier().equals(p)) {
                        Methods.RespawnFlagEliminated();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent e) {
        if (GameManager.getActiveGame() != null) {
            Game game = GameManager.getActiveGame();
            Player player = e.getPlayer();
            GameStart.GivePlayerKitInventory(player, game.getParticipantFromPlayer(player).getKit());
            GameStart.TeleportParticipant(game.getParticipantFromPlayer(e.getPlayer()));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (GameManager.getActiveGame() != null) {
            Game game = GameManager.getActiveGame();
            for (Participant p : game.getParticipants()) {
                if (e.getPlayer().equals(p.getPlayer())) {
                    if (game.getFlagCarrier() != null && game.getFlagCarrier().equals(p)) {
                        Methods.RespawnFlagEliminated();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCLickToSelectKit(InventoryClickEvent e) {
        if (GameManager.getActiveGame() != null) {
            Game game = GameManager.getActiveGame();
            if (Methods.UI != null && e.getInventory().equals(Methods.UI)) {
                if (Methods.iconsAndKits.containsKey(e.getCurrentItem())) {
                    Kit kit = game.getKitByName(Methods.iconsAndKits.get(e.getCurrentItem()));
                    System.out.println(kit.getName());
                }
            }
        }
    }

    @EventHandler
    public void onDurabilityLoss(PlayerItemDamageEvent e) {
        if (GameManager.getActiveGame() != null) {
            Game game = GameManager.getActiveGame();
            if (game.getParticipants().contains(game.getParticipantFromPlayer(e.getPlayer()))) {
                e.setCancelled(true);
            }
        }
    }

}
