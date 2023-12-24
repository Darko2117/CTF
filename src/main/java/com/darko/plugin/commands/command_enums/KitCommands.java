package com.darko.plugin.commands.command_enums;

public enum KitCommands {
    CREATE("create"),
    DELETE("delete"),
    SET_ICON("seticon"),
    ADD_POTION_EFFECT("addpotioneffect");

    private final String command;

    KitCommands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
