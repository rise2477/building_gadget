package org.rise.buildingGadget.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.rise.buildingGadget.config.ConfigManager;
import org.rise.buildingGadget.utils.BuildUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildingGadgetCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.PREFIX + ChatColor.RED + ConfigManager.MESSAGE_ONLY_PLAYER);
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("bgadget")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (player.hasPermission("buildinggadget.give")) {
                        BuildUtils.giveBuildingGadget(player);
                    } else {
                        player.sendMessage(ConfigManager.PREFIX + ChatColor.RED + ConfigManager.MESSAGE_NO_PERM);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("confirm")) {
                    BuildUtils.confirmPlacement(player);
                    return true;
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("buildinggadget.reload")) {
                        ConfigManager.load();
                        player.sendMessage(ConfigManager.PREFIX + ConfigManager.MESSAGE_CONFIG_RELOADED);
                    } else {
                        player.sendMessage(ConfigManager.PREFIX + ChatColor.RED + ConfigManager.MESSAGE_NO_PERM);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> argument = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("buildinggadget.give")) argument.add("reload");
            if (sender instanceof Player || sender.hasPermission("buildinggadget.give")) argument.add("give");
            argument.add("confirm");
            Collections.sort(argument);
        }
        return argument;
    }
}