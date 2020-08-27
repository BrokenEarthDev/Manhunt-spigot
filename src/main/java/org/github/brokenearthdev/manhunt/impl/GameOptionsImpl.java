package org.github.brokenearthdev.manhunt.impl;

import org.github.brokenearthdev.manhunt.GameOptions;
import org.github.brokenearthdev.manhunt.ManhuntPlugin;

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

    public GameOptionsImpl(boolean gracePeriodEnabled, boolean allowTrackers, boolean generateMainWorld,
                           boolean generateNetherWorld, boolean generateEnd, boolean dumpToConfig,
                           int maxPlayers, int minPlayers, int gracePeriod) {
        this.gracePeriodEnabled = gracePeriodEnabled;
        this.allowTrackers = allowTrackers;
        this.generateMainWorld = generateMainWorld;
        this.generateNetherWorld = generateNetherWorld;
        this.generateEnd = generateEnd;
        this.dumpToConfig = dumpToConfig;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.gracePeriod = gracePeriod;
        if (dumpToConfig) {
            ManhuntPlugin.getInstance().incrementLargestID();
            gameID = ManhuntPlugin.getInstance().getLargestGameID();
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
    public int gracePeriodSeconds() {
        return gracePeriod;
    }
}
