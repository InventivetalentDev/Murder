package org.inventivetalent.murder.listener;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.player.PlayerData;

public class SpectatorListener implements Listener {

	private Murder plugin;

	public SpectatorListener(Murder plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		ItemStack itemStack = event.getItem();
		if (itemStack != null && plugin.itemManager.getTeleporter().equals(itemStack)) {
			PlayerData data = Murder.instance.playerManager.getData(event.getPlayer().getUniqueId());
			if (data != null) {
				if (data.isInGame() && data.isSpectator) {
					if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
						plugin.spectateManager.teleportToClosestPlayer(data);
					} else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						plugin.spectateManager.openSpectatorMenu(data);
					}
				}
			}
		}
	}

	@EventHandler
	public void on(PlayerToggleSneakEvent event) {
		PlayerData playerData = plugin.playerManager.getData(event.getPlayer().getUniqueId());
		if (playerData != null) {
			if (playerData.isInGame() && playerData.isSpectator) {
				//Reset the gamemode when the player "leaves" their target
				event.getPlayer().setGameMode(GameMode.ADVENTURE);
				event.getPlayer().setAllowFlight(true);
				event.getPlayer().setFlying(true);
			}
		}
	}

}
