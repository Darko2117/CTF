package com.darko.plugin.register;

import com.darko.plugin.events.duringTheGameChecks.Checks;
import com.darko.plugin.events.listeners.*;
import com.darko.plugin.commands.CTFCommand;
import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.Main;
import org.bukkit.event.Listener;

public class Register implements Listener {

    public static void RegisterEvents() {

        registerEvents(
                new GameStart(),
                new FlagCaptured(),
                new FlagDeposited(),
                new GameEnd(),
                new Checks()
        );

    }

    private static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            Main.getInstance().getServer().getPluginManager().registerEvents(listener, Main.getInstance());
        }
    }

    public static void RegisterCommands() {
        Main.getInstance().getCommand("ctf").setExecutor(new CTFCommand());
        Main.getInstance().getCommand("ctf").setTabCompleter(new CTFCommandTabComplete());
    }
}
