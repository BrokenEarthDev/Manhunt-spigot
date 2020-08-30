package org.github.brokenearthdev.manhunt.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.github.brokenearthdev.manhunt.HunterTracker;
import org.github.brokenearthdev.manhunt.ManhuntGame;
import org.github.brokenearthdev.manhunt.ManhuntPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Gives compass to hunters if, for some reason, the hunter doesn't have
 * the compass.
 */
public class ManhuntTrackerCommand extends ManhuntCommand {

    public ManhuntTrackerCommand() {
        super("/mhtracker", null, true, 0);
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, String[] args) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (game != null && game.gameOngoing() && !game.gracePeriodOngoing()) {
            Player player = (Player) sender;
            if (!game.getHunters().contains(player)) {
                player.sendMessage(ChatColor.RED + "You need to be the hunter to get the tracker!");
                return;
            }
            if (!game.getOptions().allowTrackers()) {
                player.sendMessage(ChatColor.RED + "Trackers are disabled in this game!");
                return;
            }
            ItemStack[] contents = player.getInventory().getContents();
            for (ItemStack item : contents) {
                if (item.hasItemMeta()) {
                    String displayName = item.getItemMeta().getDisplayName();
                    if (displayName.equalsIgnoreCase(HunterTracker.COMPASS_NAME)) {
                        player.sendMessage(ChatColor.RED + "You already have a tracker in your inventory!");
                        return;
                    }
                }
            }
            HunterTracker playerTracker = game.getTrackerFor(player);
            if (playerTracker == null) {
                player.sendMessage(ChatColor.RED + "Can't find a tracker for you!");
                return;
            }
            playerTracker.giveCompass();
            player.sendMessage(ChatColor.GREEN + "Done! You now have your tracker.");
        } else if (game == null || !game.gameOngoing())
            sender.sendMessage(ChatColor.RED + "A game isn't running right now!");
        else if (game.gracePeriodOngoing()) sender.sendMessage(ChatColor.RED + "You are currently in a grace period!");

    }
}
