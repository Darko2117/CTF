package com.darko.plugin.repeatingchecks;

import com.darko.plugin.Main;
import com.darko.plugin.Methods;
import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.events.events.FlagCapturedEvent;
import com.darko.plugin.events.events.FlagDepositedEvent;
import com.darko.plugin.events.events.GameEndEvent;
import com.darko.plugin.gameclasses.*;
import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.IntStream;

public class RepeatingChecks {

    static Game game;
    static BossBar capturingUnavailableBossBar;
    static BossBar defendingTheFlagCapture;
    static {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component message1 = miniMessage.deserialize("<red><bold>CAPTURING UNAVAILABLE WHILE PLAYERS FROM DIFFERENT TEAMS ARE ON THE FLAG!!!");
        Component message2 = miniMessage.deserialize("<green><bold>YOU ARE DEFENDING THE FLAG BY STANDING NEAR IT!!!");

        capturingUnavailableBossBar = Bukkit.createBossBar(LegacyComponentSerializer.legacyAmpersand().serialize(message1),
                BarColor.RED, BarStyle.SOLID);
        defendingTheFlagCapture = Bukkit.createBossBar(LegacyComponentSerializer.legacyAmpersand().serialize(message2),
                BarColor.GREEN, BarStyle.SOLID);
    }

