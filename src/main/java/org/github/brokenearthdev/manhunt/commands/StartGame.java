package org.github.brokenearthdev.manhunt.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.github.brokenearthdev.manhunt.ManhuntGame;
import org.github.brokenearthdev.manhunt.GameCreator;
import org.github.brokenearthdev.manhunt.Speedrunner;
import org.github.brokenearthdev.manhunt.SpeedrunnerUtils;

public class StartGame implements CommandExecutor {

    public boolean onCommand(  CommandSender sender,   Command command,   String label,   String[] args) {
        if (SpeedrunnerUtils.cmdRunCheck(sender, args.length, true, "/startgame", new Integer[]{0})) {
            ManhuntGame game = Speedrunner.getInstance().getRunningGame();
            if (game != null && game.gameOngoing()) {
                sender.sendMessage(ChatColor.RED.toString() + "Game already started!");
            } else {
                if (Bukkit.getOnlinePlayers().size() <= 1) {
                    sender.sendMessage(ChatColor.RED + "There should be at least 2 players");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "Generating a world... This may take approximately one minute");
                ManhuntGame newGame = new GameCreator().setGracePeriod(30).generateNether(true).generateMainWorld(true).generateEnd(true).createGame();
                newGame.startGame();
                Speedrunner.getInstance().setRunningGame(newGame);
            }
        }
        return true;
    }

}
