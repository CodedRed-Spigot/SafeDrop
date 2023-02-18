package dev.codedred.safedrop.listeners;

import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.managers.DropManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuit implements Listener {

    private static final String HEAD = "saves.";

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DataManager dataManager = DataManager.getInstance();
        DropManager dropManager = DropManager.getInstance();

        boolean exists = dataManager.getSaves().contains(HEAD + event.getPlayer().getUniqueId());
        if (exists)
            dropManager.addDropStatus(event.getPlayer().getUniqueId(),
                    dataManager.getSaves().getBoolean(HEAD + event.getPlayer().getUniqueId()));
        else
            dropManager.addDropStatus(event.getPlayer().getUniqueId(), dataManager.getConfig().getBoolean("safe-drop.enabled"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DataManager dataManager = DataManager.getInstance();
        DropManager dropManager = DropManager.getInstance();
        dataManager.getSaves().set(HEAD + event.getPlayer().getUniqueId(), dropManager.getStatus(event.getPlayer().getUniqueId()));
        dataManager.saveSaves();

        dropManager.removeDropStatus(event.getPlayer().getUniqueId());
    }
}
