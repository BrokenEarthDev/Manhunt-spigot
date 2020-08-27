package org.github.brokenearthdev.manhunt.gui.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.github.brokenearthdev.manhunt.gui.ItemFactory;
import org.github.brokenearthdev.manhunt.gui.buttons.Button;
import org.github.brokenearthdev.manhunt.gui.menu.GameMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GracePeriodMenu extends GameMenu {

    private final List<Consumer<InventoryClickEvent>> onClickToBack = new ArrayList<>();
    private final List<Button> allButtons = new ArrayList<>();
    private Button zeroSeconds;
    private Button fifteenSeconds;
    private Button thirtySeconds;
    private Button sixtySeconds;
    private Button threeMinutes;
    private Button fiveMinutes;
    private Button tenMinutes;
    private Button back;
    private int gracePeriod;

    public GracePeriodMenu(int gracePeriod) {
        super("Grace Period Menu", 6);
        this.gracePeriod = gracePeriod;

        addItems();
        allButtons.add(zeroSeconds);
        allButtons.add(fifteenSeconds);
        allButtons.add(thirtySeconds);
        allButtons.add(sixtySeconds);
        allButtons.add(threeMinutes);
        allButtons.add(fiveMinutes);
        allButtons.add(tenMinutes);
    }

    private void addItems() {
        gracePeriod = closestLazy(gracePeriod);
        zeroSeconds = gracePeriodButtonSecs(0, gracePeriod == 0);
        fifteenSeconds = gracePeriodButtonSecs(15, gracePeriod == 15);
        thirtySeconds = gracePeriodButtonSecs(30, gracePeriod == 30);
        sixtySeconds = gracePeriodButtonSecs(60, gracePeriod == 60);
        threeMinutes = gracePeriodButtonMins(3, gracePeriod == 180);
        fiveMinutes = gracePeriodButtonMins(5, gracePeriod == 300);
        tenMinutes = gracePeriodButtonMins(10, gracePeriod == 600);
        back = new Button(49, ItemFactory.create(Material.ARROW).setName(ChatColor.GREEN + "Back").create())
                .addAction(action -> {
                    onClickToBack.forEach(e -> e.accept(action));
                    // unregister stuff
                    zeroSeconds.getOnClick().clear();
                    fifteenSeconds.getOnClick().clear();
                    thirtySeconds.getOnClick().clear();
                    sixtySeconds.getOnClick().clear();
                    threeMinutes.getOnClick().clear();
                    fiveMinutes.getOnClick().clear();
                    tenMinutes.getOnClick().clear();
                });
        setButton(zeroSeconds).setButton(fifteenSeconds).setButton(thirtySeconds).setButton(sixtySeconds)
                .setButton(threeMinutes).setButton(fiveMinutes).setButton(tenMinutes).setButton(back);
        // setScenery(ItemFactory.create(Material.BLACK_STAINED_GLASS_PANE).setName(" ").create());
    }

    private Button gracePeriodButtonSecs(int seconds, boolean selected) {
        Button button = new Button(appropriateSlotLazySecs(seconds), gracePeriodEntrySecs(seconds, selected));
        return button.addAction(action -> {
            if (action.getCurrentItem().getType() == Material.RED_CONCRETE) {
                gracePeriod = -1;
                deselectAll(action);
                action.setCurrentItem(gracePeriodEntrySecs(seconds, false));
            } else {
                gracePeriod = seconds;
                deselectAll(action);
                action.setCurrentItem(gracePeriodEntrySecs(seconds, true));
            }
        });
    }

    private Button gracePeriodButtonMins(int mins, boolean selected) {
        Button button = new Button(appropriateSlotLazyMins(mins), gracePeriodEntryMins(mins, selected));
        return button.addAction(action -> {
            if (action.getCurrentItem().getType() == Material.RED_CONCRETE) {
                gracePeriod = -1;
                deselectAll(action);
                action.setCurrentItem(gracePeriodEntryMins(mins, false));
            } else {
                gracePeriod = mins * 60;
                deselectAll(action);
                action.setCurrentItem(gracePeriodEntryMins(mins, true));
            }
        });
    }

    private void deselectAll(InventoryClickEvent action) {
        allButtons.forEach(button -> {
            action.getView().setItem(button.getSlot(), ItemFactory.create(Material.GREEN_CONCRETE).setName(button.getItem().getItemMeta().getDisplayName()).create());

            action.setCurrentItem(
                    ItemFactory.create(Material.GREEN_CONCRETE).setName(button.getItem().getItemMeta().getDisplayName()).create());
        });
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    private ItemStack gracePeriodEntrySecs(int seconds, boolean selected) {
        return ItemFactory.create(selected ? Material.RED_CONCRETE : Material.GREEN_CONCRETE)
                .setName(ChatColor.RED.toString() + seconds + ChatColor.GREEN + " seconds").create();
    }

    private ItemStack gracePeriodEntryMins(int mins, boolean selected) {
        return ItemFactory.create(selected ? Material.RED_CONCRETE : Material.GREEN_CONCRETE)
                .setName(ChatColor.RED.toString() + mins + ChatColor.GREEN + " minutes").create();
    }

    private int appropriateSlotLazySecs(int seconds) {
        if (seconds == 0) return 10;
        else if (seconds == 15) return 12;
        else if (seconds == 30) return 14;
        else if (seconds == 60) return 16;
        else return -1;
    }

    private int appropriateSlotLazyMins(int mins) {
        if (mins == 3) return 29;
        else if (mins == 5) return 31;
        else if (mins == 10) return 33;
        return -1;
    }

    private int closestLazy(int seconds) {
        if (seconds <= 7) return 0;
        else if (seconds <= 22) return 15;
        else if (seconds <= 45) return 30;
        else if (seconds <= 120) return 60;
        else if (seconds <= 240) return 180;
        else if (seconds <= 360) return 300;
        else return 600;
    }

    public void addOnClickToBack(Consumer<InventoryClickEvent> event) {
        onClickToBack.add(event);
    }

}

