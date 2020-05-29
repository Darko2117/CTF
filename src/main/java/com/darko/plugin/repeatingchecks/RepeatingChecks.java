package com.darko.plugin.repeatingchecks;

import com.darko.plugin.Main;
import com.darko.plugin.commands.CTFCommandTabComplete;
import com.darko.plugin.gameclasses.Game;
import com.darko.plugin.gameclasses.GameManager;
import com.darko.plugin.gameclasses.Participant;
import com.darko.plugin.gameclasses.Team;
import org.bukkit.Bukkit;
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
    static List<Participant> participants = new ArrayList<>();
    static HashMap<Participant, Team> playersInRangeOfFlags = new HashMap<>();
    static HashMap<Team, Boolean> teamsAndIfTheirFlagIsAvailableToBeCaptured = new HashMap<>();
    static HashMap<Participant, HashMap<Team, Integer>> capturingProgress = new HashMap<>();

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
                } else {
                    game = null;
                    participants.clear();
                    playersInRangeOfFlags.clear();
                    teamsAndIfTheirFlagIsAvailableToBeCaptured.clear();
                    capturingProgress.clear();
                    CheckUnusedScoreboardTeams();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private static void UpdateParticipants() {
        participants.clear();
        for (Team t : game.getTeams()) {
            for (Participant p : t.getTeamMembers()) {
                if (!p.getPlayer().isDead()) participants.add(p);
            }
        }
        game.setParticipants(participants);
    }

    private static void UpdatePlayersInRangeOfFlags() {
        playersInRangeOfFlags.clear();
        Integer range = game.getFlagRadius();
        for (Participant p : participants) {
            for (Team t : game.getTeams()) {
                if (p.getPlayer().getLocation().distance(t.getFlag().getLocation()) <= range)
                    playersInRangeOfFlags.put(p, t);
            }
        }
    }

    private static void CheckCapturingAvailability() {
        teamsAndIfTheirFlagIsAvailableToBeCaptured.clear();
        for (Team t : game.getTeams()) {
            teamsAndIfTheirFlagIsAvailableToBeCaptured.put(t, true);
        }
        for (Map.Entry<Team, Boolean> entry : teamsAndIfTheirFlagIsAvailableToBeCaptured.entrySet()) {
            Team lastTeam = null;
            for (Map.Entry<Participant, Team> entry1 : playersInRangeOfFlags.entrySet()) {
                if (entry1.getValue().equals(entry.getKey())) {
                    if (lastTeam == null) lastTeam = entry1.getKey().getTeam();
                    else if (!lastTeam.equals(entry1.getKey().getTeam()))
                        teamsAndIfTheirFlagIsAvailableToBeCaptured.put(entry.getKey(), false);
                }
            }
        }
    }

    private static void CheckCapturingProgress() {
        for (Map.Entry<Participant, Team> entry : playersInRangeOfFlags.entrySet()) {
            Participant capturer = entry.getKey();
            Team teamWhosFlagIsGettingCaptured = entry.getValue();
            if (teamsAndIfTheirFlagIsAvailableToBeCaptured.get(teamWhosFlagIsGettingCaptured)) {
                if (!capturer.getTeam().equals(teamWhosFlagIsGettingCaptured)) {
                    HashMap<Team, Integer> progress = new HashMap<>();
                    if (!capturingProgress.containsKey(capturer)) {
                        progress.put(teamWhosFlagIsGettingCaptured, 1);
                        capturingProgress.put(capturer, progress);
                    } else {
                        progress.put(teamWhosFlagIsGettingCaptured, capturingProgress.get(capturer).get(teamWhosFlagIsGettingCaptured) + 1);
                        capturingProgress.put(capturer, progress);
                    }
                }
            }
        }
//        for (Map.Entry<Participant, HashMap<Team, Integer>> entry : capturingProgress.entrySet()) {
//            for (Map.Entry<Team, Integer> entry1 : capturingProgress.get(entry.getKey()).entrySet()) {
//                System.out.println(entry.getKey().getPlayer().getName() + " " + entry1.getKey().getName() + " " + entry.getValue().get(entry1.getKey()));
//            }
//        }
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