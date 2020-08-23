package org.github.brokenearthdev.manhunt.impl;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.github.brokenearthdev.manhunt.GameOptions;
import org.github.brokenearthdev.manhunt.Speedrunner;

import java.util.*;
import java.util.stream.IntStream;

public class GameOptionsImpl implements GameOptions {

    private boolean gracePeriodEnabled = true;
    private boolean allowTrackers = true;
    private boolean generateMainWorld = false;
    private boolean generateNetherWorld = false;
    private boolean generateEnd = false;
    private boolean dumpToConfig = false;
    private int maxPlayers = Integer.MAX_VALUE;
    private int minPlayers = 2;
    private int gracePeriod = 30;
    private long gameID = -1;
    private World main = Bukkit.getWorld("world");
    private World nether = Bukkit.getWorld("world_nether");
    private World end = Bukkit.getWorld("world_the_end");

    public GameOptionsImpl(boolean gracePeriodEnabled, boolean allowTrackers, boolean generateMainWorld,
                           boolean generateNetherWorld, boolean generateEnd, boolean dumpToConfig,
                           int maxPlayers, int minPlayers, int gracePeriod, World main, World nether, World end) {
        this.gracePeriodEnabled = gracePeriodEnabled;
        this.allowTrackers = allowTrackers;
        this.generateMainWorld = generateMainWorld;
        this.generateNetherWorld = generateNetherWorld;
        this.generateEnd = generateEnd;
        this.dumpToConfig = dumpToConfig;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.gracePeriod = gracePeriod;
        this.main = main;
        this.nether = nether;
        this.end = end;
        if (dumpToConfig) {
            Speedrunner.getInstance().incrementLargestID();
            gameID = Speedrunner.getInstance().getLargestGameID();
        }
    }

    public GameOptionsImpl() {}

    @Override
    public boolean gracePeriodEnabled() {
        return gracePeriodEnabled;
    }

    @Override
    public boolean allowTrackers() {
        return allowTrackers;
    }

    @Override
    public boolean isGenerateMainWorld() {
        return generateMainWorld;
    }

    @Override
    public boolean isGenerateNetherWorld() {
        return generateNetherWorld;
    }

    @Override
    public boolean isGenerateEndWorld() {
        return generateEnd;
    }

    @Override
    public boolean dumpInfoToConfig() {
        return dumpToConfig;
    }

    @Override
    public int getMaxPlayersCount() {
        return maxPlayers;
    }

    @Override
    public int getMinimumPlayersCount() {
        return minPlayers;
    }

    @Override
    public long getGameID() {
        return gameID;
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
    public World getEndWorld() {
        return end;
    }

    @Override
    public int gracePeriodSeconds() {
        return gracePeriod;
    }
}
