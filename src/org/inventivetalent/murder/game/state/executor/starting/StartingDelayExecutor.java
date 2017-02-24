package org.inventivetalent.murder.game.state.executor.starting;

import org.inventivetalent.murder.game.CountdownType;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.executor.CountdownExecutor;

public class StartingDelayExecutor extends CountdownExecutor {

	public StartingDelayExecutor(Game game) {
		super(game, CountdownType.START);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean finished() {
		return super.finished();
	}
}
