package org.rise.buildingGadget.listener;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.rise.buildingGadget.BuildingGadget;
import org.rise.buildingGadget.config.ConfigManager;
import org.rise.buildingGadget.utils.BlockSelection;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        BuildingGadget plugin = BuildingGadget.getInstance();

        if (clickedBlock != null && player.getInventory().getItemInMainHand().getType() == ConfigManager.MATERIAL) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            int requiredModelData = plugin.getConfig().getInt("CustomModelData");
            if (itemInHand.getType() == ConfigManager.MATERIAL && itemInHand.hasItemMeta() &&
                    itemInHand.getItemMeta().hasCustomModelData() &&
                    itemInHand.getItemMeta().getCustomModelData() == requiredModelData) {

                BlockSelection selection = BuildingGadget.playerSelections.get(player);

                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (selection == null) {
                        BuildingGadget.playerSelections.put(player, new BlockSelection(clickedBlock.getLocation(), null));
                        player.sendMessage(ConfigManager.PREFIX + ChatColor.GREEN + ConfigManager.MESSAGE_FIRST_BLOCK_SELECTED);
                    } else {
                        player.sendMessage(ConfigManager.PREFIX + ChatColor.RED + ConfigManager.MESSAGE_BOTH_BLOCK_SELECTED);
                    }
                }

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (selection != null && selection.getSecondBlock() == null) {
                        selection.setSecondBlock(clickedBlock.getLocation());
                        player.sendMessage(ConfigManager.PREFIX + ChatColor.GREEN + ConfigManager.MESSAGE_SECOND_BLOCK_SELECTED);
                    } else if (selection != null && selection.getSecondBlock() != null) {
                        player.sendMessage(ConfigManager.PREFIX + ChatColor.RED + ConfigManager.MESSAGE_BOTH_BLOCK_SELECTED);
                    } else {
                        player.sendMessage(ConfigManager.PREFIX + ChatColor.RED + ConfigManager.MESSAGE_MUST_SELECTED_FIRST);
                    }
                }
            }
        }
    }
}
