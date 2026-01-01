package com.wbclorecore.portals;

import com.wbclorecore.WBCLoreCore;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PortalData {

    private final WBCLoreCore plugin;
    private final File file;
    private FileConfiguration config;
    private List<Location> portals;

    public PortalData(WBCLoreCore plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "portals.yml");
        this.portals = new ArrayList<>();
        load();
    }

    public void load() {
        if (!file.exists()) {
            plugin.saveResource("portals.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        List<?> list = config.getList("portals");
        if (list != null) {
            for (Object obj : list) {
                if (obj instanceof Location) {
                    portals.add((Location) obj);
                }
            }
        }
    }

    public void save() {
        config.set("portals", portals);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save portals.yml!");
            e.printStackTrace();
        }
    }

    public void addPortal(Location location) {
        portals.add(location);
        save();
    }

    public void removePortal(Location location) {
        portals.removeIf(loc -> loc.getWorld().equals(location.getWorld()) && loc.distanceSquared(location) < 4);
        save();
    }

    public Location findNearest(Location target, int radius) {
        Location nearest = null;
        double minDistance = Double.MAX_VALUE;
        double radiusSq = radius * radius;

        for (Location loc : portals) {
            if (loc.getWorld().equals(target.getWorld())) {
                double distSq = loc.distanceSquared(target);
                if (distSq <= radiusSq && distSq < minDistance) {
                    minDistance = distSq;
                    nearest = loc;
                }
            }
        }
        return nearest;
    }
}
