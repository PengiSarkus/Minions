package com.sarkus.minions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MinionInteractListener implements Listener {

    private final Minions plugin;

    public MinionInteractListener(Minions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        Player player = event.getPlayer();

        if (clickedBlock == null) {
            return;
        }

        Location clickedLocation = clickedBlock.getLocation();

        for (FarmingBlock farmingBlock : plugin.getActiveFarmingBlocks()) {
            if (farmingBlock.getCenter().equals(clickedLocation)) {
                event.setCancelled(true);
                farmingBlock.openMainMenu(player);
                return;
            }
        }
    }
}