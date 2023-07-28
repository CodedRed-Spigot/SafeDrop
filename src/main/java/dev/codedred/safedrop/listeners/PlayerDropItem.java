package dev.codedred.safedrop.listeners;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.utils.chat.ChatUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.UUID;

public class PlayerDropItem implements Listener {

	private final SafeDrop plugin;

	public PlayerDropItem(SafeDrop plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		if (!event.getPlayer().hasPermission("sd.use"))
			return;

		if (event.getPlayer().getInventory().firstEmpty() == -1)
			return;

		DropManager dropManager = DropManager.getInstance();
		DataManager dataManager = DataManager.getInstance();
		UUID uuid = event.getPlayer().getUniqueId();

		// already tried to drop item, this is them confirming it.
		if (dropManager.hasRequested(uuid))
			return;

		// send confirm message if safe drop is enabled.
		if (plugin.getDatabaseManager().getUsersTable().getByUuid(event.getPlayer().getUniqueId()).isEnabled()) {
			dropManager.addRequest(uuid);

			event.setCancelled(true);
			Player player = event.getPlayer();

			if (dataManager.getConfig().getBoolean("safe-drop.text-message.enabled"))
				player.sendMessage(ChatUtils.format(dataManager.getConfig().getString("messages.drop-text-message")));

			if (dataManager.getConfig().getBoolean("safe-drop.actionbar-message.enabled")) {
				TextComponent message = new TextComponent(ChatUtils.format(dataManager.getConfig().getString("messages.drop-actionbar-message")));
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
			}
		}
	}

}
