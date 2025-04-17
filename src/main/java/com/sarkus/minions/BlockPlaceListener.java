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
        if (!item.getItemMeta().getDisplayName().startsWith("Farmer")) return;
        Location loc = event.getBlockPlaced().getLocation();
        Player player = event.getPlayer();
        if(item.getItemMeta().getDisplayName().equals("FarmerSeviye1")) {
        FarmingBlock farmingBlock = new FarmingBlock(loc, player,1, Material.WHEAT,plugin);
        farmingBlock.onPlace();}
        if(item.getItemMeta().getDisplayName().equals("FarmerSeviye2")) {
            FarmingBlock farmingBlock = new FarmingBlock(loc, player,2, Material.WHEAT,plugin);
            farmingBlock.onPlace();}



    }
    }

