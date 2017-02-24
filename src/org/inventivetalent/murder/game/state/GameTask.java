package org.inventivetalent.murder.game.state;

import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.Game;

import java.util.HashSet;
import java.util.Set;

public class GameTask extends BukkitRunnable {

	private Murder plugin;

	public GameTask(Murder plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		Set<Game> games = new HashSet<>(plugin.gameManager.gameMap.values());
		for (Game game : games) {
			game.tick();
		}
	}
}
