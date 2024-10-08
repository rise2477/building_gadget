package org.rise.buildingGadget.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.rise.buildingGadget.BuildingGadget;

import java.io.File;

public class ConfigManager {
    private static final File configPath = new File(BuildingGadget.getInstance().getDataFolder() + File.separator + "config.yml");
    private static FileConfiguration config;

    public static String PREFIX;
    public static Material MATERIAL;
    public static int MODEL_DATA;
    public static String MESSAGE_ONLY_PLAYER;
    public static String MESSAGE_NO_PERM;
    public static String MESSAGE_RECEIVED_GADGET;
    public static String MESSAGE_FIRST_BLOCK_SELECTED;
    public static String MESSAGE_SECOND_BLOCK_SELECTED;
    public static String MESSAGE_BOTH_BLOCK_SELECTED;
    public static String MESSAGE_MUST_SELECTED_FIRST;
    public static String MESSAGE_NOT_ENOUGH_BLOCK;
    public static String MESSAGE_BLOCK_PLACED;
    public static String MESSAGE_INVALID_FIRST_BLOCK;
    public static String MESSAGE_CONFIG_RELOADED;
    public static String MESSAGE_INVALID_SELECTION;

    public static void load() {
        if (!configPath.exists()) {
            BuildingGadget.getInstance().saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configPath);

        PREFIX = ChatColor.translateAlternateColorCodes('&', config.getString("Prefix", "&7[&6BuildingGadget&7] "));
        MATERIAL = Material.valueOf(config.getString("BuildingMat", "BLAZE_ROD"));
        MODEL_DATA = config.getInt("CustomModelData", 1050);
        MESSAGE_ONLY_PLAYER = config.getString("Messages.OnlyPlayers", "Only players can use this command.");
        MESSAGE_NO_PERM = config.getString("Messages.NoPermission", "You do not have permission to use this command.");
        MESSAGE_RECEIVED_GADGET = config.getString("Messages.ReceivedGadget", "You have received a Building Gadget.");
        MESSAGE_FIRST_BLOCK_SELECTED = config.getString("Messages.FirstBlockSelected", "First block selected.");
        MESSAGE_SECOND_BLOCK_SELECTED = config.getString("Messages.SecondBlockSelected", "Second block selected.");
        MESSAGE_BOTH_BLOCK_SELECTED = config.getString("Messages.BothBlocksAlreadySelected", "Both blocks already selected.");
        MESSAGE_MUST_SELECTED_FIRST = config.getString("Messages.MustSelectFirst", "You must select the first block first.");
        MESSAGE_NOT_ENOUGH_BLOCK = config.getString("Messages.NotEnoughBlocks", "Not enough blocks in your inventory. You Need:");
        MESSAGE_BLOCK_PLACED = config.getString("Messages.BlocksPlaced", "Blocks placed successfully.");
        MESSAGE_INVALID_FIRST_BLOCK = config.getString("Messages.InvalidFirstBlock", "&cThe first selected block is AIR. Please select a valid block.");
        MESSAGE_CONFIG_RELOADED = config.getString("Messages.ConfigReloaded", "Configuration has been reloaded.");
        MESSAGE_INVALID_SELECTION = config.getString("Messages.InvalidSelection", "You must select two blocks first.");
    }

}
