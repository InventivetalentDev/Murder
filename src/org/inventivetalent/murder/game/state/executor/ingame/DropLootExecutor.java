/*
 * Copyright 2013-2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

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
