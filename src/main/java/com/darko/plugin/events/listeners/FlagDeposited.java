package com.darko.plugin.events.listeners;

import com.darko.plugin.events.events.FlagDepositedEvent;
import com.darko.plugin.gameclasses.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FlagDeposited implements Listener {

    @EventHandler
    public void onFlagDeposited(FlagDepositedEvent e) {

        Game game = GameManager.getActiveGame();

        Team teamWhoDepositedTheFlag = e.getTeamWhoDepositedTheFlag();
        Team teamWhosFlagWasDeposited = e.getTeamWhosFlagWasDeposited();

        for (Participant p : teamWhosFlagWasDeposited.getTeamMembers()) {
            Player pl = p.getPlayer();
            Bukkit.getWorld(game.getFlagDepositLocation().getWorld().getName()).strikeLightningEffect(game.getFlagDepositLocation());
            Bukkit.getWorld(pl.getWorld().getName()).strikeLightningEffect(pl.getLocation().clone().set(pl.getLocation().clone().getBlockX() + 0.5, pl.getLocation().clone().getBlockY(), pl.getLocation().clone().getBlockZ() + 0.5));
        }

        teamWhosFlagWasDeposited.setCanRespawn(false);
        Flag flag = game.getFlagFromTeam(teamWhosFlagWasDeposited);
        flag.setBlock(Bukkit.getWorld(flag.getBlock().getLocation().getWorld().getName()).getBlockAt(6969, 69, 6969));

        for (Participant p : game.getParticipants()) {
            if (p.getTeam().equals(teamWhosFlagWasDeposited)) {
                p.getPlayer().sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Your team's flag was scored!", "You can no longer respawn", 5, 60, 5);
            } else {
                p.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', teamWhosFlagWasDeposited.getDisplayName()) + ChatColor.GOLD + ChatColor.BOLD + "'s flag was scored!", "They can no longer respawn", 5, 60, 5);
            }
        }
        System.out.println(teamWhoDepositedTheFlag.getName() + " captured " + teamWhosFlagWasDeposited.getName() + "'s flag!");

    }
}
