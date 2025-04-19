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
import java.util.UUID;

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
    }

    public static final Map<Material, Material> BLOCK_TO_ITEM_MAP = new HashMap<>();
    static {
        BLOCK_TO_ITEM_MAP.put(Material.WHEAT, Material.WHEAT);
        BLOCK_TO_ITEM_MAP.put(Material.CARROTS, Material.CARROT);
        BLOCK_TO_ITEM_MAP.put(Material.POTATOES, Material.POTATO);
    }


    public FarmingBlock(Location center, Player owner, int level, Material cropTypeBlock, Minions plugin) {
        this.center = center;
        this.owner = owner;
        this.level = level;
        if (BLOCK_TO_ITEM_MAP.containsKey(cropTypeBlock) && cropTypeBlock.isBlock()) {
            this.cropType = cropTypeBlock;
        } else {
            this.cropType = Material.WHEAT;
        }
        this.plugin = plugin;
        this.storageInventory = Bukkit.createInventory(new MinionInventoryHolder(this), 54, ChatColor.YELLOW + "Hasatlar Deposu");
    }
    public FarmingBlock(Location center, UUID ownerUUID, int level, Material cropTypeBlock, Inventory storageInventory, Minions plugin) {
        this.center = center;
        this.owner = Bukkit.getOfflinePlayer(ownerUUID).getPlayer();
        this.level = level;
        if (BLOCK_TO_ITEM_MAP.containsKey(cropTypeBlock) && cropTypeBlock.isBlock()) {
            this.cropType = cropTypeBlock;
        } else {
            this.cropType = Material.WHEAT;
        }
        this.plugin = plugin;
        this.storageInventory = storageInventory;
    }


    public int getLevel() {
        return level;
    }

    public Player getOwner() {
        return owner;
    }

    public UUID getOwnerUUID() {
        return owner != null ? owner.getUniqueId() : null;
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
        if (owner != null && !breaker.getUniqueId().equals(owner.getUniqueId())) {
            breaker.sendMessage(ChatColor.RED + "Bu farm bloğunu yalnızca sahibi kırabilir.");
            return;
        }

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
        } else if (this.level == 3) {
            meta.setDisplayName("FarmerSeviye3");
            meta.addEnchant(Enchantment.UNBREAKING, 2, true);
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

        int range = (level == 3) ? 2 : 1;
        World world = center.getWorld();
        if (world != null) {
            for (int x = -range; x <= range; x++) {
                for (int z = -range; z <= range; z++) {
                    if (x == 0 && z == 0) continue;
                    Location blockLoc = center.clone().add(x, 0, z);
                    Location cropLoc = center.clone().add(x, 1, z);

                    Block farmLandBlock = world.getBlockAt(blockLoc);
                    Block cropBlock = world.getBlockAt(cropLoc);

                    if (farmLandBlock.getType() == Material.FARMLAND) {
                        farmLandBlock.setType(Material.DIRT);
                    }
                    if (BLOCK_TO_ITEM_MAP.containsKey(cropBlock.getType())) {
                        cropBlock.breakNaturally();
                    }
                }
            }
        }
    }


    private void cancelGrowthTasks() {
        for (Location loc : new HashMap<>(growthTasks).keySet()) {
            BukkitTask task = growthTasks.remove(loc);
            if (task != null && !task.isCancelled()) {
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

        if (!newCropBlockType.isBlock() || !BLOCK_TO_ITEM_MAP.containsKey(newCropBlockType)) {
            changer.sendMessage(ChatColor.RED + "Geçersiz ekin türü seçildi! (Bu blok türü ekim için uygun değil.)");
            return;
        }


        if (this.cropType == newCropBlockType) {
            changer.sendMessage(ChatColor.YELLOW + "Ekin zaten " + selectedItemMaterial.name().replace("_SEEDS", "").toLowerCase().replace("_WART", " Wart").replace("_BLOCK", "") + " olarak ayarlı.");
            return;
        }

        cancelGrowthTasks();

        this.cropType = newCropBlockType;
        changer.sendMessage(ChatColor.GREEN + "Ekin türü " + selectedItemMaterial.name().replace("_SEEDS", "").toLowerCase().replace("_WART", " Wart").replace("_BLOCK", "") + " olarak değiştirildi!");

        World world = center.getWorld();
        if (world == null) return;

        int range = (level == 3) ? 2 : 1;
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                if (x == 0 && z == 0) continue;

                Location cropLocation = center.clone().add(x, 1, z);
                Block cropBlock = world.getBlockAt(cropLocation);
                Location soilLocation = center.clone().add(x, 0, z);
                Block soilBlock = world.getBlockAt(soilLocation);

                if (soilBlock.getType() != Material.FARMLAND) {
                    soilBlock.setType(Material.FARMLAND);
                }
                cropBlock.setType(newCropBlockType);
                BlockData bd = cropBlock.getBlockData();
                if (bd instanceof Ageable) {
                    Ageable ageable = (Ageable) bd;
                    ageable.setAge(0);
                    cropBlock.setBlockData(ageable, true);
                }
                // Use the delay based on the minion's level
                long delay = (level == 1) ? 2600L : 1700L;
                startGrowth(cropLocation, delay);
            }
        }
    }

    public void onPlace() {
        long delay;
        if (level == 1) {
            delay = 1700L;
        } else {
            delay = 1100L;
        }
        World world = center.getWorld();
        if (world == null) return;

        cancelGrowthTasks();

        int range = (level == 3) ? 2 : 1;

        boolean canPlace = true;
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                if (x == 0 && z == 0) continue;

                Location cropLocation = center.clone().add(x, 1, z);
                Block crop = world.getBlockAt(cropLocation);

                if (crop.getType() != Material.AIR) {
                    Player currentOwner = getOwner();
                    if (currentOwner != null && currentOwner.isOnline()) {
                        currentOwner.sendMessage(ChatColor.RED + "Farm bloğunun etrafındaki alan boş olmalı!");
                    }
                    canPlace = false;
                    break;
                }
            }
            if (!canPlace) break;
        }

        if (!canPlace) {
            center.getBlock().setType(Material.AIR);
            return;
        }
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                if (x == 0 && z == 0) continue;

                Location soilLocation = center.clone().add(x, 0, z);
                Block soil = world.getBlockAt(soilLocation);
                Location cropLocation = soilLocation.clone().add(0, 1, 0);
                Block crop = world.getBlockAt(cropLocation);

                if (soil.getType() != Material.FARMLAND) {
                    soil.setType(Material.FARMLAND);
                }
                if (crop.getType() == Material.AIR) {
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
                    } else {
                    }
                    return;
                } else {
                    if (growthTasks.containsKey(cropLocation)) {
                        BukkitTask selfTask = growthTasks.remove(cropLocation);
                        if (selfTask != null) selfTask.cancel();
                    }
                    return;
                }
            }

            BlockData blockData = cropBlock.getBlockData();
            if (blockData instanceof Ageable) {
                Ageable ageable = (Ageable) blockData;
                int currentAge = ageable.getAge();
                int maxAge = ageable.getMaximumAge();

                if (currentAge < maxAge) {
                    ageable.setAge(currentAge + 1);
                    cropBlock.setBlockData(ageable, true);
                } else {
                    Collection<ItemStack> drops = cropBlock.getDrops();
                    HashMap<Integer, ItemStack> remaining = storageInventory.addItem(drops.toArray(new ItemStack[0]));

                    if (!remaining.isEmpty()) {
                    } else {
                        cropBlock.setType(Material.AIR);
                        cropBlock.setType(this.cropType);
                        BlockData newBd = cropBlock.getBlockData();
                        if (newBd instanceof Ageable) {
                            Ageable newAgeable = (Ageable) newBd;
                            newAgeable.setAge(0);
                            cropBlock.setBlockData(newAgeable, true);
                        } else {
                            if (growthTasks.containsKey(cropLocation)) {
                                BukkitTask selfTask = growthTasks.remove(cropLocation);
                                if (selfTask != null) selfTask.cancel();
                            }
                        }
                    }
                }
            } else {
                if (growthTasks.containsKey(cropLocation)) {
                    BukkitTask selfTask = growthTasks.remove(cropLocation);
                    if (selfTask != null) selfTask.cancel();
                }
            }
        }, growthInterval, growthInterval);
        growthTasks.put(cropLocation, task);
    }

    public void restore() {
        long delay;
        if (level == 1) {
            delay = 1700L;
        } else {
            delay = 1100L;
        }
        World world = center.getWorld();
        if (world == null) return;

        cancelGrowthTasks();

        int range = (level == 3) ? 2 : 1;

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                if (x == 0 && z == 0) continue;

                Location soilLocation = center.clone().add(x, 0, z);
                Block soilBlock = world.getBlockAt(soilLocation);
                Location cropLocation = center.clone().add(x, 1, z);
                Block cropBlock = world.getBlockAt(cropLocation);

                if (soilBlock.getType() != Material.FARMLAND) {
                    soilBlock.setType(Material.FARMLAND);
                }

                if (cropBlock.getType() == Material.AIR || cropBlock.getType() == this.cropType) {
                    if (cropBlock.getType() == Material.AIR) {
                        cropBlock.setType(this.cropType);
                        BlockData bd = cropBlock.getBlockData();
                        if (bd instanceof Ageable) {
                            Ageable ageable = (Ageable) bd;
                            ageable.setAge(0);
                            cropBlock.setBlockData(ageable, true);
                        } else {
                        }
                    } else {
                        BlockData bd = cropBlock.getBlockData();
                        if (bd instanceof Ageable) {
                            Ageable ageable = (Ageable) bd;
                            ageable.setAge(0); // Decided to reset age on load for consistency
                            cropBlock.setBlockData(ageable, true);
                        } else {
                        }
                    }
                    startGrowth(cropLocation, delay);
                } else {
                    cropBlock.setType(this.cropType);
                    BlockData bd = cropBlock.getBlockData();
                    if (bd instanceof Ageable) {
                        Ageable ageable = (Ageable) bd;
                        ageable.setAge(0);
                        cropBlock.setBlockData(ageable, true);
                        startGrowth(cropLocation, delay);
                    } else {
                    }
                }
            }
        }
    }
    public void openMainMenu(Player player) {
        if (owner != null && !player.getUniqueId().equals(owner.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Bu farm bloğuna erişim izniniz yok.");
            return;
        }
        if (owner == null) {
            player.sendMessage(ChatColor.YELLOW + "Farm bloğu sahibi yüklenemedi.");
        }
        Inventory mainMenuInventory = Bukkit.createInventory(new MinionInventoryHolder(this), 9, ChatColor.GREEN + "Minion Kontrol Paneli");

        ItemStack storageIcon = new ItemStack(Material.CHEST);
        ItemMeta storageMeta = storageIcon.getItemMeta();
        storageMeta.setDisplayName(ChatColor.YELLOW + "Hasatlar Deposu");
        storageIcon.setItemMeta(storageMeta);
        Material cropIconMaterial = BLOCK_TO_ITEM_MAP.getOrDefault(this.cropType, Material.WHEAT_SEEDS);
        ItemStack cropIcon = new ItemStack(cropIconMaterial);
        ItemMeta cropMeta = cropIcon.getItemMeta();
        cropMeta.setDisplayName(ChatColor.AQUA + "Ekin Türü Seç (" + cropIconMaterial.name().replace("_SEEDS", "").toLowerCase().replace("_WART", " Wart").replace("_BLOCK", "") + ")");
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
        if (owner != null && !player.getUniqueId().equals(owner.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Bu menüye erişim izniniz yok.");
            return;
        }
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
        if (owner != null && !player.getUniqueId().equals(owner.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Bu depoya erişim izniniz yok.");
            return;
        }

        player.openInventory(this.storageInventory);
    }


    private ItemStack createMinionItem() {
        ItemStack minionItem = new ItemStack(Material.END_STONE);
        ItemMeta meta = minionItem.getItemMeta();
        if (this.level == 1) {
            meta.setDisplayName("FarmerSeviye1");
        } else if (this.level == 2) {
            meta.setDisplayName("FarmerSeviye2");
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        } else if (this.level == 3) {
            meta.setDisplayName("FarmerSeviye3");
            meta.addEnchant(Enchantment.UNBREAKING, 2, true);
        }
        minionItem.setItemMeta(meta);
        return minionItem;
    }
}