package com.darko.plugin.commands.command_enums;

public enum CTFCommands {
    START("start"),
    STOP("stop"),
    RELOAD("reload"),
    CONFIG_RELOAD("configreload"),
    KIT("kit"),
    TEAM("team"),
    SET_DEFAULT_KIT("setdefaultkit"),
    SET_FLAG_RADIUS("setflagradius"),
    SET_FLAG_COUNT("setflagparticlecount"),
    SET_SECONDS("setsecondsneededforcapture"),
    SET_RESPAWN_POINT("setfinalrespawnpoint"),
    SET_DEATH_TIMER("setdeathtimer"),
    SET_WINNER("setwinnerpermission");

    private final String command;

    CTFCommands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}