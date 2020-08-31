package org.github.brokenearthdev.manhunt;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.brokenearthdev.manhunt.impl.AdvancedTracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * An event handler for the game
 */
public class GameEventHandler implements Listener {

    private static final Map<ManhuntGame, HashMap<UUID, Boolean>> OFFLINE_PLAYERS = new HashMap<>();

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
    public void onDragonKill(EntityDeathEvent event) {
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

    private static void createRunnable(ManhuntGame game, OfflinePlayer player) {
        final int[] minsLeft = {5};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (game == null || !game.gameOngoing()) {
                    this.cancel(); // game stopped
                    return;
                }
                minsLeft[0]--;
                if (minsLeft[0] <= 0) {
                    this.cancel();
                    List<Player> incl = game.getIncludedPlayers();
                    incl.forEach(p -> {
                        if (incl.size() >= 2) {
                            p.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() +
                                    ChatColor.GREEN + " has been excluded from the game");
                        } else if (incl.size() == 1) {
                            Player index0 = incl.get(0);
                            boolean hunter = game.getHunters().contains(index0);
                            game.announceWin(hunter);
                        } else {
                            // no players
                            try {
                                game.stopGame(); // stops game
                            } catch (Exception e) {
                                ManhuntPlugin.getInstance().getLogger().warning("An error had occurred while stopping game");
                                ManhuntPlugin.getInstance().getLogger().warning("Attempting to kill game...");
                                ManhuntPlugin.getInstance().setRunningGame(null);
                                ManhuntPlugin.getInstance().getLogger().info("Successfully killed game");
                            }
                        }
                        if (p.equals(game.getSpeedrunner())) game.announceWin(true);
                    });
                    HashMap<UUID, Boolean> map = OFFLINE_PLAYERS.get(game);
                    if (map != null) {
                        map.remove(player.getUniqueId());
                        OFFLINE_PLAYERS.put(game, map);
                    }
                }
            }
        }.runTaskTimer(ManhuntPlugin.getInstance(), 60 * 20, 60 * 20);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (game != null && game.getIncludedPlayers().contains(event.getPlayer()) && game.gameOngoing()) {
            // player left while the game is running
            game.getIncludedPlayers().remove(event.getPlayer());
            if (game.getIncludedPlayers().size() >= 2)
                event.setQuitMessage(ChatColor.GOLD + event.getPlayer().getName() +
                        ChatColor.GREEN + " has to join within 5 minutes or the player will be excluded!");
            else event.setQuitMessage(ChatColor.GOLD + event.getPlayer().getName()
                    + ChatColor.GREEN + " has to join within 5 minutes or the game will be stopped!");
            HashMap<UUID, Boolean> otherOfflinePlayers = OFFLINE_PLAYERS.get(game);
            if (otherOfflinePlayers != null) {
                otherOfflinePlayers.put(event.getPlayer().getUniqueId(), game.getHunters().contains(event.getPlayer()));
            } else {
                HashMap<UUID, Boolean> map = new HashMap<>();
                map.put(event.getPlayer().getUniqueId(), game.getHunters().contains(event.getPlayer()));
                OFFLINE_PLAYERS.put(game, map);
            }
            if (game.getHunters().contains(event.getPlayer())) {
                HunterTracker tracker = game.getTrackerFor(event.getPlayer());
                if (tracker != null)
                    game.getHunterTrackers().remove(tracker);
            }
            game.getHunters().remove(event.getPlayer());
            List<HunterTracker> trackersList = game.getHunterTrackers();
            if (trackersList != null) {
                trackersList.forEach(list -> {
                    if (list.getTrackedPlayer() != null && list.getTrackedPlayer().equals(event.getPlayer())) {
                        // this player is tracked
                        list.getHunter().sendMessage(ChatColor.GOLD + "Because the tracked player had left the game, " +
                                "spawn will be tracked");
                        list.setTrackSpawn();
                    }
                });
            }
            createRunnable(game, event.getPlayer());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (game != null && game.gameOngoing()) {
            // check if player among offline players
            Player player = event.getPlayer();
            HashMap<UUID, Boolean> map = OFFLINE_PLAYERS.get(game);
            if (map != null) {
                Boolean bool = map.get(player.getUniqueId());
                if (bool != null) {
                    // then player in game
                    game.getIncludedPlayers().forEach(p -> p.sendMessage(ChatColor.GREEN + player.getName() + "" +
                            " has rejoined the game"));
                    game.getIncludedPlayers().add(player);
                    if (bool) {
                        game.getHunters().add(player);
                        game.getHunterTrackers().add(new AdvancedTracker(event.getPlayer(), game));
                    }
                    // else, player is a speedrunner
                }
            }
        }
    }

}
