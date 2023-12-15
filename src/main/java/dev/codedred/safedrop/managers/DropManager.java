package dev.codedred.safedrop.managers;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.DataManager;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DropManager {

  private static DropManager instance;

  private final Map<UUID, Boolean> dropMap;
  private final List<UUID> requestList;

  private boolean whitelistEnchantedItems;
  private boolean whitelistEnabled;
  private List<String> whitelist;

  public static DropManager getInstance() {
    if (instance == null) instance = new DropManager();
    return instance;
  }

  private DropManager() {
    dropMap = new ConcurrentHashMap<>();
    requestList = new CopyOnWriteArrayList<>();
    whitelist = new CopyOnWriteArrayList<>();
    whitelistEnabled = false;
    whitelistEnchantedItems = false;
  }

  public void addRequest(UUID uuid) {
    requestList.add(uuid);
    startRequestDelay(uuid);
  }

  public void removeRequest(UUID uuid) {
    requestList.remove(uuid);
  }

  public boolean hasRequested(UUID uuid) {
    return requestList.contains(uuid);
  }

  public void addDropStatus(UUID uuid, boolean drop) {
    dropMap.put(uuid, drop);
  }

  public void removeDropStatus(UUID uuid) {
    dropMap.remove(uuid);
  }

  public boolean getStatus(UUID uuid) {
    return dropMap.get(uuid);
  }

  public void setWhitelist(List<String> whitelist) {
    this.whitelist = new CopyOnWriteArrayList<>(whitelist);
  }

  public List<String> getWhitelist() {
    return whitelist;
  }

  public boolean isWhitelisted(String item) {
    return whitelist.contains(item.toUpperCase());
  }

  public void setWhitelist(boolean whitelist) {
    this.whitelistEnabled = whitelist;
  }

  public boolean isWhitelistEnabled() {
    return whitelistEnabled;
  }

  public void setEnchantedItemsWhitelist(boolean whitelist) {
    this.whitelistEnchantedItems = whitelist;
  }

  public boolean isEnchantedItemsWhitelisted() {
    return whitelistEnchantedItems;
  }

  private void startRequestDelay(UUID uuid) {
    DataManager dataManager = DataManager.getInstance();
    new BukkitRunnable() {
      @Override
      public void run() {
        if (hasRequested(uuid)) removeRequest(uuid);
        cancel();
      }
    }
      .runTaskLaterAsynchronously(
        JavaPlugin.getPlugin(SafeDrop.class),
        dataManager.getConfig().getLong("safe-drop.seconds") * 20L - 15L
      );
  }
}
