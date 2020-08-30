package org.github.brokenearthdev.manhunt.gui.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.github.brokenearthdev.manhunt.*;
import org.github.brokenearthdev.manhunt.gui.ItemFactory;
import org.github.brokenearthdev.manhunt.gui.buttons.BooleanButton;
import org.github.brokenearthdev.manhunt.gui.buttons.Button;
import org.github.brokenearthdev.manhunt.gui.menu.GameMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * The game start menu.
 */
public class StartMenu extends GameMenu {

    private final GameOptions options;
    private final ButtonFunctions functions = new ButtonFunctions();
    // BUTTONS
    private Button runnerSelectorButton, randomSelectorButton, hunterSelectorButton, includedSelectorButton = null;
    private Button startButton, closeButton, resetButton = null;
    private Button gameSettingsButton, dumpInfoButton, allowTrackersButton, gracePeriodButton = null;
    private Button worldButton, netherWorldButton, endWorldButton = null;
    // FIELDS
    private int gracePeriod;
    private boolean dumpInfo;
    private boolean allowTrackers;
    private List<Player> includedPlayers;
    private List<Player> hunters = new ArrayList<>();
    private Player speedrunner;
    private World world, nether, end;

    /**
     * Initializes a menu object
     */
    public StartMenu() {
        super("Start Menu", 6);
        options = ManhuntPlugin.getInstance().getDefaultOptions();
        reset();
    }


    /**
     * Removes the functionalities of the object (so that it may not affect
     * the functionality of other gui's with the same name and slot).
     */
    public void removeFunctionalities() {
        this.unregisterButtons();
        super.closeActions.clear();
        super.globalActions.clear();
    }

    /**
     * Resets the buttons to the default options
     */
    public void reset() {
        if (!options.isGenerateMainWorld()) world = Bukkit.getWorld("world");
        else world = null;
        if (!options.isGenerateNetherWorld()) nether = Bukkit.getWorld("world_nether");
        else nether = null;
        if (!options.isGenerateEndWorld()) end = Bukkit.getWorld("world_the_end");
        else end = null;
        dumpInfo = options.dumpInfoToConfig();
        allowTrackers = options.allowTrackers();
        gracePeriod = options.gracePeriodSeconds();
        includedPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        initButtons();
        addFunctions();
        this.setButton(gameSettingsButton).setButton(runnerSelectorButton).setButton(randomSelectorButton).setButton(hunterSelectorButton)
                .setButton(includedSelectorButton).setButton(worldButton).setButton(netherWorldButton).setButton(endWorldButton)
                .setButton(dumpInfoButton).setButton(allowTrackersButton).setButton(gracePeriodButton).setButton(startButton)
                .setButton(closeButton).setButton(resetButton);
    }

    /**
     * Creates a game
     */
    private void createGame() {
        GameCreator creator = new GameCreator();
        if (speedrunner != null) creator.setSpeedrunner(speedrunner);
        if (hunters != null && hunters.size() > 0) hunters.forEach(creator::addHunter);
        if (includedPlayers != null && includedPlayers.size() >= 2) {
            includedPlayers.forEach(player -> {
                if (player != null) creator.includePlayer(player);
            });
        }
        if (world == null) creator.generateMainWorld(true);
        else creator.setMainWorld(world);
        if (end == null) creator.generateEnd(true);
        else creator.setEnd(end);
        if (nether == null) creator.generateNether(true);
        else creator.setNetherWorld(nether);
        ManhuntGame game = creator.setGracePeriod(gracePeriod)
                .dumpToConfig(dumpInfo)
                .allowTrackers(allowTrackers)
                .createGame();
        game.startGame();
    }

    /**
     * Adds the functions
     */
    private void addFunctions() {
        functions.addRandomSelectorFunction();
        functions.addHunterSelectorFunction();
        functions.addIncludedSelectorFunction();
        functions.addWorldSelectorFunction();
        functions.addNetherSelectorFunction();
        functions.addEndSelectorFunction();
        functions.addStartGameFunction();
        functions.addCloseFunction();
        functions.addResetFunction();
        functions.addRunnerSelectorFunction();
        functions.addGracePeriodFunction();
    }

    /**
     * Unregisters the buttons' {@link Button#getOnClick()} consumers
     */
    private void unregisterButtons() {
        runnerSelectorButton.getOnClick().clear();
        randomSelectorButton.getOnClick().clear();
        hunterSelectorButton.getOnClick().clear();
        includedSelectorButton.getOnClick().clear();
        startButton.getOnClick().clear();
        closeButton.getOnClick().clear();
        resetButton.getOnClick().clear();
        gameSettingsButton.getOnClick().clear();
        dumpInfoButton.getOnClick().clear();
        allowTrackersButton.getOnClick().clear();
        gracePeriodButton.getOnClick().clear();
        worldButton.getOnClick().clear();
        netherWorldButton.getOnClick().clear();
        endWorldButton.getOnClick().clear();
    }

