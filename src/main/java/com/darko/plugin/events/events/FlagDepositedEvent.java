package com.darko.plugin.events.events;

import com.darko.plugin.gameclasses.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagDepositedEvent extends Event {

    private final Team scoringTeam;
    private final Team losingTeam;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public FlagDepositedEvent(Team scoringTeam, Team losingTeam) {
        this.scoringTeam = scoringTeam;
        this.losingTeam = losingTeam;
    }

    public Team getScoringTeam() {
        return this.scoringTeam;
    }

    public Team getLosingTeam() {
        return this.losingTeam;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }


}
