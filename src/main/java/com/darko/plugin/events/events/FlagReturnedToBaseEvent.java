package com.darko.plugin.events.events;

import com.darko.plugin.gameclasses.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FlagReturnedToBaseEvent extends Event {

    Team team;

    public FlagReturnedToBaseEvent(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return this.team;
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
