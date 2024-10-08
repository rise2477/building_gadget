package org.rise.buildingGadget.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.rise.buildingGadget.BuildingGadget;
import org.rise.buildingGadget.config.ConfigManager;

public class BlockSelection {

    private BuildingGadget plugin;
    private Location firstBlock;
    private Location secondBlock;

    public BlockSelection(Location firstBlock, Location secondBlock) {
        this.firstBlock = firstBlock;
        this.secondBlock = secondBlock;
        this.plugin = BuildingGadget.getInstance();
    }

    public Location getFirstBlock() {
        return firstBlock;
    }

    public Location getSecondBlock() {
        return secondBlock;
    }

    public void setSecondBlock(Location secondBlock) {
        this.secondBlock = secondBlock;
    }

    public void placeBlocks(Player player, Material blockType) {
        if (firstBlock != null && secondBlock != null) {
            int minX = Math.min(firstBlock.getBlockX(), secondBlock.getBlockX());
            int maxX = Math.max(firstBlock.getBlockX(), secondBlock.getBlockX());
            int minY = Math.min(firstBlock.getBlockY(), secondBlock.getBlockY());
            int maxY = Math.max(firstBlock.getBlockY(), secondBlock.getBlockY());
            int minZ = Math.min(firstBlock.getBlockZ(), secondBlock.getBlockZ());
            int maxZ = Math.max(firstBlock.getBlockZ(), secondBlock.getBlockZ());

            int blocksToPlace = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);

            ItemStack[] inventory = player.getInventory().getContents();
            int totalBlocksAvailable = getAvailableBlock(player, blockType);

            if (totalBlocksAvailable >= blocksToPlace) {
                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            Location loc = new Location(firstBlock.getWorld(), x, y, z);
                            Block block = loc.getBlock();
                            if (block.getType() == Material.AIR) {
                                block.setType(blockType);
                            }
                        }
                    }
                }

                int remainingToPlace = blocksToPlace;
                for (int i = 0; i < inventory.length && remainingToPlace > 0; i++) {
                    ItemStack item = inventory[i];
                    if (item != null && item.getType() == blockType) {
                        int amount = item.getAmount();
                        if (amount <= remainingToPlace) {
                            remainingToPlace -= amount;
                            inventory[i] = null;
                        } else {
                            item.setAmount(amount - remainingToPlace);
                            remainingToPlace = 0;
                        }
                    }
                }

                player.getInventory().setContents(inventory);
                player.sendMessage(ConfigManager.PREFIX + ChatColor.GREEN + ConfigManager.MESSAGE_BLOCK_PLACED);
            } else {
                player.sendMessage(ConfigManager.PREFIX + ChatColor.RED + ConfigManager.MESSAGE_NOT_ENOUGH_BLOCK + (blocksToPlace - totalBlocksAvailable));
            }
        }
    }

    private int getAvailableBlock(Player player, Material blockType) {
        ItemStack[] inventory = player.getInventory().getContents();
        int totalBlocksAvailable = 0;

        for (ItemStack item : inventory) {
            if (item != null && item.getType() == blockType) {
                totalBlocksAvailable += item.getAmount();
            }
        }
        return totalBlocksAvailable;
    }
}
