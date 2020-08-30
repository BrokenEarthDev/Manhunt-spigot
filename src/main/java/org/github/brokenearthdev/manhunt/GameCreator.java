package org.github.brokenearthdev.manhunt;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.github.brokenearthdev.manhunt.impl.GameOptionsImpl;
import org.github.brokenearthdev.manhunt.impl.ManhuntGameImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Creates a {@link ManhuntGame} object
 */
public class GameCreator {

    private List<Player> includedPlayers = new ArrayList<>();
    private Player speedrunner;
    private List<Player> hunters;
    private World main, nether, end;
    private int gracePeriod;
    private boolean dumpToConfig;
    private boolean allowTrackers = true;
    private boolean genWorld, genNether, genEnd;
    private int maxPlayersCount;
    private int minPlayersCount;

    public GameCreator(GameOptions options) {
        gracePeriod = options.gracePeriodEnabled() ? options.gracePeriodSeconds() : 0;
        dumpToConfig = options.dumpInfoToConfig();
        dumpToConfig = options.allowTrackers();
        genWorld = options.isGenerateMainWorld();
        genNether = options.isGenerateNetherWorld();
        genEnd = options.isGenerateEndWorld();
        maxPlayersCount = options.getMaxPlayersCount() < 2 || options.getMaxPlayersCount() <
                options.getMinimumPlayersCount() ? Integer.MAX_VALUE - 1 : options.getMaxPlayersCount();
        minPlayersCount = options.getMinimumPlayersCount() < 2 || options.getMinimumPlayersCount() >
                options.getMaxPlayersCount() ? 2 : options.getMinimumPlayersCount();
    }

    public GameCreator() {
        this(ManhuntPlugin.getInstance().getDefaultOptions());
    }

    /**
     * @param player Player to include
     * @return This object
     */
    public GameCreator includePlayer(Player player) {
        if (player != null && !includedPlayers.contains(player))
            includedPlayers.add(player);
        return this;
    }

    /**
     * @param player The speedrunner
     * @return This object
     */
    public GameCreator setSpeedrunner(Player player) {
        includePlayer(player);
        speedrunner = player;
        return this;
    }

    /**
     * @param player The hunter
     * @return This object
     */
    public GameCreator addHunter(Player player) {
        includePlayer(player);
        if (hunters == null)
            hunters = new ArrayList<>(1);
        hunters.add(player);
        return this;
    }

    public GameCreator removeHunter(Player player) {
        includedPlayers.remove(player);
        if (hunters != null)
            hunters.remove(player);
        return this;
    }

