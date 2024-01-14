package com.darko.plugin.commands.command_enums;

public enum TeamCommands {
    CREATE("create"),
    DELETE("delete"),
    ADD_KIT("addkit"),
    REMOVE_KIT("removekit"),
    ADD_SPAWN_LOCATION("addspawnlocation"),
    SET_DISPLAY_NAME("setdisplayname"),
    //SET_BASE_LOCATION("setbaselocation"),
    SET_COLOR("setcolor"),
    SET_FLAG_LOCATION("setflaglocation");

    private final String command;

    TeamCommands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}