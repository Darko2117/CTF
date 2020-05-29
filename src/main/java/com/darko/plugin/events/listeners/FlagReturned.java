package com.darko.plugin.events.listeners;

import com.darko.plugin.Methods;
import com.darko.plugin.events.events.FlagReturnedToBaseEvent;
import com.darko.plugin.events.events.GameEndEvent;
import com.darko.plugin.gameclasses.Game;
import com.darko.plugin.gameclasses.GameManager;
import com.darko.plugin.gameclasses.Team;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FlagReturned implements Listener {

    @EventHandler
    public void onFlagReturned(FlagReturnedToBaseEvent e) {

        Team team = e.getTeam();
        team.addPoint();
        System.out.println(team.getName() + " " + team.getPoints());
        Game game = GameManager.getActiveGame();

        Methods.RespawnFlagReturned(team);

        if (team.getPoints().equals(game.getPointsNeededToWin())){
            GameEndEvent event = new GameEndEvent(game);
            Bukkit.getPluginManager().callEvent(event);
        }

    }
}
