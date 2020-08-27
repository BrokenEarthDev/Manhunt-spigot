package org.github.brokenearthdev.manhunt.gui.menu;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.github.brokenearthdev.manhunt.gui.buttons.Button;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a dynamic menu
 */
public class DynamicMenu extends GameMenu {

    /**
     * Creates a new menu
     *
     * @param title Menu title
     * @param rows  Menu rows
     */
    public DynamicMenu(String title, int rows) {
        super(title, rows);
    }

    @Override
    public GameMenu setButton(Button button) {
        DynamicButton dynamicButton = new DynamicButton(this, button.getSlot(), button.getItem());
        button.getOnClick().forEach(dynamicButton::addAction);
        super.setButton(dynamicButton);
        return this;
    }

    /**
     * Sets a button and forces an update to an entity
     *
     * @param button The button
     * @param to     The entity
     * @return This object
     */
    public GameMenu setButtonAndForceUpdate(Button button, HumanEntity to) {
        this.setButton(button);
        forceUpdate(to);
        return this;
    }

    /**
     * Forces an update to the entity if they are viewing this inventory.
     *
     * @param entity The entity
     * @return Whether the update was successful or not
     */
    public boolean forceUpdate(HumanEntity entity) {
        InventoryView inventory = entity.getOpenInventory();
        Inventory top = inventory.getTopInventory();
        if (inventory.getTitle().equals(title) && top.getSize() == size) {
            buttons.forEach((slot, button) -> {
                top.setItem(slot, button.getItem());
            });
            if (scenery != null) {
                for (int i = 0; i < top.getSize(); i++) {
                    if (top.getItem(i) == null)
                        top.setItem(i, scenery);
                }
            }
            if (entity instanceof Player) ((Player) entity).updateInventory();
            return true;
        }
        return false;
    }

    /**
     * Represents a dynamic button
     */
    public static class DynamicButton extends Button {

        private final DynamicMenu menu;
        private final Consumer<InventoryClickEvent> belatedConsumer = event -> event.setCurrentItem(item);

        DynamicButton(DynamicMenu menu, int slot, ItemStack item) {
            super(slot, item);
            this.menu = menu;
            addAction(belatedConsumer);
        }

        @Override
        public void setItem(ItemStack item) {
            super.setItem(item);
        }

        public void update(HumanEntity forEntity) {
            InventoryView open = forEntity.getOpenInventory();
            if (open.getTitle().equals(menu.title) && open.getTopInventory().getSize() == menu.size) {
                menu.forceUpdate(forEntity);
                return;
            }
            menu.display(forEntity);
        }

        @Override
        public List<Consumer<InventoryClickEvent>> getOnClick() {
            // create a linked list to make sure it executes last
            super.getOnClick().remove(belatedConsumer); // temporarily remove
            LinkedList<Consumer<InventoryClickEvent>> onClicks = new LinkedList<>(super.getOnClick());
            onClicks.add(belatedConsumer);
            return onClicks;
        }
    }

}
