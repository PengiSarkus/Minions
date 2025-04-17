package com.sarkus.minions;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;


import java.util.Random;

public class FarmingBlock {
    private final Location center;
    private final Player owner;
    private final Material cropType;
    private final int level;
    private final Minions plugin;
    private int growthLevel;
    public FarmingBlock(Location center, Player owner,int level, Material cropType, Minions plugin) {
        this.center = center;
        this.owner = owner;
        this.level = level;
        this.cropType = cropType;
        this.plugin = plugin;
        this.growthLevel = 0;
    }

    public void onPlace() {
        long delay;
        if (level == 1) {
            delay = 100L; // slower growth
        } else {
            delay = 50L;  // faster growth
        }
        World world = center.getWorld();
        for (int x = -1; x<=1; x++){
            for (int z = -1; z<=1; z++){
                Location soilLocation = center.clone().add(x, 0, z);
                Block soil = world.getBlockAt(soilLocation);
                if (soilLocation.equals(center)) continue;
                if (!(soil.getType() == Material.FARMLAND)){
                    soil.setType(Material.FARMLAND);
                }
                Location cropLocation = soilLocation.clone().add(0, 1, 0);
                Block crop = world.getBlockAt(cropLocation);
                if (!(crop.getType() == Material.AIR)){
                    owner.sendMessage(ChatColor.RED + "Üzeri Boş Olmalı");
                    return;
                }
                else{
                    crop.setType(cropType);
                }

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Block b = world.getBlockAt(cropLocation);
                    BlockData blockData = b.getBlockData();

                    if (blockData instanceof org.bukkit.block.data.Ageable) {
                        org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable) blockData;
                        ageable.setAge(growthLevel+1);// Max age for wheat
                        growthLevel++;
                        b.setBlockData(ageable);

                        Bukkit.getLogger().info("Crop (" + b.getType() + ") age set to: " + ageable.getAge());

                    } else {
                        Bukkit.getLogger().warning("The block is not Ageable: " + b.getType());
                    }
                }, delay);


            }
        }
    }
}
