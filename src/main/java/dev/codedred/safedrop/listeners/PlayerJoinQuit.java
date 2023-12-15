package dev.codedred.safedrop.listeners;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.model.User;
import lombok.val;
import org.bukkit.entity.Player;
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

  public static void initalizePlayerDropStatus(Player player, SafeDrop plugin) {
    DataManager dataManager = DataManager.getInstance();
    DropManager dropManager = DropManager.getInstance();
    if (
      dataManager.getConfig().getBoolean("database-settings.enabled") &&
      plugin.getDatabaseManager().getDataSource().getConnection() != null
    ) {
      val uniqueId = player.getUniqueId();
      val usersTable = plugin.getDatabaseManager().getUsersTable();
      User user = usersTable.getByUuid(uniqueId);

      if (user == null) {
        user =
          new User(
            player.getUniqueId(),
            dataManager.getConfig().getBoolean("safe-drop.enabled")
          );
        usersTable.insert(user);
      }

      dropManager.addDropStatus(player.getUniqueId(), user.isEnabled());
    } else {
      boolean exists = dataManager
        .getSaves()
        .contains(HEAD + player.getUniqueId());
      if (exists) dropManager.addDropStatus(
        player.getUniqueId(),
        dataManager.getSaves().getBoolean(HEAD + player.getUniqueId())
      ); else dropManager.addDropStatus(
        player.getUniqueId(),
        dataManager.getConfig().getBoolean("safe-drop.enabled")
      );
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    initalizePlayerDropStatus(event.getPlayer(), plugin);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    DataManager dataManager = DataManager.getInstance();
    DropManager dropManager = DropManager.getInstance();

    if (
      !dataManager.getConfig().getBoolean("database-settings.enabled") ||
      (
        dataManager.getConfig().getBoolean("database-settings.enabled") &&
        plugin.getDatabaseManager().getDataSource().getConnection() == null
      )
    ) {
      dataManager
        .getSaves()
        .set(
          HEAD + event.getPlayer().getUniqueId(),
          dropManager.getStatus(event.getPlayer().getUniqueId())
        );
      dataManager.saveSaves();
    }

    dropManager.removeDropStatus(event.getPlayer().getUniqueId());
  }
}
