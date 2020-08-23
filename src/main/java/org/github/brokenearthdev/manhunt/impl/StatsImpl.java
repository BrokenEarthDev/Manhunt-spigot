package org.github.brokenearthdev.manhunt.impl;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.github.brokenearthdev.manhunt.GameStatistics;

import java.util.List;

public class StatsImpl implements GameStatistics {

    private int hunterDeaths;
    private int speedRunnerLived;
    private int gracePeriod;
    private List<Player> hunters;
    private Player speedrunner;
    private World world;

    public void setHunterDeaths(int deaths) {
        this.hunterDeaths = deaths;
    }

    public void setSpeedrunnerLivedSeconds(int seconds) {
        this.speedRunnerLived = seconds;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public void setHunters(List<Player> hunters) {
        this.hunters = hunters;
    }

    public void setSpeedrunner(Player sr) {
        this.speedrunner = sr;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public int getHunterDeaths() {
        return hunterDeaths;
    }

    @Override
    public int getSpeedrunnerLivedSeconds() {
        return speedRunnerLived;
    }

    @Override
    public int getGracePeriod() {
        return gracePeriod;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public List<Player> getHunters() {
        return hunters;
    }


    @Override
    public Player getSpeedrunner() {
        return speedrunner;
    }
}
