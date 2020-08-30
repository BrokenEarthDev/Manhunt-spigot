package org.github.brokenearthdev.manhunt;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class GameEventHandler implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (game != null && game.gameOngoing() && game.getIncludedPlayers().contains(dead)) {
            // in game
            PlayerProfile profile = ManhuntPlugin.getInstance().getProfile(dead);
            profile.setDeaths(profile.getDeaths() + 1);

            // check if speed runner
            if (game.getSpeedrunner().equals(dead)) {
                game.announceWin(true);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
            // check whether speedrunner wins or not
            ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
            if (game != null && game.gameOngoing()) {
                Player killer = event.getEntity().getKiller();
                if (killer != null && killer.getWorld().equals(game.getEnd())) game.announceWin(false);
            }
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
            if (game != null && killer != null && game.getIncludedPlayers().contains(killer) && game.gameOngoing()) {
                // in game
                PlayerProfile profile = ManhuntPlugin.getInstance().getProfile(killer);
                if (event.getEntity() instanceof Player) profile.setKills(profile.getKills() + 1);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            // player damaged
            ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
            if (game != null) {
                if (ManhuntPlugin.getInstance().isInGame((Player) event.getEntity()) && game.gracePeriodOngoing()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPortalEnter(PlayerPortalEvent event) {
        if (ManhuntPlugin.getInstance().getRunningGame() != null) {
            ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
            if (game != null) {
                if (game.gameOngoing() && game.getIncludedPlayers().contains(event.getPlayer())) {
                    Location to = event.getTo();
                    if (to != null && to.getWorld() != null) {
                        World world = to.getWorld();
                        World instead = null;
                        switch (world.getEnvironment()) {
                            case NETHER:
                                instead = game.getNetherWorld();
                                break;
                            case THE_END:
                                instead = game.getEnd();
                                break;
                            case NORMAL:
                                instead = game.getMainWorld();
                                break;
                        }
                        if (instead == null) {
                            ManhuntPlugin.getInstance().getLogger().warning("Unable to warp player. The target world is " +
                                    "null!");
                            return;
                        }
                        Location newLocation = new Location(instead, to.getX(), to.getY(), to.getZ());
                        event.setTo(newLocation);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDragonKill(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.ENDER_DRAGON) {
            ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
            if (game != null && game.gameOngoing()) {
                game.announceWin(false);
            }
        }
    }

}
