package org.github.brokenearthdev.manhunt;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.github.brokenearthdev.manhunt.revxrsal.ItemFactory;

import java.util.*;

public class SpeedrunnerUtils {

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
        SkullMeta meta = (SkullMeta) stack;
        meta.setDisplayName(name);
        meta.setOwningPlayer(owningPlayer);
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
        Map<String, Object> values = Speedrunner.getInstance().getGameConfig().getValues(true);
        if (values.get("games") == null) return -1;
        Set<String> under = ((Map<String, Object>) values.get("games")).keySet();
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

}
