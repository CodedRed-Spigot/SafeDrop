package dev.codedred.safedrop.commands;

import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Drop implements CommandExecutor {

    private static final String PERMISSION_USE = "sd.use";
    private static final String PERMISSION_ADMIN = "sd.admin";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        DataManager dataManager = DataManager.getInstance();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("[SafeDrop] Commands can only be ran in-game!");
            return false;
        }

        if (args.length != 1) {
            sendUsage(player);
            return false;
        }

        DropManager dropManager = DropManager.getInstance();
        switch (args[0].toUpperCase()) {
            case "ON" -> {
                if (player.hasPermission(PERMISSION_USE)) {
                    dropManager.addDropStatus(player.getUniqueId(), true);
                    player.sendMessage(ChatUtil.format(dataManager.getConfig().getString("messages.safedrop-on")));
                } else
                    sendError(player);
            }
            case "OFF" -> {
                if (player.hasPermission(PERMISSION_USE)) {
                    dropManager.addDropStatus(player.getUniqueId(), false);
                    player.sendMessage(ChatUtil.format(dataManager.getConfig().getString("messages.safedrop-off")));
                } else
                    sendError(player);
            }
            case "RELOAD" -> {
                if (player.hasPermission(PERMISSION_ADMIN)) {
                    dataManager.reload();
                    player.sendMessage(ChatUtil.format("\n&d&lSafe Drop &dhas successfully reloaded.\n"));
                } else
                    sendError(player);
            }
            case "REPORTBUG" -> {
                if (player.hasPermission(PERMISSION_ADMIN))
                    player.sendMessage(ChatUtil.format("&9Report issues here: link"));
                else
                    sendError(player);
            }
            default -> sendUsage(player);
        }

        return true;
    }

    private void sendUsage(Player player) {
        for (String msg : DataManager.getInstance().getConfig().getStringList("messages.usage"))
            player.sendMessage(ChatUtil.format(msg));
        if (player.hasPermission(PERMISSION_ADMIN))
            player.sendMessage(ChatUtil.format("&c&lAdmin Command:\n&c/&8sd reload &7- reloads plugin\\n&c/&8sd reportbug &7- report a plugin bug"));
    }

    private void sendError(Player player) {
        player.sendMessage(ChatUtil.format(DataManager.getInstance().getConfig().getString("messages.no-permission")));
    }
}
