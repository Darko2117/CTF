package com.darko.plugin.config;

import com.darko.plugin.Main;

public class ConfigSetup {

    public static void Initialize() {
        Main.getInstance().saveConfig();
    }

}
