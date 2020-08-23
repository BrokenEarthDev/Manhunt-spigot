package org.github.brokenearthdev.manhunt;

import org.bukkit.World;

/**
 * An immutable interface storing the game options
 */
public interface GameOptions {

    /**
     * @return Whether grace period is enabled or not
     */
    boolean gracePeriodEnabled();

    /**
     * @return Whether trackers are allowed or not
     */
    boolean allowTrackers();

    /**
     * @return Whether a main world - discrete from the default main world - will be regenerated
     */
    boolean isGenerateMainWorld();

    /**
     * @return Whether a nether world - discrete from the default nether world - will be regenerated
     */
    boolean isGenerateNetherWorld();

    /**
     * @return Whether an end world - discrete from the default end world - will be regenerated
     */
    boolean isGenerateEndWorld();

    /**
     * @return Whether the info about the game will be dumped to the config after
     *         completion
     */
    boolean dumpInfoToConfig();

    /**
     * @return The maximum players count
     */
    int getMaxPlayersCount();

    /**
     * @return The minimum players count
     */
    int getMinimumPlayersCount();

    /**
     * @return A random game id.
     */
    long getGameID();

    /**
     * @return The main world
     */
    World getMainWorld();

    /**
     * @return The nether world
     */
    World getNetherWorld();

    /**
     * @return The end world
     */
    World getEndWorld();

    /**
     * @return The grace period in seconds (or 0 if the grace period isn't
     * enabled)
     */
    int gracePeriodSeconds();

}
