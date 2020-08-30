package org.github.brokenearthdev.manhunt.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A base class for manhunt commands.
 */
public abstract class ManhuntCommand implements CommandExecutor {

    private final List<Integer> args = new ArrayList<>();
    private final String usage;
    private final String permission;
    private final boolean onlyPlayers;

    public ManhuntCommand(String usage, String permission, boolean onlyPlayers, int... args) {
        this.usage = usage;
        for (int argument : args) {
            if (!this.args.contains(argument))
                this.args.add(argument);
        }
        this.permission = permission;
        this.onlyPlayers = onlyPlayers;
    }

    public ManhuntCommand(String usage, int... args) {
        this(usage, null, false, args);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!checkAll(sender, args.length))
            return true;
        handleCommand(sender, args);
        return true;
    }

    /**
     * Checks the permission of the sender. If the sender doesn't have the permission
     * to execute a command, a message will be sent to them.
     *
     * @param sender     The sender
     * @param permission The permission
     * @return Whether the sender has the permission or not
     */
    protected boolean checkPermission(CommandSender sender, String permission) {
        if (permission != null) {
            if (!sender.hasPermission(permission)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to access this command");
                return false;
            }
        }
        return true;
    }

    /**
     * Allows only players with the permission to execute the command. If false, a message
     * will be sent to them.
     *
     * @param sender     The sender
     * @param permission The permission
     * @return Whether the sender is a player and has permission or not
     */
    protected boolean allowOnlyPlayers(CommandSender sender, String permission) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, only players can execute the command!");
            return true;
        }
        return checkPermission(sender, permission);
    }

    /**
     * Allows only players to execute the command. If false, a message will be sent to
     * them.
     *
     * @param sender The sender
     * @return Whether the sender is a player or not
     */
    protected boolean allowOnlyPlayers(CommandSender sender) {
        return allowOnlyPlayers(sender, null);
    }

    /**
     * Checks the arguments and sends an error message if the arguments are wrong.
     *
     * @param sender The sender
     * @param args   The arguments of the command
     * @return Whether the arguments are correct or not
     */
    protected boolean checkArgs(CommandSender sender, int args) {
        if (this.args.size() == 0) return true; // no args size set
        if (!this.args.contains(args)) {
            sender.sendMessage(ChatColor.RED + "Invalid usage!" + ((usage == null) ? "" : " " + usage));
            return false;
        }
        return true;
    }

    private boolean checkAll(CommandSender sender, int args) {
        if (onlyPlayers && !allowOnlyPlayers(sender)) return false;
        if (!checkPermission(sender, permission)) return false;
        return checkArgs(sender, args);
    }

    /**
     * Handles the command
     *
     * @param sender The sender
     * @param args   The arguments
     */
    public abstract void handleCommand(@NotNull CommandSender sender, String[] args);

}

