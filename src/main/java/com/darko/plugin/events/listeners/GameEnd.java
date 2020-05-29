package com.darko.plugin.events.listeners;

import com.darko.plugin.events.events.GameEndEvent;
import com.darko.plugin.gameclasses.Game;
import com.darko.plugin.gameclasses.GameManager;
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

        Integer mostPoints = null;
        Team winningTeam = null;
        for (Team t : game.getTeams()) {
            if (mostPoints == null) {
                mostPoints = t.getPoints();
                winningTeam = t;
            } else {
                if (t.getPoints() > mostPoints) winningTeam = t;
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', winningTeam.getDisplayName()) + ChatColor.GOLD + ChatColor.BOLD + " WON!!!", "THEY WERE THE FIRST TO CAPTURE THE FLAG " + game.getPointsNeededToWin() + " TIMES!!!", 10, 200, 60);
        }

        GameManager.setActiveGame(null);


    }

}
