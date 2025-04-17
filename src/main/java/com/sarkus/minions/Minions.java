package com.sarkus.minions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class Minions extends JavaPlugin {

    @Override
    public void onEnable() {
        createFarmingBlockRecipe();
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    private void createFarmingBlockRecipe() {
        ItemStack item = new ItemStack(Material.DIRT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Tarım");
        item.setItemMeta(meta);
        ShapedRecipe recipe = new ShapedRecipe(item);
        recipe.shape("PCP", "WDW", "PCP");
        recipe.setIngredient('P', Material.POTATO);
        recipe.setIngredient('W', Material.WHEAT);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('C', Material.CARROT);
        getServer().addRecipe(recipe);
    }
}
