package com.darko.plugin.gameclasses;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game {

    private final List<Participant> participants = new ArrayList<>();

    final List<Team> teams = new ArrayList<>();

    final List<Kit> kits = new ArrayList<>();

    private int flagRadius;

    private int flagParticleCount;

    private int secondsNeededForCapture;

    private final List<Flag> flags = new ArrayList<>();

    private Location flagDepositLocation;

    private Location finalRespawnLocation;

    private int deathTimer;

    private String winnerPermission;


    public List<Participant> getParticipants() {
        return this.participants;
    }

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    public Optional<Participant> getParticipantFromPlayer(Player player) {
        return this.participants.stream().filter(participant -> participant.getPlayer().equals(player)).findFirst();
    }

    public void clearParticipants() {
        this.participants.clear();
    }


    public List<Team> getTeams() {
        return this.teams;
    }

    public void addTeam(Team team) {
        this.teams.add(team);
    }

    public void removeTeam(Team team) {
        this.teams.remove(team);
    }


    public List<Kit> getKits() {
        return this.kits;
    }

    public void addAvailableKit(Kit kit) {
        if (!this.kits.contains(kit)) this.kits.add(kit);
    }


    public int getFlagRadius() {
        return this.flagRadius;
    }

    public void setFlagRadius(Integer flagRadius) {
        this.flagRadius = flagRadius;
    }


    public int getFlagParticleCount() {
        return this.flagParticleCount;
    }

    public void setFlagParticleCount(Integer flagParticleCount) {
        this.flagParticleCount = flagParticleCount;
    }


    public int getSecondsNeededForCapture() {
        return this.secondsNeededForCapture;
    }

    public void setSecondsNeededForCapture(Integer secondsNeededForCapture) {
        this.secondsNeededForCapture = secondsNeededForCapture;
    }


    public List<Flag> getFlags() {
        return this.flags;
    }

    public Optional<Flag> getFlagFromTeam(Team team) {
        return flags.stream().filter(flag -> flag.getTeam().equals(team)).findAny();
    }

    public void addFlag(Flag flag){this.flags.add(flag);}


    public Location getFlagDepositLocation() {
        return flagDepositLocation;
    }

    public void setFlagDepositLocation(Location flagDepositLocation) {
        this.flagDepositLocation = flagDepositLocation;
    }


    public Location getFinalRespawnLocation() {
        return finalRespawnLocation;
    }

    public void setFinalRespawnLocation(Location finalRespawnLocation) {
        this.finalRespawnLocation = finalRespawnLocation;
    }

    public int getDeathTimer() {
        return deathTimer;
    }

    public void setDeathTimer(Integer deathTimer) {
        this.deathTimer = deathTimer;
    }


    public String getWinnerPermission() {
        return winnerPermission;
    }

    public void setWinnerPermission(String winnerPermission) {
        this.winnerPermission = winnerPermission;
    }
}
