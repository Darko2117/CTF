package com.darko.plugin.events.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event {

    CommandSender starter;

    public GameStartEvent(CommandSender sender) {
        this.starter = sender;
    }

    public CommandSender getStarter() {
        return this.starter;
    }


    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }


}
