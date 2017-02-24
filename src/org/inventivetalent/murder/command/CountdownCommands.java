package org.inventivetalent.murder.command;

import org.bukkit.entity.Player;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.arena.Arena;
import org.inventivetalent.murder.command.error.MurderErrorHandler;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.command.Command;
import org.inventivetalent.pluginannotations.command.OptionalArg;
import org.inventivetalent.pluginannotations.command.Permission;
import org.inventivetalent.pluginannotations.message.MessageFormatter;
import org.inventivetalent.pluginannotations.message.MessageLoader;

public class CountdownCommands {

	static MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.command", null);

	private Murder plugin;

	public CountdownCommands(Murder plugin) {
		this.plugin = plugin;
	}

	@Command(name = "murderCountdown",
			 aliases = {
					 "mCountdown",
					 "mC" },
			 usage = "<Time> [Arena]",
			 min = 1,
			 max = 2,
			 errorHandler = MurderErrorHandler.class)
	@Permission("murder.countdown")
	public void countdown(Player sender, Integer time, @OptionalArg String name) {
		PlayerData playerData = plugin.playerManager.getData(sender.getUniqueId());

		Game game;
		if (name != null && !name.isEmpty()) {
			Arena arena = plugin.arenaManager.getArenaByName(name);
			if (arena == null) {
				sender.sendMessage(MESSAGE_LOADER.getMessage("arena.error.notFound", "arena.error.notFound"));
				return;
			}
			game = plugin.gameManager.getGameForArenaId(arena.id);
		} else {
			if (playerData == null || playerData.getGame() == null) {
				sender.sendMessage(MESSAGE_LOADER.getMessage("game.error.notIngame", "game.error.notIngame"));
				return;
			} else {
				game = playerData.getGame();
			}
		}
		if (game == null) {
			sender.sendMessage(MESSAGE_LOADER.getMessage("countdown.error.gameNotFound", "countdown.error.gameNotFound"));
			return;
		}

		final int countdown = Math.abs(time);
		final GameState state = game.gameState;
		if (state == GameState.LOBBY) {
			game.lobbyCountdown = countdown;
			sender.sendMessage(MESSAGE_LOADER.getMessage("countdown.set.lobby", "countdown.set.lobby", new MessageFormatter() {
				@Override
				public String format(String key, String message) {
					return String.format(message, countdown);
				}
			}));
		} else if (state.ordinal() >= GameState.STARTING.ordinal() && state.ordinal() <= GameState.GIVE_ITEMS.ordinal()) {
			game.startCountdown = countdown;
			sender.sendMessage(MESSAGE_LOADER.getMessage("countdown.set.start", "countdown.set.start", new MessageFormatter() {
				@Override
				public String format(String key, String message) {
					return String.format(message, countdown);
				}
			}));
		} else {
			sender.sendMessage(MESSAGE_LOADER.getMessage("countdown.error.invalidState", "countdown.error.invalidState", new MessageFormatter() {
				@Override
				public String format(String key, String message) {
					return String.format(message, state.name());
				}
			}));
		}
	}

}


