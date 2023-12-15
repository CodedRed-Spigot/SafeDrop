package dev.codedred.safedrop;

import dev.codedred.safedrop.commands.Drop;
import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.data.database.manager.DatabaseManager;
import dev.codedred.safedrop.listeners.PlayerDropItem;
import dev.codedred.safedrop.listeners.PlayerJoinQuit;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.utils.Items;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SafeDrop extends JavaPlugin {

  @Getter
  private DatabaseManager databaseManager;

  @Override
  public void onEnable() {
    getLogger().info("SafeDrop is enabling");
    checkForUpdate();
    DataManager.getInstance().checkAndFixConfigKeys();

    registerCommands();
    registerListeners();

    this.loadDatabase();

    Collection<? extends Player> onlinePlayers = getServer().getOnlinePlayers();
    if (!onlinePlayers.isEmpty()) {
      for (Player p : onlinePlayers) {
        PlayerJoinQuit.initalizePlayerDropStatus(p, this);
      }
    }
    if (
      DataManager
        .getInstance()
        .getWhitelist()
        .getBoolean("whitelist-settings.enabled")
    ) {
      getLogger().warning("Loading whitelist...");
      loadWhitelist();
    }
  }

  public void loadDatabase() {
    if (
      DataManager
        .getInstance()
        .getConfig()
        .getBoolean("database-settings.enabled")
    ) {
      if (
        this.databaseManager != null &&
        this.databaseManager.getDataSource().getConnection() != null
      ) {
        try {
          this.databaseManager.getDataSource().closeConnection();
        } catch (Exception e) {
          getLogger()
            .warning("Error while trying to close Database connection..");
        }
      }
      try {
        this.databaseManager = new DatabaseManager(this);
      } catch (Exception e) {
        getLogger().warning("Disabling SafeDrop...");
        getServer().getPluginManager().disablePlugin(this);
      }
      this.databaseManager.load();
    }
  }

  public static void loadWhitelist() {
    DataManager dataManager = DataManager.getInstance();
    DropManager dropManager = DropManager.getInstance();

    ConfigurationSection whitelistSettings = dataManager
      .getWhitelist()
      .getConfigurationSection("whitelist-settings");
    dropManager.setWhitelist(true);

    Bukkit
      .getScheduler()
      .runTaskAsynchronously(
        SafeDrop.getPlugin(SafeDrop.class),
        () -> {
          Set<String> whitelist = new HashSet<>();

          ConfigurationSection whitelistSection = dataManager
            .getWhitelist()
            .getConfigurationSection("whitelist");
          if (whitelistSection != null) {
            Set<String> keys = whitelistSection.getKeys(false);
            for (String item : keys) {
              if (whitelistSection.getBoolean(item)) {
                whitelist.add(item);
              }
            }
          }

          if (whitelistSettings.getBoolean("whitelist-all.tools")) {
            whitelist.addAll(Items.getAllTools());
          }
          if (whitelistSettings.getBoolean("whitelist-all.spawn-eggs")) {
            whitelist.addAll(Items.getAllSpawnEggs());
          }
          if (whitelistSettings.getBoolean("whitelist-all.armor")) {
            whitelist.addAll(Items.getAllArmor());
          }
          if (whitelistSettings.getBoolean("whitelist-all.enchanted-items")) {
            dropManager.setEnchantedItemsWhitelist(true);
          }

          dropManager.setWhitelist(new ArrayList<>(whitelist));
          Bukkit
            .getLogger()
            .info("[SafeDrop] Successfully loaded item whitelist.");
        }
      );
  }

  @Override
  public void onDisable() {
    getLogger().info("SafeDrop is disabling..");
  }

  private void registerCommands() {
    Objects.requireNonNull(getCommand("safedrop")).setExecutor(new Drop(this));
  }

  private void registerListeners() {
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(new PlayerDropItem(), this);
    pm.registerEvents(new PlayerJoinQuit(this), this);
  }

  private void checkForUpdate() {
    getServer()
      .getScheduler()
      .runTaskAsynchronously(
        this,
        () -> {
          UpdateChecker updater = new UpdateChecker(this, 72585);
          try {
            if (updater.checkForUpdates()) {
              getLogger()
                .info("=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
              getLogger()
                .warning("You are using an older version of SafeDrop!");
              getLogger().info("Download the newest version here:");
              getLogger().info("https://www.spigotmc.org/resources/72585/");
              getLogger()
                .info("=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
            } else {
              getLogger()
                .info(
                  "[SafeDrop] Plugin is up to date! - " +
                  getDescription().getVersion()
                );
            }
          } catch (Exception e) {
            getLogger().info("[SafeDrop] Could not check for updates!");
          }
        }
      );
  }
}
