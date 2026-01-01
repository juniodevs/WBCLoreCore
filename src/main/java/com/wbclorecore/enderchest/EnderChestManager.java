package com.wbclorecore.enderchest;

import com.wbclorecore.WBCLoreCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EnderChestManager {

    private final WBCLoreCore plugin;
    private final File dataFolder;
    private final Map<UUID, Inventory> openInventories = new HashMap<>();
    private final Map<UUID, Location> openBlockLocations = new HashMap<>();

    public EnderChestManager(WBCLoreCore plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "enderchests");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void openEnderChest(Player player, Block block) {
        if (openInventories.containsKey(player.getUniqueId())) {
            saveEnderChest(player);
        }

        File playerFile = new File(dataFolder, player.getUniqueId() + ".yml");
        
        Block targetBlock = block;
        if (targetBlock == null) {
            targetBlock = player.getLocation().getBlock();
        }
        
        targetBlock = targetBlock.getRelative(0, -3, 0);
        
        if (targetBlock.getY() < targetBlock.getWorld().getMinHeight()) {
             targetBlock = targetBlock.getWorld().getBlockAt(targetBlock.getX(), targetBlock.getWorld().getMinHeight(), targetBlock.getZ());
        }
        
        BlockInventoryHolder holder = new EnderChestHolder(targetBlock);
        Inventory inventory = Bukkit.createInventory(holder, 54, "Ender Chest");

        if (playerFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            if (config.contains("items")) {
                List<?> list = config.getList("items");
                if (list != null) {
                    ItemStack[] content = list.toArray(new ItemStack[0]);
                    inventory.setContents(content);
                }
            }
        }

        openInventories.put(player.getUniqueId(), inventory);
        if (block != null) {
            openBlockLocations.put(player.getUniqueId(), block.getLocation());
        }
        player.openInventory(inventory);
    }

    public void saveEnderChest(Player player) {
        Inventory inventory = openInventories.remove(player.getUniqueId());
        openBlockLocations.remove(player.getUniqueId());
        if (inventory == null) {
            return;
        }

        File playerFile = new File(dataFolder, player.getUniqueId() + ".yml");
        File tmpFile = new File(dataFolder, player.getUniqueId() + ".yml.tmp");
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set("items", inventory.getContents());

        try {

            config.save(tmpFile);
            if (playerFile.exists()) {
                playerFile.delete();
            }
            boolean renamed = tmpFile.renameTo(playerFile);
            if (!renamed) {
                plugin.getLogger().severe("Could not rename temp file for " + player.getName());

            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save Ender Chest for " + player.getName());
            e.printStackTrace();
            player.sendMessage("§cError saving your Ender Chest! Please contact an admin.");
        }
    }
    
    public boolean isEnderChestOpen(Player player) {
        return openInventories.containsKey(player.getUniqueId());
    }

    public Inventory getOpenInventory(Player player) {
        return openInventories.get(player.getUniqueId());
    }

    public void closeInventoriesForBlock(Location location) {
        for (UUID uuid : new HashMap<>(openBlockLocations).keySet()) {
            Location loc = openBlockLocations.get(uuid);
            if (loc != null && loc.equals(location)) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.closeInventory();
                }
            }
        }
    }

    private static class EnderChestHolder implements BlockInventoryHolder {
        private final Block block;

        public EnderChestHolder(Block block) {
            this.block = block;
        }

        @Override
        public Block getBlock() {
            return block;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
