package com.sarkus.minions;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class Minions extends JavaPlugin {

    @Override
    public void onEnable() {
        createFarmingBlockRecipies();
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    private void createFarmingBlockRecipies() {
        ItemStack wheatLevelOneItemStack = new ItemStack(Material.END_STONE);
        ItemMeta wheatLevelOneItemStackItemMeta = wheatLevelOneItemStack.getItemMeta();
        wheatLevelOneItemStackItemMeta.setDisplayName("FarmerSeviye1");
        wheatLevelOneItemStack.setItemMeta(wheatLevelOneItemStackItemMeta);
        ShapedRecipe recipeWheatLevelOne = new ShapedRecipe(wheatLevelOneItemStack);
        recipeWheatLevelOne.shape("PCP", "WDW", "PCP");
        recipeWheatLevelOne.setIngredient('P', Material.POTATO);
        recipeWheatLevelOne.setIngredient('W', Material.WHEAT);
        recipeWheatLevelOne.setIngredient('D', Material.DIAMOND_BLOCK);
        recipeWheatLevelOne.setIngredient('C', Material.CARROT);
        getServer().addRecipe(recipeWheatLevelOne);

        ItemStack wheatLevelTwoItemStack = new ItemStack(Material.END_STONE);
        ItemMeta wheatLevelTwoItemStackItemMeta = wheatLevelTwoItemStack.getItemMeta();
        wheatLevelTwoItemStackItemMeta.setDisplayName("FarmerSeviye2");
        wheatLevelTwoItemStackItemMeta.addEnchant(Enchantment.UNBREAKING,1,true);
        wheatLevelTwoItemStack.setItemMeta(wheatLevelTwoItemStackItemMeta);
        ShapedRecipe recipeWheatLevelTwo = new ShapedRecipe(wheatLevelTwoItemStack);
        recipeWheatLevelTwo.shape("WWW", "DFD", "WWW");
        recipeWheatLevelTwo.setIngredient('W', Material.WHEAT);
        recipeWheatLevelTwo.setIngredient('D', Material.DIAMOND_BLOCK);
        recipeWheatLevelTwo.setIngredient('F', wheatLevelOneItemStack);
        getServer().addRecipe(recipeWheatLevelTwo);



    }
}
