package org.github.brokenearthdev.manhunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.github.brokenearthdev.manhunt.gui.ItemFactory;

import java.io.File;
import java.util.*;

public class ManhuntUtils {

    /**
     * @param game The game object
     * @return Whether the tracker is used at a time where it is allowed or not
     */
    public static boolean gameCheck(ManhuntGame game) {
        return game != null && game.gameOngoing() && !game.gracePeriodOngoing() && game.getOptions().allowTrackers();
    }

    /**
     * @param game   The game
     * @param player The player
     * @return Whether the game is running and the player has a tracker or not
     */
    public static boolean hasTracker(ManhuntGame game, Player player) {
        if (gameCheck(game)) {
            ItemStack[] items = player.getInventory().getContents();
            for (ItemStack stack : items) {
                if (isTracker(stack)) return true;
            }
        }
        return false;
    }

    /**
     * @param stack The item to check
     * @return Whether the item is a tracker or not
     */
    public static boolean isTracker(ItemStack stack) {
        if (stack == null) return false;
        if (stack.getItemMeta() != null) {
            ItemMeta meta = stack.getItemMeta();
            return stack.getType() == Material.COMPASS && meta.getDisplayName().equals(HunterTracker.COMPASS_NAME);
        }
        return false;
    }

    public static String potentialHunterNames(ManhuntGame game, Player compassOwner) {
        List<Player> hun = game.getHunters();
        if (hun.size() == 1) return "none";
        StringBuilder builder = new StringBuilder();
        for (Player player : hun) {
            if (!player.equals(compassOwner))
                builder.append(player.getName()).append(", ");
        }
        String str = builder.toString();
        if (str.length() - 2 < 0) return str;
        String toReturn = str.substring(0, str.length() - 2);
        if (toReturn.length() > 21) toReturn = toReturn.substring(0, 17) + "...";
        return toReturn;
    }

    public static ItemStack createPlayerHead(Player owningPlayer, String name) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setOwningPlayer(owningPlayer);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack createPlayerHead(Player owningPlayer, String name, String lore) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setOwningPlayer(owningPlayer);
        meta.setLore(Collections.singletonList(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack redGlass() {
        return ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(" ").create();
    }

    public static final boolean cmdRunCheck(CommandSender sender, int argsSize, boolean opRequired, String usage, Integer[] args) {
        if (opRequired && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED.toString() + "You need to be op to access this command!");
            return false;
        } else if (!Arrays.asList(args).contains(argsSize)) {
            sender.sendMessage(ChatColor.RED.toString() + "Invalid usage! Use " + usage);
            return false;
        } else {
            return true;
        }
    }

    public static final int findLargestID() {
        Map<String, Object> values = ManhuntPlugin.getInstance().getGameConfig().getValues(true);
        if (values.get("games") == null) return -1;
        Set<String> under = ((MemorySection) values.get("games")).getKeys(false);
        List<Integer> integerList = new ArrayList<>();
        for (String string : under) {
            integerList.add(Integer.valueOf(string.substring(5)));
        }
        if (integerList.size() == 0) return -1;
        else {
            Collections.sort(integerList);
            Collections.reverse(integerList);
            return integerList.get(0) + 1;
        }
    }

    public static boolean deleteFiles(File file) {
        if (file.isDirectory()) {
            File[] sub = file.listFiles();
            for (File file1 : sub) {
                deleteFiles(file1);
            }
        } else return file.delete();
        return file.delete();
    }

    public static List<World> getNormalWorlds() {
        List<World> overworld = new ArrayList<>();
        Bukkit.getWorlds().forEach(world -> {
            if (world.getEnvironment() == World.Environment.NORMAL)
                overworld.add(world);
        });
        return overworld;
    }

    public static List<World> getNetherWorlds() {
        List<World> netherWorlds = new ArrayList<>();
        Bukkit.getWorlds().forEach(world -> {
            if (world.getEnvironment() == World.Environment.NETHER)
                netherWorlds.add(world);
        });
        return netherWorlds;
    }

    public static List<World> getEndWorlds() {
        List<World> endWorlds = new ArrayList<>();
        Bukkit.getWorlds().forEach(world -> {
            if (world.getEnvironment() == World.Environment.THE_END)
                endWorlds.add(world);
        });
        return endWorlds;
    }
}
