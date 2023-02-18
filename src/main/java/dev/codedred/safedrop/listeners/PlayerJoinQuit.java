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
        boolean status = dataManager.getSaves().contains(HEAD + event.getPlayer().getUniqueId())
                && dataManager.getSaves().getBoolean(HEAD + event.getPlayer().getUniqueId());

        DropManager dropManager = DropManager.getInstance();
        dropManager.addDropStatus(event.getPlayer().getUniqueId(), status);
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
