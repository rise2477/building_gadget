package org.rise.buildingGadget;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.rise.buildingGadget.command.BuildingGadgetCommand;
import org.rise.buildingGadget.config.ConfigManager;
import org.rise.buildingGadget.listener.PlayerListener;
import org.rise.buildingGadget.utils.BlockSelection;

import java.util.*;

public final class BuildingGadget extends JavaPlugin implements Listener {

    private static BuildingGadget instance;
    public static Map<Player, BlockSelection> playerSelections = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.load();
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        Objects.requireNonNull(getCommand("bgadget")).setExecutor(new BuildingGadgetCommand());
        Objects.requireNonNull(getCommand("bgadget")).setTabCompleter(new BuildingGadgetCommand());
    }

    public static BuildingGadget getInstance() {
        return instance;
    }

    //HelloWorld
    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Plugin disabled!");
    }
}