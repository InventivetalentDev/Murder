package org.inventivetalent.murder.game.state.executor.ingame;

import com.google.common.base.Predicate;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.inventivetalent.murder.Role;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.player.PlayerData;

import java.util.UUID;

public class StartedExecutor extends IngameExecutor {

	boolean firstTick = true;

	public StartedExecutor(Game game) {
		super(game);
	}

	@Override
	public void tick() {
		super.tick();

		if (firstTick) {
			firstTick = false;

			updatePlayerStates(GameState.STARTED, new Predicate<PlayerData>() {
				@Override
				public boolean apply(PlayerData playerData) {
					Player player = playerData.getPlayer();

					//Remove black screen
					player.getInventory().setHelmet(null);

					//Clear effects
					for (PotionEffect effect : player.getActivePotionEffects()) {
						player.removePotionEffect(effect.getType());
					}
					for (UUID uuid1 : game.players) {
						Player player1 = game.getPlayer(uuid1);
						if (player1 != null) { player1.showPlayer(playerData.getPlayer()); }
					}

					player.setWalkSpeed(0.2f);
					if (playerData.role != Role.MURDERER) {
						player.setFoodLevel(6);
					}
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
