package com.darko.plugin.repeatingchecks;

import com.darko.plugin.Main;
import com.darko.plugin.Methods;
import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.events.events.FlagCapturedEvent;
import com.darko.plugin.events.events.FlagDepositedEvent;
import com.darko.plugin.events.events.GameEndEvent;
import com.darko.plugin.gameclasses.*;
import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class RepeatingChecks {

    //    static List<Participant> participantsInFlagRange = new ArrayList<>();
//    static HashMap<Participant, Integer> captureProgress = new HashMap<>();
//    static BossBar capturingUnavailableBossBar = Bukkit.createBossBar(ChatColor.RED + "" + ChatColor.BOLD + "YOU CAN'T CAPTURE THE FLAG WHILE PLAYERS FROM OTHER TEAMS ARE ON IT!", BarColor.RED, BarStyle.SOLID);
//    static BossBar capturingProgressBossBar;
//    static Game game;
//
//    public static void Start() {
//        SetDefaultProgressBar();
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (GameManager.getActiveGame() != null) {
//                    game = GameManager.getActiveGame();
//                    UpdateOnlineParticipants();
//                    FlagParticlesCheck();
//                    CheckParticipantLocations();
//                    GlowOnFlagCarrierEffect();
//                    CheckIfFlagIsReturned();
//                } else {
//                    game = null;
//                    CheckUnusedScoreboardTeams();
//                }
//            }
//        }.runTaskTimer(Main.getInstance(), 0, 1);
//    }
//
//    private static void FlagParticlesCheck() {
//
//        if (game.getFlagCarrier() != null) return;
//
//        Block flagBlock = GameManager.getActiveGame().getFlag();
//        Location location = flagBlock.getLocation().set(flagBlock.getX() + 0.5, flagBlock.getY(), flagBlock.getZ() + 0.5);
//
//        Random r = new Random();
//
//        Integer radius = game.getFlagRadius();
//        Integer count = game.getFlagParticleCount();
//
//        ParticleBuilder areaParticle = new ParticleBuilder(Particle.REDSTONE);
//
//        for (Integer i = 0; i < count; i++) {
//            areaParticle.color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
//            areaParticle.location(location);
//            areaParticle.offset(radius, radius / 2, radius);
//            areaParticle.spawn();
//        }
//        //I know that you can .count(count) them but that way they won't be all random color, it'll be in batches of count.
//        //Really unnecessary and creates a bigger load but :)
//
//    }
//
//    private static void CheckParticipantLocations() {
//
//        if (game.getFlagCarrier() != null) return;
//
//        CheckParticipantsInFlagRange();
//
//        CheckCapturing();
//
//        CheckCapturingProgress();
//
//    }
//
//    private static void CheckParticipantsInFlagRange() {
//
//        participantsInFlagRange.clear();
//
//        Location flagLocation = game.getFlag().getLocation();
//
//        for (Participant p : game.getParticipants()) {
//            if (p.getPlayer().getLocation().distance(flagLocation.clone().set(flagLocation.clone().getBlockX() + 0.5, flagLocation.clone().getBlockY(), flagLocation.clone().getBlockZ() + 0.5)) <= game.getFlagRadius()) {
//                participantsInFlagRange.add(p);
//            }
//        }
//
//    }
//
//    private static void CheckCapturing() {
//
//        Boolean capturingAvailable = true;
//
//        for (Integer i = 0; i < participantsInFlagRange.size(); i++) {
//            if (i != 0 && !participantsInFlagRange.get(i - 1).getTeam().equals(participantsInFlagRange.get(i).getTeam())) {
//                capturingAvailable = false;
//                break;
//            }
//        }
//
//        if (!capturingAvailable) {
//            for (Participant p : participantsInFlagRange) {
//                capturingUnavailableBossBar.addPlayer(p.getPlayer());
//            }
//            captureProgress.clear();
//        } else {
//            capturingUnavailableBossBar.removeAll();
//        }
//
//
//        if (capturingAvailable) {
//            if (participantsInFlagRange.size() == 0) captureProgress.clear();
//            for (Participant p : participantsInFlagRange) {
//                if (!captureProgress.containsKey(p)) {
//                    captureProgress.put(p, 1);
//                } else {
//                    Integer percentage = captureProgress.get(p);
//                    percentage++;
//                    Integer seconds = game.getSecondsNeededForCapture();
//                    if (!captureProgress.containsValue(seconds * 20)) captureProgress.put(p, percentage);
//                }
//            }
//        }
//
//        if (!captureProgress.isEmpty()) {
//
//            Team teamCapturing = null;
//            Integer captureProgressInt = 0;
//            for (Map.Entry<Participant, Integer> entry : captureProgress.entrySet()) {
//                if (entry.getValue() > captureProgressInt) {
//                    captureProgressInt = entry.getValue();
//                    teamCapturing = entry.getKey().getTeam();
//                }
//            }
//
//            Double secondsNeededForCapture = Double.parseDouble(game.getSecondsNeededForCapture().toString());
//            secondsNeededForCapture *= 20;
//
//            StringBuilder teamName = new StringBuilder(teamCapturing.getDisplayName());
//
//            List<Integer> colorCodes = new ArrayList<>();
//            for (Integer i = 0; i < teamName.length(); i++) {
//                if (teamName.charAt(i) == '&') colorCodes.add(i);
//            }
//            for (Integer i = 0; i < colorCodes.size(); i++) {
//                teamName.insert(colorCodes.get(i) + 2 + i * 2, "&l");
//            }
//            //Why is this so overly complicated just to add a bold to the name holy shit am I just stupid and doing it the wrong way or what is this
//            //This took me way too long I am not proud of that fact
//
//            String title = capturingProgressBossBar.getTitle().replace("%team%", teamName);
//            title = ChatColor.translateAlternateColorCodes('&', title);
//
//            capturingProgressBossBar.setTitle(title);
//            capturingProgressBossBar.setProgress(captureProgressInt / secondsNeededForCapture);
//
//            for (Player p : Bukkit.getOnlinePlayers()) {
//                capturingProgressBossBar.addPlayer(p);
//            }
//
//        } else {
//            SetDefaultProgressBar();
//        }
//
//    }
//
//    private static void CheckCapturingProgress() {
//
//        try {
//            Integer seconds = game.getSecondsNeededForCapture() * 20;
//            captureProgress.forEach((k, v) -> {
//                System.out.println(k.getPlayer().getName() + " is capturing the flag. Their progress is " + v + "/" + seconds + ".");
//                if (v.equals(seconds)) {
//                    FlagCapturedEvent event = new FlagCapturedEvent(k);
//                    Bukkit.getPluginManager().callEvent(event);
//                    captureProgress.clear();
//                    SetDefaultProgressBar();
//                }
//            });
//        } catch (ConcurrentModificationException e) {
//        }
//
//    }
//
//    private static void GlowOnFlagCarrierEffect() {
//
//        if (game.getFlagCarrier() == null) return;
//
//        Participant carrier = game.getFlagCarrier();
//
//        Boolean isGlowing = false;
//
//        for (PotionEffect pot : carrier.getPlayer().getActivePotionEffects()) {
//            if (pot.getType().equals(PotionEffectType.GLOWING)) {
//                PotionEffect extend = new PotionEffect(PotionEffectType.GLOWING, 20, 0);
//                carrier.getPlayer().removePotionEffect(pot.getType());
//                carrier.getPlayer().addPotionEffect(extend);
//                isGlowing = true;
//                break;
//            }
//        }
//        if (!isGlowing) {
//            PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, 20, 0);
//            carrier.getPlayer().addPotionEffect(effect);
//        }
//
//    }
//
//    private static void CheckUnusedScoreboardTeams() {
//
//        List<String> teamNames = CTFCommandTabComplete.getTeamNames();
//
//        for (String teamName : teamNames) {
//            org.bukkit.scoreboard.Team team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(teamName);
//            if (team != null) {
//                team.unregister();
//                System.out.println(teamName + " was still here and the game is not running. Removing it now.");
//            }
//        }
//
//    }
//
//    private static void UpdateOnlineParticipants() {
//        GameManager.getActiveGame().getParticipants().clear();
//        for (Team t : game.getTeams()) {
//            for (Participant p : t.getTeamMembers()) {
//                if (p.getPlayer().isOnline()) {
//                    GameManager.getActiveGame().addParticipant(p);
//                }
//            }
//        }
//    }
//
//    private static void CheckIfFlagIsReturned() {
//
//        if (game.getFlagCarrier() == null) return;
//
//        for (Participant p : GameManager.getActiveGame().getParticipants()) {
//
//            if (game.getFlagCarrier().equals(p)) {
//                if (p.getPlayer().getLocation().distance(p.getTeam().getBaseLocation()) <= game.getFlagRadius()) {
//                    p.getPlayer().getInventory().setHelmet(null);
//                    Bukkit.getWorld(p.getPlayer().getLocation().getWorld().getName()).strikeLightningEffect(p.getPlayer().getLocation());
//                    game.setFlagCarrier(null);
//
//                    FlagReturnedToBaseEvent event = new FlagReturnedToBaseEvent(p.getTeam());
//                    Bukkit.getPluginManager().callEvent(event);
//
//                    System.out.println(p.getPlayer().getName() + " returned the flag.");
//                }
//            }
//        }
//
//    }
//
//    private static void SetDefaultProgressBar() {
//
//        if(capturingProgressBossBar != null) capturingProgressBossBar.removeAll();
//        capturingProgressBossBar = Bukkit.createBossBar("%team%" + ChatColor.YELLOW + ChatColor.BOLD + " IS CAPTURING THE FLAG!!!", BarColor.YELLOW, BarStyle.SOLID);
//
//    }
    static Game game;
    static BossBar capturingUnavailableBossBar = Bukkit.createBossBar(ChatColor.RED + "" + ChatColor.BOLD + "CAPTURING UNAVAILABLE WHILE PLAYERS FROM DIFFERENT TEAMS ARE ON THE FLAG!!!", BarColor.RED, BarStyle.SOLID);
    static BossBar defendingTheFlagCapture = Bukkit.createBossBar(ChatColor.GREEN + "" + ChatColor.BOLD + "YOU ARE DEFENDING THE FLAG BY STANDING NEAR IT!!!", BarColor.GREEN, BarStyle.SOLID);

    public static void Start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (GameManager.getActiveGame() != null) {
                    game = GameManager.getActiveGame();
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
                    game = null;
                    CheckUnusedScoreboardTeams();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private static void UpdateParticipants() {
        game.clearParticipants();
        for (Team t : game.getTeams()) {
            for (Participant p : t.getTeamMembers()) {
                if (p.getPlayer().isOnline()) game.addParticipant(p);
                else t.removeTeamMember(p);
            }
        }
    }

    private static void UpdatePlayersInRangeOfFlags() {
        Integer range = game.getFlagRadius();
        for (Flag f : game.getFlags()) {
            f.clearParticipantsInCaptureRange();
            for (Participant p : game.getParticipants()) {
                if (p.getPlayer().getLocation().distance(f.getBlock().getLocation()) <= range) {
                    f.addParticipantInCaptureRange(p);
                }
            }
        }
    }

    private static void CheckCapturingAvailability() {
//        teamsAndIfTheirFlagIsAvailableToBeCaptured.clear();
//        for (Team t : game.getTeams()) {
//            teamsAndIfTheirFlagIsAvailableToBeCaptured.put(t, true);
//        }
//        for (Map.Entry<Team, Boolean> entry : teamsAndIfTheirFlagIsAvailableToBeCaptured.entrySet()) {
//            Team lastTeam = null;
//            for (Map.Entry<Participant, Team> entry1 : playersInRangeOfFlags.entrySet()) {
//                if (entry1.getValue().equals(entry.getKey())) {
//                    if (lastTeam == null) lastTeam = entry1.getKey().getTeam();
//                    else if (!lastTeam.equals(entry1.getKey().getTeam()))
//                        teamsAndIfTheirFlagIsAvailableToBeCaptured.put(entry.getKey(), false);
//                }
//            }
//
//        for (Flag f : game.getFlags()) {
//            System.out.println(f.getTeam().getName() + " " + f.getCanBeCaptured());
//        }
        defendingTheFlagCapture.removeAll();
        capturingUnavailableBossBar.removeAll();
        for (Flag f : game.getFlags()) {
            f.setCanBeCaptured(true);
            if (f.getCarrier() != null) f.setCanBeCaptured(false);
            Team lastTeam = null;
            for (Participant p : f.getParticipantsInCaptureRange()) {
                if (p.getTeam().equals(f.getTeam())) f.setCanBeCaptured(false);
                if (lastTeam == null) lastTeam = p.getTeam();
                else if (!lastTeam.equals(p.getTeam())) f.setCanBeCaptured(false);
            }
        }
        for (Flag f : game.getFlags()) {
            if (!f.getCanBeCaptured() && f.getCarrier() == null) {
                for (Participant p : game.getParticipants()) {
                    if (f.getParticipantsInCaptureRange().contains(p)) {
                        if (p.getTeam().equals(f.getTeam())) defendingTheFlagCapture.addPlayer(p.getPlayer());
                        else capturingUnavailableBossBar.addPlayer(p.getPlayer());
                    }
                }
            }
        }
        for (Participant p : game.getParticipants()) {
            Boolean defending = false;
            Boolean capturing = false;
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
            if (!defending) defendingTheFlagCapture.removePlayer(p.getPlayer());
            if (!capturing) capturingUnavailableBossBar.removePlayer(p.getPlayer());
        }
    }

    private static void CheckCapturingProgress() {
        for (Flag f : game.getFlags()) {
            if (f.getCanBeCaptured()) {
                HashMap<Participant, Integer> progress = f.getParticipantsCapturingTheFlag();
                Integer max = game.getSecondsNeededForCapture() * 20;
                for (Participant p : f.getParticipantsInCaptureRange()) {
                    if (!p.getTeam().equals(f.getTeam())) {
                        if (!progress.containsValue(max)) {
                            if (!progress.containsKey(p)) {
                                progress.put(p, 1);
                            } else {
                                progress.put(p, progress.get(p) + 1);
                            }
                            System.out.println(p.getPlayer().getName() + " " + f.getTeam().getName() + " " + progress.get(p) + "/" + max);
                        }
                    }
                }
                f.setParticipantsCapturingTheFlag(progress);
            } else {
                f.clearParticipantsCapturingTheFlag();
            }
            if (f.getParticipantsInCaptureRange().isEmpty()) {
                f.clearParticipantsCapturingTheFlag();
            }
            if (f.getCarrier() != null) {
                f.clearParticipantsCapturingTheFlag();
            }
        }


//        for (Map.Entry<Participant, Team> entry : playersInRangeOfFlags.entrySet()) {
//            Participant capturer = entry.getKey();
//            Team teamWhosFlagIsGettingCaptured = entry.getValue();
//            if (teamsAndIfTheirFlagIsAvailableToBeCaptured.get(teamWhosFlagIsGettingCaptured)) {
//                if (!capturer.getTeam().equals(teamWhosFlagIsGettingCaptured)) {
//                    HashMap<Team, Integer> progress = new HashMap<>();
//                    if (!capturingProgress.containsKey(capturer)) {
//                        progress.put(teamWhosFlagIsGettingCaptured, 1);
//                        capturingProgress.put(capturer, progress);
//                    } else {
//                        progress.put(teamWhosFlagIsGettingCaptured, capturingProgress.get(capturer).get(teamWhosFlagIsGettingCaptured) + 1);
//                        capturingProgress.put(capturer, progress);
//                    }
//                }
//            }
//        }
//        for (Map.Entry<Participant, HashMap<Team, Integer>> entry : capturingProgress.entrySet()) {
//            for (Map.Entry<Team, Integer> entry1 : capturingProgress.get(entry.getKey()).entrySet()) {
//                System.out.println(entry.getKey().getPlayer().getName() + " " + entry1.getKey().getName() + " " + entry.getValue().get(entry1.getKey()));
//            }
//        }
    }

    private static void UpdateCaptureProgressBossBars() {
        for (Flag f : game.getFlags()) {
            if (!f.getParticipantsCapturingTheFlag().isEmpty()) {
                Integer progress = 0;
                Team teamCapturing = null;
                Team teamWhosFlagIsGettingCaptured = null;
                Integer ticksNeededForCapture = game.getSecondsNeededForCapture() * 20;
                for (Map.Entry<Participant, Integer> entry : f.getParticipantsCapturingTheFlag().entrySet()) {
                    if (entry.getValue() > progress) {
                        progress = entry.getValue();
                        teamCapturing = entry.getKey().getTeam();
                        teamWhosFlagIsGettingCaptured = f.getTeam();
                    }
                }
                if (f.getCapturingProgressBossBar() == null) {
                    BossBar bossBar = Bukkit.createBossBar("%team%" + ChatColor.YELLOW + ChatColor.BOLD + " IS CAPTURING " + "%team1%" + ChatColor.YELLOW + ChatColor.BOLD + "'s FLAG!!!", BarColor.YELLOW, BarStyle.SOLID);

                    StringBuilder[] names = new StringBuilder[2];
                    names[0] = new StringBuilder(teamCapturing.getDisplayName());
                    names[1] = new StringBuilder(teamWhosFlagIsGettingCaptured.getDisplayName());

                    String title = "";

                    for (Integer i = 0; i < names.length; i++) {

                        List<Integer> colorCodes = new ArrayList<>();
                        for (Integer j = 0; j < names[i].length(); j++) {
                            if (names[i].charAt(j) == '&') colorCodes.add(j);
                        }
                        for (Integer j = 0; j < colorCodes.size(); j++) {
                            names[i].insert(colorCodes.get(j) + 2 + j * 2, "&l");
                        }
                        if (i == 0) title = bossBar.getTitle().replace("%team%", names[i]);
                        else title = bossBar.getTitle().replace("%team1%", names[i]);
                        bossBar.setTitle(title);
                    }
                    //Why is this so overly complicated just to add a bold to the name holy shit am I just stupid and doing it the wrong way or what is this
                    //This took me way too long I am not proud of that fact

                    title = ChatColor.translateAlternateColorCodes('&', title);

                    bossBar.setTitle(title);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        bossBar.addPlayer(p);
                    }

                    bossBar.setProgress(Double.parseDouble(String.valueOf((progress))) / Double.parseDouble(String.valueOf((ticksNeededForCapture))));

                    f.setCapturingProgressBossBar(bossBar);
                } else {
                    f.getCapturingProgressBossBar().setProgress(Double.parseDouble(String.valueOf((progress))) / Double.parseDouble(String.valueOf((ticksNeededForCapture))));
                }
            } else {
                if (f.getCapturingProgressBossBar() != null) {
                    f.getCapturingProgressBossBar().removeAll();
                    f.setCapturingProgressBossBar(null);
                }
            }
        }
    }

    private static void CheckIfFlagsAreCaptured() {
        for (Flag f : game.getFlags()) {
            for (Map.Entry<Participant, Integer> entry : f.getParticipantsCapturingTheFlag().entrySet()) {
                if (entry.getValue() == game.getSecondsNeededForCapture() * 20) {
                    f.setCarrier(entry.getKey());
                    FlagCapturedEvent event = new FlagCapturedEvent(entry.getKey().getTeam(), f.getTeam());
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        }
    }

    private static void CheckFlagCarrierGlow() {
        List<Participant> carriers = new ArrayList<>();
        for (Flag f : game.getFlags()) {
            if (f.getCarrier() != null) {
                carriers.add(f.getCarrier());
            }
        }
        for (Participant carrier : carriers) {
            Boolean isGlowing = false;
            for (PotionEffect pot : carrier.getPlayer().getActivePotionEffects()) {
                if (pot.getType().equals(PotionEffectType.GLOWING)) {
                    PotionEffect extend = new PotionEffect(PotionEffectType.GLOWING, 20, 0);
                    carrier.getPlayer().removePotionEffect(pot.getType());
                    carrier.getPlayer().addPotionEffect(extend);
                    isGlowing = true;
                    break;
                }
            }
            if (!isGlowing) {
                PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, 20, 0);
                carrier.getPlayer().addPotionEffect(effect);
            }
        }
    }

    private static void CheckIfFlagIsReturned() {
        Integer range = game.getFlagRadius();
        for (Flag f : game.getFlags()) {
            if (f.getCarrier() != null) {
                Participant carrier = f.getCarrier();
                if (carrier.getPlayer().getLocation().distance(game.getFlagDepositLocation()) <= range) {
                    f.setCarrier(null);
                    FlagDepositedEvent event = new FlagDepositedEvent(carrier.getTeam(), f.getTeam());
                    Bukkit.getPluginManager().callEvent(event);
                    Methods.GivePlayerKitInventory(carrier.getPlayer(), carrier.getKit());
                }
            }
        }
    }

    private static void SpawnFlagDepositLocationParticles() {

        Location depositLocation = GameManager.getActiveGame().getFlagDepositLocation();

        Random r = new Random();

        Integer radius = game.getFlagRadius();
        Integer count = game.getFlagParticleCount();

        ParticleBuilder areaParticle = new ParticleBuilder(Particle.REDSTONE);

        for (Integer i = 0; i < count; i++) {
            areaParticle.color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
            areaParticle.location(depositLocation);
            areaParticle.offset(radius, radius / 2, radius);
            areaParticle.spawn();
        }
        //I know that you can .count(count) them but that way they won't be all random color, it'll be in batches of count.
        //Really unnecessary and creates a bigger load but :)

    }

    private static void CheckKitEffects() {
        for (Participant p : game.getParticipants()) {
            if (p.getKit() != null && p.getKit().getPotionEffects() != null) {
                for (PotionEffect pot : p.getKit().getPotionEffects()) {
                    if (p.getPlayer().getActivePotionEffects().contains(pot)) {
                        PotionEffect extend = new PotionEffect(pot.getType(), 19, pot.getAmplifier());
                        p.getPlayer().removePotionEffect(pot.getType());
                        p.getPlayer().addPotionEffect(extend);
                    } else {
                        PotionEffect effect = new PotionEffect(pot.getType(), 19, pot.getAmplifier());
                        p.getPlayer().addPotionEffect(effect);
                    }
                }
            }
        }
    }

    private static void CheckIfGameIsOver() {
        for (Team t : game.getTeams()) {
            if (t.getTeamMembers().isEmpty()) {
                game.removeTeam(t);
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
            if (team != null) {
                team.unregister();
                System.out.println(teamName + " was still here and the game is not running. Removing it now.");
            }
        }
    }

}