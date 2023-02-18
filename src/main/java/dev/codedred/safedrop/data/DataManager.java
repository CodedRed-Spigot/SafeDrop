package dev.codedred.safedrop.data;

import org.bukkit.configuration.file.FileConfiguration;

import dev.codedred.safedrop.SafeDrop;

public class DataManager {

	private static DataManager instance = null;
	
	public static DataManager getInstance() {
		if (instance == null)
			instance = new DataManager();
		return instance;
	}
	
	private final CustomFile config = new CustomFile(SafeDrop.getPlugin(SafeDrop.class), "config.yml");
	private final CustomFile saves = new CustomFile(SafeDrop.getPlugin(SafeDrop.class), "saves.yml");
	
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


	// lol 5head naming
	public void saveSaves() {
		saves.saveConfig();
	}
	
}
