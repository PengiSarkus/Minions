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
                    Block a = world.getBlockAt(cropLocation);
                    if (a.getBlockData() instanceof Ageable ageable){
                        ageable.setAge(7);
                    }
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Block b = world.getBlockAt(cropLocation);
                    BlockData blockData = b.getBlockData();

                    if (blockData instanceof Ageable ageable) {
                        // Fully grow the crop by setting it to the maximum growth stage
                        ageable.setAge(7);
                        // Log the crop growth stage for debugging
                        Bukkit.getLogger().info("Crop age set to: " + ageable.getAge());
                    } else {
                        Bukkit.getLogger().warning("The block is not a crop: " + crop.getType());
                    }
                }, 20L);  // 60 tick + 0-39 random tick arası


            }
        }
    }
}
