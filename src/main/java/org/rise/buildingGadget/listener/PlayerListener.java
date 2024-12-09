package org.rise.buildingGadget.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.rise.buildingGadget.BuildingGadget;
import org.rise.buildingGadget.config.ConfigManager;
import org.rise.buildingGadget.utils.BlockSelection;
import org.rise.buildingGadget.utils.BuildUtils;

import java.util.Objects;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock != null && player.getInventory().getItemInMainHand().getType() == ConfigManager.MATERIAL) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            int requiredModelData = ConfigManager.MODEL_DATA;
            if (requiredModelData != -1) {
                if (itemInHand.getItemMeta().hasCustomModelData()){
                    if ((itemInHand.getItemMeta()).getCustomModelData() != requiredModelData) {
                        return;
                    }
                } else {
                    return;
                }
            }
            if (itemInHand.getType() == ConfigManager.MATERIAL) {

                // ตรวจสอบ Permission หลังจากตรวจสอบ Mat และ CustomModelData แล้ว
                boolean isPermissionEnabled = ConfigManager.isPermissionEnabled;
                if (!isPermissionEnabled || ConfigManager.hasUsePermission(player)) {

                    BuildingGadget.playerSelections.putIfAbsent(player, new BlockSelection(null, null));
                    BlockSelection selection = BuildingGadget.playerSelections.get(player);

                    if (player.isSneaking()) {
                        switch (event.getAction()) {
                            case LEFT_CLICK_BLOCK:
                                BuildingGadget.playerSelections.remove(player);
                                player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_CANCEL);
                                event.setCancelled(true);
                                return;
                            case RIGHT_CLICK_BLOCK:
                                if (selection == null || selection.getFirstBlock() == null || selection.getSecondBlock() == null) {
                                    player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_INVALID_SELECTION);
                                    event.setCancelled(true);
                                    return;
                                }
                                BuildUtils.confirmPlacement(player);
                                event.setCancelled(true);
                                return;
                            default:
                                return;
                        }
                    } else {
                        // select first time
                        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            selection.setFirstBlock(clickedBlock.getLocation());
                            player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_FIRST_BLOCK_SELECTED);
                            event.setCancelled(true);
                        }

                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            if (selection != null && selection.getSecondBlock() == null) {
                                selection.setSecondBlock(clickedBlock.getLocation(), player);
                                player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_SECOND_BLOCK_SELECTED);
                            } else if (selection != null && selection.getSecondBlock() != null) {
                                selection.setSecondBlock(clickedBlock.getLocation(), player);
                                player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_SECOND_BLOCK_SELECTED);
                            } else {
                                player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_MUST_SELECTED_FIRST);
                            }
                            event.setCancelled(true);
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
