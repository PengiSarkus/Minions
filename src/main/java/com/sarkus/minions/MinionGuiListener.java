package com.sarkus.minions;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.InventoryView;
import java.util.logging.Level;

public class MinionGuiListener implements Listener {

    private final Minions plugin;

    public MinionGuiListener(Minions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MinionInventoryHolder)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory clickedInv = event.getInventory();
        InventoryView view = event.getView();
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        int rawSlot = event.getRawSlot();

        MinionInventoryHolder holder = (MinionInventoryHolder) clickedInv.getHolder();
        FarmingBlock farmingBlock = holder.getFarmingBlock();

        String viewTitle = view.getTitle();

        if (viewTitle.equals(ChatColor.GREEN + "Minion Kontrol Paneli")) {

            event.setCancelled(true);

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.getType() == Material.CHEST && rawSlot == 2) {
                farmingBlock.openStorageMenu(player);
            } else if (clickedItem.getType() == Material.WHEAT_SEEDS && rawSlot == 4) {
                farmingBlock.openCropSelectionMenu(player);
            } else if (clickedItem.getType() == Material.BARRIER && rawSlot == 6) {
                player.closeInventory();
                farmingBlock.breakMinion(player);
            }


        } else if (viewTitle.equals(ChatColor.AQUA + "Ekin Türü Seç")) {
            event.setCancelled(true);

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            Material selectedCropItem = null;
            if (rawSlot == 3 && clickedItem.getType() == Material.WHEAT) { selectedCropItem = Material.WHEAT; }
            else if (rawSlot == 4 && clickedItem.getType() == Material.CARROT) { selectedCropItem = Material.CARROT; }
            else if (rawSlot == 5 && clickedItem.getType() == Material.POTATO) { selectedCropItem = Material.POTATO; }


            if (selectedCropItem != null) {
                farmingBlock.setCropType(selectedCropItem, player);
                player.closeInventory();
            }

        } else if (viewTitle.equals(ChatColor.YELLOW + "Hasatlar Deposu")) {
            if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof MinionInventoryHolder) {

                InventoryAction action = event.getAction();
                ClickType click = event.getClick();


                boolean isTakingAction = (action == InventoryAction.PICKUP_ALL || action == InventoryAction.PICKUP_HALF ||
                        action == InventoryAction.PICKUP_ONE || action == InventoryAction.PICKUP_SOME ||
                        action == InventoryAction.COLLECT_TO_CURSOR || action == InventoryAction.MOVE_TO_OTHER_INVENTORY); // MOVE_TO_OTHER_INVENTORY is shift-click


                boolean isTakingClick = (click == ClickType.LEFT || click == ClickType.RIGHT ||
                        click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT ||
                        click == ClickType.DOUBLE_CLICK);


                if (isTakingAction && isTakingClick) {
                    event.setCancelled(false);



                } else {

                    event.setCancelled(true);

                }

            } else {

                event.setCancelled(true);
            }

        } else {

            event.setCancelled(true);
            plugin.getLogger().warning("Cancelled click in unknown MinionInventory title: " + viewTitle);
        }
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (!(event.getInventory().getHolder() instanceof MinionInventoryHolder)) {
            return;
        }


        event.setCancelled(false);


    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof MinionInventoryHolder) {

        }
    }
}