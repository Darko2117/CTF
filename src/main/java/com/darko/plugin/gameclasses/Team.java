package com.darko.plugin.gameclasses;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Team {

    List<Participant> teamMembers = new ArrayList<>();

    String name;

    List<Location> spawnLocations = new ArrayList<>();

    List<Kit> availableKits = new ArrayList<>();

    String displayName;

    //Location baseLocation;

    Integer points = 0;

    ChatColor color;

    Block flag;


    public List<Participant> getTeamMembers() {
        return this.teamMembers;
    }

    public void addTeamMember(Participant participant) {
        this.teamMembers.add(participant);
    }

    public void setTeamMembers(List<Participant> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public void removeTeamMember(Participant teamMember) {
        this.teamMembers.remove(teamMember);
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Location> getSpawnLocations() {
        return this.spawnLocations;
    }

    public void setSpawnLocations(List<Location> spawnLocations) {
        this.spawnLocations = spawnLocations;
    }

    public void addSpawnLocation(Location spawnLocation) {
        this.spawnLocations.add(spawnLocation);
    }


    public List<Kit> getAvailableKits() {
        return this.availableKits;
    }

    public void setAvailableKits(List<Kit> kits) {
        this.availableKits = kits;
    }

    public void addAvailableKits(Kit kit) {
        this.availableKits.add(kit);
    }


    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }


//    public Location getBaseLocation() {
//        return this.baseLocation;
//    }
//
//    public void setBaseLocation(Location location) {
//        this.baseLocation = location;
//    }


    public Integer getPoints() {
        return this.points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void addPoint() {
        this.points++;
    }


    public ChatColor getColor() {
        return this.color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }


    public Block getFlag() {
        return this.flag;
    }

    public void setFlag(Block flag) {
        this.flag = flag;
    }
}
