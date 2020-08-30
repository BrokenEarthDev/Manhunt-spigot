package org.github.brokenearthdev.manhunt.impl;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.github.brokenearthdev.manhunt.HunterTracker;
import org.github.brokenearthdev.manhunt.ManhuntGame;
import org.github.brokenearthdev.manhunt.ManhuntPlugin;
import org.github.brokenearthdev.manhunt.ManhuntUtils;
import org.github.brokenearthdev.manhunt.gui.ItemFactory;
import org.github.brokenearthdev.manhunt.gui.buttons.BooleanButton;
import org.github.brokenearthdev.manhunt.gui.buttons.Button;
import org.github.brokenearthdev.manhunt.gui.menu.GameMenu;

import java.util.List;
import java.util.Objects;

public class SimpleTracker implements HunterTracker, Listener {

    private final Player hunter;
    private final ManhuntGame game;

    private static final String COMPASS_NAME = ChatColor.GREEN + ChatColor.BOLD.toString() + "Player Tracker " +
            ChatColor.GOLD + ChatColor.BOLD.toString() + " | " + ChatColor.GREEN
            + ChatColor.BOLD.toString() + "Left or right click";

    private static final ItemStack COMPASS = ItemFactory.create(Material.COMPASS)
            .setName(COMPASS_NAME).setUnbreakable(true).create();

    public SimpleTracker(Player player, ManhuntGame game) {
        this.hunter = player;
        this.game = game;
        Bukkit.getPluginManager().registerEvents(this, ManhuntPlugin.getInstance());
    }

    private boolean loc = true;
    private boolean distance = true;
    private boolean autoCollect = true;
    private boolean actionBar = true;
    private Player tracked;
    private Location lastPortalLocation = null;
    private Location hunterPortalLocation;

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        if (game.gameOngoing() && !game.gracePeriodOngoing()) {
            if (event.getPlayer().equals(game.getSpeedrunner())) lastPortalLocation = event.getFrom();
            if (event.getPlayer().equals(hunter)) hunterPortalLocation = event.getTo();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();
        if (game != null && stack != null && stack.getItemMeta() != null) {
            ItemMeta meta = stack.getItemMeta();
            if (game.gameOngoing() && !game.gracePeriodOngoing() && meta.getDisplayName().equals(COMPASS_NAME)
                    && stack.getType() == Material.COMPASS) {
                openTrackingInterface();
            }
        }
    }

