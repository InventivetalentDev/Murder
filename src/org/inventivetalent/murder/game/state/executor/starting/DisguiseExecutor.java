package org.inventivetalent.murder.game.state.executor.starting;

import com.google.common.base.Predicate;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.CountdownType;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.game.state.executor.CountdownExecutor;
import org.inventivetalent.murder.player.PlayerData;

public class DisguiseExecutor extends CountdownExecutor {

	public boolean firstTick = true;

	public DisguiseExecutor(Game game) {
		super(game, CountdownType.START);
	}

	@Override
	public void tick() {
		super.tick();
		if (firstTick) {
			firstTick = false;

			updatePlayerStates(GameState.DISGUISE, new Predicate<PlayerData>() {
				@Override
				public boolean apply(PlayerData playerData) {
					Murder.instance.playerManager.disguisePlayer(playerData.getPlayer(), Murder.instance.nameManager.randomName());
					return true;
				}
			});
		}
	}

	@Override
	public boolean finished() {
		return super.finished() || !firstTick;
	}
}
