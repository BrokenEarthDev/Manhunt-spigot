package org.github.brokenearthdev.manhunt.impl;

import org.bukkit.entity.Player;
import org.github.brokenearthdev.manhunt.GameResults;
import org.github.brokenearthdev.manhunt.ManhuntGame;

import java.util.Collections;
import java.util.List;

public class GameResultsImpl implements GameResults {

    private boolean speedrunnersWin;
    private boolean hunterWin;
    private List<Player> winners;
    private List<Player> losers;
    private ManhuntGame instance;

    private GameResultsImpl(boolean speedrunnersWin, boolean hunterWin, List<Player> winners,
                            List<Player> losers, ManhuntGame instance) {
        this.speedrunnersWin = speedrunnersWin;
        this.hunterWin = hunterWin;
        this.winners = winners;
        this.losers = losers;
        this.instance = instance;
    }

    public static GameResults hunterWin(ManhuntGame game) {
        return new GameResultsImpl(false, true, game.getHunters(),
                Collections.singletonList(game.getSpeedrunner()), game);
    }

    public static GameResults speedrunnerWin(ManhuntGame game) {
        return new GameResultsImpl(true, false, Collections.singletonList(game.getSpeedrunner()),
                game.getHunters(), game);
    }

    @Override
    public boolean speedrunnersWin() {
        return false;
    }

    @Override
    public boolean hunterWin() {
        return false;
    }

    @Override
    public List<Player> getWinners() {
        return null;
    }

    @Override
    public List<Player> getLosers() {
        return null;
    }

    @Override
    public ManhuntGame getGame() {
        return null;
    }
}
