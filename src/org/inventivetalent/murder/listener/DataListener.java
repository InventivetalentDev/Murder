package org.inventivetalent.murder.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.player.PlayerData;

public class DataListener implements Listener {

	private Murder plugin;

	public DataListener(Murder plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final PlayerData playerData = plugin.playerManager.loadFromFile(event.getPlayer().getUniqueId());
		if (playerData.stored) {
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					if (event.getPlayer().isOnline() && playerData.stored) { playerData.restoreData(); }
				}
			}, 10);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		PlayerData playerData = plugin.playerManager.getData(event.getPlayer().getUniqueId());
		if (playerData != null && playerData.getGame() != null) {
			playerData.getGame().leavingPlayers.add(event.getPlayer().getUniqueId());
		}
	}

}