    /**
     * @param gracePeriod The grace period
     * @return This object
     */
    public GameCreator setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
        return this;
    }

    /**
     * @param disable Whether grace period is disabled or not
     * @return This object
     */
    public GameCreator disableGracePeriod(boolean disable) {
        setGracePeriod(0);
        return this;
    }

    /**
     * @param world The main world
     * @return This object
     */
    public GameCreator setMainWorld(World world) {
        this.main = world;
        return this;
    }

    /**
     * @param nether The nether world
     * @return This object
     */
    public GameCreator setNetherWorld(World nether) {
        this.nether = nether;
        return this;
    }

    /**
     * @param end The end
     * @return This object
     */
    public GameCreator setEnd(World end) {
        this.end = end;
        return this;
    }

    /**
     * @param main Whether the main world will be generated or not
     * @return This object
     */
    public GameCreator generateMainWorld(boolean main) {
        genWorld = main;
        return this;
    }

    /**
     * @param nether Whether the nether world will be generated or not
     * @return This object
     */
    public GameCreator generateNether(boolean nether) {
        this.genNether = nether;
        return this;
    }

    /**
     * @param end Whether the end world will be generated or not
     * @return This object
     */
    public GameCreator generateEnd(boolean end) {
        this.genEnd = end;
        return this;
    }

    /**
     * @param dump Whether to dump game to config
     * @return This object
     */
    public GameCreator dumpToConfig(boolean dump) {
        dumpToConfig = dump;
        return this;
    }

    /**
     * @param allow Whether to allow trackers
     * @return This object
     */
    public GameCreator allowTrackers(boolean allow) {
        allowTrackers = allow;
        return this;
    }

    /**
     * @param maxPlayersCount The maximum players count
     * @return This object
     */
    public GameCreator maxPlayers(int maxPlayersCount) {
        if (maxPlayersCount >= 2 && maxPlayersCount >= minPlayersCount)
            this.maxPlayersCount = maxPlayersCount;
        return this;
    }

    /**
     * @param minPlayersCount The minimum players count
     * @return This object
     */
    public GameCreator minPlayers(int minPlayersCount) {
        if (minPlayersCount >= 2 && minPlayersCount <= maxPlayersCount)
            this.minPlayersCount = minPlayersCount;
        return this;
    }


    /**
     * Creates a {@link ManhuntGame} object
     *
     * @return The {@link ManhuntGame} object
     */
    public ManhuntGame createGame() {
        includedPlayers = (includedPlayers == null) ? new ArrayList<>() : includedPlayers;
        setWorldIfNull();
        generateWorldsIfEnabled();
        modifyIncludedPlayersList(minPlayersCount, maxPlayersCount);

        GameOptions options = new GameOptionsImpl(
                gracePeriod > 0, allowTrackers, genWorld, genNether, genEnd,
                dumpToConfig, maxPlayersCount, minPlayersCount, gracePeriod
        );
        return new ManhuntGameImpl(speedrunner, hunters, gracePeriod, main, nether, end, includedPlayers, options);
    }

    private void setWorldIfNull() {
        if (main == null) main = Bukkit.getWorld("world");
        if (nether == null) nether = Bukkit.getWorld("world_nether");
        if (end == null) end = Bukkit.getWorld("world_the_end");
    }

    private void generateWorldsIfEnabled() {
        int random = ThreadLocalRandom.current().nextInt(0, 9999);
        if (genWorld) {
            this.main = new WorldCreator("world_manhunt_" + random).environment(World.Environment.NORMAL).createWorld();
        }
        if (genNether) {
            this.nether = new WorldCreator("nether_manhunt_" + random).environment(World.Environment.NETHER).createWorld();
        }
        if (genEnd) {
            this.end = new WorldCreator("end_manhunt_" + random).environment(World.Environment.THE_END).createWorld();
        }
    }

    private void modifyIncludedPlayersList(int min, int max) {
        List<Player> bukkitPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (includedPlayers.size() == 0) {
            int randomDeterminedSize = ThreadLocalRandom.current().nextInt(min, max + 1);
            resizeList(randomDeterminedSize, includedPlayers);
        }
        if (bukkitPlayers.size() < includedPlayers.size()) {
            // server player count less than included players
            this.minPlayersCount = bukkitPlayers.size();
            this.maxPlayersCount = bukkitPlayers.size();
            this.includedPlayers = bukkitPlayers;
            modifyIncludedPlayersList(bukkitPlayers.size(), bukkitPlayers.size());
            return;
        }
        if (includedPlayers.size() < min)
            resizeList(min, includedPlayers);
        else if (includedPlayers.size() > max) resizeList(max, includedPlayers);
    }

    private void resizeList(int size, List<Player> list) {
        List<Player> bukkitPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (list.size() < size) {
            for (int i = 0; i < bukkitPlayers.size(); i++) {
                if (bukkitPlayers.size() == list.size()) break;
                if (bukkitPlayers.get(i) == null) continue;
                if (!list.contains(bukkitPlayers.get(i)))
                    list.add(bukkitPlayers.get(i));
            }
        }
        if (list.size() > size) {
            for (int i = 0; i < bukkitPlayers.size(); i++) {
                if (bukkitPlayers.size() == list.size()) break;
                list.remove(bukkitPlayers.get(i));
            }
        }
    }

}
