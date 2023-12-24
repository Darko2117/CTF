package com.darko.plugin.register;

import com.darko.plugin.events.duringTheGameChecks.Checks;
import com.darko.plugin.events.listeners.*;
import com.darko.plugin.commands.CTFCommand;
import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.Main;
import org.bukkit.command.PluginCommand;
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
        PluginCommand ctf = Main.getInstance().getCommand("ctf");
        if (ctf == null) {
            Main.getInstance().getLogger().severe("Unable to get command shutting the plugin down");
            //noinspection UnstableApiUsage
            Main.getInstance().setEnabled(false);
            return;
        }
        ctf.setExecutor(new CTFCommand());
        ctf.setTabCompleter(new CTFCommandTabComplete());
    }
}