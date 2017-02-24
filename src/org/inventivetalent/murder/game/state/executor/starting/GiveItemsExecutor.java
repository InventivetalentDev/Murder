package org.inventivetalent.murder.game.state.executor.starting;

import com.google.common.base.Predicate;
import org.bukkit.entity.Player;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.Role;
import org.inventivetalent.murder.game.CountdownType;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.game.state.executor.CountdownExecutor;
import org.inventivetalent.murder.player.PlayerData;

public class GiveItemsExecutor extends CountdownExecutor {

	boolean firstTick = true;

	public GiveItemsExecutor(Game game) {
		super(game, CountdownType.START);
	}

	@Override
	public void tick() {
		super.tick();

		if (firstTick) {
			firstTick = false;

			updatePlayerStates(GameState.GIVE_ITEMS, new Predicate<PlayerData>() {
				@Override
				public boolean apply(PlayerData data) {
					Player player = data.getPlayer();

					if (data.role == Role.DEFAULT) {
						player.getInventory().setItem(8, Murder.instance.itemManager.getSpeedBoost());
					}
					if (data.role == Role.WEAPON) {
						player.getInventory().setItem(4, Murder.instance.itemManager.getGun());
						player.getInventory().setItem(8, Murder.instance.itemManager.getBullet());
					}

					if (data.role == Role.MURDERER) {
						player.getInventory().setItem(4, Murder.instance.itemManager.getKnife());
					}

					return true;
				}
			});
		}
	}

	@Override
	public boolean finished() {
		return super.finished() && !firstTick;
	}
}
