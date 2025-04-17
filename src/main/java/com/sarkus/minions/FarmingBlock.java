package com.sarkus.minions;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.Location;


import java.util.Random;

public class FarmingBlock {
    private final Location center;
    private final Player owner;
    private final Material cropType;
    private final Minions plugin;
    public FarmingBlock(Location center, Player owner, Material cropType, Minions plugin) {
        this.center = center;
        this.owner = owner;
        this.cropType = cropType;
        this.plugin = plugin;
    }

    public void onPlace() {
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
                        ageable.setAge(7); // Max age for wheat
                        b.setBlockData(ageable);

                        Bukkit.getLogger().info("Crop (" + b.getType() + ") age set to: " + ageable.getAge());

                    } else {
                        Bukkit.getLogger().warning("The block is not Ageable: " + b.getType());
                    }
                }, 20L);


            }
        }
    }
}
