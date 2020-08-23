package org.github.brokenearthdev.manhunt;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Contains methods that allow you to alter the state of
 * the game.
 */
public interface ManhuntGame {

    List<Player> getIncludedPlayers();

    /**
     * @return The hunter(s)
     */
    List<Player> getHunters();

    /**
     * @return The speed runner
     */
    Player getSpeedrunner();

    /**
     * Starts the game
     */
    void startGame();

    /**
     * Starts the grace period
     */
    void startGracePeriod();

    /**
     * Cancels the grace period
     */
    void cancelGracePeriod();

    /**
     * Dumps the game to the configuration. Please note that, in order for this
     * to work, the game should have a game id. In order to have that, the game configuration
     * should be read at the start (meaning there were no exceptions thrown when the plugin
     * was loaded).
     * <p>
     * It is recommended to use this method with caution, as the game info will be already
     * be dumped if {@link GameOptions#dumpInfoToConfig()} is true.
     */
    void dumpToConfig();

    /**
     * Stops the game
     */
    void stopGame();

    /**
     * @return The grace period (in seconds)
     */
    int getGracePeriod();

    /**
     * @param huntersOrSpeedrunners Set to {@code true} if hunters are the winners, or
     *                              {@code false} if the speedrunner is the winner
     */
    void announceWin(boolean huntersOrSpeedrunners);

    /**
     * @return Whether the grace period is ongoing or not
     */
    boolean gracePeriodOngoing();

    /**
     * @return Whether the game is ongoing (not stopped) or not
     */
    boolean gameOngoing();

    /**
     * @return The main world
     */
    World getMainWorld();

    /**
     * @return The nether world
     */
    World getNetherWorld();

    /**
     * @return The end
     */
    World getEnd();

    /**
     * @return The options
     */
    GameOptions getOptions();

    /**
     * @return The game results (or null if there isn't any)
     */
    GameResults getResults();

}
