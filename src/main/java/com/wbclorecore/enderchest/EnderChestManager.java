package com.wbclorecore.enderchest;

import com.wbclorecore.WBCLoreCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
    // Cache open inventories to ensure we save the right instance
    private final Map<UUID, Inventory> openInventories = new HashMap<>();

    public EnderChestManager(WBCLoreCore plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "enderchests");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void openEnderChest(Player player) {
        // If already open (shouldn't happen normally with interact event, but good safety), return
        if (openInventories.containsKey(player.getUniqueId())) {
            player.openInventory(openInventories.get(player.getUniqueId()));
            return;
        }

        File playerFile = new File(dataFolder, player.getUniqueId() + ".yml");
        Inventory inventory = Bukkit.createInventory(null, 54, "Ender Chest");

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
        player.openInventory(inventory);
    }

    public void saveEnderChest(Player player) {
        Inventory inventory = openInventories.remove(player.getUniqueId());
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
}
