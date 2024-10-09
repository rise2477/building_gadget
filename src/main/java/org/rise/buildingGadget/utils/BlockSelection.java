package org.rise.buildingGadget.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Particle;
import org.bukkit.World;
import org.rise.buildingGadget.BuildingGadget;
import org.rise.buildingGadget.config.ConfigManager;
import org.bukkit.Color;

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

    public void setSecondBlock(Location secondBlock, Player player) {
        this.secondBlock = secondBlock;
        // เรียกใช้ฟังก์ชันเพื่อแสดงอนุภาคเมื่อเลือก pos2
        spawnParticles(player);
    }

    public void placeBlocks(Player player, Material blockType) {
        if (firstBlock != null && secondBlock != null) {
            int minX = Math.min(firstBlock.getBlockX(), secondBlock.getBlockX());
            int maxX = Math.max(firstBlock.getBlockX(), secondBlock.getBlockX());
            int minY = Math.min(firstBlock.getBlockY(), secondBlock.getBlockY());
            int maxY = Math.max(firstBlock.getBlockY(), secondBlock.getBlockY());
            int minZ = Math.min(firstBlock.getBlockZ(), secondBlock.getBlockZ());
            int maxZ = Math.max(firstBlock.getBlockZ(), secondBlock.getBlockZ());

            int airBlocksToPlace = 0;
            World world = firstBlock.getWorld();

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Location loc = new Location(firstBlock.getWorld(), x, y, z);
                        Block block = loc.getBlock();
                        if (block.getType() == Material.AIR) {
                            airBlocksToPlace++;
                        }
                    }
                }
            }

            int totalBlocksAvailable = getAvailableBlock(player, blockType);

            if (totalBlocksAvailable >= airBlocksToPlace) {
                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            Location loc = new Location(firstBlock.getWorld(), x, y, z);
                            Block block = loc.getBlock();
                            if (block.getType() == Material.AIR) {
                                block.setType(blockType);
                                // แสดงผล Particle ที่ตำแหน่งของบล็อกที่วาง
                                player.spawnParticle(Particle.DUST, loc, 10, new Particle.DustOptions(Color.fromRGB(0, 255, 0), 1.0f));
                            }
                        }
                    }
                }

                // คำนวนบล็อคอากาศเพื่อเปลี่ยนเป็นจำนวนจริง
                int remainingToPlace = airBlocksToPlace;
                ItemStack[] inventory = player.getInventory().getContents();
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
                player.sendMessage(ConfigManager.PREFIX + ChatColor.RED + ConfigManager.MESSAGE_NOT_ENOUGH_BLOCK + (airBlocksToPlace - totalBlocksAvailable));
            }
        }
    }

    public void spawnParticles(Player player) {
        if (firstBlock != null && secondBlock != null) {
            World world = firstBlock.getWorld();
            int minX = Math.min(firstBlock.getBlockX(), secondBlock.getBlockX());
            int maxX = Math.max(firstBlock.getBlockX(), secondBlock.getBlockX());
            int minY = Math.min(firstBlock.getBlockY(), secondBlock.getBlockY());
            int maxY = Math.max(firstBlock.getBlockY(), secondBlock.getBlockY());
            int minZ = Math.min(firstBlock.getBlockZ(), secondBlock.getBlockZ());
            int maxZ = Math.max(firstBlock.getBlockZ(), secondBlock.getBlockZ());

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Location loc = new Location(world, x, y, z);
                        // ส่งอนุภาคให้กับผู้เล่นที่ใช้เท่านั้น
                        player.spawnParticle(Particle.HAPPY_VILLAGER, loc, 10);
                    }
                }
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
