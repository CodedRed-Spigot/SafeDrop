package dev.codedred.safedrop.listeners;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.model.User;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuit implements Listener {

    private static final String HEAD = "saves.";
    SafeDrop plugin;

    public PlayerJoinQuit(SafeDrop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DataManager dataManager = DataManager.getInstance();
        DropManager dropManager = DropManager.getInstance();

        val uniqueId = event.getPlayer().getUniqueId();
        val usersTable = plugin.getDatabaseManager().getUsersTable();
        User user = usersTable.getByUuid(uniqueId);

        if (dataManager.getConfig().getBoolean("MySQL")) {

            if (user == null) {
                User newUser = new User(event.getPlayer().getUniqueId(), dataManager.getConfig().getBoolean("safe-drop.enabled"));
                usersTable.insert(newUser);
                return;
            }

            if (user.isEnabled()) {
                dropManager.addDropStatus(event.getPlayer().getUniqueId(), user.isEnabled());
            }
            return;
        }

        boolean exists = dataManager.getSaves().contains(HEAD + event.getPlayer().getUniqueId());
        if (exists)
            dropManager.addDropStatus(event.getPlayer().getUniqueId(),
                    dataManager.getSaves().getBoolean(HEAD + event.getPlayer().getUniqueId()));
        else
            dropManager.addDropStatus(event.getPlayer().getUniqueId(), dataManager.getConfig().getBoolean("safe-drop.enabled"));

        return;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DataManager dataManager = DataManager.getInstance();
        DropManager dropManager = DropManager.getInstance();

        if (!dataManager.getConfig().getBoolean("MySQL")) {

            dataManager.getSaves().set(HEAD + event.getPlayer().getUniqueId(), dropManager.getStatus(event.getPlayer().getUniqueId()));
            dataManager.saveSaves();

        }

        dropManager.removeDropStatus(event.getPlayer().getUniqueId());
    }
}