    public static void Start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Optional<Game> optionalGame = GameManager.getActiveGame();
                if (optionalGame.isPresent()) {
                    game = optionalGame.get();
                    UpdateParticipants();
                    UpdatePlayersInRangeOfFlags();
                    CheckCapturingAvailability();
                    CheckCapturingProgress();
                    UpdateCaptureProgressBossBars();
                    CheckIfFlagsAreCaptured();
                    CheckFlagCarrierGlow();
                    CheckIfFlagIsReturned();
                    SpawnFlagDepositLocationParticles();
                    CheckKitEffects();
                    CheckIfGameIsOver();
                } else {
                    RepeatingChecks.game = null;
                    CheckUnusedScoreboardTeams();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private static void UpdateParticipants() {
        game.clearParticipants();
        game.getTeams().forEach(team -> team.getTeamMembers().forEach(participant -> {
            if (participant.getPlayer().isOnline())
                game.addParticipant(participant);
            else
                team.removeTeamMember(participant);
        }));
    }

    private static void UpdatePlayersInRangeOfFlags() {
        int range = game.getFlagRadius();
        game.getFlags().forEach(flag -> {
            flag.clearParticipantsInCaptureRange();
            game.getParticipants().stream()
                    .filter(participant -> participant.getPlayer().getLocation().distance(flag.getBlock().getLocation()) <= range)
                    .forEach(flag::addParticipantInCaptureRange);
        });
    }

    private static void CheckCapturingAvailability() {
        defendingTheFlagCapture.removeAll();
        capturingUnavailableBossBar.removeAll();
        for (Flag flag : game.getFlags()) {
            flag.setCanBeCaptured(true);
            if (flag.getCarrier() != null)
                flag.setCanBeCaptured(false);
            Team lastTeam = null;
            for (Participant p : flag.getParticipantsInCaptureRange()) {
                if (p.getTeam().equals(flag.getTeam()))
                    flag.setCanBeCaptured(false);
                if (lastTeam == null)
                    lastTeam = p.getTeam();
                else if (!lastTeam.equals(p.getTeam()))
                    flag.setCanBeCaptured(false);
            }
        }

        game.getFlags().stream()
                .filter(flag -> !flag.getCanBeCaptured())
                .filter(flag -> flag.getCarrier() == null)
                .forEach(flag -> game.getParticipants().stream()
                        .filter(participant -> flag.getParticipantsInCaptureRange().contains(participant))
                        .forEach(participant -> {
                            if (participant.getTeam().equals(flag.getTeam()))
                                defendingTheFlagCapture.addPlayer(participant.getPlayer());
                            else
                                capturingUnavailableBossBar.addPlayer(participant.getPlayer());
                        }));

        for (Participant p : game.getParticipants()) {
            boolean defending = false;
            boolean capturing = false;
            for (Flag f : game.getFlags()) {
                if (f.getParticipantsInCaptureRange().contains(p)) {
                    if (p.getTeam().equals(f.getTeam())) {
                        defending = true;
                    } else {
                        capturing = true;
                    }
                    break;
                }
            }
            if (!defending)
                defendingTheFlagCapture.removePlayer(p.getPlayer());
            if (!capturing)
                capturingUnavailableBossBar.removePlayer(p.getPlayer());
        }
    }

    private static void CheckCapturingProgress() {
        for (Flag flag : game.getFlags()) {
            if (flag.getCanBeCaptured()) {
                HashMap<Participant, Integer> progress = flag.getParticipantsCapturingTheFlag();
                Integer max = game.getSecondsNeededForCapture() * 20;
                flag.getParticipantsInCaptureRange().stream()
                        .filter(p -> !p.getTeam().equals(flag.getTeam()))
                        .filter(p -> !progress.containsValue(max))
                        .forEach(p -> {
                            if (!progress.containsKey(p)) {
                                progress.put(p, 1);
                            } else {
                                progress.put(p, progress.get(p) + 1);
                            }
                            Main.getInstance().getLogger()
                                    .info(p.getPlayer().getName() + " " + flag.getTeam().getName() + " " + progress.get(p) + "/" + max);
                        });
                flag.setParticipantsCapturingTheFlag(progress);
            } else {
                flag.clearParticipantsCapturingTheFlag();
            }

            if (flag.getParticipantsInCaptureRange().isEmpty()) {
                flag.clearParticipantsCapturingTheFlag();
            }
            if (flag.getCarrier() != null) {
                flag.clearParticipantsCapturingTheFlag();
            }
        }
    }

    private static void UpdateCaptureProgressBossBars() {
        for (Flag flag : game.getFlags()) {
            if (flag.getParticipantsCapturingTheFlag().isEmpty()) {
                if (flag.getCapturingProgressBossBar() != null) {
                    flag.getCapturingProgressBossBar().removeAll();
                    flag.setCapturingProgressBossBar(null);
                }
                continue;
            }

            int progress = 0;
            Team teamCapturing = null;
            Team beingCapturedTeam = null;
            int ticksNeededForCapture = game.getSecondsNeededForCapture() * 20;
            for (Map.Entry<Participant, Integer> entry : flag.getParticipantsCapturingTheFlag().entrySet()) {
                if (entry.getValue() > progress) {
                    progress = entry.getValue();
                    teamCapturing = entry.getKey().getTeam();
                    beingCapturedTeam = flag.getTeam();
                }
            }

            if (teamCapturing == null || beingCapturedTeam == null)
                continue;

            if (flag.getCapturingProgressBossBar() != null) {
                flag.getCapturingProgressBossBar().setProgress((double) progress / (double) ticksNeededForCapture);
                continue;
            }


            String message = "<capturing_team><yellow><bold> IS CAPTURING <being_captured_team>'S FLAG!!";
            Component capturingTeamName = teamCapturing.getDisplayName().decorate(TextDecoration.BOLD);
            Component beingCapturedTeamName = beingCapturedTeam.getDisplayName().decorate(TextDecoration.BOLD);
            Component componentThatBossBarsShouldBeUsing = MiniMessage.miniMessage().deserialize(message, TagResolver.resolver(
                    Placeholder.component("capturing_team", capturingTeamName),
                    Placeholder.component("being_captured_team", beingCapturedTeamName)
            ));
            String stringBossBarsActuallyUse = LegacyComponentSerializer.legacyAmpersand().serialize(componentThatBossBarsShouldBeUsing);

            BossBar bossBar = Bukkit.createBossBar(stringBossBarsActuallyUse, BarColor.YELLOW, BarStyle.SOLID);
            bossBar.setProgress(Double.parseDouble(String.valueOf((progress))) / Double.parseDouble(String.valueOf((ticksNeededForCapture))));

            Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
            flag.setCapturingProgressBossBar(bossBar);
        }
    }

    private static void CheckIfFlagsAreCaptured() {
        game.getFlags().forEach(flag -> flag.getParticipantsCapturingTheFlag().entrySet().stream()
                .filter(entry -> entry.getValue() == game.getSecondsNeededForCapture() * 20)
                .forEach(entry -> {
                    flag.setCarrier(entry.getKey());
                    FlagCapturedEvent event = new FlagCapturedEvent(entry.getKey().getTeam(), flag.getTeam());
                    Bukkit.getPluginManager().callEvent(event);
                }));
    }

    private static void CheckFlagCarrierGlow() {
        game.getFlags().stream()
                .map(Flag::getCarrier)
                .filter(Objects::nonNull)
                .map(Participant::getPlayer)
                .forEach(player -> {
                    boolean isGlowing = false;
                    for (PotionEffect pot : player.getActivePotionEffects()) {
                        if (!pot.getType().equals(PotionEffectType.GLOWING)) {
                            continue;
                        }
                        PotionEffect extend = new PotionEffect(PotionEffectType.GLOWING, 20, 0);
                        player.removePotionEffect(pot.getType());
                        player.addPotionEffect(extend);
                        isGlowing = true;
                        break;
                    }
                    if (!isGlowing) {
                        PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, 20, 0);
                        player.addPotionEffect(effect);
                    }
                });
    }

