package com.darko.plugin.gameclasses;

import com.darko.plugin.Main;
import com.darko.plugin.commands.CTFCommandTabComplete;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Game {

    List<Participant> participants = new ArrayList<>();

    List<Team> teams = new ArrayList<>();

    List<Kit> kits = new ArrayList<>();

    Participant flagCarrier;

    Block flag;

    Integer flagRadius;

    Integer flagParticleCount;

    Integer secondsNeededForCapture;

    Integer pointsNeededToWin;


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

    public Participant getParticipantFromPlayer(Player player){
        for(Participant p : this.participants){
            if(p.getPlayer().equals(player)){
                return p;
            }
        }
        return null;
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


    public List<Kit> getKits() {
        return this.kits;
    }

    public Kit getKitByName(String name) {

        this.kits.clear();

        for (String s : CTFCommandTabComplete.getKitNames()) {

            Kit kit = new Kit();

            kit.setName(s);

            String inventory = Main.getInstance().getConfig().getString("Kits." + s + ".Inventory");
            kit.setInventory(inventory);

            List<String> availableAbilities = Main.getInstance().getConfig().getStringList("Kits." + s + ".AvailableAbilities");
            kit.setAvailableAbilities(availableAbilities);

            ItemStack icon = null;
            if (Main.getInstance().getConfig().getItemStack("Kits." + s + ".Icon") != null) {
                icon = Main.getInstance().getConfig().getItemStack("Kits." + s + ".Icon");
            }
            kit.setIcon(icon);

            this.kits.add(kit);

        }

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


    public Block getFlag() {
        return this.flag;
    }

    public void setFlag(Block flag) {
        this.flag = flag;
    }


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


    public Integer getPointsNeededToWin(){return this.pointsNeededToWin;}

    public void setPointsNeededToWin(Integer pointsNeededToWin){this.pointsNeededToWin = pointsNeededToWin;}

}
