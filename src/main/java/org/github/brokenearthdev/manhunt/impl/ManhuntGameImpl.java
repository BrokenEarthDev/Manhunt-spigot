package org.github.brokenearthdev.manhunt.impl;

import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.brokenearthdev.manhunt.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ManhuntGameImpl implements ManhuntGame {

    private List<HunterTracker> trackers = new ArrayList<>();

    private List<Player> hunters;
    private Player speedrunner;
    private int gracePeriod = 30;
    private int left;
    private List<Player> inclPlayers;
    private boolean allowGracePeriod = true;
    private boolean gameOngoing = false;
    private boolean gracePeriodOngoing = false;
    private World main, nether, end;
    private GameOptions options;
    private GameResults results;

    public ManhuntGameImpl(Player speedrunner, List<Player> hunters, int gracePeriod, World main, World nether, World end,
                           List<Player> inclPlayers, GameOptions options) {
        this.allowGracePeriod = gracePeriod > 0;
        this.speedrunner = speedrunner;
        this.hunters = hunters;
        this.gracePeriod = gracePeriod;
        this.main = main;
        this.nether = nether;
        this.end = end;
        this.inclPlayers = inclPlayers;
        this.options = options;
        left = gracePeriod;
    }

    @Override
    public List<Player> getIncludedPlayers() {
        return new ArrayList<>(inclPlayers);
    }

    @Override
    public List<Player> getHunters() {
        return hunters;
    }

    @Override
    public Player getSpeedrunner() {
        return speedrunner;
    }

    private void assignPlayerRoles() {
        if (hunters == null) this.hunters = new ArrayList<>(0);
        if (speedrunner != null) {
            if (hunters.size() == 0) {
                ArrayList<Player> hunters = new ArrayList<>();
                for (Player player : inclPlayers) {
                    if (player != speedrunner) hunters.add(player);
                }
                this.hunters = hunters;
            }
        } else {
            Player speedRunner;
            ArrayList<Player> candidates = new ArrayList<>();
            inclPlayers.forEach(p -> {
                if (!hunters.contains(p))
                    candidates.add(p);
            });
            int random = new Random().nextInt(candidates.size());
            speedRunner = inclPlayers.get(random);
            if (hunters.size() == 0) {
                candidates.remove(speedRunner);
                hunters = candidates;
            }
            this.speedrunner = speedRunner;
        }
    }

    @Override
    public void startGame() {
        if (inclPlayers.size() < 2) {
            Bukkit.getLogger().severe("Can't start a manhunt game. There are less than two players chosen");
            return;
        }
        ManhuntGame running = Speedrunner.getInstance().getRunningGame();
        if (running == null || !running.equals(this))
            Speedrunner.getInstance().setRunningGame(this);

        this.gameOngoing = true;
        this.assignPlayerRoles();
        this.setupPlayers();
        // display roles
        speedrunner.sendTitle(ChatColor.RED + "ROLE: SPEEDRUNNER", null, 10, 40, 15);
        hunters.forEach(h -> h.sendTitle(ChatColor.AQUA + "ROLE: HUNTER", null, 10, 40, 15));

        inclPlayers.forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 100, 100));
        inclPlayers.forEach(player -> player.sendMessage(ChatColor.GREEN + "Game Started!"));

        this.startGracePeriod();
    }

    private void setupPlayers() {
        getIncludedPlayers().forEach(player -> player.teleport(main.getSpawnLocation()));
        getIncludedPlayers().forEach(player -> player.setHealth(20));
        getIncludedPlayers().forEach(player -> player.setSaturation(20));;
    }

    @Override
    public void startGracePeriod() {
        if (!allowGracePeriod || gracePeriod <= 0) return;
        inclPlayers.forEach(player -> player.sendMessage(ChatColor.GREEN + "Grace Period Started"));
        int delay = (gracePeriod <= 5) ? 1 : (gracePeriod <= 30) ? 5 : (gracePeriod <= 60) ? 10 : (gracePeriod <= 120) ? 15 :
                (gracePeriod <= 300) ? 25 : (gracePeriod <= 900) ? 45 : 60;
        // temporarily hide the speedrunner
        inclPlayers.forEach(player -> {
            if (!player.equals(speedrunner)) {
                player.hidePlayer(Speedrunner.getInstance(), speedrunner);
                player.sendMessage(ChatColor.GREEN + "The speedrunner will remain hidden until the grace period" +
                        " ends (" + ChatColor.LIGHT_PURPLE + gracePeriod + ChatColor.GREEN + " seconds)");
            }
        });
        this.gracePeriodOngoing = true;
        // start counting
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!allowGracePeriod || !gameOngoing) {
                    // just in case
                    this.cancel();
                    return;
                }
                inclPlayers.forEach(player -> player.sendMessage(ChatColor.LIGHT_PURPLE + String.valueOf(left) +
                        ChatColor.GREEN + " seconds left"));
                ManhuntGameImpl.this.left -= delay;
                if (left <= 0) {
                    // grace period ended
                    inclPlayers.forEach(player -> player.sendMessage(ChatColor.RED + "Grace period ended!"));
                    hunters.forEach(player -> player.sendTitle(ChatColor.AQUA + "Chase the Speedrunner Down",
                            null, 10, 30, 15));
                    speedrunner.sendTitle(ChatColor.GREEN + "Good Luck", null, 10, 30, 15);

                    // un-hide speedrunner
                    inclPlayers.forEach(player -> {
                        if (!player.equals(speedrunner)) {
                            player.showPlayer(Speedrunner.getInstance(), speedrunner);
                            player.sendMessage(ChatColor.GREEN + "You can now see the speedrunner");
                        }
                    });
                    gracePeriodOngoing = false;
                    if (options.allowTrackers())
                        createTrackerEntries();
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(Speedrunner.getInstance(), 0, delay * 20);
    }

    private void createTrackerEntries() {
        hunters.forEach(hun -> {
            HunterTracker tracker = new SimpleTracker(hun, this);
            trackers.add(tracker);
            tracker.giveCompass();
        });
    }

    @Override
    public void cancelGracePeriod() {
        allowGracePeriod = false;
        inclPlayers.forEach(player -> player.sendMessage(ChatColor.RED + "Grace period cancelled"));
        inclPlayers.forEach(player -> {
            if (!player.equals(speedrunner)) {
                player.showPlayer(Speedrunner.getInstance(), speedrunner);
                player.sendMessage(ChatColor.GREEN + "You can now see the speedrunner");
            }
        });
    }

    @Override
    public void dumpToConfig() {
        long game = options.getGameID();
        if (game >= 0) {
            YamlConfiguration config = Speedrunner.getInstance().getGameConfig();
            List<String> uuidString = new ArrayList<>();
            hunters.forEach(hun -> uuidString.add(hun.getUniqueId().toString()));
            config.set("games.game#" + game + ".players.hunters", uuidString);
            config.set("games.game#" + game + ".players.speedrunners", speedrunner.getUniqueId().toString());
            config.set("games.game#" + game + ".winners", results.hunterWin() ? "hunters" : "speedrunners");
            config.set("games.game#" + game + ".options.allowTrackers", options.allowTrackers());
            config.set("games.game#" + game + ".options.generateNewMainWorld", options.isGenerateMainWorld());
            config.set("games.game#" + game + ".options.generateNewNetherWorld", options.isGenerateNetherWorld());
            config.set("games.game#" + game + ".options.generateNewEndWorld", options.isGenerateEndWorld());
            config.set("games.game#" + game + ".options.gracePeriodEnabled", options.gracePeriodEnabled());
            config.set("games.game#" + game + ".options.gracePeriodSeconds",options.gracePeriodSeconds());
            config.set("games.game#" + game + ".options.maxPlayersCount", options.getMaxPlayersCount());
            config.get("games.game#" + game + "options.minPlayersCount", options.getMinimumPlayersCount());
        }
    }

    @Override
    public void stopGame() {
        // remove compasses
        HunterTracker.removeCompasses();
        gameOngoing = false;
        trackers.clear();
        deleteWorldsIfApplicable();
        if (options.dumpInfoToConfig()) dumpToConfig();
        Speedrunner.getInstance().setRunningGame(null); // no game running
    }

    @Override
    public int getGracePeriod() {
        return gracePeriod;
    }

    @Override
    public void announceWin(boolean huntersOrSpeedrunners) {
        if (huntersOrSpeedrunners) {
            inclPlayers.forEach(player -> player.sendMessage(ChatColor.GREEN + "Hunters have won the game!"));
            hunters.forEach(hunter -> hunter.sendTitle(ChatColor.GREEN + "You Won the Game!",null, 10, 30, 15));
            speedrunner.sendTitle(ChatColor.RED + "You Lost the Game!", null, 10, 30, 15);
            results = GameResultsImpl.hunterWin(this);
        } else {
            inclPlayers.forEach(player -> player.sendMessage(ChatColor.RED + speedrunner.getName() + ChatColor.GREEN +
                    " have won the game!"));
            hunters.forEach(hunter -> hunter.sendTitle(ChatColor.RED + "You Lost the Game!", null, 10, 30, 15));
            speedrunner.sendTitle(ChatColor.GREEN + "You Won the Game!", null, 10, 30, 15);
            results = GameResultsImpl.speedrunnerWin(this);
        }
        inclPlayers.forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 100, 100));
        results = (huntersOrSpeedrunners) ? GameResultsImpl.hunterWin(this) : GameResultsImpl.speedrunnerWin(this);
        this.stopGame();
    }

    private void deleteWorldsIfApplicable() {
        if (options.isGenerateMainWorld())
            deleteWorld(main);
        if (options.isGenerateNetherWorld())
            deleteWorld(nether);
        if (options.isGenerateEndWorld())
            deleteWorld(end);
    }

    private void deleteWorld(World world) {
        if (Bukkit.getServer().unloadWorld(world, false)) {
            Speedrunner.getInstance().getLogger().fine("Successfully unloaded " + world.getName());
            if (new File(world.getName()).delete())
                Speedrunner.getInstance().getLogger().fine("Deleted world file");
            else Speedrunner.getInstance().getLogger().warning("Can't delete world file. Please make sure to " +
                    "remember to manually delete the world");
        }
    }

    @Override
    public boolean gracePeriodOngoing() {
        return gracePeriodOngoing;
    }

    @Override
    public boolean gameOngoing() {
        return gameOngoing;
    }

    @Override
    public World getMainWorld() {
        return main;
    }

    @Override
    public World getNetherWorld() {
        return nether;
    }

    @Override
    public World getEnd() {
        return end;
    }

    @Override
    public GameOptions getOptions() {
        return options;
    }

    @Override
    public GameResults getResults() {
        return results;
    }

}
