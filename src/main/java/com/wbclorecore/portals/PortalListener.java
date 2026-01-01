package com.wbclorecore.portals;

import com.wbclorecore.WBCLoreCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class PortalListener implements Listener {

    private final WBCLoreCore plugin;

    public PortalListener(WBCLoreCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.FLINT_AND_STEEL) {
                Block clicked = event.getClickedBlock();
                if (clicked != null && clicked.getType() == Material.CRYING_OBSIDIAN) {
                    Block relative = clicked.getRelative(event.getBlockFace());
                    if (relative.getType() == Material.AIR || relative.getType() == Material.FIRE) {
                        if (PortalManager.tryCreatePortal(relative)) {
                            plugin.getPortalData().addPortal(relative.getLocation());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        if (event.getReason() == PortalCreateEvent.CreateReason.FIRE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            Block fromBlock = event.getFrom().getBlock();
            
            if (PortalManager.isCryingObsidianPortal(fromBlock)) {
                event.setCanCreatePortal(false);
                event.setSearchRadius(0);
                
                Location from = event.getFrom();
                World toWorld = null;
                double scale = 1.0;
                
                if (from.getWorld().getEnvironment() == World.Environment.NORMAL) {
                    for (World w : Bukkit.getWorlds()) {
                        if (w.getEnvironment() == World.Environment.NETHER) {
                            toWorld = w;
                            break;
                        }
                    }
                    scale = 0.125;
                } else if (from.getWorld().getEnvironment() == World.Environment.NETHER) {
                    for (World w : Bukkit.getWorlds()) {
                        if (w.getEnvironment() == World.Environment.NORMAL) {
                            toWorld = w;
                            break;
                        }
                    }
                    scale = 8.0;
                }
                
                if (toWorld != null) {
                    Location target = from.clone();
                    target.setWorld(toWorld);
                    target.setX(from.getX() * scale);
                    target.setZ(from.getZ() * scale);
                    
                    PortalData data = plugin.getPortalData();
                    Location existing = data.findNearest(target, 128);
                    
                    if (existing != null) {
                        Block b = existing.getBlock();
                        if (b.getType() != Material.NETHER_PORTAL) {
                            data.removePortal(existing);
                            existing = null;
                        }
                    }

                    if (existing != null) {
                        Location dest = existing.clone().add(0.5, 0, 0.5);
                        dest.setYaw(from.getYaw());
                        dest.setPitch(from.getPitch());
                        event.setTo(dest);
                    } else {
                        Location newPortal = PortalManager.generatePortal(target);
                        data.addPortal(newPortal);
                        Location dest = newPortal.clone().add(0.5, 0, 0.5);
                        dest.setYaw(from.getYaw());
                        dest.setPitch(from.getPitch());
                        event.setTo(dest);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
