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

        if (clickedBlock != null && player.getInventory().getItemInMainHand().getType() == ConfigManager.MATERIAL) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            int requiredModelData = BuildingGadget.getInstance().getConfig().getInt("CustomModelData");
            if (itemInHand.getType() == ConfigManager.MATERIAL && itemInHand.hasItemMeta() &&
                    itemInHand.getItemMeta().hasCustomModelData() &&
                    itemInHand.getItemMeta().getCustomModelData() == requiredModelData) {

                // ตรวจสอบ Permission หลังจากตรวจสอบ Mat และ CustomModelData แล้ว
                boolean isPermissionEnabled = ConfigManager.isPermissionEnabled;
                if (!isPermissionEnabled || ConfigManager.hasUsePermission(player)) {

                    BlockSelection selection = BuildingGadget.playerSelections.get(player);

                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        if (selection == null) {
                            BuildingGadget.playerSelections.put(player, new BlockSelection(clickedBlock.getLocation(), null));
                            player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_FIRST_BLOCK_SELECTED);
                        } else {
                            player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_BOTH_BLOCK_SELECTED);
                        }
                    }

                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if (selection != null && selection.getSecondBlock() == null) {
                            selection.setSecondBlock(clickedBlock.getLocation(), player);
                            player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_SECOND_BLOCK_SELECTED);
                        } else if (selection != null && selection.getSecondBlock() != null) {
                            player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_BOTH_BLOCK_SELECTED);
                        } else {
                            player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_MUST_SELECTED_FIRST);
                        }
                    }
                } else {
                    // ถ้าไม่มีสิทธิ์ ให้แสดงข้อความนี้
                    player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_NO_PERM);
                }
            }
        }
    }
}
