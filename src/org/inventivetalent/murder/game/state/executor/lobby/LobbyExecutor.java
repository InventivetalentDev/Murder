package org.inventivetalent.murder.game.state.executor.lobby;

import org.bukkit.entity.Player;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.arena.spawn.SpawnType;
import org.inventivetalent.murder.game.CountdownType;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.game.state.executor.CountdownExecutor;
import org.inventivetalent.murder.player.PlayerData;

import java.util.UUID;

public class LobbyExecutor extends CountdownExecutor {

	public LobbyExecutor(Game game) {
		super(game, CountdownType.LOBBY);
		resetTime();
	}

	@Override
	public void tick() {
		super.tick();
		if (!game.waitingForResourcepack.isEmpty()) {
			for (UUID uuid : game.waitingForResourcepack) {
				//Send the resource pack
				Player player = game.getPlayer(uuid);
				if (player != null) {
					player.setResourcePack(Murder.instance.gamePackUrl);//TODO: hash
				}
			}
			game.waitingForResourcepack.clear();
		}
		if (!game.joiningPlayers.isEmpty()) {
			for (UUID uuid : game.joiningPlayers) {
				game.players.add(uuid);

				PlayerData data = Murder.instance.playerManager.getData(uuid);
				data.gameState = GameState.LOBBY;

				data.getPlayer().teleport(game.arena.getFirstSpawnPoint(SpawnType.LOBBY).getLocation(game.arena.getWorld()));
				game.broadcastJoin(uuid);

				game.waitingForResourcepack.add(uuid);
			}
			game.joiningPlayers.clear();

			Murder.instance.gameManager.refreshSigns(game);
		}
	}

	@Override
	public boolean revert() {
		//All players left, go back to WAITING
		return game.players.isEmpty();
	}
}
