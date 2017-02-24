package org.inventivetalent.murder.game.state.executor.ended;

import com.google.common.base.Predicate;
import org.bukkit.entity.Item;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.game.state.executor.LeavableExecutor;
import org.inventivetalent.murder.player.PlayerData;

public class ResetExecutor extends LeavableExecutor {

	int ticks = 0;

	public ResetExecutor(Game game) {
		super(game);
	}

	@Override
	public void tick() {
		super.tick();
		if (ticks == 0) {
			updatePlayerStates(GameState.RESET, new Predicate<PlayerData>() {
				@Override
				public boolean apply(PlayerData playerData) {
					if (playerData.getOfflinePlayer().isOnline()) {
						game.leavingPlayers.add(playerData.uuid);
						//						//Restore data and delete data file
						//						playerData.restoreData();
						////						Murder.instance.playerManager.deleteDataFile(playerData.uuid);
						//
						//						//Reset resource pack
						//						ResourcePackAPI.setResourcepack(playerData.getPlayer(), Murder.instance.resetPackUrl, Murder.instance.resetPackHash);
					}

					return true;
				}
			});

			//Remove dropped items
			for (Item item : game.droppedItems) {
				item.remove();
			}

			//Despawn corpses
			Murder.instance.corpseManager.removeCorpses(game);
		}
		ticks++;
	}

	@Override
	public boolean finished() {
		return super.finished() || ticks > 2;
	}
}
