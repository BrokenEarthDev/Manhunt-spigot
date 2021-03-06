package org.github.brokenearthdev.manhunt.gui.game;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.github.brokenearthdev.manhunt.ManhuntUtils;

import java.util.List;
import java.util.function.Function;

/**
 * A player selector menu
 */
public class PlayerSelectorMenu extends SelectorMenu<Player> {

    private static final Function<Player, ItemStack> OFF_ITEM =
            player -> ManhuntUtils.createPlayerHead(player, ChatColor.GREEN + player.getName() + ChatColor.RED + " (not selected)");
    private static final Function<Player, ItemStack> ON_ITEM =
            player -> ManhuntUtils.createPlayerHead(player, ChatColor.GREEN + player.getName() + " (selected)");

    public PlayerSelectorMenu(String title, List<Player> list, List<Player> selected, boolean onlyOne) {
        super(title, list, selected, onlyOne, OFF_ITEM, ON_ITEM);
        super.setSelectMessage(ChatColor.GREEN + "Success! Player is selected");
        super.setDeselectMessage(ChatColor.GREEN + "Player is deselected");
        super.setOnlyOneAllowedMessage(ChatColor.RED + "Only one player is allowed");
    }

//    private final List<Player> players;
//    private final List<Player> selected;
//    private final List<Consumer<InventoryClickEvent>> onClickToMainPage;
//    private final boolean onlyOne;
//    private String selectMessage, unselectedMessage;
//
//    /**
//     * Creates a new menu.
//     *
//     * @param title   Menu title
//     * @param players The players
//     */
//    public PlayerSelectorMenu(String title, boolean onlyOne, List<Player> players, List<Player> selected) {
//        super(title, (int) Math.ceil(players.size() / 9.0));
//        this.onlyOne = onlyOne;
//        this.players = players;
//        this.selected = selected;
//        onClickToMainPage = new ArrayList<>();
//        // setScenery(ItemFactory.create(Material.BLACK_STAINED_GLASS_PANE).setName(" ").create());
//    }
//
//    @Override
//    public void display(HumanEntity entity, int pageIndex) {
//        addStuff();
//        super.display(entity, pageIndex);
//    }
//
//    @Override
//    public void display(HumanEntity entity) {
//        addStuff();
//        super.display(entity);
//    }
//
//    private void addStuff() {
//        if (pages.size() != 0) {
//            GameMenu page0 = pages.get(0);
//            Button butt = new Button(page0.getSize() - 9, ItemFactory.create(Material.ARROW).setName(
//                    ChatColor.GREEN + "Back to Main Page").create()).addAction(action -> onClickToMainPage.forEach(e -> e.accept(action)));
//            page0.setButton(butt);
//        }
//        // adds stuff
//        for (int i = 0; i < players.size(); i++) {
//            Player player = players.get(i);
//            BooleanButton button = new BooleanButton(i, selected.contains(player), (event, bool) -> {
//                HumanEntity entity = event.getWhoClicked();
//                if (bool && !selected.contains(player)) {
//                    if (onlyOne && selected.size() == 1)
//                        entity.sendMessage(ChatColor.RED + "Only one entity is allowed! Please make sure to deselect the entity first!");
//                    else {
//                        if (selectMessage != null) entity.sendMessage(selectMessage);
//                        selected.add(player);
//                    }
//                } else {
//                    if (unselectedMessage != null) entity.sendMessage(unselectedMessage);
//                    selected.remove(player);
//                }
//            }, ManhuntUtils.createPlayerHead(player, ChatColor.GREEN + player.getName() + ": SELECTED"),
//                    ManhuntUtils.createPlayerHead(player, ChatColor.GREEN + player.getName() + ": " + ChatColor.RED + "NOT SELECTED"));
//            this.setButton(button);
//        }
//        closeActions.add(e -> {
//            display(e.getPlayer());
//        });
//        this.setNavigatorScenery(ItemFactory.create(Material.GREEN_STAINED_GLASS_PANE).setName(" ").create());
//    }
//
//    /**
//     * Sets the select message
//     *
//     * @param msg The select message
//     * @return This object
//     */
//    public PlayerSelectorMenu setSelectMessage(String msg) {
//        selectMessage = msg;
//        return this;
//    }
//
//    /**
//     * Sets the unselect message.
//     *
//     * @param unselectedMessage The message
//     * @return This object
//     */
//    public PlayerSelectorMenu setUnselectedMessage(String unselectedMessage) {
//        this.unselectedMessage = unselectedMessage;
//        return this;
//    }
//
//    /**
//     * Adds a consumer that will get triggered if a player clicks on a button
//     * that should direct them to the main page.
//     *
//     * @param consumer The consumer
//     */
//    public void addOnClickToMainPage(Consumer<InventoryClickEvent> consumer) {
//        onClickToMainPage.add(consumer);
//    }
//
//    public List<Player> getSelected() {
//        return selected;
//    }

}
