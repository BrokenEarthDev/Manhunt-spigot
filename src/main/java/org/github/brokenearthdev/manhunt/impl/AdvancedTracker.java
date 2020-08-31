package org.github.brokenearthdev.manhunt.impl;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.github.brokenearthdev.manhunt.HunterTracker;
import org.github.brokenearthdev.manhunt.ManhuntGame;
import org.github.brokenearthdev.manhunt.ManhuntUtils;
import org.github.brokenearthdev.manhunt.gui.ItemFactory;
import org.github.brokenearthdev.manhunt.gui.game.CompassTrackingMenu;
import org.github.brokenearthdev.manhunt.gui.menu.ListPaginatedMenu;

import java.util.ArrayList;
import java.util.List;

public class AdvancedTracker implements HunterTracker {

    private final Player hunter;
    private final ManhuntGame game;
    private final CompassTrackingMenu menu;

    private static final ItemStack COMPASS = ItemFactory.create(Material.COMPASS)
            .setName(COMPASS_NAME).setUnbreakable(true).create();
    private Location hunterPortalLocation = null;

    private boolean loc = true;
    private boolean distance = true;
    private boolean autoCollect = true;
    private boolean actionBar = true;
    private Player tracked;
    private Location lastPortalLocation = null;

    public AdvancedTracker(Player player, ManhuntGame game) {
        this.hunter = player;
        this.game = game;
        this.tracked = game.getSpeedrunner();
        this.menu = new CompassTrackingMenu(this);
    }

    public void setTrackedPortalLocation(Location sr) {
        lastPortalLocation = sr;
    }

    public void setPersonalPortalLocation(Location hpl) {
        hunterPortalLocation = hpl;
    }

    @Override
    public Player getHunter() {
        return hunter;
    }

    @Override
    public ManhuntGame getGame() {
        return game;
    }

    public void openHuntersInterface() {
        if (!game.gameOngoing() || game.gracePeriodOngoing()) return;
        ArrayList<Player> huntersCopy = new ArrayList<>(game.getHunters());
        huntersCopy.remove(hunter);
        ListPaginatedMenu<Player> listPaginatedMenu = new ListPaginatedMenu<>("Hunters",
                huntersCopy, player -> ManhuntUtils.createPlayerHead(player, ChatColor.AQUA + player.getName()));
        listPaginatedMenu.addOnItemClick(((player, event) -> {
            setTracked(player);
            hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
            hunter.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " is now tracked!");
        }));
        listPaginatedMenu.setReturntoGui(menu);
        listPaginatedMenu.display(hunter);
    }


    public void openTrackersInterface() {
        if (game.gracePeriodOngoing() || !game.gameOngoing()) return;
        List<HunterTracker> hunterTrackers = game.getHunterTrackers();
        hunterTrackers.remove(this);
        List<Player> trackers = new ArrayList<>();
        hunterTrackers.forEach(h -> {
            if (h.getTrackedPlayer().equals(hunter)) trackers.add(h.getHunter());
        });
        ListPaginatedMenu<Player> menu = new ListPaginatedMenu<>("Your Trackers",
                trackers, (player) -> ManhuntUtils.createPlayerHead(player, ChatColor.AQUA + player.getName()));
        menu.setReturntoGui(this.menu);
        menu.display(hunter);
    }

    @Override
    public void openTrackingInterface() {
        if (!game.gameOngoing() || game.gracePeriodOngoing()) return;
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
    public boolean isIncludeInfoAtActionbar() {
        return actionBar;
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
    public void setIncludeInfoAtActionbar(boolean include) {
        actionBar = include;
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
