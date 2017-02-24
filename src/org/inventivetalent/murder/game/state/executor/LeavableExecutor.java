package org.inventivetalent.murder.game.state.executor;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.StateExecutor;
import org.inventivetalent.murder.player.PlayerData;

import java.util.UUID;

public class LeavableExecutor extends StateExecutor {

	public LeavableExecutor(Game game) {
		super(game);
	}

	@Override
	public void tick() {
		if (!game.leavingPlayers.isEmpty()) {
			for (UUID uuid : game.leavingPlayers) {
				game.players.remove(uuid);
				game.broadcastLeave(uuid);

				PlayerData data = Murder.instance.playerManager.getData(uuid);
				if (data != null) {
					data.gameId = null;
					if (data.getOfflinePlayer().isOnline()) {
						//Remove black screen
						data.getPlayer().getInventory().setHelmet(null);

						//Clear effects
						for (PotionEffect effect : data.getPlayer().getActivePotionEffects()) {
							data.getPlayer().removePotionEffect(effect.getType());
						}

						data.restoreData();
						Murder.instance.playerManager.resetPlayer(data.getOfflinePlayer());

						for (UUID uuid1 : game.players) {
							Player player = game.getPlayer(uuid1);
							if (player != null) { player.showPlayer(data.getPlayer()); }
						}

						//Reset resource pack
						data.getPlayer().setResourcePack(Murder.instance.resetPackUrl);//TODO: hash

						//Reset BossBar
						if (data.bossBar != null) {
							data.bossBar.removePlayer(data.getPlayer());
						}

						Murder.instance.playerManager.removeData(uuid);
					}
				}
			}
			game.leavingPlayers.clear();

			Murder.instance.gameManager.refreshSigns(game);
		}

	}

	@Override
	public boolean finished() {
		return super.finished();
	}
}
