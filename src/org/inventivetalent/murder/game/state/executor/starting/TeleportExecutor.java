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
