package com.darko.plugin.gameclasses;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final List<Participant> teamMembers = new ArrayList<>();

    private String name;

    private final List<Location> spawnLocations = new ArrayList<>();

    private final List<Kit> availableKits = new ArrayList<>();

    private Component displayName;

    private NamedTextColor color;

    private Block flag;

    private Boolean canRespawn;

    private Inventory kitSelectInventory;


    public List<Participant> getTeamMembers() {
        return this.teamMembers;
    }

    public void addTeamMember(Participant participant) {
        this.teamMembers.add(participant);
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

    public void addSpawnLocation(Location spawnLocation) {
        this.spawnLocations.add(spawnLocation);
    }


    public List<Kit> getAvailableKits() {
        return this.availableKits;
    }

    public void addAvailableKits(Kit kit) {
        this.availableKits.add(kit);
    }


    public Component getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(Component name) {
        this.displayName = name;
    }

    public NamedTextColor getColor() {
        return this.color;
    }

    public void setColor(NamedTextColor color) {
        this.color = color;
    }


    public Block getFlag() {
        return this.flag;
    }

    public void setFlag(Block flag) {
        this.flag = flag;
    }


    public Boolean getCanRespawn() {
        return canRespawn;
    }

    public void setCanRespawn(Boolean canRespawn) {
        this.canRespawn = canRespawn;
    }


    public Inventory getKitSelectInventory() {
        return kitSelectInventory;
    }

    public void setKitSelectInventory(Inventory kitSelectInventory) {
        this.kitSelectInventory = kitSelectInventory;
    }
}
