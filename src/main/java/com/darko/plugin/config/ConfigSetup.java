package com.darko.plugin.config;

import com.darko.plugin.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigSetup {

    public static void Initialize() {

        FileConfiguration config = Main.getInstance().getConfig();

        Main.getInstance().saveConfig();

    }

}
