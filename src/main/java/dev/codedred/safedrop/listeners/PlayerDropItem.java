package dev.codedred.safedrop.listeners;

import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.utils.ChatUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.UUID;

public class PlayerDropItem implements Listener {
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		if (!event.getPlayer().hasPermission("sd.use"))
			return;

		DropManager dropManager = DropManager.getInstance();
		DataManager dataManager = DataManager.getInstance();
		UUID uuid = event.getPlayer().getUniqueId();

		// already tried to drop item, this is them confirming it.
		if (dropManager.hasRequested(uuid)) {
			dropManager.removeRequest(uuid);
			return;
		}

		// send confirm message if safe drop is enabled.
		if (dropManager.getStatus(uuid)) {
			dropManager.addRequest(uuid);

			event.setCancelled(true);
			Player player = event.getPlayer();

			if (dataManager.getConfig().getBoolean("safe-drop.text-message.enabled"))
				player.sendMessage(ChatUtil.format(dataManager.getConfig().getString("messages.drop-text-message")));

			if (dataManager.getConfig().getBoolean("safe-drop.actionbar-message.enabled")) {
				TextComponent message = new TextComponent(ChatUtil.format(dataManager.getConfig().getString("messages.drop-actionbar-message")));
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
			}
		}
	}

}
