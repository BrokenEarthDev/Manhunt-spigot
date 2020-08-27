package org.github.brokenearthdev.manhunt.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.github.brokenearthdev.manhunt.ManhuntGame;
import org.github.brokenearthdev.manhunt.ManhuntPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Manhunt stop command handler
 */
public class ManhuntStopCommand extends ManhuntCommand {

    public ManhuntStopCommand() {
        super("/mhstop", "manhunt.game.stop", false, 0);
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, String[] args) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (game == null || !game.gameOngoing()) {
            sender.sendMessage(ChatColor.RED.toString() + "There are no games you can stop");
        } else {
            try {
                sender.sendMessage(ChatColor.GREEN + "Stopping game...");
                game.stopGame();
                sender.sendMessage(ChatColor.GREEN + "Successfully stopped game");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "An issue had occurred while attempting to stop a game.");
                ManhuntPlugin.getInstance().getLogger().severe("Can't stop game " + game);
                ManhuntPlugin.getInstance().getLogger().info("Attempting to stop a game had produced an " + "exception: " + e.getMessage());
                sender.sendMessage(ChatColor.RED + "Attempting to kill game...");
                ManhuntPlugin.getInstance().getLogger().info("Attempting to kill game...");
                ManhuntPlugin.getInstance().killGame(game);
                String msg1 = "Successfully killed game. Game info won't be dumped " +
                        "to config (if option is enabled) nor will any world get deleted.";
                String msg2 = "If there are bugs, feel free to manually reload the " +
                        "server";
                sender.sendMessage(ChatColor.GREEN + msg1);
                sender.sendMessage(ChatColor.GREEN + msg2);
                ManhuntPlugin.getInstance().getLogger().fine(msg1);
                ManhuntPlugin.getInstance().getLogger().fine(msg2);
            }
        }
    }
}
