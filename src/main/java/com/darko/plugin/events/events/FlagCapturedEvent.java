package com.darko.plugin.events.events;

import com.darko.plugin.gameclasses.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagCapturedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Team capturingTeam;
    private final Team flagOwnerTeam;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public FlagCapturedEvent(Team capturingTeam, Team flagOwnerTeam) {
        this.capturingTeam = capturingTeam;
        this.flagOwnerTeam = flagOwnerTeam;
    }

    public Team getFlagOwnerTeam() {
        return this.flagOwnerTeam;
    }

    public Team getCapturingTeam() {
        return this.capturingTeam;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }


}
