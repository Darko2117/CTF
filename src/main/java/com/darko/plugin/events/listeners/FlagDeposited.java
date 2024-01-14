package com.darko.plugin.events.listeners;

import com.darko.plugin.Main;
import com.darko.plugin.events.events.FlagDepositedEvent;
import com.darko.plugin.gameclasses.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.util.Optional;

public class FlagDeposited implements Listener {

    @EventHandler
    public void onFlagDeposited(FlagDepositedEvent e) {

        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            return;
        }

        Game game = optionalGame.get();
        Team scoringTeam = e.getScoringTeam();
        Team losingTeam = e.getLosingTeam();

        World depositLocation = game.getFlagDepositLocation().getWorld();
        losingTeam.getTeamMembers().stream().map(Participant::getPlayer).forEach(pl -> {
            depositLocation.strikeLightningEffect(game.getFlagDepositLocation());
            pl.getWorld().strikeLightningEffect(pl.getLocation().clone()
                    .set(pl.getLocation().clone().getBlockX() + 0.5,
                            pl.getLocation().getBlockY(),
                            pl.getLocation().getBlockZ() + 0.5));
        });

        losingTeam.setCanRespawn(false);
        game.getFlagFromTeam(losingTeam).ifPresent(flag -> {
            flag.setBlock(flag.getBlock().getLocation().getWorld().getBlockAt(6969, 69, 6969));
            //TODO delete the flag or something if possible
        });


        notifyPlayers(game, losingTeam);

        Main.getInstance().getLogger().info(scoringTeam.getName() + " captured " + losingTeam.getName() + "'s flag!");
    }

    private void notifyPlayers(Game game, Team losingTeam) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Title.Times times = Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(3), Duration.ofMillis(250));
        Title yourTeamDead = Title.title(
                miniMessage.deserialize("<gold><bold>Your team's flag was scored!</bold></gold>"),
                miniMessage.deserialize("<red>You can no longer respawn</red>"),
                times);
        Title otherTeamDead = Title.title(
                miniMessage.deserialize("<aqua><losing_team></aqua><gold><bold>'s flag was scored!</bold></gold>", Placeholder.component("losing_team", losingTeam.getDisplayName())),
                miniMessage.deserialize("They can no longer respawn"),
                times);

        game.getParticipants().forEach(p ->
                p.getPlayer().showTitle(p.getTeam().equals(losingTeam) ? yourTeamDead : otherTeamDead)
        );
    }
}
