package org.inventivetalent.murder.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.player.PlayerData;

public class GameListenerArrow implements Listener {

	private Murder plugin;

	public GameListenerArrow(Murder plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void on(PlayerPickupArrowEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.playerManager.getData(player.getUniqueId());
		if (data != null) {
			if (data.isInGame()) {
				if (event.getArrow() != null) {
					if (event.getArrow().hasMetadata("MURDER")) {
						event.setCancelled(true);
						event.getArrow().remove();
					}
				}
			}
		}
	}
}
