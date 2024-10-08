package org.rise.buildingGadget.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rise.buildingGadget.BuildingGadget;
import org.rise.buildingGadget.config.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class BuildUtils {

    public static void confirmPlacement(Player player) {
        BlockSelection selection = BuildingGadget.playerSelections.get(player);
        if (selection == null) {
            player.sendMessage(ConfigManager.PREFIX + ChatColor.RED + ConfigManager.MESSAGE_INVALID_SELECTION);
            return;
        }

        Material blockType = selection.getFirstBlock().getBlock().getType();

        if (blockType == Material.AIR) {
            player.sendMessage(ConfigManager.PREFIX + ChatColor.RED + ConfigManager.MESSAGE_INVALID_FIRST_BLOCK);
            return;
        }

        selection.placeBlocks(player, blockType);
        BuildingGadget.playerSelections.remove(player);
    }

    public static void giveBuildingGadget(Player player) {
        ItemStack item = new ItemStack(ConfigManager.MATERIAL);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(ConfigManager.MODEL_DATA);

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigManager.TOOL_NAME));
            List<String> lore = new ArrayList<>();

            lore.add(ChatColor.translateAlternateColorCodes('&', ConfigManager.TOOL_LORE));
            meta.setLore(lore);

            item.setItemMeta(meta);
        }

        player.getInventory().addItem(item);
        player.sendMessage(ConfigManager.PREFIX + ChatColor.GREEN + ConfigManager.MESSAGE_RECEIVED_GADGET);
    }
}
