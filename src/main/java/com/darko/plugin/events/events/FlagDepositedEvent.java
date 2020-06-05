package com.darko.plugin.events.events;

import com.darko.plugin.gameclasses.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FlagDepositedEvent extends Event {

    Team teamWhoDepositedTheFlag;
    Team teamWhosFlagWasDeposited;

    public FlagDepositedEvent(Team teamWhoDepositedTheFlag, Team teamWhosFlagWasDeposited) {
        this.teamWhoDepositedTheFlag = teamWhoDepositedTheFlag;
        this.teamWhosFlagWasDeposited = teamWhosFlagWasDeposited;
    }

    public Team getTeamWhoDepositedTheFlag() {
        return this.teamWhoDepositedTheFlag;
    }

    public Team getTeamWhosFlagWasDeposited() {
        return this.teamWhosFlagWasDeposited;
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
