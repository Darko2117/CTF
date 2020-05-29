package com.darko.plugin;

import com.darko.plugin.config.ConfigSetup;
import com.darko.plugin.register.Register;
import com.darko.plugin.repeatingchecks.RepeatingChecks;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        System.out.println("--------------------------------------------------");

        instance = this;

        ConfigSetup.Initialize();

        Register.RegisterEvents();

        Register.RegisterCommands();

        RepeatingChecks.Start();

        Main.getInstance().getLogger().info("Capture the flag plugin started!");
        System.out.println("--------------------------------------------------");
    }
}
