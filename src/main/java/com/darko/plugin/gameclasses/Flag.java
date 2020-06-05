package com.darko.plugin.gameclasses;

import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Flag {

    Block block;

    Team team;

    List<Participant> participantsInCaptureRange = new ArrayList<>();

    Boolean canBeCaptured;

    HashMap<Participant, Integer> participantsCapturingTheFlag = new HashMap<>();

    Participant carrier;

    BossBar capturingProgressBossBar;


    public Block getBlock() {
        return this.block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }


    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }


    public List<Participant> getParticipantsInCaptureRange() {
        return this.participantsInCaptureRange;
    }

    public void setParticipantsInCaptureRange(List<Participant> participants) {
        this.participantsInCaptureRange = participants;
    }

    public void addParticipantInCaptureRange(Participant participant) {
        this.participantsInCaptureRange.add(participant);
    }

    public void clearParticipantsInCaptureRange() {
        this.participantsInCaptureRange.clear();
    }


    public Boolean getCanBeCaptured() {
        return this.canBeCaptured;
    }

    public void setCanBeCaptured(Boolean canBeCaptured) {
        this.canBeCaptured = canBeCaptured;
    }


    public HashMap<Participant, Integer> getParticipantsCapturingTheFlag() {
        return this.participantsCapturingTheFlag;
    }

    public void setParticipantsCapturingTheFlag(HashMap<Participant, Integer> participantsCapturingTheFlag) {
        this.participantsCapturingTheFlag = participantsCapturingTheFlag;
    }

    public void clearParticipantsCapturingTheFlag(){this.participantsCapturingTheFlag.clear();}


    public Participant getCarrier() {
        return this.carrier;
    }

    public void setCarrier(Participant carrier) {
        this.carrier = carrier;
    }


    public BossBar getCapturingProgressBossBar() {
        return capturingProgressBossBar;
    }

    public void setCapturingProgressBossBar(BossBar capturingProgressBossBar) {
        this.capturingProgressBossBar = capturingProgressBossBar;
    }

}
