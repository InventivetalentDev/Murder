package org.inventivetalent.murder.game.state.executor.starting;

import com.google.common.base.Predicate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.inventivetalent.murder.game.CountdownType;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.game.state.executor.CountdownExecutor;
import org.inventivetalent.murder.player.PlayerData;

public class StartingExecutor extends CountdownExecutor {

	boolean firstTick = true;

	public StartingExecutor(Game game) {
		super(game, CountdownType.START);
		resetTime();
	}

	@Override
	public void tick() {
		super.tick();
		if (firstTick) {
			firstTick = false;

			updatePlayerStates(GameState.STARTING, new Predicate<PlayerData>() {
				@Override
				public boolean apply(PlayerData playerData) {
					Player player = playerData.getPlayer();

					//Make the screen dark
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 2, false, false));
					player.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));

					//Prevent movement
					player.setWalkSpeed(0);
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 250, false, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 255, false, false));

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
