package dev.codedred.safedrop.listeners;

import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.managers.DropManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuit implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DataManager manager = DataManager.getInstance();
        boolean status = manager.getSaves().contains("saves." + event.getPlayer().getUniqueId())
                && manager.getSaves().getBoolean("saves." + event.getPlayer().getUniqueId());

        DropManager.getInstance().addDropStatus(event.getPlayer().getUniqueId(), status);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DropManager dropManager = DropManager.getInstance();
        DataManager.getInstance().getSaves().set("saves." + event.getPlayer().getUniqueId(), dropManager.getStatus(event.getPlayer().getUniqueId()));
        DataManager.getInstance().saveSaves();

        DropManager.getInstance().removeDropStatus(event.getPlayer().getUniqueId());
    }
}
