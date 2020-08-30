package org.github.brokenearthdev.manhunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.github.brokenearthdev.manhunt.commands.ManhuntProfileCommand;
import org.github.brokenearthdev.manhunt.commands.ManhuntStartCommand;
import org.github.brokenearthdev.manhunt.commands.ManhuntStopCommand;
import org.github.brokenearthdev.manhunt.commands.ManhuntTrackerCommand;
import org.github.brokenearthdev.manhunt.gui.menu.GameMenu;
import org.github.brokenearthdev.manhunt.impl.DefaultOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ManhuntPlugin extends JavaPlugin implements Listener {

    private YamlConfiguration playerConfig;
    private YamlConfiguration gameConfig;
    private static final File playerConfigFile = new File("plugins/manhunt/playerConfig.yaml");
    private static final File gameConfigFile = new File("plugins/manhunt/gameConfig.yaml");
    private static final File optionsConfigFile = new File("plugins/manhunt/optionsConfig.yaml");
    private static final File manhuntPluginFolder = new File("plugins/manhunt");
    private static ManhuntPlugin plugin;
    private YamlConfiguration optionsConfig;
    private GameOptions defaultOptions;
    protected static ManhuntGame runningGame;
    private static final HashSet<PlayerProfile> profiles = new HashSet<>();
    private boolean saveConfig = false;

    private int largestGameID = -1;

    public static ManhuntPlugin getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        // loading
        if (!manhuntPluginFolder.exists()) manhuntPluginFolder.mkdir();
        try {
            boolean newOptions = false;
            if (!gameConfigFile.exists()) gameConfigFile.createNewFile();
            if (!playerConfigFile.exists()) playerConfigFile.createNewFile();
            if (!optionsConfigFile.exists())
                newOptions = optionsConfigFile.createNewFile();
            saveConfig = true;
            playerConfig = YamlConfiguration.loadConfiguration(playerConfigFile);
            gameConfig = YamlConfiguration.loadConfiguration(gameConfigFile);
            optionsConfig = YamlConfiguration.loadConfiguration(optionsConfigFile);
            if (newOptions) dumpOptionConfigDefaults();
            largestGameID = ManhuntUtils.findLargestID();
            defaultOptions = new DefaultOptions();
        } catch (IOException e) {
            getLogger().warning("An IO error had occurred: " + e.getMessage());
            getLogger().warning("Because an error occurred while loading/creating a configuration files, " +
                    "information retained in the plugin will not be accurate!");
            playerConfig = new YamlConfiguration(); // create a temporary configuration
            gameConfig = new YamlConfiguration();
        }
        // register event listeners & commands
        getServer().getPluginManager().registerEvents(new GameMenu.MenuListener(), this);
        getServer().getPluginManager().registerEvents(new GameEventHandler(), this);
        getServer().getPluginCommand("mhprofile").setExecutor(new ManhuntProfileCommand());
        getServer().getPluginCommand("mhstart").setExecutor(new ManhuntStartCommand());
        getServer().getPluginCommand("mhstop").setExecutor(new ManhuntStopCommand());
        getServer().getPluginCommand("mhtracker").setExecutor(new ManhuntTrackerCommand());
    }

    @Override
    public void onDisable() {
        // saving
        try {
            profiles.forEach(PlayerProfile::saveValues);
            if (saveConfig) {
                gameConfig.save(gameConfigFile);
                playerConfig.save(playerConfigFile);
            }
        } catch (IOException e) {
            getLogger().warning("An IO exception had occurred: " + e.getMessage());
            getLogger().warning("Because an error occurred while saving configuration file(s), " +
                    "information can't be saved");
        }
        // delete all manhunt worlds
        List<World> manhuntWorlds = new ArrayList<>(Bukkit.getWorlds().size());
        Bukkit.getWorlds().forEach(world -> {
            if (world.getName().startsWith("world_manhunt_") || world.getName().startsWith("nether_manhunt_") ||
                    world.getName().startsWith("end_manhunt_")) manhuntWorlds.add(world);
        });
        manhuntWorlds.forEach(e -> {
            getLogger().info("Found a manhunt world: " + e.getName());
            getLogger().info("Attempting to unload world ---");
            boolean unloaded = Bukkit.getServer().unloadWorld(e, false);
            if (unloaded) getLogger().fine("Unloaded world");
            else getLogger().warning("Can't unload world");
            getLogger().info("Attempting to delete world ---");
            boolean deleted = ManhuntUtils.deleteFiles(e.getWorldFolder());
            if (deleted) getLogger().fine("Deleted world");
            else getLogger().warning("Can't delete world. Make sure to delete it manually.");
        });
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

    private void dumpOptionConfigDefaults() throws IOException {
        optionsConfig.set("allow_grace_period", true);
        optionsConfig.set("grace_period_seconds", 30);
        optionsConfig.set("generator_settings.generate_world", false);
        optionsConfig.set("generator_settings.generate_nether", false);
        optionsConfig.set("generator_settings.generate_end", false);
        optionsConfig.set("allow_hunter_trackers", true);
        optionsConfig.set("players.max_players", -1);
        optionsConfig.set("players.min_players", 2);
        //optionsConfig.set("kick_players_on_end", false);
        optionsConfig.set("dump_game_info_to_config", false);
        optionsConfig.save(optionsConfigFile);

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

    public void killGame(ManhuntGame game) {
        this.setRunningGame(null);
        game.getIncludedPlayers().forEach(player -> player.sendMessage(ChatColor.RED + "The game has been stopped"));
    }

    public YamlConfiguration getPlayerConfig() {
        return playerConfig;
    }

    public YamlConfiguration getGameConfig() {
        return gameConfig;
    }

    public YamlConfiguration getOptionsConfig() {
        return optionsConfig;
    }

    public void incrementLargestID() {
        largestGameID++;
    }

    public int getLargestGameID() {
        return largestGameID;
    }

    public GameOptions getDefaultOptions() {
        return defaultOptions;
    }

}
