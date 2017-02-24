package org.inventivetalent.murder.game.state.executor.starting;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.bukkit.Location;
import org.inventivetalent.murder.arena.spawn.SpawnPoint;
import org.inventivetalent.murder.arena.spawn.SpawnType;
import org.inventivetalent.murder.game.CountdownType;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.game.state.executor.CountdownExecutor;
import org.inventivetalent.murder.player.PlayerData;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class TeleportExecutor extends CountdownExecutor {

	boolean firstTick = true;

	public TeleportExecutor(Game game) {
		super(game, CountdownType.START);
	}

	@Override
	public void tick() {
		super.tick();
		if (firstTick) {
			firstTick = false;

			final Set<SpawnPoint> spawnPoints = game.arena.getSpawnPoints(SpawnType.PLAYER);
			final Iterator<SpawnPoint> iterator = Iterables.cycle(spawnPoints).iterator();
			updatePlayerStates(GameState.TELEPORT, new Predicate<PlayerData>() {
				@Override
				public boolean apply(PlayerData playerData) {
					Location location = iterator.next().getLocation(game.arena.getWorld()).clone();
					Random random = new Random();
					location.add(random.nextDouble(), random.nextDouble(), random.nextDouble());
					playerData.getPlayer().teleport(location);
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
