package org.inventivetalent.murder.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.player.PlayerData;

public class ProtectionListener implements Listener {

	private Murder plugin;

	public ProtectionListener(Murder plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		PlayerData playerData = plugin.playerManager.getData(player.getUniqueId());
		if (playerData != null && playerData.getGame() != null) {
			if (playerData.getGame().arena.contains(event.getBlock().getLocation().toVector())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		PlayerData playerData = plugin.playerManager.getData(player.getUniqueId());
		if (playerData != null && playerData.getGame() != null) {
			if (playerData.getGame().arena.contains(event.getBlock().getLocation().toVector())) {
				event.setCancelled(true);
			}
		}
	}

}
