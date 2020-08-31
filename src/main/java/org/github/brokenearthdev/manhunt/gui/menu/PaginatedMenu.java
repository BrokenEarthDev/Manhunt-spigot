package org.github.brokenearthdev.manhunt.gui.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.github.brokenearthdev.manhunt.gui.ItemFactory;
import org.github.brokenearthdev.manhunt.gui.buttons.Button;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a paginated GUI menu
 */
public class PaginatedMenu extends GameMenu {

    protected final List<GameMenu> pages;
    protected ItemStack navigatorScenery = null;
    protected final List<Consumer<InventoryClickEvent>> nextPageConsumers = new ArrayList<>();
    protected final List<Consumer<InventoryClickEvent>> prevPageConsumers = new ArrayList<>();
    protected final List<Consumer<InventoryClickEvent>> backMenuConsumers = new ArrayList<>();
    protected GameMenu back = null;

    /**
     * Creates a new menu.
     *
     * @param title Menu title
     * @param rows  Menu rows
     */
    public PaginatedMenu(String title, int rows) {
        super(title, Math.min(rows <= 0 ? 1 : rows + 1, 6));
        this.pages = createPages(rows <= 0 ? 1 : rows);
    }

    /**
     * Displays a specific page to the human entity
     *
     * @param entity    The {@link HumanEntity}
     * @param pageIndex The page index (0 being first, 1 being second, etc.)
     */
    public void display(HumanEntity entity, int pageIndex) {
        addPageNavigators(pages.get(pageIndex), pageIndex).display(entity);
    }

    /**
     * Displays a specific page to the human entity
     *
     * @param entity The {@link HumanEntity}
     */
    @Override
    public void display(HumanEntity entity) {
        this.display(entity, 0);
    }

    /**
     * Sets the button. The last row of each page is ignored. Adding a
     * button to the navigator row will cause it to be added into the
     * next page with an index of less than 44.
     *
     * @param button Button to add. Set to null to remove.
     * @return This object
     */
    @Override
    public GameMenu setButton(Button button) {
        int index = 0;
        int sub = button.getSlot();
        while (sub >= 44) {
            index++;
            sub -= 44;
        }
        Button copy = new Button(sub, button.getItem());
        button.getOnClick().forEach(copy::addAction);
        this.pages.get(index).setButton(copy);
        return this;
    }

    @Override
    public GameMenu setScenery(ItemStack scenery) {
        this.pages.forEach(page -> page.setScenery(scenery));
        return this;
    }

    @Override
    public Button getButton(int slot) {
        int index = 0;
        int sub = slot;
        while (sub >= 44) {
            index++;
            sub -= 44;
        }
        return pages.get(index).getButton(sub);
    }

    /**
     * Sets the scenery for the navigation row (bottom)
     *
     * @param scenery The scenery
     * @return This object
     */
    public GameMenu setNavigatorScenery(ItemStack scenery) {
        this.navigatorScenery = scenery;
        return this;
    }

    /**
     * Sets the gui in which a player can return to
     *
     * @param menu The gui
     * @return This object
     */
    public GameMenu setReturntoGui(GameMenu menu) {
        back = menu;
        return this;
    }

    /**
     * Adds an event that fires when a player clicks on the button that
     * allows them to return to the gui
     *
     * @param eventConsumer The consumer
     */
    public void addOnReturntoGui(Consumer<InventoryClickEvent> eventConsumer) {
        this.backMenuConsumers.add(eventConsumer);
    }

    /**
     * @return A copy of the menus that compose the paginated menu
     */
    public List<GameMenu> getPages() {
        return new LinkedList<>(pages);
    }

    /**
     * Creates pages depending on the number of rows. The last row of the
     * page is always used as a navigator, so adding a button to the navigator
     * row will cause it to be displayed in the next page.
     *
     * @param rows The rows
     * @return The pages
     */
    protected List<GameMenu> createPages(int rows) {
        List<GameMenu> pages = new LinkedList<>();
        int sub = rows;
        int page = 1;
        while (sub >= 5) {
            sub -= 5;
            pages.add(new GameMenu(title + " (Page " + page + ")", 6));
            page++;
        }
        if (sub != 0) pages.add(new GameMenu(title + " (Page " + page + ")", sub + 1));
        return pages;
    }

    /**
     * Adds the page navigators to the game menu.
     *
     * @param menu      The game menu
     * @param pageIndex The page index
     * @return The object passed in
     */
    protected GameMenu addPageNavigators(GameMenu menu, int pageIndex) {
        Button nextPage = new Button(menu.getSize() - 1,
                ItemFactory.create(Material.ARROW).setName(ChatColor.GREEN + "Next Page").create())
                .addAction(e -> {
                    nextPageConsumers.forEach(page -> page.accept(e));
                    display(e.getWhoClicked(), pageIndex + 1);
                });
        Button previousPage = new Button(menu.getSize() - 9,
                ItemFactory.create(Material.ARROW).setName(ChatColor.GREEN + "Previous Page").create())
                .addAction(e -> {
                    prevPageConsumers.forEach(page -> page.accept(e));
                    display(e.getWhoClicked(), pageIndex - 1);
                });
        Button back = new Button(menu.getSize() - 9, ItemFactory.create(Material.ARROW)
                .setName(ChatColor.GREEN + "Return").create()).addAction(e -> {
            if (this.back != null) {
                backMenuConsumers.forEach(page -> page.accept(e));
                this.back.display(e.getWhoClicked());
            }
        });
        if (pageIndex != 0) {
            menu.setButton(previousPage);
        } else if (this.back != null) {
            menu.setButton(back);
        }
        if (pageIndex != pages.size() - 1) {
            menu.setButton(nextPage);
        }
        for (int i = menu.size - 9; i < menu.size; i++) {
            if (menu.getButton(i) == null && this.navigatorScenery != null) {
                menu.setButton(new Button(i, navigatorScenery));
            }
        }
        return menu;
    }

}
