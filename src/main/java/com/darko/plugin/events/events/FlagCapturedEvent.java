package com.darko.plugin.events.events;

import com.darko.plugin.gameclasses.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FlagCapturedEvent extends Event {

    Team teamWhoCapturedTheFlag;
    Team teamWhosFlagHasBeenCaptured;

    public FlagCapturedEvent(Team teamWhoCapturedTheFlag, Team teamWhosFlagHasBeenCaptured) {
        this.teamWhoCapturedTheFlag = teamWhoCapturedTheFlag;
        this.teamWhosFlagHasBeenCaptured = teamWhosFlagHasBeenCaptured;
    }

    public Team getTeamWhosFlagHasBeenCaptured() {
        return this.teamWhosFlagHasBeenCaptured;
    }

    public Team getTeamWhoCapturedTheFlag() {
        return this.teamWhoCapturedTheFlag;
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
