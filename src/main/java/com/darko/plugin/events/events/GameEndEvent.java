package com.darko.plugin.events.events;

import com.darko.plugin.gameclasses.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Game game;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public GameEndEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return this.game;
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }


}
