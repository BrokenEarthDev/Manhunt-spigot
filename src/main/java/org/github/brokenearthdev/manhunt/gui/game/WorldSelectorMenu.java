package org.github.brokenearthdev.manhunt.gui.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.github.brokenearthdev.manhunt.gui.ItemFactory;
import org.github.brokenearthdev.manhunt.gui.buttons.BooleanButton;
import org.github.brokenearthdev.manhunt.gui.buttons.Button;
import org.github.brokenearthdev.manhunt.gui.menu.GameMenu;
import org.github.brokenearthdev.manhunt.gui.menu.PaginatedMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents a world selector menu
 */
public class WorldSelectorMenu extends PaginatedMenu {

    private final List<World> worlds;
    private final World placeHolder;
    private final Material material;
    private final StartMenu menu;
    private final List<Consumer<InventoryClickEvent>> onClickToMain = new ArrayList<>();
    private World selected;
    private String selectedMsg, unselectedMsg;
    private boolean generateWorld = false;

    public WorldSelectorMenu(String title, StartMenu menu, Material worldMaterial, List<World> worlds, World selected, World placeHolder) {
        super(title, (int) (Math.ceil((worlds.size() + 1.0) / 9.0)));
        this.worlds = worlds;
        this.selected = selected;
        this.placeHolder = placeHolder;
        this.material = worldMaterial;
        generateWorld = selected == null;
        this.menu = menu;
        addStuff();
    }

    /**
     * Sets the message that pops up when a world is selected
     *
     * @param selectedMessage Selected message
     */
    public void setSelectedMessage(String selectedMessage) {
        this.selectedMsg = selectedMessage;
    }

    /**
     * Sets the message that pops up when a world is unselected
     *
     * @param unselectedMsg Unselect message
     */
    public void setUnselectedMsg(String unselectedMsg) {
        this.unselectedMsg = unselectedMsg;
    }

    @Override
    public void display(HumanEntity entity, int pageIndex) {
        addStuff();
        super.display(entity, pageIndex);
    }

    @Override
    public void display(HumanEntity entity) {
        addStuff();
        super.display(entity);
    }

    /**
     * @return The selected world
     */
    public World getSelected() {
        return selected;
    }

    public boolean generateNew() {
        return generateWorld;
    }

    public void addOnClickToMain(Consumer<InventoryClickEvent> eventConsumer) {
        onClickToMain.add(eventConsumer);
    }

    private void addStuff() {
        navigatorAdd();
        for (int i = 0; i < worlds.size(); i++) {
            World world = worlds.get(i);
            ItemStack item = ItemFactory.create(material).setName(ChatColor.GREEN + world.getName() + ": SELECTED").create();
            ItemStack offItem = ItemFactory.create(material).setName(ChatColor.GREEN + world.getName() + ": " + ChatColor.RED + "NOT SELECTED").create();
            BooleanButton button = new BooleanButton(i, Objects.equals(selected, world), (event, bool) -> {
                if (generateWorld) {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "Please deselect \"Generate World\" first!");
                    return;
                }
                if (bool) {
                    if (selectedMsg != null) event.getWhoClicked().sendMessage(selectedMsg);
                    generateWorld = false;
                    event.setCurrentItem(item);
                    selected = world;
                } else {
                    if (unselectedMsg != null) event.getWhoClicked().sendMessage(unselectedMsg);
                    event.setCurrentItem(offItem);
                    generateWorld = false;
                    selected = null;
                }
            }, item, offItem);
            this.setButton(button);
        }
        ItemStack onItem = ItemFactory.create(Material.RAIL).setName(ChatColor.GREEN + "Generate World: ON").addGlowEffect(true).create();
        ItemStack offItem = ItemFactory.create(Material.RAIL).setName(ChatColor.GREEN + "Generate World: " + ChatColor.RED + "OFF").create();
        Button generateWorld = new Button(worlds.size(), this.generateWorld ? onItem : offItem).addAction(event -> {
            if (selected != null) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Please deselect \"" + selected.getName() + "\" " +
                        "first!");
                return;
            }
            if (generateNew()) {
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "Success! The world will not be generated.");
                genWorld(false);
                event.setCurrentItem(offItem);
            } else {
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "The world will be generated");
                genWorld(true);
                event.setCurrentItem(onItem);
            }
        });
        setButton(generateWorld);
        //closeActions.add(c -> display(c.getPlayer()));
        setNavigatorScenery(ItemFactory.create(Material.GREEN_STAINED_GLASS_PANE).setName(" ").create());
    }

    private void genWorld(boolean gen) {
        if (!gen) {
            generateWorld = false;
            selected = placeHolder;
        } else {
            generateWorld = true;
            selected = null;
        }
    }

    private void navigatorAdd() {
        if (pages.size() != 0) {
            GameMenu page0 = pages.get(0);
            Button butt = new Button(page0.getSize() - 9, ItemFactory.create(Material.ARROW).setName(
                    ChatColor.GREEN + "Back to Main Page").create());
            butt.addAction(action -> {
                onClickToMain.forEach(click -> click.accept(action));
                if (selected == null && !generateWorld) {
                    action.getWhoClicked().sendMessage(ChatColor.RED + "Please select a world first!");
                } else {
                    action.getWhoClicked().sendMessage(ChatColor.GREEN + "Success! " + (generateWorld ? "" +
                            "The world will be generated." : selected.getName() + " is selected!"));
                    menu.display(action.getWhoClicked());
                }
            });
            page0.setButton(butt);
        }
    }


}
