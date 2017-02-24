package org.inventivetalent.murder.game.state.executor.ingame;

import org.bukkit.entity.Item;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.arena.spawn.SpawnPoint;
import org.inventivetalent.murder.arena.spawn.SpawnType;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;

import java.util.ArrayList;
import java.util.List;

public class DropLootExecutor extends IngameExecutor {

	boolean firstTick   = true;
	int     lootSeconds = 0;

	List<SpawnPoint> lootSpawnPoints;
	int dropIndex = 0;

	public DropLootExecutor(Game game) {
		super(game);
		game.ticks = 0;
		lootSpawnPoints = new ArrayList<>(game.arena.getSpawnPoints(SpawnType.LOOT));
	}

	@Override
	public void tick() {
		super.tick();

		if (firstTick) {
			firstTick = false;
			updatePlayerStates(GameState.DROP_LOOT, null);
		} else {
			game.ticks++;
			if (game.ticks >= 20) {
				lootSeconds++;
				if (!game.droppingLoot) {// wait until the delay is over
					if (lootSeconds >= Murder.instance.lootDelay) {
						lootSeconds = 0;
						game.droppingLoot = true;
					}
				} else {// drop loot
					if (lootSeconds >= Murder.instance.lootInterval) {
						lootSeconds = 0;

						if (!lootSpawnPoints.isEmpty()) {
							if (dropIndex >= lootSpawnPoints.size()) {
								dropIndex = 0;
							}
							SpawnPoint point = lootSpawnPoints.get(dropIndex);
							Item dropped = game.arena.getWorld().dropItemNaturally(point.getLocation(game.arena.getWorld()), Murder.instance.itemManager.getLoot());
							game.droppedItems.add(dropped);

							dropIndex++;
						}
					}
				}

				game.ticks = 0;
			}
		}
	}

	@Override
	public boolean finished() {
		return super.finished();
	}
}