    private static void CheckIfFlagIsReturned() {
        int range = game.getFlagRadius();
        game.getFlags().stream()
                .filter(flag -> flag.getCarrier() != null)
                .forEach(flag -> {
                    Participant carrier = flag.getCarrier();
                    if (!(carrier.getPlayer().getLocation().distance(game.getFlagDepositLocation()) <= range)) {
                        return;
                    }
                    flag.setCarrier(null);
                    FlagDepositedEvent event = new FlagDepositedEvent(carrier.getTeam(), flag.getTeam());
                    Bukkit.getPluginManager().callEvent(event);
                    Methods.GivePlayerKitInventory(carrier.getPlayer(), carrier.getKit());
                });
    }

    private static void SpawnFlagDepositLocationParticles() {
        Optional<Game> optionalGame = GameManager.getActiveGame();
        if (optionalGame.isEmpty())
            return;

        Location depositLocation = optionalGame.get().getFlagDepositLocation();

        Random r = new Random();

        int radius = game.getFlagRadius();

        ParticleBuilder areaParticle = new ParticleBuilder(Particle.REDSTONE);

        IntStream.range(0, game.getFlagParticleCount()).forEach(i -> {
            areaParticle.color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
            areaParticle.location(depositLocation);
            areaParticle.offset(radius, (double) radius / 2, radius);
            areaParticle.spawn();
        });
    }

    private static void CheckKitEffects() {
        game.getParticipants().stream()
                .filter(participant -> participant.getKit() != null)
                .filter(participant -> participant.getKit().getPotionEffects() != null)
                .forEach(participant -> participant.getKit().getPotionEffects()
                        .forEach(potionEffect -> {
                            Player player = participant.getPlayer();
                            if (player.getActivePotionEffects().contains(potionEffect)) {
                                PotionEffect extend = new PotionEffect(potionEffect.getType(), 19, potionEffect.getAmplifier());
                                player.removePotionEffect(potionEffect.getType());
                                player.addPotionEffect(extend);
                            } else {
                                PotionEffect effect = new PotionEffect(potionEffect.getType(), 19, potionEffect.getAmplifier());
                                player.addPotionEffect(effect);
                            }
                        }));
    }

    private static void CheckIfGameIsOver() {
        for (Team team : game.getTeams()) {
            if (team.getTeamMembers().isEmpty()) {
                game.removeTeam(team);
            }
        }
        if (game.getTeams().size() == 1) {
            GameEndEvent event = new GameEndEvent(game);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    private static void CheckUnusedScoreboardTeams() {
        List<String> teamNames = CTFCommandTabComplete.getTeamNames();
        for (String teamName : teamNames) {
            org.bukkit.scoreboard.Team team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(teamName);
            if (team == null) {
                continue;
            }
            team.unregister();
            Main.getInstance().getLogger().info(teamName + " was still in the scoreboard and the game is not running. Removing it now.");
        }
    }

}