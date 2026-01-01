package com.wbclorecore;

import com.wbclorecore.enderchest.EnderChestListener;
import com.wbclorecore.enderchest.EnderChestManager;
import com.wbclorecore.portals.PortalData;
import com.wbclorecore.portals.PortalListener;
import org.bukkit.plugin.java.JavaPlugin;

public class WBCLoreCore extends JavaPlugin {

    private PortalData portalData;
    private EnderChestManager enderChestManager;

    @Override
    public void onEnable() {
        getLogger().info("WBCLoreCore has been enabled!");
        this.portalData = new PortalData(this);
        this.enderChestManager = new EnderChestManager(this);

        getServer().getPluginManager().registerEvents(new PortalListener(this), this);
        getServer().getPluginManager().registerEvents(new com.wbclorecore.portals.StrongholdPortalListener(this), this);
        getServer().getPluginManager().registerEvents(new EnderChestListener(this, enderChestManager), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("WBCLoreCore has been disabled!");
    }

    public PortalData getPortalData() {
        return portalData;
    }

    public EnderChestManager getEnderChestManager() {
        return enderChestManager;
    }
}
