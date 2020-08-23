//package org.github.brokenearthdev.manhunt.commands;
//
//import org.bukkit.ChatColor;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.github.brokenearthdev.manhunt.Speedrunner;
//import org.github.brokenearthdev.manhunt.SpeedrunnerUtils;
//
//public class GracePeriod implements CommandExecutor {
//
//    public boolean onCommand(  CommandSender sender,   Command command,   String label, String[] args) {
//        if (!SpeedrunnerUtils.cmdRunCheck(sender, args.length, true, "/graceperiod <seconds>", new Integer[]{1})) {
//            return true;
//        } else if (Speedrunner.getInstance().gameStarted) {
//            sender.sendMessage(ChatColor.RED.toString() + "Sorry, but a game is ongoing. Please try again later");
//            return true;
//        } else {
//            try {
//                String var6 = args[0];
//                int seconds = Integer.parseInt(var6);
//                if (seconds < 0) {
//                    sender.sendMessage(ChatColor.RED.toString() + "Negative numbers are not allowed!");
//                    return true;
//                }
//
//                Speedrunner.getInstance().gracePeriodSeconds = seconds;
//                sender.sendMessage(ChatColor.GREEN.toString() + "Success! The grace period is " + ChatColor.LIGHT_PURPLE + seconds + ChatColor.GREEN + " seconds!");
//            } catch (Exception var8) {
//                if (var8 instanceof NumberFormatException) {
//                    sender.sendMessage(ChatColor.GREEN.toString() + "'" + ChatColor.LIGHT_PURPLE + args[0] + ChatColor.GREEN + "' is not a number!");
//                } else {
//                    sender.sendMessage(ChatColor.RED.toString() + var8.getClass().getName() + ": " + var8.getMessage());
//                }
//            }
//
//            return true;
//        }
//    }
//
//}
