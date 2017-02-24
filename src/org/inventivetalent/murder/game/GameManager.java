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
			if (game.arena.disabled) {
				sign.setLine(plugin.signLinePlayers, String.format(plugin.signFormatPlayers, "-", "-"));
			} else if (game.gameState == GameState.DISPOSE) {
				sign.setLine(plugin.signLinePlayers, String.format(plugin.signFormatPlayers, 0, game.arena.maxPlayers));
			} else {
				sign.setLine(plugin.signLinePlayers, String.format(plugin.signFormatPlayers, game.players.size(), game.arena.maxPlayers));
			}

			sign.update();
		}
	}

	public void removeGame(UUID uuid) {
		gameMap.remove(uuid);
	}

}
