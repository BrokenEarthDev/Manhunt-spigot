package org.github.brokenearthdev.manhunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Allows you to manipulate hunter trackers and to update them.
 */
public interface HunterTracker {

    /**
     * @return The hunter
     */
    Player getHunter();

    /**
     * @return The game
     */
    ManhuntGame getGame();

    /**
     * Opens the tracking interface
     */
    void openTrackingInterface();

    /**
     * Sets the tracking type (either location - {@code true} or health - {@code false})
     *
     * @param locationOrHealth Whether location should be tracked or health
     */
    void setTrackingType(boolean locationOrHealth);

    /**
     * @param distanceOrCoordinates Whether distance should be tracked or the exact coordinates
     */
    void setTrackingDistance(boolean distanceOrCoordinates);

    /**
     * @param autoCollect Whether auto collect is enabled or not
     */
    void setAutoCollect(boolean autoCollect);

    /**
     * @param player The player to track
     */
    void setTracked(Player player);

    /**
     * Track spawn
     */
    void setTrackSpawn();

    /**
     * @return Whether location (true) or health (false) is tracked
     */
    boolean getTrackingType();

    /**
     * @return Whether distance (true) or the exact coordinates of the target (false) is tracked
     */
    boolean isTrackingDistance();

    /**
     * @return Whether auto collect is enabled or not
     */
    boolean isAutoCollect();

    /**
     * @return The tracked player
     */
    Player getTrackedPlayer();

    /**
     * @return Whether spawn is tracked or not (equivalent to {@code getTrackedPlayer() == null})
     */
    boolean trackingSpawn();

    /**
     * Updates the tracker
     */
    void updateTracker();

    /**
     * Gives the compass to the hunter
     */
    void giveCompass();

    /**
     * Removes all tracker compasses from all players
     */
    static void removeCompasses() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContents().length; i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null) continue;
                if (item.getType() == Material.COMPASS && item.getItemMeta() != null) {
                    if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + ChatColor.BOLD.toString() + "Player Tracker " +
                            ChatColor.GOLD + ChatColor.BOLD.toString() + " | " + ChatColor.GREEN
                            + ChatColor.BOLD.toString() + "Left or right click")) {
                        inventory.setItem(i, null);
                    }
                }
            }
        }
    }

    String COMPASS_NAME = ChatColor.GREEN + ChatColor.BOLD.toString() + "Player Tracker " +
            ChatColor.GOLD + ChatColor.BOLD.toString() + " | " + ChatColor.GREEN
            + ChatColor.BOLD.toString() + "Left or right click";

}
