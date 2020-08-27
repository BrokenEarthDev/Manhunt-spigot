/*
 * * Copyright 2019-2020 github.com/ReflxctionDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.github.brokenearthdev.manhunt.gui.menu;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.github.brokenearthdev.manhunt.gui.buttons.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

/**
 * Represents a GUI menu
 */
public class GameMenu {

    /**
     * The game
     */
    protected Map<Integer, Button> buttons = new HashMap<>();

    /**
     * An ItemStack that replaces every empty slot
     */
    protected ItemStack scenery;

    /**
     * The kit title
     */
    protected String title;

    /**
     * Whether should clicks be automatically cancelled or not
     */
    protected boolean cancelAllClicks = true;

    protected List<Consumer<InventoryClickEvent>> globalActions = new ArrayList<>();

    protected List<Consumer<InventoryCloseEvent>> closeActions = new ArrayList<>();

    /**
     * The kit size
     */
    protected int size;

    /**
     * Creates a new menu
     *
     * @param title Menu title
     * @param rows  Menu rows
     */
    public GameMenu(String title, int rows) {
        Preconditions.checkNotNull(title);
        Preconditions.checkArgument(rows <= 6, "Invalid rows number: " + rows + " (Must be <= 6)");
        this.title = title;
        size = rows * 9;
    }

    /**
     * Sets the item that is displayed in every empty slot, as a scenery. Clicking this item does nothing.
     *
     * @param scenery Item to fill in
     * @return This menu
     */
    public GameMenu setScenery(ItemStack scenery) {
        this.scenery = scenery;
        return this;
    }

    /**
     * Sets the button
     *
     * @param button Button to add. Set to null to remove.
     * @return The added button
     */
    public GameMenu setButton(Button button) {
        if (button == null) {
            buttons.remove(button.getSlot());
            return this;
        }
        buttons.put(button.getSlot(), button);
        return this;
    }

    /**
     * Returns the button at the specified slot
     *
     * @param slot Slot of the button
     * @return The button, or {@code null} if none
     */
    public Button getButton(int slot) {
        return buttons.get(slot);
    }

    /**
     * Creates the kit
     *
     * @return The kit
     */
    protected Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(null, size, title);
        buttons.forEach((slot, button) -> inventory.setItem(slot, button.getItem()));
        if (scenery != null)
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null) inventory.setItem(i, scenery);
            }
        return inventory;
    }

    private static final Map<InventoryHolder, GameMenu> OPEN_MENUS = new HashMap<>();

    public void display(HumanEntity entity) {
        entity.openInventory(createInventory());
        OPEN_MENUS.put(entity, this);
    }

    /**
     * Invoked on the kit click event
     *
     * @param event Event
     */
    protected void onClick(InventoryClickEvent event) {
        if (event.getAction() != InventoryAction.NOTHING)
            event.setCancelled(true);
        if (event.getRawSlot() > event.getInventory().getSize() || event.getSlotType() == InventoryType.SlotType.OUTSIDE)
            return;
        if (event.getView().getBottomInventory() == event.getClickedInventory()) {
            event.setCancelled(true);
            return;
        }
        if (!event.getView().getTitle().equals(title)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().isSimilar(scenery)) {
            event.setCancelled(true);
            return;
        }
        if (cancelAllClicks) event.setCancelled(true);
        globalActions.forEach(e -> e.accept(event));
        Button button = getButton(event.getRawSlot());
        if (button != null && button.getOnClick() != null) button.getOnClick().forEach(task -> task.accept(event));
    }

    public int getSize() {
        return size;
    }

    private static final Map<IntPredicate, Integer> SLOT_SIZE = new HashMap<>();

    public String getTitle() {
        return title;
    }

    private static boolean isBetween(int a, int b, int test) {
        return test >= a && test <= b;
    }

    public static int getAppropriateSize(int size) {
        return SLOT_SIZE.entrySet().stream().filter(e -> e.getKey().test(size)).findFirst().map(Entry::getValue).orElse(6);
    }

    public ItemStack getScenery() {
        return scenery.clone();
    }

    static {
        SLOT_SIZE.put((v) -> isBetween(0, 9, v), 1);
        SLOT_SIZE.put((v) -> isBetween(10, 18, v), 2);
        SLOT_SIZE.put((v) -> isBetween(19, 27, v), 3);
        SLOT_SIZE.put((v) -> isBetween(28, 36, v), 4);
        SLOT_SIZE.put((v) -> isBetween(37, 45, v), 5);
        SLOT_SIZE.put((v) -> isBetween(46, 54, v), 6);
    }

    public static class MenuListener implements Listener {

        @EventHandler(ignoreCancelled = true)
        public void onInventoryClick(InventoryClickEvent event) {
            GameMenu openMenu = OPEN_MENUS.get(event.getWhoClicked());
            if (openMenu != null) {
                Inventory inv = event.getView().getTopInventory();
                boolean open = inv.getSize() == openMenu.size && event.getView().getTitle().equals(openMenu.title);
                if (open) {
                    openMenu.onClick(event);
                }
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onInventoryClose(InventoryCloseEvent event) {
            GameMenu openMenu = OPEN_MENUS.get(event.getPlayer());
            if (openMenu != null) {
                Inventory inv = event.getView().getTopInventory();
                boolean open = inv.getSize() == openMenu.size && event.getView().getTitle().equals(openMenu.title);
                if (open)
                    openMenu.closeActions.forEach(a -> a.accept(event));
                OPEN_MENUS.remove(event.getPlayer());
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onItemMove(InventoryMoveItemEvent event) {
            Inventory inv = event.getDestination();
            InventoryHolder holder = event.getSource().getHolder();
            if (holder == null) return;
            GameMenu menu = OPEN_MENUS.get(holder);
            if (menu != null) {
                if (holder.getInventory().getViewers().size() != 0) {
                    String title = holder.getInventory().getViewers().get(0).getOpenInventory().getTitle();
                    boolean open = inv.getSize() == menu.size && title.equals(menu.title);
                    if (open) event.setCancelled(true);
                }
            }
        }
    }

}
