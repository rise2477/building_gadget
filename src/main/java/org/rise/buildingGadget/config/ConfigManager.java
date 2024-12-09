package org.rise.buildingGadget.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
    public static String MESSAGE_EXCEED_MAX_SELECTION;
    public static String TOOL_NAME;
    public static String TOOL_LORE;
    public static String MESSAGE_CANCEL;
    public static Boolean isPermissionEnabled;
    public static int MAX_BLOCKS;
    public static Boolean LandsIntegration;
    public static Boolean LandsForceInLands;
    public static Boolean WorldGuardIntegration;


    public static void load() {
        if (!configPath.exists()) {
            BuildingGadget.getInstance().saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configPath);

        PREFIX = ChatColor.translateAlternateColorCodes('&', config.getString("Prefix", "&7[&6BuildingGadget&7] "));
        TOOL_NAME = ChatColor.translateAlternateColorCodes('&', config.getString("ToolName", "Building Gadget"));
        TOOL_LORE = ChatColor.translateAlternateColorCodes('&', config.getString("ToolLore", "Use This To Make Your Build Easy"));
        MATERIAL = Material.valueOf(config.getString("Item", "BLAZE_ROD"));
        MODEL_DATA = config.getInt("CustomModelData", 1050);
        MESSAGE_CANCEL = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.Cancel-Massage", "Cancel Selected Success."));
        MESSAGE_ONLY_PLAYER = ChatColor.translateAlternateColorCodes('&',config.getString("Messages.OnlyPlayers", "Only players can use this command."));
        MESSAGE_NO_PERM = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NoPermission", "You do not have permission to use this command."));
        MESSAGE_RECEIVED_GADGET = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.ReceivedGadget", "You have received a Building Gadget."));
        MESSAGE_FIRST_BLOCK_SELECTED = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.FirstBlockSelected", "First block selected."));
        MESSAGE_SECOND_BLOCK_SELECTED = ChatColor.translateAlternateColorCodes('&',config.getString("Messages.SecondBlockSelected", "Second block selected."));
        MESSAGE_BOTH_BLOCK_SELECTED = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.BothBlocksSelected", "Both blocks already selected."));
        MESSAGE_MUST_SELECTED_FIRST = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.MustSelectFirst", "You must select the first block first."));
        MESSAGE_NOT_ENOUGH_BLOCK = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NotEnoughBlocks", "Not enough blocks in your inventory. You Need:"));
        MESSAGE_BLOCK_PLACED = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.BlocksPlaced", "Blocks placed successfully."));
        MESSAGE_INVALID_FIRST_BLOCK = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.InvalidFirstBlock", "&cThe first selected block is AIR. Please select a valid block."));
        MESSAGE_CONFIG_RELOADED = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.ConfigReloaded", "Configuration has been reloaded."));
        MESSAGE_INVALID_SELECTION = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.InvalidSelection", "You must select two blocks first."));
        MESSAGE_EXCEED_MAX_SELECTION = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.ExceedMaxSelection", "You have exceeded the maximum selection limit."));
        isPermissionEnabled = config.getBoolean("Permission.Enable");
        LandsIntegration = config.getBoolean("Lands.Integration");
        LandsForceInLands = config.getBoolean("Lands.ForceInLands");
        WorldGuardIntegration = config.getBoolean("WorldGuard.Integration");
        MAX_BLOCKS = config.getInt("MaxBlocks", 1000);

    }

    public static boolean hasUsePermission(Player player) {
        return player.hasPermission("bgadget.use");
    }

}
