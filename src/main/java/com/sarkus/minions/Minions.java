package com.sarkus.minions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.inventory.Inventory; // Import Inventory

public final class Minions extends JavaPlugin {
    private final List<FarmingBlock> activeFarmingBlocks = new ArrayList<>();

    public List<FarmingBlock> getActiveFarmingBlocks() {
        return activeFarmingBlocks;
    }
    public void registerFarmingBlock(FarmingBlock block) {
        activeFarmingBlocks.add(block);
    }
    public void removeFarmingBlock(FarmingBlock block) {
        activeFarmingBlocks.remove(block);
    }

    @Override
    public void onEnable() {
        createFarmingBlockRecipies();
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        loadFarmingBlocks();

        getServer().getPluginManager().registerEvents(new MinionInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new MinionGuiListener(this), this);
    }

    @Override
    public void onDisable() {
        saveFarmingBlocks();
    }

    private void createFarmingBlockRecipies() {
        ItemStack wheatLevelOneItemStack = new ItemStack(Material.END_STONE);
        ItemMeta wheatLevelOneItemStackItemMeta = wheatLevelOneItemStack.getItemMeta();
        wheatLevelOneItemStackItemMeta.setDisplayName("FarmerSeviye1");
        wheatLevelOneItemStack.setItemMeta(wheatLevelOneItemStackItemMeta);
        ShapedRecipe recipeWheatLevelOne = new ShapedRecipe(new NamespacedKey(this, "farmer_level_1"), wheatLevelOneItemStack);
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
        ShapedRecipe recipeWheatLevelTwo = new ShapedRecipe(new NamespacedKey(this, "farmer_level_2"), wheatLevelTwoItemStack);
        recipeWheatLevelTwo.shape("WWW", "DFD", "WWW");
        recipeWheatLevelTwo.setIngredient('W', Material.WHEAT);
        recipeWheatLevelTwo.setIngredient('D', Material.DIAMOND_BLOCK);
        recipeWheatLevelTwo.setIngredient('F', wheatLevelOneItemStack);
        getServer().addRecipe(recipeWheatLevelTwo);
    }

    public void saveFarmingBlocks() {
        File file = new File(getDataFolder(), "farming_blocks.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("blocks", null);

        int i = 0;
        for (FarmingBlock block : activeFarmingBlocks) {
            String path = "blocks." + i;
            config.set(path + ".world", block.getCenter().getWorld().getName());
            config.set(path + ".x", block.getCenter().getBlockX());
            config.set(path + ".y", block.getCenter().getBlockY());
            config.set(path + ".z", block.getCenter().getBlockZ());
            config.set(path + ".owner", block.getOwner().getUniqueId().toString());
            config.set(path + ".level", block.getLevel());

            Material blockType = block.getCropType();
            Material itemType = FarmingBlock.BLOCK_TO_ITEM_MAP.getOrDefault(blockType, blockType);
            config.set(path + ".crop", itemType.name());

            Inventory storageInventory = block.getStorageInventory();
            if (storageInventory != null) {
                config.set(path + ".storage", storageInventory.getContents());
            }

            i++;
        }

        try {
            config.save(file);
            getLogger().log(Level.INFO, "Saved " + i + " farming blocks.");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save farming blocks!", e);
        }
    }
    public void loadFarmingBlocks() {
        File file = new File(getDataFolder(), "farming_blocks.yml");
        if (!file.exists()) {
            getLogger().log(Level.INFO, "No farming_blocks.yml found, skipping load.");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection blocks = config.getConfigurationSection("blocks");
        if (blocks == null) {
            getLogger().log(Level.INFO, "No 'blocks' section in farming_blocks.yml, skipping load.");
            return;
        }

        int loadedCount = 0;
        for (String key : blocks.getKeys(false)) {
            String path = "blocks." + key;
            World world = Bukkit.getWorld(config.getString(path + ".world"));
            int x = config.getInt(path + ".x");
            int y = config.getInt(path + ".y");
            int z = config.getInt(path + ".z");
            UUID ownerUUID = UUID.fromString(config.getString(path + ".owner"));
            int level = config.getInt(path + ".level");
            String itemMaterialName = config.getString(path + ".crop");

            Material itemType = null;
            Material cropBlockType = null;

            try {
                itemType = Material.valueOf(itemMaterialName);
                cropBlockType = FarmingBlock.ITEM_TO_BLOCK_MAP.getOrDefault(itemType, itemType);

                if (!cropBlockType.isBlock()) {
                    throw new IllegalArgumentException("Material " + cropBlockType.name() + " is not a block.");
                }

            } catch (IllegalArgumentException e) {
                getLogger().warning("Failed to parse or map crop material '" + itemMaterialName + "' from config for block at " + world.getName() + "," + x + "," + y + "," + z + ". Skipping load.");
                continue;
            }

            Player owner = Bukkit.getOfflinePlayer(ownerUUID).getPlayer();

            if (world == null) {
                getLogger().warning("Failed to load farming block at " + config.getString(path + ".world") + "," + x + "," + y + "," + z + " - World not found. Skipping.");
                continue;
            }
            if (owner == null) {
                getLogger().warning("Farming block owner (UUID: " + ownerUUID + ") for block at " + world.getName() + "," + x + "," + y + "," + z + " is offline. Block will be loaded.");
            }

            FarmingBlock block = new FarmingBlock(new Location(world, x, y, z), owner, level, cropBlockType, this);
            registerFarmingBlock(block);
            List<?> rawInventory = config.getList(path + ".storage");
            if (rawInventory != null) {
                ItemStack[] contents = rawInventory.toArray(new ItemStack[0]);
                try {
                    block.getStorageInventory().setContents(contents);
                } catch (IllegalArgumentException e) {
                    getLogger().warning("Failed to load inventory contents for block at " + world.getName() + "," + x + "," + y + "," + z + ". Inventory size mismatch? Error: " + e.getMessage());
                }
            }
            //

            block.restore();
            loadedCount++;
        }
        getLogger().log(Level.INFO, "Loaded " + loadedCount + " farming blocks.");
    }
}