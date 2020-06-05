package com.darko.plugin.events.duringTheGameChecks;

import com.darko.plugin.Main;
import com.darko.plugin.Methods;
import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.gameclasses.*;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

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
//                    if (game.getFlagCarrier() != null && game.getFlagCarrier().equals(p)) {
//                        Methods.RespawnFlagEliminated();
//                    }
                    for (Flag f : game.getFlags()) {
                        if (f.getCarrier() != null && f.getCarrier() == p) {
                            Methods.RespawnFlag(f.getTeam());
                        }
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
            if (game.getParticipantFromPlayer(player).getTeam().getCanRespawn()) {
                player.teleport(game.getFinalRespawnLocation());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Methods.OpenKitSelectionUI(game.getParticipantFromPlayer(player));
                    }
                }.runTaskLater(Main.getInstance(), 10);
//                Methods.GivePlayerKitInventory(player, game.getParticipantFromPlayer(player).getKit());
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You will be teleported back into action in " + game.getDeathTimer() + " seconds!");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (game.getParticipantFromPlayer(player).getTeam().getCanRespawn()) {
                            Methods.TeleportParticipantToOneOfTheSpawnLocations(game.getParticipantFromPlayer(e.getPlayer()));
                        } else {
                            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Your team lost the flag in the meantime. You can't respawn anymore.");
                        }
                    }
                }.runTaskLater(Main.getInstance(), game.getDeathTimer() * 20);
            } else {
                Participant participant = game.getParticipantFromPlayer(player);
                Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(participant.getTeam().getName()).removeEntry(participant.getPlayer().getName());
                player.teleport(game.getFinalRespawnLocation());
                game.getParticipantFromPlayer(player).getTeam().removeTeamMember(game.getParticipantFromPlayer(player));
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (GameManager.getActiveGame() != null) {
            Game game = GameManager.getActiveGame();
            for (Participant p : game.getParticipants()) {
                if (e.getPlayer().equals(p.getPlayer())) {
//                    if (game.getFlagCarrier() != null && game.getFlagCarrier().equals(p)) {
//                        Methods.RespawnFlagEliminated();
//                    }
                    for (Flag f : game.getFlags()) {
                        if (f.getCarrier() != null && f.getCarrier() == p) {
                            Methods.RespawnFlag(f.getTeam());
                        }
                    }
                    e.getPlayer().getInventory().clear();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCLickToSelectKit(InventoryClickEvent e) {
        if (GameManager.getActiveGame() != null) {
            Game game = GameManager.getActiveGame();
            Participant participant = game.getParticipantFromPlayer((Player) e.getWhoClicked());
            Team team = participant.getTeam();
            Inventory UI = team.getKitSelectInventory();
            if (UI != null && e.getInventory().equals(UI)) {
                if (e.getClick().equals(ClickType.LEFT)) {
                    if (UI.contains(e.getCurrentItem())) {
                        Kit kit = null;
                        for (Kit k : team.getAvailableKits()) {
                            if (k.getIcon().equals(e.getCurrentItem())) {
                                kit = k;
                            }
                        }
                        for (String s : CTFCommandTabComplete.getKitNames()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + participant.getPlayer().getName() + " permission unset ctf.kit." + s);
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + participant.getPlayer().getName() + " permission set ctf.kit." + kit.getName());
                        participant.setKit(kit);
                        participant.getPlayer().closeInventory();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCloseKitSelectionInventory(InventoryCloseEvent e) {
        if (GameManager.getActiveGame() != null) {
            Game game = GameManager.getActiveGame();
            Participant participant = game.getParticipantFromPlayer((Player) e.getPlayer());
            Team team = participant.getTeam();
            Inventory UI = team.getKitSelectInventory();
            if (UI != null && e.getInventory().equals(UI)) {
                if (participant.getKit() == null) {
                    Kit kit = participant.getTeam().getAvailableKits().get(0);
                    participant.setKit(kit);
                    for (String s : CTFCommandTabComplete.getKitNames()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + participant.getPlayer().getName() + " permission unset ctf.kit." + s);
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + participant.getPlayer().getName() + " permission set ctf.kit." + kit.getName());
                }
                Methods.GivePlayerKitInventory(participant.getPlayer(), participant.getKit());
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
