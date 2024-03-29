package dev.codedred.safedrop.listeners;

import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.utils.chat.ChatUtils;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDropItem implements Listener {

  @EventHandler
  public void onItemDrop(PlayerDropItemEvent event) {
    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
    if (!event.getPlayer().hasPermission("sd.use")) return;

    if (event.getPlayer().getInventory().firstEmpty() == -1) return;

    DropManager dropManager = DropManager.getInstance();
    DataManager dataManager = DataManager.getInstance();
    UUID uuid = event.getPlayer().getUniqueId();

    if (dropManager.hasRequested(uuid)) return;

    if (dropManager.isWhitelistEnabled() && dropManager.getStatus(uuid)) {
      ItemStack itemInHand = event.getItemDrop().getItemStack();
      String itemName = itemInHand.getType().name();
      boolean isEnchantedItemsWhitelisted = dropManager.isEnchantedItemsWhitelisted();
      boolean itemEnchanted = !itemInHand.getEnchantments().isEmpty();

      if (
        dropManager.isWhitelisted(itemName) ||
        (isEnchantedItemsWhitelisted && itemEnchanted)
      ) {
        event.setCancelled(true);
        handlePlayerDrop(event.getPlayer(), uuid, dataManager, dropManager);
      }
    } else if (dropManager.getStatus(uuid)) {
      event.setCancelled(true);
      handlePlayerDrop(event.getPlayer(), uuid, dataManager, dropManager);
    }
  }

  private void handlePlayerDrop(
    Player player,
    UUID uuid,
    DataManager dataManager,
    DropManager dropManager
  ) {
    dropManager.addRequest(uuid);

    if (dataManager.getConfig().getBoolean("safe-drop.text-message.enabled")) {
      player.sendMessage(
        ChatUtils.format(
          dataManager.getConfig().getString("messages.drop-text-message")
        )
      );
    }

    if (
      dataManager.getConfig().getBoolean("safe-drop.actionbar-message.enabled")
    ) {
      TextComponent message = new TextComponent(
        ChatUtils.format(
          dataManager.getConfig().getString("messages.drop-actionbar-message")
        )
      );
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
    }
  }
}
