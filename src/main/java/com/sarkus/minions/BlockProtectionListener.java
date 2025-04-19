package com.sarkus.minions;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockProtectionListener implements Listener {
    private final Minions plugin;

    public BlockProtectionListener(Minions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block broken = event.getBlock();
        for (FarmingBlock block : plugin.getActiveFarmingBlocks()) {
            if (block.getCenter().getBlock().equals(broken)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Farm bloğu kırılamaz!");
                return;
            }
        }
    }
}