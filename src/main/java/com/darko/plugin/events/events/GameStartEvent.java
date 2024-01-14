package com.darko.plugin.events.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameStartEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final CommandSender starter;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public GameStartEvent(CommandSender sender) {
        this.starter = sender;
    }

    public CommandSender getStarter() {
        return this.starter;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }


}
