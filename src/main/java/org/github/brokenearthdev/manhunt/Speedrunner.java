package org.github.brokenearthdev.manhunt;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.github.brokenearthdev.manhunt.commands.ProfileCommand;
import org.github.brokenearthdev.manhunt.commands.StartGame;
import org.github.brokenearthdev.manhunt.commands.StopCommand;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class Speedrunner extends JavaPlugin {

    private YamlConfiguration playerConfig;
    private YamlConfiguration gameConfig;

    private static final File playerConfigFile = new File("plugins/speedrunner/playerConfig.yaml");
    private static final File gameConfigFile = new File("plugins/speedrunner/gameConfig.yaml");
    private static final File speedrunnerFolder = new File("plugins/speedrunner");

    private static Speedrunner plugin;
    protected static ManhuntGame runningGame;
    private static final HashSet<PlayerProfile> profiles = new HashSet<>();

    private boolean savePlayerConfig = false;
    private boolean saveGameConfig = false;

    private int largestGameID = -1;

    @Override
    public void onEnable() {
        plugin = this;
        // loading
        if (!speedrunnerFolder.exists()) speedrunnerFolder.mkdir();
        try {
            if (!gameConfigFile.exists()) gameConfigFile.createNewFile();
            if (!playerConfigFile.exists()) playerConfigFile.createNewFile();
            playerConfig = YamlConfiguration.loadConfiguration(playerConfigFile);
            gameConfig = YamlConfiguration.loadConfiguration(gameConfigFile);
            savePlayerConfig = saveGameConfig = true;
            largestGameID = SpeedrunnerUtils.findLargestID();
        } catch (IOException e) {
            getLogger().warning("An IO error had occurred: " + e.getMessage());
            getLogger().warning("Because an error occurred while loading/creating a configuration files, " +
                    "information retained in the plugin will not be accurate!");
            playerConfig = new YamlConfiguration(); // create a temporary configuration
            gameConfig = new YamlConfiguration();
        }

        // register event listeners & commands
        getServer().getPluginManager().registerEvents(new GameEventHandler(), this);
        getServer().getPluginCommand("profile").setExecutor(new ProfileCommand());
        getServer().getPluginCommand("startgame").setExecutor(new StartGame());
        getServer().getPluginCommand("stopgame").setExecutor(new StopCommand());
    }

    @Override
    public void onDisable() {
        // saving
        try {
            profiles.forEach(PlayerProfile::saveValues);
            if (saveGameConfig) gameConfig.save(gameConfigFile);
            if (savePlayerConfig) playerConfig.save(playerConfigFile);
        } catch (IOException e) {
            getLogger().warning("An IO exception had occurred: " + e.getMessage());
            getLogger().warning("Because an error occurred while saving configuration file(s), " +
                    "information can't be saved");
        }
    }

    /**
     * Creates a configuration player for the {@link Player}. If there is a configuration
     * entry for that player, then nothing will change.
     *
     * @param player The player
     */
    public void createConfigurationEntry(Player player) {
        PlayerProfile profile = new PlayerProfile(player);
        profile.saveValues(); // creates a configuration entry for the player
                              // If there aren't any
    }

    public boolean isInGame(Player player) {
        return runningGame != null && runningGame.getIncludedPlayers().contains(player);
    }

    public ManhuntGame getRunningGame() {
        return runningGame;
    }

    public void setRunningGame(ManhuntGame game) {
        runningGame = game;
    }


    public PlayerProfile getProfile(Player player) {
        for (PlayerProfile profile : profiles) {
            if (profile.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                profile.refresh(); // auto-refresh
                return profile;
            }
        }
        // no profile set for player
        PlayerProfile profile = new PlayerProfile(player);
        profiles.add(profile);
        return profile;
    }

    public static Speedrunner getInstance() {
        return plugin;
    }

    public YamlConfiguration getPlayerConfig() {
        return playerConfig;
    }

    public YamlConfiguration getGameConfig() {
        return gameConfig;
    }

    public void incrementLargestID() {
        largestGameID++;
    }

    public int getLargestGameID() {
        return largestGameID;
    }

}
