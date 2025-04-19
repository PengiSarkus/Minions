package com.sarkus.minions;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MinionInventoryHolder implements InventoryHolder {

    private final FarmingBlock farmingBlock;

    public MinionInventoryHolder(FarmingBlock farmingBlock) {
        this.farmingBlock = farmingBlock;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public FarmingBlock getFarmingBlock() {
        return farmingBlock;
    }
}