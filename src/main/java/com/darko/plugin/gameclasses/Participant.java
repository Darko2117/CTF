package com.darko.plugin.gameclasses;

import org.bukkit.entity.Player;

public class Participant {

    private Player player;

    private Team team;

    private Kit kit;


    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }


    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }


    public Kit getKit() {
        return this.kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

}