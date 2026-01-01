package com.wbclorecore;

import com.wbclorecore.portals.PortalData;
import com.wbclorecore.portals.PortalListener;
import org.bukkit.plugin.java.JavaPlugin;

public class WBCLoreCore extends JavaPlugin {

    private PortalData portalData;

    @Override
    public void onEnable() {
        getLogger().info("WBCLoreCore has been enabled!");
        this.portalData = new PortalData(this);
        getServer().getPluginManager().registerEvents(new PortalListener(this), this);
        getServer().getPluginManager().registerEvents(new com.wbclorecore.portals.StrongholdPortalListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("WBCLoreCore has been disabled!");
    }

    public PortalData getPortalData() {
        return portalData;
    }
}
