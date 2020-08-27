package org.github.brokenearthdev.manhunt.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.github.brokenearthdev.manhunt.ManhuntPlugin;
import org.github.brokenearthdev.manhunt.PlayerProfile;
import org.github.brokenearthdev.manhunt.SpeedrunnerUtils;
import org.github.brokenearthdev.manhunt.gui.ItemFactory;
import org.github.brokenearthdev.manhunt.gui.buttons.Button;
import org.github.brokenearthdev.manhunt.gui.menu.GameMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Manhunt profile command handler
 */
public class ManhuntProfileCommand extends ManhuntCommand {

    public ManhuntProfileCommand() {
        super("/mhprofile or /mhprofile player", "manhunt.profile", true, 0, 1);
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, String[] args) {
        Player player = args.length == 1 ? Bukkit.getPlayer(args[0]) : (Player) sender;
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Can't find an online player with that name!");
        } else {
            openProfileInterface((Player) sender, player);
        }
    }

    private void openProfileInterface(Player player, Player target) {
        PlayerProfile profile = ManhuntPlugin.getInstance().getProfile(target);
        String kdrString = String.format(ChatColor.GREEN + "Kill/Death ratio: " + ChatColor.RED + "%.2f", ((float) profile.getKills() / (float) profile.getDeaths()));
        GameMenu menu = new GameMenu(ChatColor.GOLD + ChatColor.BOLD.toString() + player.getName() + "'s profile", 6);
        menu.setButton(new Button(4, ItemFactory.create(
                SpeedrunnerUtils.createPlayerHead(target, ChatColor.GREEN + player.getName()))
                .setLore(ChatColor.YELLOW + "Speedrunner Profile").create()));
        menu.setButton(new Button(19, ItemFactory.create(Material.DIAMOND_SWORD).setName(ChatColor.GREEN + "Kills: " + ChatColor.RED + profile.getKills()).create()));
        menu.setButton(new Button(20, ItemFactory.create(Material.REDSTONE).setName(ChatColor.GREEN + "Deaths: " + ChatColor.RED + profile.getDeaths()).create()));
        menu.setButton(new Button(21, ItemFactory.create(Material.EMERALD).setName(ChatColor.GREEN + "Total Games: " + ChatColor.RED + profile.getTotalGamesPlayed()).create()));
        menu.setButton(new Button(23, ItemFactory.create(Material.BOW).setName(ChatColor.GREEN + "Times hunter: " + ChatColor.RED + profile.getTimesHunter()).create()));
        menu.setButton(new Button(24, ItemFactory.create(Material.ENDER_EYE).setName(ChatColor.GREEN + "Times speedrunner: " + ChatColor.RED + profile.getTimesSpeedrunner()).create()));
        menu.setButton(new Button(25, ItemFactory.create(Material.DIAMOND).setName(String.format(ChatColor.GREEN + "Average time survived: " + ChatColor.RED + "%.2f", profile.getAverageTimeSurvived())).create()));
        menu.setButton(new Button(28, ItemFactory.create(Material.RAIL).setName(kdrString).create()));
        addItems(ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(ChatColor.RED + "Empty").create(),
                Arrays.asList(29, 30, 32, 33, 34, 37, 38, 39, 41, 42, 43), menu);
        menu.display(player);
    }

    private void addItems(ItemStack stack, List<Integer> slots, GameMenu menu) {
        slots.forEach(n -> menu.setButton(new Button(n, stack)));
    }
}
