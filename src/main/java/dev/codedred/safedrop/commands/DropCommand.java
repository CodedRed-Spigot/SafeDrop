package dev.codedred.safedrop.commands;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.model.User;
import dev.codedred.safedrop.utils.chat.ChatUtils;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DropCommand implements CommandExecutor {

    private static final String PERMISSION_USE = "sd.use";

    private SafeDrop plugin;

    public DropCommand(SafeDrop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        DataManager dataManager = DataManager.getInstance();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("[SafeDrop] Commands can only be ran in-game!");
            return false;
        }

        if (!(player.hasPermission(PERMISSION_USE)))
            return false;

        val uniqueId = player.getUniqueId();

        val usersTable = plugin.getDatabaseManager().getUsersTable();

        User user = usersTable.getByUuid(uniqueId);

        if (user == null) {
            User newUser = new User(player.getUniqueId(), true);
            usersTable.insert(newUser);
            player.sendMessage(ChatUtils.format(dataManager.getConfig().getString("messages.safedrop-on")));
            return false;
        }

        if (user.isEnabled()) {
            user.setEnabled(false);
            usersTable.update(user);
            player.sendMessage(ChatUtils.format(dataManager.getConfig().getString("messages.safedrop-off")));
        } else {
            user.setEnabled(true);
            usersTable.update(user);
            player.sendMessage(ChatUtils.format(dataManager.getConfig().getString("messages.safedrop-on")));
        }

        return false;

/*        if (!dropManager.getStatus(player.getUniqueId())) {
            dropManager.addDropStatus(player.getUniqueId(), true);
            player.sendMessage(ChatUtils.format(dataManager.getConfig().getString("messages.safedrop-on")));
            return false;
        } else
            sendError(player);

        if (dropManager.getStatus(player.getUniqueId())) {
            dropManager.addDropStatus(player.getUniqueId(), false);
            player.sendMessage(ChatUtils.format(dataManager.getConfig().getString("messages.safedrop-off")));
            return false;

        } else
            sendError(player);*/
    }

}