    @EventHandler
    public void onSpawn(PlayerRespawnEvent event) {
        if (event.getPlayer().equals(hunter) && game != null && game.gameOngoing() && !game.gracePeriodOngoing()) {
            event.getPlayer().getInventory().addItem(COMPASS);
            updateTracker();
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (game.gameOngoing() && !game.gracePeriodOngoing() && event.getPlayer().equals(hunter) && autoCollect) {
            if (event.getItemDrop().getItemStack().getType() == Material.COMPASS) {
                ItemStack stack = event.getItemDrop().getItemStack();
                if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().equals(COMPASS_NAME)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (game.gameOngoing() && !game.gracePeriodOngoing() && (event.getPlayer().equals(hunter)) || event.getPlayer().equals(tracked)) {
            updateTracker();
        }
    }

    @Override
    public Player getHunter() {
        return hunter;
    }

    @Override
    public ManhuntGame getGame() {
        return game;
    }

    private void openHuntersInterface() {
        if (!game.gameOngoing() || game.gracePeriodOngoing()) return;
        int slots = 9;
        while (game.getHunters().size() >= slots) {
            slots *= 2;
            if (slots >= 54) {
                slots = 54;
                break;
            }
        }
        GameMenu menu = new GameMenu("Hunters", slots / 9);
        List<Player> gameHunters = game.getHunters();
        for (int i = 0; i < gameHunters.size(); i++) {
            if (!gameHunters.get(i).equals(hunter)) {
                int finalI = i;
                menu.setButton(new Button(i, ManhuntUtils.createPlayerHead(gameHunters.get(i),
                        ChatColor.AQUA + gameHunters.get(i).getName())).addAction(e -> {
                    setTracked(gameHunters.get(finalI));
                    hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
                }));
            }
        }
        menu.setButton(new Button(slots - 1, ItemFactory.create(Material.ARROW).setName(ChatColor.GREEN + "Previous Page").create())
                .addAction(e -> openTrackingInterface()));
        menu.setScenery(ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(" ").create());
        menu.display(hunter);
    }

    public void openTrackersInterface() {
        if (game.gracePeriodOngoing() || !game.gameOngoing()) return;
        int slots = 9;
        List<HunterTracker> trackers = game.getHunterTrackers();
        while (trackers.size() >= slots) {
            slots *= 2;
            if (slots >= 54) {
                slots = 54;
                break;
            }
        }
        GameMenu menu = new GameMenu("Your Trackers", slots / 9);
        for (int i = 0; i < trackers.size(); i++) {
            if (!trackers.get(i).getHunter().equals(hunter) && Objects.equals(hunter, trackers.get(i).getTrackedPlayer())) {
                menu.setButton(new Button(i, ManhuntUtils.createPlayerHead(trackers.get(i).getHunter(), ChatColor.AQUA + trackers.get(i).getHunter().getName())));
            }
        }
        menu.setButton(new Button(slots - 1, ItemFactory.create(Material.ARROW).setName(ChatColor.GREEN + "Previous Page").create())
                .addAction(e -> openTrackingInterface()));
        menu.setScenery(ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(" ").create());
        menu.display(hunter);
    }

    @Override
    public void openTrackingInterface() {
        if (!game.gameOngoing() || game.gracePeriodOngoing()) return;
        GameMenu menu = new GameMenu("Tracker Settings", 6);
        menu.setButton(new Button(4, ItemFactory.create(Material.COMPASS).setName(ChatColor.YELLOW +
                "Tracking Settings").create()));
        menu.setButton(new Button(19, ItemFactory.create(ManhuntUtils.createPlayerHead(game.getHunters().get(0),
                ChatColor.GREEN + "Track Hunter(s)")).addLoreLine(ChatColor.GREEN + "Potential Hunter(s) Include(s): "
                + ChatColor.RED + ManhuntUtils.potentialHunterNames(game, hunter)).create()).addAction((e) -> {
            this.openHuntersInterface();
        }));
        String text2 = game.getSpeedrunner().equals(tracked) ? ChatColor.RED + "You're Already Tracking a Speedrunner"
                : ChatColor.GREEN + "Track Speedrunner";
        menu.setButton(new Button(20, ManhuntUtils.createPlayerHead(tracked, text2)).addAction(e -> {
            if (game.getSpeedrunner().equals(tracked)) {
                hunter.sendMessage(ChatColor.RED + "The speedrunner is already being tracked!");
                hunter.playSound(hunter.getLocation(), Sound.ENTITY_SKELETON_DEATH, 100, 100);
            } else {
                setTracked(game.getSpeedrunner());
                hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
                e.setCurrentItem(ManhuntUtils.createPlayerHead(tracked, ChatColor.RED + "You're Already Tracking a Speedrunner"));
            }
        }));
        String text3 = tracked == null ? ChatColor.RED + "You're Already Tracking Spawn" : ChatColor.GREEN + "Track Spawn";
        menu.setButton(new Button(21, ItemFactory.create(Material.GHAST_SPAWN_EGG).setName(text3).create())
                .addAction(e -> {
                    if (!loc) {
                        hunter.sendMessage(ChatColor.RED + "Please track location first, and not health!");
                        hunter.playSound(hunter.getLocation(), Sound.ENTITY_SKELETON_DEATH, 100, 100);
                    } else {
                        if (tracked == null) {
                            hunter.sendMessage(ChatColor.RED + "You're already tracking spawn!");
                            hunter.playSound(hunter.getLocation(), Sound.ENTITY_SKELETON_DEATH, 100, 100);
                        } else {
                            e.setCurrentItem(ItemFactory.create(Material.GHAST_SPAWN_EGG)
                                    .setName(ChatColor.RED + "You're Already Tracking Spawn").create());
                            setTrackSpawn();
                            hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
                        }
                    }
                }));
        menu.setButton(new BooleanButton(23, actionBar, (event, change) -> {
            actionBar = change;
            hunter.sendMessage(change ? ChatColor.GREEN + "You can now see info in the actionbar" :
                    ChatColor.RED + "You can't see info in the actionbar");
            hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
        }, ItemFactory.create(Material.REDSTONE_TORCH)
                .setName(ChatColor.GREEN + "Actionbar: ENABLED").addGlowEffect(true).create(), ItemFactory.create(Material.LEVER)
                .setName(ChatColor.RED + "Actionbar: DISABLED").create()));


        menu.setButton(new BooleanButton(24, loc, (event, change) -> {
            loc = change;
            if (loc) hunter.sendMessage(ChatColor.GREEN + "Tracking location");
            else hunter.sendMessage(ChatColor.GREEN + "Tracking health");
            hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
        }, ItemFactory.create(Material.ARROW)
                .setName(ChatColor.RED + "Tracked: location").create(), ItemFactory.create(Material.APPLE)
                .setName(ChatColor.GREEN + "Tracked: health").create()));

        menu.setButton(new BooleanButton(25, autoCollect, (event, aBoolean) -> {
            autoCollect = aBoolean;
            hunter.sendMessage(ChatColor.GREEN + "Auto collect has been " + (aBoolean ? "enabled" : "disabled"));
            hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
        }, ItemFactory.create(
                Material.DIAMOND_PICKAXE).setName(ChatColor.GREEN + "Auto Collect: ENABLED").addGlowEffect(true).create(),
                ItemFactory.create(Material.WOODEN_PICKAXE).setName(ChatColor.RED + "Auto Collect: DISABLED")
                        .create()));

        menu.setButton(new BooleanButton(28, distance, (event, aBoolean) -> {
            distance = aBoolean;
            hunter.sendMessage(ChatColor.GREEN + "Tracking " + (aBoolean ? "distance" : "coordinates"));
            hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
        }, ItemFactory.create(Material.RAIL)
                .setName(ChatColor.GREEN + "Location Tracked: DISTANCE").create(), ItemFactory.create(Material.MAP)
                .setName(ChatColor.GREEN + "Location Tracked: COORDINATES").create()));
        menu.setButton(new Button(33, ItemFactory.create(Material.BOW).setName(ChatColor.GREEN + "Your Trackers").create())
                .addAction(event -> openTrackersInterface()));
        ItemStack redGlass = ManhuntUtils.redGlass();
        menu.setButton(new Button(29, redGlass));
        menu.setButton(new Button(30, redGlass));
        menu.setButton(new Button(32, redGlass));
        menu.setButton(new Button(34, redGlass));
        //  menu.setScenery(ItemFactory.create(Material.BLACK_STAINED_GLASS_PANE).setName(" ").create());
        menu.display(hunter);
    }

    @Override
    public void setTrackingType(boolean locationOrHealth) {
        loc = locationOrHealth;
    }

    @Override
    public void setTrackingDistance(boolean distanceOrCoordinates) {
        distance = distanceOrCoordinates;
    }

    @Override
    public void setAutoCollect(boolean autoCollect) {
        this.autoCollect = autoCollect;
    }

    @Override
    public void setTracked(Player player) {
        if (!player.equals(hunter))
            this.tracked = player;
    }

    @Override
    public void setTrackSpawn() {
        tracked = null;
    }

    @Override
    public boolean getTrackingType() {
        return loc;
    }

    @Override
    public boolean isTrackingDistance() {
        return distance;
    }

    @Override
    public boolean isAutoCollect() {
        return autoCollect;
    }

    @Override
    public Player getTrackedPlayer() {
        return tracked;
    }

    @Override
    public boolean trackingSpawn() {
        return tracked == null;
    }

    @Override
    public void updateTracker() {
        if (!game.gameOngoing() || game.gracePeriodOngoing()) return;
        Location or = (tracked == null) ? game.getMainWorld().getSpawnLocation() : tracked.getLocation();
        Location location = or;
        if (!or.getWorld().equals(hunter.getWorld())) {
            if (hunter.getWorld().equals(game.getMainWorld())) {
                // track portal
                location = lastPortalLocation;
            }
            if (!hunter.getWorld().equals(game.getMainWorld())) {
                // track hunter portal
                location = hunterPortalLocation;
            }
        }
        if (location == null)
            location = or; // backup location
        hunter.setCompassTarget(location);
        if (actionBar) {
            if (!loc && tracked != null) {
                hunter.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                        ChatColor.GREEN + ChatColor.BOLD.toString() + "Player Tracker" + ChatColor.GOLD + ChatColor.BOLD.toString()
                        + " | " + ChatColor.GREEN + ChatColor.BOLD.toString() + tracked.getName() + ": " + ((int) tracked.getHealth())
                ));
                return;
            }
            int x = (int) location.getX();
            int y = (int) location.getY();
            int z = (int) location.getZ();
            String space = or.getWorld().equals(hunter.getWorld()) ? "     " : "   ";
            String end = or.getWorld().equals(hunter.getWorld()) ? "" : space + ChatColor.AQUA + ChatColor.BOLD + "World: " + or.getWorld().getName();
            if (distance){
                x = (int) (location.getX() - hunter.getLocation().getX());
                y = (int) (location.getY() - hunter.getLocation().getY());
                z = (int) (location.getZ() - hunter.getLocation().getZ());
            }
            hunter.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    ChatColor.GREEN + ChatColor.BOLD.toString() + "Player Tracker" + ChatColor.GOLD +
                            ChatColor.BOLD.toString() + " | " + ChatColor.RED + ChatColor.BOLD.toString()
                            + "X: " + x + space + ChatColor.BLUE + ChatColor.BOLD.toString() + "Y: " + y + space + ChatColor.GREEN
                            + ChatColor.BOLD + "Z: " + z + end
            ));
        }
    }

    @Override
    public void giveCompass() {
        hunter.getInventory().addItem(COMPASS);
    }
}
