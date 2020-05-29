package com.darko.plugin.events.events;

import com.darko.plugin.gameclasses.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event {

    Game game;

    public GameEndEvent(Game game){ this.game = game;
    }

    public Game getGame(){return this.game;}

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }


}
