package org.inventivetalent.murder.game.state.executor.init;

import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.executor.LeavableExecutor;

public class WaitingExecutor extends LeavableExecutor {

	public WaitingExecutor(Game game) {
		super(game);
	}

	@Override
	public boolean finished() {
		//If there are players, go to LOBBY
		return !game.players.isEmpty() || !game.joiningPlayers.isEmpty();
	}

	@Override
	public boolean revert() {
		//If no players are waiting, go "back" to DISPOSE
		return game.players.isEmpty() && game.joiningPlayers.isEmpty();
	}
}
