package com.darko.plugin.events.duringTheGameChecks;

import com.darko.plugin.Main;
import com.darko.plugin.Methods;
import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.gameclasses.*;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
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

import java.util.Optional;

public class Checks implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        Game game = optionalGame.get();
        game.getParticipantFromPlayer(player)
                .ifPresent(participant -> event.setCancelled(true));
    }

    @EventHandler
    public void onPlayerClickItemForDropping(InventoryClickEvent event) {
        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            return;
        }
        Game game = optionalGame.get();
        game.getParticipants().stream()
                .filter(p -> p.getPlayer().equals(event.getWhoClicked()))
                .filter(p -> event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR)
                .forEach(p -> event.setCancelled(true));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            return;
        }
        Game game = optionalGame.get();

        game.getParticipantFromPlayer(event.getPlayer()).ifPresent(player -> {
            event.getDrops().clear();
            game.getFlags().stream()
                    .filter(flag -> flag.getCarrier() != null)
                    .filter(flag -> flag.getCarrier() == player)
                    .map(Flag::getTeam)
                    .forEach(Methods::respawnFlag);
        });
    }

    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent event) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            return;
        }
        Game game = optionalGame.get();
        Player player = event.getPlayer();
        Optional<Participant> optionalParticipant = game.getParticipantFromPlayer(player);
        if (optionalParticipant.isEmpty()) {
            return;
        }

        Participant participant = optionalParticipant.get();
        if (participant.getTeam().getCanRespawn()) {
            player.teleport(game.getFinalRespawnLocation());
            new BukkitRunnable() {
                @Override
                public void run() {
                    Methods.openKitSelectionUI(participant);
                }
            }.runTaskLater(Main.getInstance(), 10);
            player.sendMessage(miniMessage.deserialize(
                    "<green><bold>You will be teleported back into action in <seconds> seconds!</bold></green>",
                    Placeholder.parsed("seconds", String.valueOf(game.getDeathTimer())))
            );
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (participant.getTeam().getCanRespawn()) {
                        Methods.teleportParticipantToOneOfTheSpawnLocations(participant);
                    } else {
                        player.sendMessage(miniMessage.deserialize("<red><bold>Your team lost the flag in the meantime. You can't respawn anymore.</bold></red>"));
                    }
                }
            }.runTaskLater(Main.getInstance(), game.getDeathTimer() * 20L);
        } else {
            org.bukkit.scoreboard.Team scoreboardTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(participant.getTeam().getName());
            if (scoreboardTeam != null)
                scoreboardTeam.removeEntry(participant.getPlayer().getName());
            player.teleport(game.getFinalRespawnLocation());
            participant.getTeam().removeTeamMember(participant);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            return;
        }

        Game game = optionalGame.get();
        game.getParticipantFromPlayer(event.getPlayer()).ifPresent(participant -> {
            game.getFlags().stream()
                    .filter(flag -> flag.getCarrier() != null)
                    .filter(flag -> flag.getCarrier().equals(participant))
                    .map(Flag::getTeam)
                    .forEach(Methods::respawnFlag);
            event.getPlayer().getInventory().clear();
        });
    }

    @EventHandler
    public void onPlayerCLickToSelectKit(InventoryClickEvent event) {
        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            return;
        }
        Game game = optionalGame.get();
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        game.getParticipantFromPlayer(player).ifPresent(participant -> {
            Team team = participant.getTeam();
            Inventory UI = team.getKitSelectInventory();
            if (UI == null || !event.getInventory().equals(UI)) {
                return;
            }
            if (!event.getClick().equals(ClickType.LEFT)) {
                return;
            }
            if (!UI.contains(event.getCurrentItem())) {
                return;
            }

            Optional<Kit> optionalKit = team.getAvailableKits().stream().filter(kit -> kit.getIcon().equals(event.getCurrentItem())).findAny();
            if (optionalKit.isEmpty()) {
                return;
            }
            Kit kit = optionalKit.get();

            CTFCommandTabComplete.getKitNames().forEach(kitName ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + participant.getPlayer().getName() + " permission unset ctf.kit." + kitName));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + participant.getPlayer().getName() + " permission set ctf.kit." + kit.getName());
            participant.setKit(kit);
            participant.getPlayer().closeInventory();
        });

    }

    @EventHandler
    public void onPlayerCloseKitSelectionInventory(InventoryCloseEvent event) {
        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            return;
        }
        Game game = optionalGame.get();

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        Optional<Participant> optionalParticipant = game.getParticipantFromPlayer(player);
        if (optionalParticipant.isEmpty()) {
            return;
        }

        Participant participant = optionalParticipant.get();
        Team team = participant.getTeam();
        Inventory UI = team.getKitSelectInventory();
        if (UI == null || !event.getInventory().equals(UI)) {
            return;
        }

        if (participant.getKit() == null) {
            Kit kit = participant.getTeam().getAvailableKits().get(0);
            participant.setKit(kit);
            CTFCommandTabComplete.getKitNames().forEach(kitName -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + participant.getPlayer().getName() + " permission unset ctf.kit." + kitName));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + participant.getPlayer().getName() + " permission set ctf.kit." + kit.getName());
        }
        Methods.givePlayerKitInventory(participant.getPlayer(), participant.getKit());
    }

    @EventHandler
    public void onDurabilityLoss(PlayerItemDamageEvent event) {
        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            return;
        }

        Game game = optionalGame.get();
        if (game.getParticipantFromPlayer(event.getPlayer()).isEmpty()) {
            return;
        }

        event.setCancelled(true);
    }

}
