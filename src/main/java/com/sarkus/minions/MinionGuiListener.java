package com.sarkus.minions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
        String rawTitle = ChatColor.stripColor(viewTitle);


        if (rawTitle.equals("Minion Kontrol Paneli")) {

            event.setCancelled(true);

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (rawSlot == 2 && clickedItem.getType() == Material.CHEST) {
                farmingBlock.openStorageMenu(player);
            } else if (rawSlot == 4 && (clickedItem.getType() == Material.WHEAT_SEEDS || clickedItem.getType() == Material.WHEAT || clickedItem.getType() == Material.CARROT || clickedItem.getType() == Material.POTATO || clickedItem.getType() == Material.BEETROOT_SEEDS || clickedItem.getType() == Material.NETHER_WART)) {
                farmingBlock.openCropSelectionMenu(player);
            } else if (rawSlot == 6 && clickedItem.getType() == Material.BARRIER) {
                player.closeInventory();
                farmingBlock.breakMinion(player);
            }


        } else if (rawTitle.equals("Ekin Türü Seç")) {
            event.setCancelled(true);

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            Material selectedCropItem = null;
            if (rawSlot == 3 && clickedItem.getType() == Material.WHEAT) {
                selectedCropItem = Material.WHEAT;
            } else if (rawSlot == 4 && clickedItem.getType() == Material.CARROT) {
                selectedCropItem = Material.CARROT;
            } else if (rawSlot == 5 && clickedItem.getType() == Material.POTATO) {
                selectedCropItem = Material.POTATO;
            }


            if (selectedCropItem != null) {
                farmingBlock.setCropType(selectedCropItem, player);
                player.closeInventory();
            }


        } else if (rawTitle.equals("Hasatlar Deposu")) {
            if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof MinionInventoryHolder) {

                InventoryAction action = event.getAction();
                ClickType click = event.getClick();

                boolean isTakingAction = (action == InventoryAction.PICKUP_ALL || action == InventoryAction.PICKUP_HALF ||
                        action == InventoryAction.PICKUP_ONE || action == InventoryAction.PICKUP_SOME ||
                        action == InventoryAction.COLLECT_TO_CURSOR);

                boolean isTakingClick = (click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.DOUBLE_CLICK);

                boolean isShiftClickToPlayer = (action == InventoryAction.MOVE_TO_OTHER_INVENTORY && event.getClickedInventory().equals(clickedInv));


                if ( (isTakingAction && isTakingClick) || isShiftClickToPlayer) {
                    event.setCancelled(false);

                } else {
                    event.setCancelled(true);
                }

            } else {
                event.setCancelled(false);
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

        String viewTitle = event.getView().getTitle();
        String rawTitle = ChatColor.stripColor(viewTitle);


        if (rawTitle.equals("Minion Kontrol Paneli") || rawTitle.equals("Ekin Türü Seç")) {
            event.setCancelled(true);
        }
        else if (rawTitle.equals("Hasatlar Deposu")) {
            boolean draggingIntoStorage = event.getNewItems().keySet().stream()
                    .anyMatch(rawSlot -> event.getView().getInventory(rawSlot).getHolder() instanceof MinionInventoryHolder);

            if (draggingIntoStorage) {
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
            }
        } else {
            event.setCancelled(true);
            plugin.getLogger().warning("Cancelled drag event in unknown MinionInventory title: " + viewTitle);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof MinionInventoryHolder) {

        }
    }
}