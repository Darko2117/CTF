package com.darko.plugin.events.listeners;

import com.darko.plugin.events.events.GameEndEvent;
import com.darko.plugin.gameclasses.Game;
import com.darko.plugin.gameclasses.GameManager;
import com.darko.plugin.gameclasses.Team;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class GameEnd implements Listener {

    @EventHandler
    public void onGameEnd(@NotNull GameEndEvent gameEndEvent) {
        Game game = gameEndEvent.getGame();

        Team winningTeam = game.getTeams().get(0);
        MiniMessage miniMessage = MiniMessage.miniMessage();
        String formattedMessage = "<aqua><team></aqua><gold><bold> WON!!!</bold></gold>";

        Title title = Title.title(
                miniMessage.deserialize(formattedMessage, Placeholder.component("team", winningTeam.getDisplayName())),
                miniMessage.deserialize("<dark_aqua>They are the last team standing!</dark_aqua>"),
                Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(20), Duration.ofMillis(250)));
        Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(title));

        GameManager.setActiveGame(null);

        game.getParticipants().forEach(player ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getPlayer().getName() + " permission set " + game.getWinnerPermission())
        );
    }

}
