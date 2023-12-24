package com.darko.plugin.events.listeners;

import com.darko.plugin.events.events.FlagCapturedEvent;
import com.darko.plugin.gameclasses.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.Optional;


public class FlagCaptured implements Listener {

    @EventHandler
    public void onFlagCapture(FlagCapturedEvent e) {
        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty()) {
            return;
        }
        Game game = optionalGame.get();
        Team capturingTeam = e.getCapturingTeam();
        Team flagOwnerTeam = e.getFlagOwnerTeam();
        Optional<Flag> optionalFlag = game.getFlagFromTeam(flagOwnerTeam);
        if (optionalFlag.isEmpty())
            return;
        Flag flag = optionalFlag.get();
        Participant carrier = flag.getCarrier();
        ItemStack flagItem = new ItemStack(flag.getBlock().getType());
        Location flagLocation = flag.getBlock().getLocation();

        flagLocation.getWorld().getBlockAt(flagLocation).setType(Material.AIR);

        flagLocation.getNearbyEntities(game.getFlagRadius(), game.getFlagRadius(), game.getFlagRadius()).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .forEach(player -> {
                    Location location = player.getLocation();
                    location.getWorld().strikeLightningEffect(location);
                    flagLocation.getWorld().strikeLightningEffect(flagLocation.clone().set(
                            flagLocation.getBlockX() + 0.5,
                            flagLocation.getBlockY(),
                            flagLocation.getBlockZ() + 0.5));
                });

        carrier.getPlayer().getInventory().setHelmet(flagItem);

        System.out.println(capturingTeam.getName() + "(" + carrier.getPlayer().getName() + ") captured " + flagOwnerTeam.getName() + "'s flag!");

        MiniMessage miniMessage = MiniMessage.miniMessage();
        Title.Times times = Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(4), Duration.ofMillis(250));
        String titleMsg = "<aqua><capturing></aqua><gold> captured <aqua><flagOwner></aqua><gold>'s flag!</gold>";
        TagResolver resolver = TagResolver.resolver(
                Placeholder.component("capturing_team", capturingTeam.getDisplayName()),
                Placeholder.component("flag_owner_team", flagOwnerTeam.getDisplayName()));
        Title titleToSend = Title.title(miniMessage.deserialize(titleMsg, resolver), Component.empty(), times);

        Bukkit.getOnlinePlayers().forEach(p -> p.showTitle(titleToSend));
    }
}
