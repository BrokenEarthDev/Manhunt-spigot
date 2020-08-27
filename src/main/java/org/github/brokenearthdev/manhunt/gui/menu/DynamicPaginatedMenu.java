package org.github.brokenearthdev.manhunt.gui.menu;

import org.bukkit.entity.HumanEntity;
import org.github.brokenearthdev.manhunt.gui.buttons.Button;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a dynamic paginated menu. Please note that {@link #getButton(int)} will
 * <i>never</i> return an instance of {@link org.github.brokenearthdev.manhunt.gui.buttons.BooleanButton},
 * {@link org.github.brokenearthdev.manhunt.gui.buttons.NumberIncreaseButton}, or
 * {@link org.github.brokenearthdev.manhunt.gui.buttons.NumberDecreaseButton}.
 * <p>
 * Instead, it'll return an instance of a dynamic button.
 */
public class DynamicPaginatedMenu extends PaginatedMenu {

    /**
     * Creates a new menu
     *
     * @param title Menu title
     * @param rows  Menu rows
     */
    public DynamicPaginatedMenu(String title, int rows) {
        super(title, rows);
    }

    @Override
    public GameMenu setButton(Button button) {
        int index = 0;
        int sub = button.getSlot();
        while (sub >= 44) {
            index++;
            sub -= 44;
        }
        DynamicMenu menu = (DynamicMenu) this.pages.get(index);
        DynamicMenu.DynamicButton copy = new DynamicMenu.DynamicButton(menu, sub, button.getItem());
        button.getOnClick().forEach(copy::addAction);
        menu.setButton(copy);
        return this;
    }

    /**
     * Forces the update of an inventory in a specific page. Please note
     * that this almost will have <i>no</i> effect if the entity isn't
     * viewing the page but will return {@code true}.
     *
     * @param entity    The entity
     * @param pageIndex The page index
     * @return Whether the page was updated for not
     */
    public boolean forceUpdate(HumanEntity entity, int pageIndex) {
        DynamicMenu dynamicMenu = (DynamicMenu) pages.get(pageIndex);
        return dynamicMenu.forceUpdate(entity);
    }

    /**
     * Forces the update of an inventory in a specific page. This is useful
     * if the page the entity is on can't be retrieved.
     * <p>
     * If the page the entity is on could be retrieved, it is advisable to use
     * {@link #forceUpdate(HumanEntity, int)} as the latter one is more efficient.
     *
     * @param entity The entity
     * @return Whether every page is updated or not
     */
    public boolean forceUpdate(HumanEntity entity) {
        boolean allUpdated = true;
        for (int i = 0; i < pages.size(); i++) {
            DynamicMenu menu = (DynamicMenu) pages.get(i);
            if (!menu.forceUpdate(entity))
                allUpdated = false;
        }
        return allUpdated;
    }

    @Override
    protected List<GameMenu> createPages(int rows) {
        List<GameMenu> pages = new LinkedList<>();
        int sub = rows;
        int page = 1;
        while (sub >= 5) {
            sub -= 5;
            pages.add(new DynamicMenu(title + " (Page " + page + ")", 6));
            page++;
        }
        if (sub != 0) pages.add(new DynamicMenu(title + " (Page " + page + ")", sub + 1));
        return pages;
    }

}
