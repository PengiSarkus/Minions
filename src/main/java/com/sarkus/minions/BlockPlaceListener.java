package com.sarkus.minions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {
    private final Minions plugin;
    public BlockPlaceListener(Minions plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        if (!(item.hasItemMeta())) return;
        String displayName = item.getItemMeta().getDisplayName();
        if (displayName == null || (!displayName.startsWith("Farmer"))) return;

        Location loc = event.getBlockPlaced().getLocation();
        Player player = event.getPlayer();
        FarmingBlock farmingBlock = null;

        if(displayName.equals("FarmerSeviye1")) {
            farmingBlock = new FarmingBlock(loc, player, 1, Material.WHEAT, plugin);
            farmingBlock.onPlace();
            plugin.registerFarmingBlock(farmingBlock);
        } else if(displayName.equals("FarmerSeviye2")) {
            farmingBlock = new FarmingBlock(loc, player, 2, Material.WHEAT, plugin);
            farmingBlock.onPlace();
            plugin.registerFarmingBlock(farmingBlock);
        } else if (displayName.equals("FarmerSeviye3")) {
            farmingBlock = new FarmingBlock(loc, player, 3, Material.WHEAT, plugin);
            farmingBlock.onPlace();
            plugin.registerFarmingBlock(farmingBlock);

        }
    }
}