    private ItemStack createItem(Material material, String name) {
        return ItemFactory.create(material).setName(name).create();
    }

    private ItemStack createItem(Material material, String name, String line) {
        return ItemFactory.create(material).setName(name).setLore(line).create();
    }

    private ItemStack appropriateItemGracePeriod() {
        ItemStack enabled = createItem(Material.CLOCK,
                ChatColor.GREEN + "Grace Period: ENABLED", ChatColor.GREEN + "Time: " + ChatColor.RED +
                        gracePeriod + " seconds");
        ItemStack disabled = createItem(Material.CLOCK, ChatColor.GREEN + "Grace" +
                " Period: " + ChatColor.RED + "DISABLED");
        return gracePeriod > 0 ? enabled : disabled;
    }

    /**
     * Initializes the buttons
     */
    private void initButtons() {
        boolean generateNewWorld = options.isGenerateMainWorld();
        boolean generateNetherWorld = options.isGenerateNetherWorld();
        boolean generateEndWorld = options.isGenerateEndWorld();
        String normalWorldText = ChatColor.GREEN + "Overworld: " + (generateNewWorld ? "GENERATE NEW" : world.getName().toUpperCase());
        String netherWorldText = ChatColor.GREEN + "Nether: " + (generateNetherWorld ? "GENERATE NEW" : nether.getName().toUpperCase());
        String endWorldText = ChatColor.GREEN + "End: " + (generateEndWorld ? "GENERATE NEW" : end.getName().toUpperCase());
        gameSettingsButton = new Button(4, createItem(Material.EMERALD, ChatColor.AQUA + "Game Settings"));
        runnerSelectorButton = new Button(24, createItem(Material.ARROW, "Select Speedrunner"));
        randomSelectorButton = new Button(25, createItem(Material.GHAST_SPAWN_EGG, "Select Random"));
        hunterSelectorButton = new Button(23, createItem(Material.DIAMOND_SWORD, ChatColor.AQUA + "Select Hunter"));
        includedSelectorButton = new Button(15, createItem(Material.REPEATER, "Include/Exclude Players"));
        worldButton = new Button(32, createItem(Material.GRASS_BLOCK, normalWorldText));
        netherWorldButton = new Button(33, createItem(Material.NETHER_BRICK, netherWorldText));
        endWorldButton = new Button(34, createItem(Material.END_STONE, endWorldText));
        dumpInfoButton = new BooleanButton(42, dumpInfo, (event, val) -> {
            dumpInfo = val;
            event.getWhoClicked().sendMessage(ChatColor.GREEN + "Dump info is " + (val ? "enabled" : "disabled"));
        }, createItem(Material.PAPER,
                ChatColor.GREEN + "Dump Game Info: ENABLED"), createItem(Material.PAPER, ChatColor.GREEN + "" +
                "Dump Game Info: " + ChatColor.RED + "DISABLED"));
        allowTrackersButton = new BooleanButton(41, allowTrackers, (event, val) -> {
            allowTrackers = val;
            event.getWhoClicked().sendMessage(ChatColor.GREEN + "Allow trackers is " + (val ? "enabled" : "disabled"));
        }, createItem(Material.COMPASS,
                ChatColor.GREEN + "Allow Trackers: ENABLED"), createItem(Material.COMPASS, ChatColor.GREEN +
                "Allow Trackers: " + ChatColor.RED + "DISABLED"));
        gracePeriodButton = new Button(43, appropriateItemGracePeriod());
        startButton = new Button(20, ItemFactory.create(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Create Game").create());
        closeButton = new Button(38, ItemFactory.create(Material.RED_CONCRETE).setName(ChatColor.RED + "Close and Cancel").create());
        resetButton = new Button(29, ItemFactory.create(Material.WOODEN_AXE).setName(ChatColor.RED + "Reset Options").create());
    }

    /**
     * This class is responsible for adding button functions that aren't instances of
     * {@link BooleanButton}
     */
    private class ButtonFunctions {

        private void addRandomSelectorFunction() {
            // random selector
            randomSelectorButton.addAction(e -> {
                speedrunner = null;
                hunters.clear();
                runnerSelectorButton.setItem(ItemFactory.create(Material.ARROW).setName("Select Speedrunner").create());
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "The selection will be random from the included players. " +
                        "Cleared manually selected players.");
            });
        }

        private void addHunterSelectorFunction() {
            // hunter selector
            hunterSelectorButton.addAction(action -> {
                List<Player> copyList = new ArrayList<>(includedPlayers);
                copyList.remove(speedrunner);
                PlayerSelectorMenu menu = new PlayerSelectorMenu("Hunter Selector", false, copyList, hunters);
                menu.addOnClickToMainPage(event -> {
                    if (menu.getSelected().size() == copyList.size() + 1) {
                        event.getWhoClicked().sendMessage(ChatColor.RED + "All players are selected as hunters. Make sure to" +
                                " deselect at least one players");
                        return;
                    }
                    hunters = menu.getSelected();
                    event.getWhoClicked().sendMessage(ChatColor.GREEN + "Successfully selected hunters");
                    display(event.getWhoClicked());
                });
                menu.display(action.getWhoClicked());
            });
        }

        private void addIncludedSelectorFunction() {
            // included players selector
            includedSelectorButton.addAction(event -> {
                List<Player> playersList = new ArrayList<>(Bukkit.getOnlinePlayers());
                PlayerSelectorMenu menu = new PlayerSelectorMenu("Include/Exclude Players", false,
                        playersList, includedPlayers);
                menu.setSelectMessage(ChatColor.GREEN + "Selected player");
                menu.setUnselectedMessage(ChatColor.RED + "Excluded Player");
                menu.addOnClickToMainPage(action -> {
                    if (includedPlayers.size() < 2) {
                        action.getWhoClicked().sendMessage(ChatColor.RED + "You need at least two players!");
                        return;
                    }
                    StartMenu.this.includedPlayers = menu.getSelected();
                    action.getWhoClicked().sendMessage(ChatColor.GREEN + "Successfully selected players");
                    hunters.forEach(hunter -> {
                        if (!includedPlayers.contains(hunter)) {
                            action.getWhoClicked().sendMessage(ChatColor.GREEN + "Removed " + hunter.getName() + " " +
                                    "from the game because they aren't included");
                            hunters.remove(hunter);
                        }
                    });
                    if (speedrunner != null && !includedPlayers.contains(speedrunner)) {
                        action.getWhoClicked().sendMessage(ChatColor.GREEN + "Removed " + speedrunner.getName() + " " +
                                "from the game because they aren't included");
                        speedrunner = null;
                    }
                    display(event.getWhoClicked());
                });
                menu.display(event.getWhoClicked());
            });
        }

        private void addWorldSelectorFunction() {
            // normal world selector
            worldButton.addAction(action -> {
                WorldSelectorMenu menu = new WorldSelectorMenu("World Selector", StartMenu.this, Material.GRASS_BLOCK, ManhuntUtils.getNormalWorlds(),
                        world, Bukkit.getWorld("world"));
                menu.setSelectedMessage(ChatColor.GREEN + "Successfully selected world!");
                menu.setUnselectedMsg(ChatColor.GREEN + "Successfully unselected world!");
                menu.addOnClickToMain(event -> {
                    if (menu.getSelected() != null || menu.generateNew()) {
                        StartMenu.this.world = menu.getSelected();
                        worldButton.setItem(ItemFactory.create(Material.GRASS_BLOCK)
                                .setName(ChatColor.GREEN + "Overworld: " + ((menu.generateNew() ? "" +
                                        "GENERATE NEW" : world.getName().toUpperCase()))).create());
                    }
                });

                menu.display(action.getWhoClicked());

            });
        }

        private void addNetherSelectorFunction() {
            // nether world selector
            netherWorldButton.addAction(action -> {
                WorldSelectorMenu menu = new WorldSelectorMenu("Nether Selector", StartMenu.this, Material.NETHER_BRICK,
                        ManhuntUtils.getNetherWorlds(), nether, Bukkit.getWorld("world_nether"));
                menu.setSelectedMessage(ChatColor.GREEN + "Successfully selected world!");
                menu.setUnselectedMsg(ChatColor.GREEN + "Successfully unselected world!");
                menu.addOnClickToMain(event -> {
                    if (menu.getSelected() != null || menu.generateNew()) {
                        StartMenu.this.nether = menu.getSelected();
                        netherWorldButton.setItem(ItemFactory.create(Material.NETHERRACK).setName(ChatColor.GREEN + "Nether: " + (menu.generateNew() &&
                                menu.getSelected() == null ? "GENERATE NEW" : nether.getName().toUpperCase())).create());
                        //display(event.getWhoClicked());
                    }
                });
                menu.display(action.getWhoClicked());
            });
        }

        private void addEndSelectorFunction() {
            // end world selector
            endWorldButton.addAction(action -> {
                WorldSelectorMenu menu = new WorldSelectorMenu("End Selector", StartMenu.this, Material.END_STONE,
                        ManhuntUtils.getNetherWorlds(), end, Bukkit.getWorld("world_the_end"));
                menu.setSelectedMessage(ChatColor.GREEN + "Successfully selected world!");
                menu.setUnselectedMsg(ChatColor.GREEN + "Successfully unselected world!");
                menu.addOnClickToMain(event -> {
                    if (menu.getSelected() != null || menu.generateNew()) {
                        StartMenu.this.end = menu.getSelected();
                        endWorldButton.setItem(ItemFactory.create(Material.END_STONE)
                                .setName(ChatColor.GREEN + "End: " + ((menu.generateNew() ? "" +
                                        "GENERATE NEW" : world.getName().toUpperCase()))).create());
                        //display(event.getWhoClicked());
                    }
                });

                menu.display(action.getWhoClicked());

            });
        }

        private void addStartGameFunction() {
            startButton.addAction(cons -> {
                if (includedPlayers == null || includedPlayers.size() < 2) {
                    cons.getWhoClicked().sendMessage(ChatColor.RED + "There should be at least two " +
                            "players to start the game!");
                    return;
                }
                cons.getWhoClicked().closeInventory();
                cons.getWhoClicked().sendMessage(ChatColor.GREEN + "Creating a manhunt game...");
                if (world == null || nether == null || end == null) {
                    cons.getWhoClicked().sendMessage(ChatColor.GREEN + "Because one (or more) world will be " +
                            "generated, this may take up to one minute. The players may experience lag if the " +
                            "server memory isn't sufficient.");
                }
                createGame();
            });
        }

        private void addCloseFunction() {
            // close button
            closeButton.addAction(action -> {
                action.getWhoClicked().sendMessage(ChatColor.RED + "Cancelled game configuration");
                action.getWhoClicked().closeInventory();
            });
        }

        private void addResetFunction() {
            resetButton.addAction(action -> {
                action.getWhoClicked().sendMessage(ChatColor.GREEN + "Resetted options to default!");
                reset();
                display(action.getWhoClicked());
            });
        }

        private void addRunnerSelectorFunction() {
            // runner selector
            runnerSelectorButton.addAction(action -> {
                List<Player> inclCopy = new ArrayList<>(includedPlayers);
                inclCopy.removeAll(hunters);
                List<Player> sel = new ArrayList<>();
                if (speedrunner != null) sel.add(speedrunner);
                PlayerSelectorMenu menu = new PlayerSelectorMenu("Speedrunner Selector", true, inclCopy,
                        sel);
                menu.setUnselectedMessage(ChatColor.GREEN + "Unselected player.");
                menu.addOnClickToMainPage((e) -> {
                    List<Player> selected = menu.getSelected();
                    if (selected.size() > 1)
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You selected more than one speedrunner!");
                    else if (selected.size() == 0) {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You haven't selected any speedrunner. Assuming that you" +
                                " want the speedrunner to be automatically selected, we've directed you to the main start page.");
                        runnerSelectorButton.setItem(ItemFactory.create(Material.ARROW).setName("Select Speedrunner").create());
                        display(e.getWhoClicked());
                        speedrunner = null;
                    } else if (hunters.contains(selected.get(0)))
                        e.getWhoClicked().sendMessage(ChatColor.RED + "The player was" +
                                " selected to be a hunter!");
                    else {
                        speedrunner = selected.get(0);
                        e.getWhoClicked().sendMessage(ChatColor.GREEN + "Success! " + selected.get(0).getName() + " will" +
                                " be the speedrunner!");
                        runnerSelectorButton.setItem(ManhuntUtils.createPlayerHead(speedrunner, "Select Speedrunner"));
                        display(e.getWhoClicked());
                    }
                });
                menu.display(action.getWhoClicked());
            });
        }

        private void addGracePeriodFunction() {
            gracePeriodButton.addAction((event) -> {
                GracePeriodMenu menu = new GracePeriodMenu(gracePeriod);
                menu.addOnClickToBack(e -> {
                    StartMenu.this.gracePeriod = menu.getGracePeriod();
                    gracePeriodButton.setItem(appropriateItemGracePeriod());
                    StartMenu.this.display(e.getWhoClicked());
                });
                menu.display(event.getWhoClicked());
            });
        }
    }

}
