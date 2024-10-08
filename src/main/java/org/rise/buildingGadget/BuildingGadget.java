package org.rise.buildingGadget;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class BuildingGadget extends JavaPlugin implements Listener {

    private static String prefix;
    private static Material buildingMaterial;
    private final Map<Player, BlockSelection> playerSelections = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("commit test");
        Bukkit.getLogger().info("Plugin disabled!");
    }

    private void loadConfig() {
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"));
        buildingMaterial = Material.getMaterial(getConfig().getString("BuildingMat"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + ChatColor.RED + getConfig().getString("Messages.OnlyPlayers"));
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("bgadget")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (player.hasPermission("buildinggadget.give")) {
                        giveBuildingGadget(player);
                    } else {
                        player.sendMessage(prefix + ChatColor.RED + getConfig().getString("Messages.NoPermission"));
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("confirm")) {
                    confirmPlacement(player);
                    return true;
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("buildinggadget.reload")) {
                        reloadConfig(player);
                    } else {
                        player.sendMessage(prefix + ChatColor.RED + getConfig().getString("Messages.NoPermission"));
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private void reloadConfig(Player player) {
        reloadConfig();
        loadConfig();
        player.sendMessage(prefix + ChatColor.GREEN + getConfig().getString("Messages.ConfigReloaded"));
    }

    private void giveBuildingGadget(Player player) {
        ItemStack item = new ItemStack(buildingMaterial);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(getConfig().getInt("CustomModelData"));
            meta.setDisplayName(ChatColor.GOLD + "Building Gadget");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "Use this gadget to place blocks.");
            meta.setLore(lore);

            item.setItemMeta(meta);
        }

        player.getInventory().addItem(item);
        player.sendMessage(prefix + ChatColor.GREEN + getConfig().getString("Messages.ReceivedGadget"));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock != null && player.getInventory().getItemInMainHand().getType() == buildingMaterial) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            int requiredModelData = getConfig().getInt("CustomModelData");
            if (itemInHand.getType() == buildingMaterial && itemInHand.hasItemMeta() &&
                    itemInHand.getItemMeta().hasCustomModelData() &&
                    itemInHand.getItemMeta().getCustomModelData() == requiredModelData) {

                BlockSelection selection = playerSelections.get(player);

                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (selection == null) {
                        playerSelections.put(player, new BlockSelection(clickedBlock.getLocation(), null));
                        player.sendMessage(prefix + ChatColor.GREEN + getConfig().getString("Messages.FirstBlockSelected"));
                    } else {
                        player.sendMessage(prefix + ChatColor.RED + getConfig().getString("Messages.BothBlocksSelected"));
                    }
                }

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (selection != null && selection.getSecondBlock() == null) {
                        selection.setSecondBlock(clickedBlock.getLocation());
                        player.sendMessage(prefix + ChatColor.GREEN + getConfig().getString("Messages.SecondBlockSelected"));
                    } else if (selection != null && selection.getSecondBlock() != null) {
                        player.sendMessage(prefix + ChatColor.RED + getConfig().getString("Messages.BothBlocksSelected"));
                    } else {
                        player.sendMessage(prefix + ChatColor.RED + getConfig().getString("Messages.MustSelectFirst"));
                    }
                }
            }
        }
    }

    private void confirmPlacement(Player player) {
        BlockSelection selection = playerSelections.get(player);
        if (selection == null) {
            player.sendMessage(prefix + ChatColor.RED + getConfig().getString("Messages.InvalidSelection"));
            return;
        }

        Material blockType = selection.getFirstBlock().getBlock().getType();

        if (blockType == Material.AIR) {
            player.sendMessage(prefix + ChatColor.RED + getConfig().getString("Messages.InvalidFirstBlock"));
            return;
        }

        selection.placeBlocks(player, blockType);
        playerSelections.remove(player);
    }

    private class BlockSelection {
        private Location firstBlock;
        private Location secondBlock;

        public BlockSelection(Location firstBlock, Location secondBlock) {
            this.firstBlock = firstBlock;
            this.secondBlock = secondBlock;
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
                int totalBlocksAvailable = 0;

                for (ItemStack item : inventory) {
                    if (item != null && item.getType() == blockType) {
                        totalBlocksAvailable += item.getAmount();
                    }
                }

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
                    player.sendMessage(prefix + ChatColor.GREEN + getConfig().getString("Messages.BlocksPlaced"));
                } else {
                    player.sendMessage(prefix + ChatColor.RED + getConfig().getString("Messages.NotEnoughBlocks") + (blocksToPlace - totalBlocksAvailable));
                }
            }
        }
    }
}