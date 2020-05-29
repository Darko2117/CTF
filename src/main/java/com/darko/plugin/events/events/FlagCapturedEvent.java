package com.darko.plugin.events.events;

import com.darko.plugin.gameclasses.Participant;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FlagCapturedEvent extends Event {

    Participant carrier;

    public FlagCapturedEvent(Participant carrier){
        this.carrier = carrier;
    }

    public Participant getCarrier(){return this.carrier;}

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }


}
