package dev.codedred.safedrop.commands;

import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DropCommand implements CommandExecutor {

    private static final String PERMISSION_USE = "sd.use";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        DataManager dataManager = DataManager.getInstance();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("[SafeDrop] Commands can only be ran in-game!");
            return false;
        }

        DropManager dropManager = DropManager.getInstance();

        if (player.hasPermission(PERMISSION_USE)) {
            if (!dropManager.getStatus(player.getUniqueId())) {
                dropManager.addDropStatus(player.getUniqueId(), true);
                player.sendMessage(ChatUtil.format(dataManager.getConfig().getString("messages.safedrop-on")));
                return false;
            } else
                sendError(player);

            if (dropManager.getStatus(player.getUniqueId())) {
                dropManager.addDropStatus(player.getUniqueId(), false);
                player.sendMessage(ChatUtil.format(dataManager.getConfig().getString("messages.safedrop-off")));
                return false;

            } else
                sendError(player);
        }

        return false;
    }

    private void sendError(Player player) {
        player.sendMessage(ChatUtil.format(DataManager.getInstance().getConfig().getString("messages.no-permission")));
    }
}
