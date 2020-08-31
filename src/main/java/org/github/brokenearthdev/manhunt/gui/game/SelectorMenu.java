package org.github.brokenearthdev.manhunt.gui.game;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.github.brokenearthdev.manhunt.gui.menu.ListPaginatedMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Represents a paginated menu in which items can be selected
 * (a selector menu)
 *
 * @param <T> The type
 */
public class SelectorMenu<T> extends ListPaginatedMenu<T> {

    private final List<T> selected;
    private final boolean onlyOne;
    private final Function<T, ItemStack> offItem, onItem;
    private String select, deselect, onlyOneAllowed;

    /**
     * Creates an instance of this option
     *
     * @param title    The GUI name
     * @param list     The list of items (possible selectable entries)
     * @param selected The selected items
     * @param onlyOne  Whether only one item should be selected or not
     * @param offItem  The item stack that appears if an item isn't selected
     * @param onItem   The item stack that appears if an item is selected
     */
    public SelectorMenu(String title, List<T> list, List<T> selected, boolean onlyOne, Function<T, ItemStack> offItem, Function<T, ItemStack> onItem) {
        super(title, list, newFunction(list, offItem, onItem));
        this.offItem = offItem;
        this.onItem = onItem;
        this.onlyOne = onlyOne;
        this.selected = selected;
    }

    /**
     * Creates an instance of this class
     *
     * @param title   The GUI name
     * @param list    The list of items (possible selectable entries)
     * @param offItem The item stack that appears if an item isn't selected
     * @param onItem  The item stack that appears if an item is selected
     */
    public SelectorMenu(String title, List<T> list, Function<T, ItemStack> offItem, Function<T, ItemStack> onItem) {
        this(title, list, new ArrayList<>(), false, offItem, onItem);
    }

    /**
     * Creates a new function. Items that are selected will have the select
     * function invoked and those that are deselected (or not selected) will
     * have the deselect function invoked.
     *
     * @param sel        The list of selected objects
     * @param unselected The unselect function
     * @param selected   The select function
     * @param <T>        The type
     * @return A new function
     */
    private static <T> Function<T, ItemStack> newFunction(List<T> sel, Function<T, ItemStack> unselected, Function<T, ItemStack> selected) {
        Function<T, ItemStack> newFunction = (t) -> {
            if (!sel.contains(t)) return unselected.apply(t);
            else return selected.apply(t);
        };
        return newFunction;
    }

    @Override
    public void display(HumanEntity entity) {
        super.onItemClickList.clear(); // clear list to ensure no duplicates
        super.function = newFunction(selected, offItem, onItem);
        addEvents();
        super.display(entity);
    }

    private void addEvents() {
        super.onItemClickList.add((t, event) -> {
            if (selected.contains(t)) {
                // unselect item
                selected.remove(t);
                if (deselect != null) event.getWhoClicked().sendMessage(deselect);
                if (offItem != null) event.setCurrentItem(offItem.apply(t));
            } else {
                if (onlyOne && selected.size() != 0) {
                    if (onlyOneAllowed != null) event.getWhoClicked().sendMessage(onlyOneAllowed);
                }
                if (!onlyOne || selected.size() == 0) {
                    selected.add(t);
                    if (select != null) event.getWhoClicked().sendMessage(select);
                    if (onItem != null) event.setCurrentItem(onItem.apply(t));
                }
            }
        });
    }

    /**
     * @return A copy of a list containing the selected entry(ies)
     */
    public List<T> getSelected() {
        return new ArrayList<>(selected);
    }

    /**
     * @param select The select message
     */
    public void setSelectMessage(String select) {
        this.select = select;
    }

    /**
     * @param deselect The deselect message
     */
    public void setDeselectMessage(String deselect) {
        this.deselect = deselect;
    }

    /**
     * @param onlyOneAllowed The message that tells the player that only
     *                       one entry is allowed
     */
    public void setOnlyOneAllowedMessage(String onlyOneAllowed) {
        this.onlyOneAllowed = onlyOneAllowed;
    }


}
