package org.rise.buildingGadget.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Particle;
import org.bukkit.World;
import org.rise.buildingGadget.BuildingGadget;
import org.rise.buildingGadget.config.ConfigManager;

import java.util.EnumSet;
import java.util.Set;

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

    public void setFirstBlock(Location firstBlock) {
        this.firstBlock = firstBlock;
    }

    public void setSecondBlock(Location secondBlock, Player player) {
        this.secondBlock = secondBlock;
        // เรียกใช้ฟังก์ชันเพื่อแสดงอนุภาคเมื่อเลือก pos2
        spawnParticles(player);
    }

    public boolean isReplaceable(Material material) {
        Set<Material> replaceableMaterials = EnumSet.of(
                Material.AIR,
                Material.SHORT_GRASS,
                Material.TALL_GRASS,
                Material.DANDELION,  // Example of a flower
                Material.POPPY,      // Another type of flower
                Material.ALLIUM,
                Material.AZURE_BLUET,
                Material.BLUE_ORCHID,
                Material.CORNFLOWER,
                Material.LILY_OF_THE_VALLEY,
                Material.OXEYE_DAISY,
                Material.ROSE_BUSH,
                Material.SUNFLOWER,
                Material.LILAC,
                Material.PEONY,
                Material.FERN,
                Material.LARGE_FERN,
                Material.DEAD_BUSH,
                Material.VINE,
                Material.SNOW,  // Thin layer of snow
                Material.FIRE,
                Material.LAVA,
                Material.WATER,
                Material.SEAGRASS,
                Material.TALL_SEAGRASS,
                Material.KELP,
                Material.KELP_PLANT,
                Material.BRAIN_CORAL,
                Material.BUBBLE_CORAL,
                Material.FIRE_CORAL,
                Material.HORN_CORAL,
                Material.TUBE_CORAL,
                Material.BRAIN_CORAL_FAN,
                Material.BUBBLE_CORAL_FAN,
                Material.FIRE_CORAL_FAN,
                Material.HORN_CORAL_FAN,
                Material.TUBE_CORAL_FAN,
                Material.BRAIN_CORAL_WALL_FAN,
                Material.BUBBLE_CORAL_WALL_FAN,
                Material.FIRE_CORAL_WALL_FAN,
                Material.HORN_CORAL_WALL_FAN,
                Material.TUBE_CORAL_WALL_FAN,
                Material.BROWN_MUSHROOM,
                Material.RED_MUSHROOM,
                Material.ACACIA_SAPLING,
                Material.BIRCH_SAPLING,
                Material.DARK_OAK_SAPLING,
                Material.JUNGLE_SAPLING,
                Material.OAK_SAPLING,
                Material.SPRUCE_SAPLING,
                Material.CAVE_VINES,
                Material.CAVE_VINES_PLANT,
                Material.GLOW_LICHEN,
                Material.SMALL_DRIPLEAF,
                Material.BIG_DRIPLEAF,
                Material.MANGROVE_PROPAGULE
        );
        return replaceableMaterials.contains(material);
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
                        if (isReplaceable(block.getType())) {
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
                            if (isReplaceable(block.getType())) {
                                block.setType(blockType);
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
                BuildingGadget.playerSelections.remove(player);
                player.getInventory().setContents(inventory);
                player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_BLOCK_PLACED);
            } else {
                player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_NOT_ENOUGH_BLOCK + (airBlocksToPlace - totalBlocksAvailable));
            }
        }
    }

    public void spawnParticles(Player player) {
        if (firstBlock == null || secondBlock == null) return;

        World world = firstBlock.getWorld();
        int minX = Math.min(firstBlock.getBlockX(), secondBlock.getBlockX());
        int maxX = Math.max(firstBlock.getBlockX(), secondBlock.getBlockX());
        int minY = Math.min(firstBlock.getBlockY(), secondBlock.getBlockY());
        int maxY = Math.max(firstBlock.getBlockY(), secondBlock.getBlockY());
        int minZ = Math.min(firstBlock.getBlockZ(), secondBlock.getBlockZ());
        int maxZ = Math.max(firstBlock.getBlockZ(), secondBlock.getBlockZ());

        // Add 1.0 to max values to show particles at the edges correctly
        double maxXOffset = maxX + 1.0;
        double maxYOffset = maxY + 1.0;
        double maxZOffset = maxZ + 1.0;

        // Draw all 12 edges of the cuboid
        // Bottom rectangle
        drawLine(player, world, minX, minY, minZ, maxXOffset, minY, minZ);       // Front bottom
        drawLine(player, world, minX, minY, maxZOffset, maxXOffset, minY, maxZOffset); // Back bottom
        drawLine(player, world, minX, minY, minZ, minX, minY, maxZOffset);       // Left bottom
        drawLine(player, world, maxXOffset, minY, minZ, maxXOffset, minY, maxZOffset); // Right bottom

        // Top rectangle
        drawLine(player, world, minX, maxYOffset, minZ, maxXOffset, maxYOffset, minZ);       // Front top
        drawLine(player, world, minX, maxYOffset, maxZOffset, maxXOffset, maxYOffset, maxZOffset); // Back top
        drawLine(player, world, minX, maxYOffset, minZ, minX, maxYOffset, maxZOffset);       // Left top
        drawLine(player, world, maxXOffset, maxYOffset, minZ, maxXOffset, maxYOffset, maxZOffset); // Right top

        // Vertical edges
        drawLine(player, world, minX, minY, minZ, minX, maxYOffset, minZ);           // Front left
        drawLine(player, world, maxXOffset, minY, minZ, maxXOffset, maxYOffset, minZ);   // Front right
        drawLine(player, world, minX, minY, maxZOffset, minX, maxYOffset, maxZOffset);   // Back left
        drawLine(player, world, maxXOffset, minY, maxZOffset, maxXOffset, maxYOffset, maxZOffset); // Back right
    }

    private void drawLine(Player player, World world, double x1, double y1, double z1, double x2, double y2, double z2) {
        double spacing = 0.5; // Particle spacing

        // Calculate the vector between start and end points
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        // Calculate the total distance
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Calculate the number of particles
        int particles = (int) Math.ceil(distance / spacing);

        // Normalize the direction vector
        double stepX = dx / particles;
        double stepY = dy / particles;
        double stepZ = dz / particles;

        // Spawn particles along the line
        for (int i = 0; i <= particles; i++) {
            double x = x1 + (stepX * i);
            double y = y1 + (stepY * i);
            double z = z1 + (stepZ * i);

            Location loc = new Location(world, x, y, z);
            // Check if this is a corner
            boolean isCorner = (i == 0 || i == particles);

            if (isCorner) {
                player.spawnParticle(Particle.FLAME, loc, 1, 0, 0, 0, 0);
            } else {
                player.spawnParticle(Particle.HAPPY_VILLAGER, loc, 1, 0, 0, 0, 0);
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
