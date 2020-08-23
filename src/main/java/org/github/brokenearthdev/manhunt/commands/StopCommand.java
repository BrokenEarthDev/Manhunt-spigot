package org.github.brokenearthdev.manhunt.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.github.brokenearthdev.manhunt.ManhuntGame;
import org.github.brokenearthdev.manhunt.Speedrunner;
import org.github.brokenearthdev.manhunt.SpeedrunnerUtils;

public class StopCommand implements CommandExecutor {

    public boolean onCommand(  CommandSender sender,   Command command,   String label,   String[] args) {
        if (!SpeedrunnerUtils.cmdRunCheck(sender, args.length, true, "/stopgame", new Integer[]{0})) {
            return true;
        } else {
            ManhuntGame game = Speedrunner.getInstance().getRunningGame();
            if (!game.gameOngoing()) {
                sender.sendMessage(ChatColor.RED.toString() + "There are no games you can stop");
                return true;
            } else {
                game.stopGame();
                return true;
            }
        }
    }
}
