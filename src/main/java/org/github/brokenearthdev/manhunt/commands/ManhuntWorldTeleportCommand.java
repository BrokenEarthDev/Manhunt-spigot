package org.github.brokenearthdev.manhunt.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.brokenearthdev.manhunt.ManhuntGame;
import org.github.brokenearthdev.manhunt.ManhuntPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Teleports players to a select world during a manhunt game
 */
public class ManhuntWorldTeleportCommand extends ManhuntCommand {

    public ManhuntWorldTeleportCommand() {
        super("/mhworld [world type] [player] or /mhworld [world type]", "manhunt.game.worldtp", false, 1, 2);
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, String[] args) {
        ManhuntGame game = ManhuntPlugin.getInstance().getRunningGame();
        if (game == null) {
            sender.sendMessage(ChatColor.RED + "There is no running game!");
            return;
        }
        World.Environment environment = parseEnv(args[0]);
        if (environment == null) {
            sender.sendMessage(ChatColor.RED + "Sorry, I didn't understand what you mean by " +
                    ChatColor.LIGHT_PURPLE + args[0]);
            sender.sendMessage(ChatColor.RED + "Possible entries are: " + ChatColor.LIGHT_PURPLE + "normal, nether, " +
                    "or end");
            return;
        }
        if (!(sender instanceof Player) && args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Please specify a player first!");
            return;
        }
        Player target;
        if (args.length == 2) target = Bukkit.getPlayerExact(args[1]);
        else target = (Player) sender;
        if (target == null) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + args[1] + ChatColor.RED + " is not a player (or may be offline!)");
            return;
        }
        World world = getWorld(environment, ManhuntPlugin.getInstance().getRunningGame());
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "Can't find world with type " + ChatColor.LIGHT_PURPLE + args[0]);
            return;
        }
        Location oldLoc = target.getLocation();
        Location newLoc = new Location(world, oldLoc.getX(), oldLoc.getY(), oldLoc.getZ(), oldLoc.getYaw(), oldLoc.getPitch());
        boolean teleported = target.teleport(newLoc);
        if (!teleported) {
            sender.sendMessage(ChatColor.RED + "Something's wrong. Can't teleport player to " + ChatColor.LIGHT_PURPLE + args[0]);
            return;
        }
        if (target.equals(sender))
            sender.sendMessage(ChatColor.GREEN + "Successfully teleported you to " + ChatColor.LIGHT_PURPLE + args[0]);
        else {
            sender.sendMessage(ChatColor.GREEN + "Successfully teleported " + ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.GREEN + " to "
                    + ChatColor.LIGHT_PURPLE + args[0]);
            target.sendMessage(ChatColor.GOLD + "You were teleported to " + args[0]);
        }
    }

    private World getWorld(World.Environment environment, ManhuntGame game) {
        if (game == null) return null;
        if (environment == World.Environment.NETHER) return game.getNetherWorld();
        else if (environment == World.Environment.NORMAL) return game.getMainWorld();
        else return game.getEnd();
    }

    private World.Environment parseEnv(String type) {
        if (type.equalsIgnoreCase("nether") || type.equalsIgnoreCase("the_nether"))
            return World.Environment.NETHER;
        else if (type.equalsIgnoreCase("end") || type.equalsIgnoreCase("the_end"))
            return World.Environment.THE_END;
        else if (type.equalsIgnoreCase("normal") || type.equalsIgnoreCase("main") || type.equalsIgnoreCase("overworld") || type.equalsIgnoreCase("world"))
            return World.Environment.NORMAL;
        return null;
    }

}
