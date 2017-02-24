package org.inventivetalent.murder.game.state;

import com.google.common.base.Predicate;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.player.PlayerData;

import java.util.UUID;

public class StateExecutor {

	public final Game game;

	public StateExecutor(Game game) {
		this.game = game;
	}

	public void updatePlayerStates(GameState state, Predicate<PlayerData> updatedPlayerCallback) {
		for (UUID uuid : game.players) {
			PlayerData data = Murder.instance.playerManager.getData(uuid);
			if (data != null) {
				if (data.gameState != state) {
					if (updatedPlayerCallback != null) {
						if (updatedPlayerCallback.apply(data)) {
							data.gameState = state;
						}
					} else {
						data.gameState = state;
					}
				}
			}
		}
	}

	public void tick() {
	}

	/**
	 * @return <code>true</code> if state didn't finish and we need to go back to the previous state
	 */
	public boolean revert() {
		return false;
	}

	/**
	 * @return <code>true</code> if this state finished and we can move on to the next state
	 */
	public boolean finished() {
		return false;
	}

}
