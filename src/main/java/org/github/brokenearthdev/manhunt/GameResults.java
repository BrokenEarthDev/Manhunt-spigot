package org.github.brokenearthdev.manhunt;

import org.bukkit.entity.Player;

import java.util.List;

public interface GameResults {

    boolean speedrunnersWin();
    boolean hunterWin();

    List<Player> getWinners();
    List<Player> getLosers();

    ManhuntGame getGame();

}
