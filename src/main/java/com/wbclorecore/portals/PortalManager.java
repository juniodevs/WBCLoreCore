package com.wbclorecore.portals;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.util.Vector;
import java.util.HashSet;
import java.util.Set;
public class PortalManager {
    private static final int MAX_SIZE = 10;
    private static final int MIN_SIZE = 2;
    public static boolean tryCreatePortal(Block clickedBlock) {
        if (checkAndCreate(clickedBlock, Axis.X)) return true;
        if (checkAndCreate(clickedBlock, Axis.Z)) return true;
        return false;
    }
    private static boolean checkAndCreate(Block start, Axis axis) {
        int maxSize = MAX_SIZE;

        BlockFace right = (axis == Axis.X) ? BlockFace.EAST : BlockFace.SOUTH;
        BlockFace left = (axis == Axis.X) ? BlockFace.WEST : BlockFace.NORTH;
        BlockFace up = BlockFace.UP;
        BlockFace down = BlockFace.DOWN;
        Block current = start;
        while (current.getRelative(down).getType() == Material.AIR || current.getRelative(down).getType() == Material.FIRE) {
            current = current.getRelative(down);
            if (start.getY() - current.getY() > maxSize) return false;
        }
        while (current.getRelative(left).getType() == Material.AIR || current.getRelative(left).getType() == Material.FIRE) {
            current = current.getRelative(left);
            if (start.getLocation().distanceSquared(current.getLocation()) > maxSize * maxSize) return false;
        }
        Block bottomLeftAir = current;
        if (bottomLeftAir.getRelative(down).getType() != Material.CRYING_OBSIDIAN) return false;
        if (bottomLeftAir.getRelative(left).getType() != Material.CRYING_OBSIDIAN) return false;
        int width = 0;
        current = bottomLeftAir;
        while (current.getType() == Material.AIR || current.getType() == Material.FIRE) {
            width++;
            if (current.getRelative(down).getType() != Material.CRYING_OBSIDIAN) return false; 
            current = current.getRelative(right);
            if (width > maxSize) return false;
        }
        if (current.getType() != Material.CRYING_OBSIDIAN) return false;
        if (width < MIN_SIZE) return false;
        int height = 0;
        for (int w = 0; w < width; w++) {
            Block colBase = bottomLeftAir.getRelative(right, w);
            int colHeight = 0;
            Block colCurrent = colBase;
            while (colCurrent.getType() == Material.AIR || colCurrent.getType() == Material.FIRE) {
                colHeight++;
                if (w == 0) {
                    if (colCurrent.getRelative(left).getType() != Material.CRYING_OBSIDIAN) return false;
                }
                if (w == width - 1) {
                    if (colCurrent.getRelative(right).getType() != Material.CRYING_OBSIDIAN) return false;
                }
                colCurrent = colCurrent.getRelative(up);
                if (colHeight > maxSize) return false;
            }
            if (colCurrent.getType() != Material.CRYING_OBSIDIAN) return false;
            if (height == 0) height = colHeight;
            else if (height != colHeight) return false; 
        }
        if (height < MIN_SIZE) return false;
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                Block portalBlock = bottomLeftAir.getRelative(right, w).getRelative(up, h);
                portalBlock.setType(Material.NETHER_PORTAL, false);
                Orientable data = (Orientable) portalBlock.getBlockData();
                data.setAxis(axis);
                portalBlock.setBlockData(data);
            }
        }
        return true;
    }
    public static boolean isCryingObsidianPortal(Block portalBlock) {
        if (portalBlock.getType() != Material.NETHER_PORTAL) return false;
        Orientable data = (Orientable) portalBlock.getBlockData();
        Axis axis = data.getAxis();
        BlockFace direction = (axis == Axis.X) ? BlockFace.EAST : BlockFace.SOUTH;
        Block current = portalBlock;
        while (current.getType() == Material.NETHER_PORTAL) {
            current = current.getRelative(direction);
        }
        return current.getType() == Material.CRYING_OBSIDIAN;
    }
    public static Location generatePortal(Location target) {
        World world = target.getWorld();
        int x = target.getBlockX();
        int z = target.getBlockZ();
        int bestY = target.getBlockY();
        boolean found = false;
        for (int i = 0; i < 64; i++) {
            int checkY = target.getBlockY() + (i % 2 == 0 ? i / 2 : -(i / 2 + 1));
            if (checkY < world.getMinHeight() + 5 || checkY > world.getMaxHeight() - 5) continue;
            Block floor = world.getBlockAt(x, checkY - 1, z);
            Block b1 = world.getBlockAt(x, checkY, z);
            Block b2 = world.getBlockAt(x, checkY + 1, z);
            Block b3 = world.getBlockAt(x, checkY + 2, z);
            if (floor.getType().isSolid() && b1.getType() == Material.AIR && b2.getType() == Material.AIR && b3.getType() == Material.AIR) {
                bestY = checkY;
                found = true;
                break;
            }
        }
        int y = Math.max(world.getMinHeight() + 5, Math.min(world.getMaxHeight() - 10, bestY));
        for (int i = -1; i <= 2; i++) { 
            for (int j = -1; j <= 3; j++) { 
                for (int k = -1; k <= 1; k++) { 
                    world.getBlockAt(x + i, y + j, z + k).setType(Material.AIR);
                }
            }
        }
        world.getBlockAt(x, y - 1, z).setType(Material.CRYING_OBSIDIAN);
        world.getBlockAt(x + 1, y - 1, z).setType(Material.CRYING_OBSIDIAN);
        world.getBlockAt(x, y + 3, z).setType(Material.CRYING_OBSIDIAN);
        world.getBlockAt(x + 1, y + 3, z).setType(Material.CRYING_OBSIDIAN);
        for (int h = 0; h < 3; h++) {
            world.getBlockAt(x - 1, y + h, z).setType(Material.CRYING_OBSIDIAN);
            world.getBlockAt(x + 2, y + h, z).setType(Material.CRYING_OBSIDIAN);
        }
        for (int w = 0; w < 2; w++) {
            for (int h = 0; h < 3; h++) {
                Block b = world.getBlockAt(x + w, y + h, z);
                b.setType(Material.NETHER_PORTAL);
                Orientable data = (Orientable) b.getBlockData();
                data.setAxis(Axis.X);
                b.setBlockData(data);
            }
        }
        Block base = world.getBlockAt(x, y - 2, z);
        if (!found || base.getType() == Material.AIR || base.getType() == Material.LAVA) {
             for (int i = -1; i <= 2; i++) {
                 for (int k = -1; k <= 1; k++) {
                     Block platform = world.getBlockAt(x + i, y - 2, z + k);
                     if (!platform.getType().isSolid()) {
                        platform.setType(Material.CRYING_OBSIDIAN);
                     }
                 }
             }
        }
        return world.getBlockAt(x, y, z).getLocation();
    }
}