package dev.codedred.safedrop.managers;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.DataManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DropManager {

	private static DropManager instance = null;

	private final Map<UUID, Boolean> dropMap;
	private final List<UUID> requestList;

	public static DropManager getInstance() {
		if (instance == null) 
			instance = new DropManager();
		return instance;
	}

	private DropManager() {
		dropMap = new HashMap<>();
		requestList = new ArrayList<>();
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


	private void startRequestDelay(UUID uuid) {
		DataManager dataManager = DataManager.getInstance();
		new BukkitRunnable() {
			int count = 0;
			final long end = dataManager.getConfig().getLong("safe-drop.seconds") * 20L - 15L;
			@Override
			public void run() {
				if (count >= end) {
					if (hasRequested(uuid))
						removeRequest(uuid);
					cancel();
				}
				count++;
			}
		}.runTaskAsynchronously(JavaPlugin.getPlugin(SafeDrop.class));
	}
}
