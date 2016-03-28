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

package org.inventivetalent.murder.game;

import org.bukkit.block.Sign;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.arena.Arena;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.game.state.GameTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

	private Murder plugin;

	public final Map<UUID, Game> gameMap = new HashMap<>();
	public GameTask gameTask;

	public GameManager(Murder plugin) {
		this.plugin = plugin;
		gameTask = new GameTask(plugin);
		gameTask.runTaskTimer(plugin, 1, 1);
	}

	public Game addGame(Game game) {
		if (gameMap.containsKey(game.gameId)) { throw new IllegalStateException("Game #" + game.gameId + " is already added"); }
		gameMap.put(game.gameId, game);
		return game;
	}

	public Game getGame(UUID uuid) {
		if (!gameMap.containsKey(uuid)) { return null; }
		return gameMap.get(uuid);
	}

	public Game getGameForArenaId(int id) {
		for (Game game : gameMap.values()) {
			if (id == game.arena.id) { return game; }
		}
		return null;
	}

	public Game getOrCreateGame(Arena arena) {
		Game game = getGameForArenaId(arena.id);
		if (game == null) {
			game = addGame(new Game(arena));
		}
		return game;
	}

	public void refreshSigns(Game game) {
		for (Sign sign : plugin.arenaManager.getArenaSigns(game.arena.id)) {
			if (game.arena.disabled) {
				sign.setLine(plugin.signLineState, Game.MESSAGE_LOADER.getMessage("state.disabled.sign", "state.disabled.sign"));
			} else if (game.gameState == GameState.DISPOSE) {
				sign.setLine(plugin.signLineState, GameState.WAITING.getSignText());
			} else {
				sign.setLine(plugin.signLineState, game.gameState.getSignText());
			}
			sign.setLine(plugin.signLinePlayers, String.format(plugin.signFormatPlayers, game.players.size(), game.arena.maxPlayers));
			sign.update();
		}
	}

	public void removeGame(UUID uuid) {
		gameMap.remove(uuid);
	}

}
