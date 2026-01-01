package com.wbclorecore.portals;

import com.wbclorecore.WBCLoreCore;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class StrongholdPortalListener implements Listener {

    private final WBCLoreCore plugin;

    public StrongholdPortalListener(WBCLoreCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!event.isNewChunk()) return;

        Chunk chunk = event.getChunk();
        if (chunk.getWorld().getEnvironment() != World.Environment.NORMAL) return;

        ChunkSnapshot snapshot = chunk.getChunkSnapshot();
        int minHeight = chunk.getWorld().getMinHeight();
        int maxHeight = chunk.getWorld().getMaxHeight();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                List<int[]> blocksToRemove = new ArrayList<>();

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = minHeight; y < maxHeight; y++) {
                            Material type = snapshot.getBlockType(x, y, z);
                            if (type == Material.END_PORTAL_FRAME || type == Material.END_PORTAL) {
                                blocksToRemove.add(new int[]{x, y, z});
                            }
                        }
                    }
                }

                if (!blocksToRemove.isEmpty()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!chunk.isLoaded()) return;
                            for (int[] coords : blocksToRemove) {
                                chunk.getBlock(coords[0], coords[1], coords[2]).setType(Material.AIR);
                            }
                        }
                    }.runTask(plugin);
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
