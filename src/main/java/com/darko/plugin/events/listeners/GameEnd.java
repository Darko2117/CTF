package com.darko.plugin.events.listeners;

import com.darko.plugin.events.events.GameEndEvent;
import com.darko.plugin.gameclasses.Game;
import com.darko.plugin.gameclasses.GameManager;
import com.darko.plugin.gameclasses.Participant;
import com.darko.plugin.gameclasses.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameEnd implements Listener {

    @EventHandler
    public void onGameEnd(GameEndEvent e) {

        Game game = e.getGame();

        Team winningTeam = game.getTeams().get(0);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', winningTeam.getDisplayName()) + ChatColor.GOLD + "" + ChatColor.BOLD + " WON!!!", "They are the last team standing!", 5, 200, 40);
        }

        GameManager.setActiveGame(null);

        for (Participant p : game.getParticipants()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getPlayer().getName() + " permission set " + game.getWinnerPermission());
        }

    }

}
