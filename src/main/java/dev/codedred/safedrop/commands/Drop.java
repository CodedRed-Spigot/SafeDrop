package dev.codedred.safedrop.commands;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.data.database.table.UsersTable;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.model.User;
import dev.codedred.safedrop.utils.chat.ChatUtils;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Drop implements CommandExecutor {

    private static final String PERMISSION_USE = "sd.use";
    private static final String PERMISSION_ADMIN = "sd.admin";

    private final SafeDrop plugin;

    public Drop(SafeDrop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        DataManager dataManager = DataManager.getInstance();
        if (!(sender instanceof Player player)) {
            if (args.length != 1 && args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(ChatUtils.format("&dReloading config.."));
                sender.sendMessage(ChatUtils.format("&dChecking for config errors.."));
                if (dataManager.getConfig().getBoolean("database-settings.enabled")) {
                    sender.sendMessage(ChatUtils.format("&dReloading database.."));
                }
                dataManager.reload();
                sender.sendMessage(ChatUtils.format("\n&d&lSafe Drop &dhas successfully reloaded.\n"));
                return true;
            }
            sender.sendMessage("[SafeDrop] Commands can only be ran in-game!");
            return true;
        }

        if (args.length != 1) {
            sendUsage(player);
            return true;
        }

        if (dataManager.getConfig().getBoolean("database-settings.enabled")) {
            handleDatabaseEnabledCommand(player, args);
        } else {
            handleDatabaseDisabledCommand(player, args);
        }

        return true;
    }

    private void handleDatabaseEnabledCommand(Player player, String[] args) {
        val uniqueId = player.getUniqueId();
        val usersTable = plugin.getDatabaseManager().getUsersTable();
        User user = usersTable.getByUuid(uniqueId);

        if (!player.hasPermission(PERMISSION_USE)) {
            sendError(player);
            return;
        }

        switch (args[0].toUpperCase()) {
            case "ON" -> updateUserStatus(user, usersTable, true, "messages.safedrop-on", player);
            case "OFF" -> updateUserStatus(user, usersTable, false, "messages.safedrop-off", player);
            default -> handleCommonCommand(player, args);
        }
    }

    private void handleDatabaseDisabledCommand(Player player, String[] args) {
        DropManager dropManager = DropManager.getInstance();

        if (!player.hasPermission(PERMISSION_USE)) {
            sendError(player);
            return;
        }

        switch (args[0].toUpperCase()) {
            case "ON" -> updateDropStatus(dropManager, player.getUniqueId(), true, "messages.safedrop-on", player);
            case "OFF" -> updateDropStatus(dropManager, player.getUniqueId(), false, "messages.safedrop-off", player);
            default -> handleCommonCommand(player, args);
        }
    }

    private void updateUserStatus(User user, UsersTable usersTable, boolean status, String messageKey, Player player) {
        user.setEnabled(status);
        usersTable.update(user);
        sendMessageByConfigKey(player, messageKey);
    }

    private void updateDropStatus(DropManager dropManager, UUID playerId, boolean status, String messageKey, Player player) {
        dropManager.addDropStatus(playerId, status);
        sendMessageByConfigKey(player, messageKey);
    }

    private void sendMessageByConfigKey(Player player, String configKey) {
        player.sendMessage(ChatUtils.format(DataManager.getInstance().getConfig().getString(configKey)));
    }

    private void handleCommonCommand(Player player, String[] args) {
        DataManager dataManager = DataManager.getInstance();

        switch (args[0].toUpperCase()) {
            case "RELOAD" -> {
                if (player.hasPermission(PERMISSION_ADMIN)) {
                    player.sendMessage(ChatUtils.format("&dReloading config.."));
                    player.sendMessage(ChatUtils.format("&dChecking for config errors.."));
                    if (dataManager.getConfig().getBoolean("database-settings.enabled")) {
                        player.sendMessage(ChatUtils.format("&dReloading database.."));
                    }
                    dataManager.reload();
                    player.sendMessage(ChatUtils.format("\n&d&lSafe Drop &dhas successfully reloaded.\n"));
                } else
                    sendError(player);
            }
            case "REPORTBUG" -> {
                if (player.hasPermission(PERMISSION_ADMIN))
                    player.sendMessage(ChatUtils.format("&9Report issues here: \nhttps://github.com/CodedRed-Spigot/SafeDrop/issues"));
                else
                    sendError(player);
            }
            default -> sendUsage(player);
        }
    }

    private void sendUsage(Player player) {
        for (String msg : DataManager.getInstance().getConfig().getStringList("messages.usage"))
            player.sendMessage(ChatUtils.format(msg));
        if (player.hasPermission(PERMISSION_ADMIN))
            player.sendMessage(ChatUtils.format("&c&lAdmin Command:\n&c/&8sd reload &7- reloads plugin\n&c/&8sd reportbug &7- report a plugin bug"));
    }

    private void sendError(Player player) {
        player.sendMessage(ChatUtils.format(DataManager.getInstance().getConfig().getString("messages.no-permission")));
    }
}
