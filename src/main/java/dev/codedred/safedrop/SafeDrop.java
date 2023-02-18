package dev.codedred.safedrop;

import dev.codedred.safedrop.commands.Drop;
import dev.codedred.safedrop.listeners.PlayerDropItem;
import dev.codedred.safedrop.listeners.PlayerJoinQuit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SafeDrop extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("SafeDrop is enabling");
        checkForUpdate();


        registerCommands();
        registerListeners();

    }

    @Override
    public void onDisable() {
        getLogger().info("SafeDrop is disabling");
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("safedrop")).setExecutor(new Drop());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerDropItem(), this);
        pm.registerEvents(new PlayerJoinQuit(), this);
    }
    private void checkForUpdate() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            UpdateChecker updater = new UpdateChecker(this, 72585);
            try {
                if (updater.checkForUpdates()) {
                    getLogger().info("=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                    getLogger().warning("You are using an older version of SafeDrop!");
                    getLogger().info("Download the newest version here:");
                    getLogger().info("https://www.spigotmc.org/resources/72585/");
                    getLogger().info("=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                } else {
                    getLogger().info("[SafeDrop] Plugin is up to date! - "
                            + getDescription().getVersion());
                }
            } catch (Exception e) {
                getLogger().info("[SafeDrop] Could not check for updates!");
            }
        });
    }

}
