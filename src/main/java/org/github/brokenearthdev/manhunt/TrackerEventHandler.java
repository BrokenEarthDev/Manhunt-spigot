package org.github.brokenearthdev.manhunt;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.github.brokenearthdev.manhunt.impl.AdvancedTracker;

import static org.github.brokenearthdev.manhunt.ManhuntUtils.*;

/**
 * An event handler for trackers
 */
public class TrackerEventHandler implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (gameCheck(game)) {
            Player interactor = event.getPlayer();
            if (isTracker(event.getItem())) {
                HunterTracker instance = game.getTrackerFor(interactor);
                if (instance != null) instance.openTrackingInterface();
            }
        }
    }

    @EventHandler
    public void onCollect(EntityPickupItemEvent event) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (gameCheck(game)) {
            if (isTracker(event.getItem().getItemStack())) {
                if (!(event.getEntity() instanceof Player)) {
                    event.setCancelled(true);
                    return;
                }
                Player player = (Player) event.getEntity();
                if (!game.getHunters().contains(player)) event.setCancelled(true);
                // check if player has compass in inventory
                for (ItemStack contents : player.getInventory()) {
                    if (isTracker(contents)) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (gameCheck(game)) {
            ItemStack stack = event.getCurrentItem();
            if (isTracker(stack)) {
                HumanEntity entity = event.getWhoClicked();
                if (!(entity instanceof Player)) {
                    event.setCancelled(true);
                    return;
                }
                Player player = (Player) event.getWhoClicked();
                boolean hunter = game.getHunters().contains(player);
                if (hunter && hasTracker(game, player)) {
                    event.setCancelled(true);
                } else if (!hunter) event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (gameCheck(game)) {
            game.getHunterTrackers().forEach(tracker -> {
                Player tracked = tracker.getTrackedPlayer();
                if (tracked != null && tracked.equals(event.getPlayer())) {
                    // update the trackers
                    AdvancedTracker advancedTracker = (AdvancedTracker) tracker;
                    advancedTracker.setTrackedPortalLocation(event.getFrom());
                }
            });
            if (game.getHunters().contains(event.getPlayer())) {
                AdvancedTracker tracker = (AdvancedTracker) game.getTrackerFor(event.getPlayer());
                if (tracker != null) tracker.setPersonalPortalLocation(event.getTo());
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (gameCheck(game)) {
            if (game.getIncludedPlayers().contains(event.getPlayer())) {
                HunterTracker tracker = game.getTrackerFor(event.getPlayer());
                if (tracker != null) {
                    tracker.updateTracker();
                }
                // update other trackers
                game.getHunterTrackers().forEach(hunterTracker -> {
                    Player tracked = hunterTracker.getTrackedPlayer();
                    if (tracked != null && tracked.equals(event.getPlayer()))
                        hunterTracker.updateTracker();
                });
            }
        }
    }

    @EventHandler
    public void onSpawn(PlayerRespawnEvent event) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (gameCheck(game)) {
            HunterTracker tracker = game.getTrackerFor(event.getPlayer());
            if (tracker != null)
                tracker.giveCompass();
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (gameCheck(game)) {
            HunterTracker tracker = game.getTrackerFor(event.getPlayer());
            if (tracker != null) {
                ItemMeta droppedMeta = event.getItemDrop().getItemStack().getItemMeta();
                if (droppedMeta != null && droppedMeta.getDisplayName().equals(HunterTracker.COMPASS_NAME)
                        && tracker.isAutoCollect()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
