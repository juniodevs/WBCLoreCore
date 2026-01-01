package com.wbclorecore.enderchest;

import com.wbclorecore.WBCLoreCore;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EnderChestListener implements Listener {

    private final WBCLoreCore plugin;
    private final EnderChestManager manager;

    public EnderChestListener(WBCLoreCore plugin, EnderChestManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType() == Material.ENDER_CHEST) {
                if (block.getRelative(0, 1, 0).getType().isOccluding()) {
                    return;
                }
                
                event.setCancelled(true);
                Player player = event.getPlayer();
                
                player.playSound(block.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
                
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    manager.openEnderChest(player, block);
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (manager.isEnderChestOpen(player)) {
                if (event.getInventory().equals(manager.getOpenInventory(player))) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.0f, 1.0f);
                    manager.saveEnderChest(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (manager.isEnderChestOpen(event.getPlayer())) {
            manager.saveEnderChest(event.getPlayer());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.ENDER_CHEST) {
            manager.closeInventoriesForBlock(event.getBlock().getLocation());
        }
    }
}
