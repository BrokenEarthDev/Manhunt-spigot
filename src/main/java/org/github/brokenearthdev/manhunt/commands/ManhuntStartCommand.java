package org.github.brokenearthdev.manhunt.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.brokenearthdev.manhunt.GameCreator;
import org.github.brokenearthdev.manhunt.ManhuntGame;
import org.github.brokenearthdev.manhunt.ManhuntPlugin;
import org.github.brokenearthdev.manhunt.gui.game.StartMenu;
import org.jetbrains.annotations.NotNull;

/**
 * Manhunt start command handler
 */
public class ManhuntStartCommand extends ManhuntCommand {

    public ManhuntStartCommand() {
        super(ChatColor.RED + "/mhstart", "manhunt.game.start", false, 0);
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, String[] args) {
        if (ManhuntPlugin.getInstance().getRunningGame() != null && ManhuntPlugin.getInstance().getRunningGame().gameOngoing()) {
            sender.sendMessage(ChatColor.RED + "Sorry, a game is running. Make sure to stop the game first and try " +
                    "again.");
            return;
        }
        if (!(sender instanceof Player)) {
            ManhuntGame game = new GameCreator().createGame();
            sender.sendMessage(ChatColor.GREEN + "Starting game...");
            game.startGame();
            return;
        }
        if (sender.hasPermission("manhunt.game.options")) {
            // open a gui for players
            StartMenu menu = new StartMenu();
            menu.display((Player) sender);
        } else {
            ManhuntGame game = new GameCreator().createGame();
            game.startGame();
        }
    }

}
