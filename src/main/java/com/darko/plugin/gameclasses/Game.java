package com.darko.plugin.gameclasses;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {

    List<Participant> participants = new ArrayList<>();

    List<Team> teams = new ArrayList<>();

    List<Kit> kits = new ArrayList<>();

    Participant flagCarrier;

    Integer flagRadius;

    Integer flagParticleCount;

    Integer secondsNeededForCapture;

    Integer pointsNeededToWin;

    List<Flag> flags = new ArrayList<>();

    Location flagDepositLocation;

    Location finalRespawnLocation;

    Integer deathTimer;

    String winnerPermission;


    public List<Participant> getParticipants() {
        return this.participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void removeParticipant(Participant participant) {
        this.participants.remove(participant);
    }

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    public Participant getParticipantFromPlayer(Player player) {
        for (Participant p : this.participants) {
            if (p.getPlayer().equals(player)) {
                return p;
            }
        }
        return null;
    }

    public void clearParticipants() {
        this.participants.clear();
    }


    public List<Team> getTeams() {
        return this.teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public void addTeam(Team team) {
        this.teams.add(team);
    }

    public void removeTeam(Team team) {this.teams.remove(team);}


    public List<Kit> getKits() {
        return this.kits;
    }

    public Kit getKitByName(String name) {
        for (Kit k : this.kits) {
            if (k.getName().equalsIgnoreCase(name)) return k;
        }
        return null;
    }

    public void setAvailableKits(List<Kit> kits) {
        this.kits = kits;
    }

    public void addAvailableKit(Kit kit) {
        if (!this.kits.contains(kit)) this.kits.add(kit);
    }


    public Participant getFlagCarrier() {
        return this.flagCarrier;
    }

    public void setFlagCarrier(Participant flagCarrier) {
        this.flagCarrier = flagCarrier;
    }


//    public Block getFlag() {
//        return this.flag;
//    }
//
//    public void setFlag(Block flag) {
//        this.flag = flag;
//    }


    public Integer getFlagRadius() {
        return this.flagRadius;
    }

    public void setFlagRadius(Integer flagRadius) {
        this.flagRadius = flagRadius;
    }


    public Integer getFlagParticleCount() {
        return this.flagParticleCount;
    }

    public void setFlagParticleCount(Integer flagParticleCount) {
        this.flagParticleCount = flagParticleCount;
    }


    public Integer getSecondsNeededForCapture() {
        return this.secondsNeededForCapture;
    }

    public void setSecondsNeededForCapture(Integer secondsNeededForCapture) {
        this.secondsNeededForCapture = secondsNeededForCapture;
    }


    public Integer getPointsNeededToWin() {
        return this.pointsNeededToWin;
    }

    public void setPointsNeededToWin(Integer pointsNeededToWin) {
        this.pointsNeededToWin = pointsNeededToWin;
    }


    public List<Flag> getFlags() {
        return this.flags;
    }

    public void setFlags(List<Flag> flags) {
        this.flags = flags;
    }

    public Flag getFlagFromTeam(Team team) {
        for (Flag f : flags) {
            if (f.getTeam().equals(team)) {
                return f;
            }
        }
        return null;
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


    public Integer getDeathTimer() {
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
