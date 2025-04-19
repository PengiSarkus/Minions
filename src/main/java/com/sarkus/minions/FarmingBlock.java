package com.sarkus.minions;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class FarmingBlock {
    private Inventory storageInventory;
    private final Location center;
    private final Player owner;
    private Material cropType;

    private final int level;
    private final Minions plugin;

    private Map<Location, BukkitTask> growthTasks = new HashMap<>();

    public static final Map<Material, Material> ITEM_TO_BLOCK_MAP = new HashMap<>();
    static {
        ITEM_TO_BLOCK_MAP.put(Material.WHEAT, Material.WHEAT);
        ITEM_TO_BLOCK_MAP.put(Material.CARROT, Material.CARROTS);
        ITEM_TO_BLOCK_MAP.put(Material.POTATO, Material.POTATOES);
        ITEM_TO_BLOCK_MAP.put(Material.BEETROOT_SEEDS, Material.BEETROOTS);
        ITEM_TO_BLOCK_MAP.put(Material.NETHER_WART, Material.NETHER_WART);
    }

    public static final Map<Material, Material> BLOCK_TO_ITEM_MAP = new HashMap<>();
    static {
        BLOCK_TO_ITEM_MAP.put(Material.WHEAT, Material.WHEAT);
        BLOCK_TO_ITEM_MAP.put(Material.CARROTS, Material.CARROT);
        BLOCK_TO_ITEM_MAP.put(Material.POTATOES, Material.POTATO);
        BLOCK_TO_ITEM_MAP.put(Material.BEETROOTS, Material.BEETROOT_SEEDS);
        BLOCK_TO_ITEM_MAP.put(Material.NETHER_WART, Material.NETHER_WART);
    }


    public FarmingBlock(Location center, Player owner, int level, Material cropTypeBlock, Minions plugin) {
        this.center = center;
        this.owner = owner;
        this.level = level;
        if (BLOCK_TO_ITEM_MAP.containsKey(cropTypeBlock)) {
            this.cropType = cropTypeBlock;
        } else {
            plugin.getLogger().warning("FarmingBlock created with invalid block type: " + cropTypeBlock + ". Defaulting to WHEAT.");
            this.cropType = Material.WHEAT;
        }
        this.plugin = plugin;
        this.storageInventory = Bukkit.createInventory(new MinionInventoryHolder(this), 54, ChatColor.YELLOW + "Hasatlar Deposu");
    }

    public int getLevel() {
        return level;
    }

    public Player getOwner() {
        return owner;
    }

    public Material getCropType() {
        return cropType;
    }

    public Location getCenter() {
        return center;
    }

    public Inventory getStorageInventory() {
        return storageInventory;
    }

    public void breakMinion(Player breaker) {
        cancelGrowthTasks();

        plugin.removeFarmingBlock(this);

        center.getBlock().setType(Material.AIR);

        ItemStack minionItem = new ItemStack(Material.END_STONE);
        ItemMeta meta = minionItem.getItemMeta();
        if (this.level == 1) {
            meta.setDisplayName("FarmerSeviye1");
        } else if (this.level == 2) {
            meta.setDisplayName("FarmerSeviye2");
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        }
        minionItem.setItemMeta(meta);

        HashMap<Integer, ItemStack> remainingItems = breaker.getInventory().addItem(minionItem);

        if (!remainingItems.isEmpty()) {
            Location dropLoc = breaker.getLocation().clone().add(0, 0.5, 0);
            for (ItemStack remainingItem : remainingItems.values()) {
                breaker.getWorld().dropItemNaturally(dropLoc, remainingItem);
            }
        }

        breaker.sendMessage(ChatColor.GREEN + "Farm bloğu başarıyla kaldırıldı!");
    }

    private void cancelGrowthTasks() {
        for (BukkitTask task : growthTasks.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        growthTasks.clear();
    }


    public void setCropType(Material selectedItemMaterial, Player changer) {
        Material newCropBlockType = ITEM_TO_BLOCK_MAP.get(selectedItemMaterial);

        if (newCropBlockType == null) {
            changer.sendMessage(ChatColor.RED + "Geçersiz ekin türü seçildi!");
            return;
        }

        if (this.cropType == newCropBlockType) {
            changer.sendMessage(ChatColor.YELLOW + "Ekin zaten " + selectedItemMaterial.name().replace("_SEEDS", "").toLowerCase().replace("_WART", " Wart") + " olarak ayarlı.");
            return;
        }

        cancelGrowthTasks();
        this.cropType = newCropBlockType;
        changer.sendMessage(ChatColor.GREEN + "Ekin türü " + selectedItemMaterial.name().replace("_SEEDS", "").toLowerCase().replace("_WART", " Wart") + " olarak değiştirildi!");

        World world = center.getWorld();
        if (world == null) return;

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                Location cropLocation = center.clone().add(x, 1, z);
                Block cropBlock = world.getBlockAt(cropLocation);

                if (cropBlock.getType() == Material.AIR || BLOCK_TO_ITEM_MAP.containsKey(cropBlock.getType())) {
                    cropBlock.setType(newCropBlockType);
                    BlockData bd = cropBlock.getBlockData();
                    if (bd instanceof Ageable) {
                        Ageable ageable = (Ageable) bd;
                        ageable.setAge(0);
                        cropBlock.setBlockData(ageable, true);
                    }
                } else {
                    plugin.getLogger().warning("Minion at " + center + " found unexpected block type " + cropBlock.getType() + " at crop location " + cropLocation + " when changing crop type. Expected a crop block or AIR.");
                }
                long delay = (level == 1) ? 10L : 5L;
                startGrowth(cropLocation, delay);
            }
        }
    }


    public void onPlace() {
        long delay;
        if (level == 1) {
            delay = 10L;
        } else {
            delay = 5L;
        }
        World world = center.getWorld();
        if (world == null) return;

        cancelGrowthTasks();

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
                    Player currentOwner = getOwner();
                    if(currentOwner != null && currentOwner.isOnline()) {
                        currentOwner.sendMessage(ChatColor.RED + "Üzeri Boş Olmalı");
                    }
                    return;
                }
                else{
                    crop.setType(cropType);
                    BlockData bd = crop.getBlockData();
                    if (bd instanceof Ageable) {
                        Ageable ageable = (Ageable) bd;
                        ageable.setAge(0);
                        crop.setBlockData(ageable, true);
                    }
                    startGrowth(cropLocation, delay);
                }
            }
        }
    }

    private void startGrowth(Location cropLocation, long growthInterval){
        World world = cropLocation.getWorld();
        if (world == null) return;

        // --- DIAGNOSTIC LOG ---
        plugin.getLogger().info("[Minions] Starting/Restarting growth task for " + this.cropType + " at " + cropLocation + " with interval: " + growthInterval + " ticks.");
        // --- END DIAGNOSTIC LOG ---


        if(growthTasks.containsKey(cropLocation)) {
            BukkitTask oldTask = growthTasks.get(cropLocation);
            if(oldTask != null && !oldTask.isCancelled()) {
                oldTask.cancel();
            }
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Block soilBlock = world.getBlockAt(cropLocation.clone().add(0, -1, 0));
            Block cropBlock = world.getBlockAt(cropLocation);

            if (soilBlock.getType() != Material.FARMLAND) {
                soilBlock.setType(Material.FARMLAND);
            }

            if (cropBlock.getType() != this.cropType) {
                if (cropBlock.getType() == Material.AIR) {
                    cropBlock.setType(this.cropType);
                    BlockData bd = cropBlock.getBlockData();
                    if (bd instanceof Ageable) {
                        Ageable ageable = (Ageable) bd;
                        ageable.setAge(0);
                        cropBlock.setBlockData(ageable, true);
                    }
                    return;
                } else {
                }
            }

            BlockData blockData = cropBlock.getBlockData();
            if (cropBlock.getType() == this.cropType && blockData instanceof Ageable) {
                Ageable ageable = (Ageable) blockData;
                int currentAge = ageable.getAge();
                int maxAge = ageable.getMaximumAge();

                if (currentAge < maxAge) {
                    ageable.setAge(currentAge + 1);
                    cropBlock.setBlockData(ageable, true);
                } else {
                    plugin.getLogger().info("Crop (" + cropBlock.getType() + ") at " + cropLocation + " is fully grown. Attempting harvest...");

                    Collection<ItemStack> drops = cropBlock.getDrops();

                    HashMap<Integer, ItemStack> remaining = storageInventory.addItem(drops.toArray(new ItemStack[0]));

                    if (!remaining.isEmpty()) {
                        plugin.getLogger().warning("Minion storage full at " + center + ". Could not add all drops from " + cropBlock.getType() + " at " + cropLocation + ". Pausing harvest for this spot.");
                        return;
                    } else {
                        plugin.getLogger().info("Harvest from " + cropBlock.getType() + " at " + cropLocation + " successfully added to storage. Replanting...");

                        cropBlock.setType(Material.AIR);

                        cropBlock.setType(this.cropType);
                        BlockData newBd = cropBlock.getBlockData();
                        if (newBd instanceof Ageable) {
                            Ageable newAgeable = (Ageable) newBd;
                            newAgeable.setAge(0);
                            cropBlock.setBlockData(newAgeable, true);
                        }
                    }
                }
            } else {
            }
        }, growthInterval, growthInterval);

        growthTasks.put(cropLocation, task);
    }

    public void restore() {
        long delay;
        if (level == 1) {
            delay = 10L;
        } else {
            delay = 5L;
        }
        World world = center.getWorld();
        if (world == null) return;

        cancelGrowthTasks();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;

                Location cropLocation = center.clone().add(x, 1, z);
                Block cropBlock = world.getBlockAt(cropLocation);

                if (cropBlock.getType() == Material.AIR || cropBlock.getType() == this.cropType) {
                    if (cropBlock.getType() == Material.AIR) {
                        cropBlock.setType(this.cropType);
                        BlockData bd = cropBlock.getBlockData();
                        if (bd instanceof Ageable) {
                            Ageable ageable = (Ageable) bd;
                            ageable.setAge(0);
                            cropBlock.setBlockData(ageable, true);
                        }
                    }
                    startGrowth(cropLocation, delay);
                } else {
                    plugin.getLogger().warning("Minion at " + center + " found unexpected block type " + cropBlock.getType() + " at crop location " + cropLocation + " during restore. Expected " + this.cropType + " or AIR.");
                }
            }
        }
    }
    public void openMainMenu(Player player) {
        Inventory mainMenuInventory = Bukkit.createInventory(new MinionInventoryHolder(this), 9, ChatColor.GREEN + "Minion Kontrol Paneli");

        ItemStack storageIcon = new ItemStack(Material.CHEST);
        ItemMeta storageMeta = storageIcon.getItemMeta();
        storageMeta.setDisplayName(ChatColor.YELLOW + "Hasatlar");
        storageIcon.setItemMeta(storageMeta);

        ItemStack cropIcon = new ItemStack(Material.WHEAT_SEEDS);
        ItemMeta cropMeta = cropIcon.getItemMeta();
        cropMeta.setDisplayName(ChatColor.AQUA + "Ekin Türü Seç");
        cropIcon.setItemMeta(cropMeta);

        ItemStack breakIcon = new ItemStack(Material.BARRIER);
        ItemMeta breakMeta = breakIcon.getItemMeta();
        breakMeta.setDisplayName(ChatColor.RED + "Bloğu Kır");
        breakIcon.setItemMeta(breakMeta);


        mainMenuInventory.setItem(2, storageIcon);
        mainMenuInventory.setItem(4, cropIcon);
        mainMenuInventory.setItem(6, breakIcon);

        player.openInventory(mainMenuInventory);
    }
    public void openCropSelectionMenu(Player player) {
        Inventory cropSelectionMenu = Bukkit.createInventory(new MinionInventoryHolder(this), 9, ChatColor.AQUA + "Ekin Türü Seç");

        ItemStack wheatIcon = new ItemStack(Material.WHEAT);
        ItemMeta wheatMeta = wheatIcon.getItemMeta();
        wheatMeta.setDisplayName(ChatColor.YELLOW + "Wheat");
        wheatIcon.setItemMeta(wheatMeta);

        ItemStack carrotIcon = new ItemStack(Material.CARROT);
        ItemMeta carrotMeta = carrotIcon.getItemMeta();
        carrotMeta.setDisplayName(ChatColor.YELLOW + "Carrot");
        carrotIcon.setItemMeta(carrotMeta);

        ItemStack potatoIcon = new ItemStack(Material.POTATO);
        ItemMeta potatoMeta = potatoIcon.getItemMeta();
        potatoMeta.setDisplayName(ChatColor.YELLOW + "Potato");
        potatoIcon.setItemMeta(potatoMeta);


        cropSelectionMenu.setItem(3, wheatIcon);
        cropSelectionMenu.setItem(4, carrotIcon);
        cropSelectionMenu.setItem(5, potatoIcon);

        player.openInventory(cropSelectionMenu);
    }
    public void openStorageMenu(Player player) {
        player.openInventory(this.storageInventory);
    }
}