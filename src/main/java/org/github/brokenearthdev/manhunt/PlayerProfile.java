package org.github.brokenearthdev.manhunt;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * A class containing info about the {@link Player} stats such as
 * kills, deaths, etc.
 */
public class PlayerProfile {

    /**
     * Offline player instance
     */
    private final OfflinePlayer player;

    /**
     * The player's kills
     */
    private int kills;

    /**
     * The player's deaths
     */
    private int deaths;

    /**
     * The average time survived in seconds
     */
    private double avgTimeSurvived;

    /**
     * The number of times the player played as a speed runner
     */
    private int timesSpeedrunner;

    /**
     * The number of times the player played as a hunter
     */
    private int timesHunter;


    private final YamlConfiguration config;

    /**
     * It is generally recommended to access {@link ManhuntPlugin#getProfile(Player)}.
     * This will help reduce the number of objects created and help increase reliability.
     * There is no need to access {@link #saveValues()} (as well as {@link #refresh()})
     * if this object was retrieved from the aforementioned function.
     *
     * @param player The player
     * @param config The configuration (where all the player data are stored)
     */
    public PlayerProfile(OfflinePlayer player, YamlConfiguration config) {
        this.player = player;
        this.config = config;
        this.refresh();
    }

    /**
     * It is generally recommended to access {@link ManhuntPlugin#getProfile(Player)}.
     * This will help reduce the number of objects created and help increase reliability.
     * There is no need to access {@link #saveValues()} (as well as {@link #refresh()})
     * if this object was retrieved from the aforementioned function.
     *
     * @param player The player
     */
    public PlayerProfile(OfflinePlayer player) {
        this(player, ManhuntPlugin.getInstance().getPlayerConfig());
    }

    /**
     * Refreshes the values.
     */
    public void refresh() {
        this.kills = config.getInt(player.getUniqueId() + ".kills");
        this.deaths = config.getInt(player.getUniqueId() + ".deaths");
        this.avgTimeSurvived = config.getDouble(player.getUniqueId() + ".avgTimeSurvived");
        this.timesSpeedrunner = config.getInt(player.getUniqueId() + ".speedrunner");
        this.timesHunter = config.getInt(player.getUniqueId() + ".hunter");
    }

    /**
     * Saves the value (sets the value in the configuration)
     */
    public void saveValues() {
        config.set(player.getUniqueId() + ".kills", kills);
        config.set(player.getUniqueId() + ".deaths", deaths);
        config.set(player.getUniqueId() + ".avgTimeSurvived", avgTimeSurvived);
        config.set(player.getUniqueId() + ".speedrunner", timesSpeedrunner);
        config.set(player.getUniqueId() + ".hunter", timesHunter);
    }

    /**
     * @param kills The kills
     */
    public void setKills(int kills) {
        this.kills = kills;
    }

    /**
     * @param deaths The deaths
     */
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    /**
     * @param val Average time survived in seconds
     */
    public void setAvgTimeSurvived(double val) {
        avgTimeSurvived = val;
    }

    /**
     * @param timesSpeedrunner The amount of times the player was
     *                         assigned the speedrunner role
     */
    public void setTimesSpeedrunner(int timesSpeedrunner) {
        this.timesSpeedrunner = timesSpeedrunner;
    }

    /**
     * @param timesHunter The amount of times the player was
     *                    assigned the hunter role
     */
    public void setTimesHunter(int timesHunter) {
        this.timesHunter = timesHunter;
    }

    /**
     * @return The number of kills
     */
    public int getKills() {
        return kills;
    }

    /**
     * @return The number of deaths
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * @return The average time survived in seconds
     */
    public double getAverageTimeSurvived() {
        return avgTimeSurvived;
    }

    /**
     * @return The number of times the player was assigned the
     * speedrunner role
     */
    public int getTimesSpeedrunner() {
        return timesSpeedrunner;
    }

    /**
     * @return The number of times the player was assigned the
     * hunter role
     */
    public int getTimesHunter() {
        return timesHunter;
    }

    /**
     * @return The total games played
     */
    public int getTotalGamesPlayed() {
        return getTimesHunter() + getTimesSpeedrunner();
    }

    /**
     * @return The player
     */
    public OfflinePlayer getPlayer() {
        return player;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj2) {
        if (!(obj2 instanceof PlayerProfile))
            return false;
        PlayerProfile profile = (PlayerProfile) obj2;
        return getPlayer().equals(profile.getPlayer());
    }

}
