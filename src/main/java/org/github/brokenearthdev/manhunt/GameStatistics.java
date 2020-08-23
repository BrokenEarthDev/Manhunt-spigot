package org.github.brokenearthdev.manhunt;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public interface GameStatistics {

    int getHunterDeaths();
    int getSpeedrunnerLivedSeconds();
    int getGracePeriod();

    World getWorld();

    List<Player> getHunters();
    Player getSpeedrunner();

}
