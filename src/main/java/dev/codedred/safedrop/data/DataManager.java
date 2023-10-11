package dev.codedred.safedrop.data;

import org.bukkit.configuration.file.FileConfiguration;

import dev.codedred.safedrop.SafeDrop;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class DataManager {

	private static DataManager instance = null;
	private final CustomFile config;
	private final CustomFile saves;

	private DataManager() {
		config = new CustomFile(JavaPlugin.getPlugin(SafeDrop.class), "config.yml");
		saves = new CustomFile(JavaPlugin.getPlugin(SafeDrop.class), "saves.yml");
	}

	public static DataManager getInstance() {
		if (instance == null)
			instance = new DataManager();
		return instance;
	}

	public void checkAndFixConfigKeys() {
		FileConfiguration cfg = config.getConfig();
		boolean modified = false;

		if (!cfg.contains("safe-drop.enabled")) {
			cfg.set("safe-drop.enabled", true);
			modified = true;
		}
		if (!cfg.contains("safe-drop.seconds")) {
			cfg.set("safe-drop.seconds", 10);
			modified = true;
		}
		if (!cfg.contains("safe-drop.text-message.enabled")) {
			cfg.set("safe-drop.text-message.enabled", true);
			modified = true;
		}
		if (!cfg.contains("safe-drop.actionbar-message.enabled")) {
			cfg.set("safe-drop.actionbar-message.enabled", true);
			modified = true;
		}

		if (!cfg.contains("database-settings.enabled")) {
			cfg.set("database-settings.enabled", false);
			modified = true;
		}
		if (!cfg.contains("database-settings.type")) {
			cfg.set("database-settings.type", "mysql");
			modified = true;
		}
		if (!cfg.contains("database-settings.host")) {
			cfg.set("database-settings.host", "localhost");
			modified = true;
		}
		if (!cfg.contains("database-settings.port")) {
			cfg.set("database-settings.port", 3306);
			modified = true;
		}
		if (!cfg.contains("database-settings.user")) {
			cfg.set("database-settings.user", "root");
			modified = true;
		}
		if (!cfg.contains("database-settings.password")) {
			cfg.set("database-settings.password", "password");
			modified = true;
		}
		if (!cfg.contains("database-settings.database")) {
			cfg.set("database-settings.database", "server1");
			modified = true;
		}

		if (!cfg.contains("messages.no-permission")) {
			cfg.set("messages.no-permission", "&cYou do not have permission to do this!");
			modified = true;
		}
		if (!cfg.contains("messages.drop-text-message")) {
			cfg.set("messages.drop-text-message", "&3&lConfirm drop &r&9by dropping item again!");
			modified = true;
		}
		if (!cfg.contains("messages.drop-actionbar-message")) {
			cfg.set("messages.drop-actionbar-message", "&3&lConfirm drop &r&9by dropping item again!");
			modified = true;
		}
		if (!cfg.contains("messages.safedrop-off")) {
			cfg.set("messages.safedrop-off", "&c&lSafeDrop has been disabled!");
			modified = true;
		}
		if (!cfg.contains("messages.safedrop-on")) {
			cfg.set("messages.safedrop-on", "&a&lSafeDrop has been enabled!");
			modified = true;
		}
		if (!cfg.contains("messages.usage")) {
			List<String> usage = Arrays.asList("&9&lTry,", "&9/&8Safedrop &7- &oTurns on/off safedrop");
			cfg.set("messages.usage", usage);
			modified = true;
		}

		if (modified) {
			saveConfig();
			JavaPlugin.getPlugin(SafeDrop.class).getLogger().info("[SafeDrop] config.yml was missing some values! Adding them now..");
		}
	}

	public FileConfiguration getConfig() {
		return config.getConfig();
	}

	public FileConfiguration getSaves() {
		return saves.getConfig();
	}

	public void reload() {
		config.reloadConfig();
		saves.reloadConfig();
	}

	public void saveConfig() {
		config.saveConfig();
	}

	public void saveSaves() {
		saves.saveConfig();
	}
}
