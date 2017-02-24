package org.inventivetalent.murder.command;

import org.bukkit.entity.Player;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.arena.Arena;
import org.inventivetalent.murder.command.error.MurderErrorHandler;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.command.Command;
import org.inventivetalent.pluginannotations.command.Completion;
import org.inventivetalent.pluginannotations.command.OptionalArg;
import org.inventivetalent.pluginannotations.command.Permission;
import org.inventivetalent.pluginannotations.command.exception.InvalidLengthException;
import org.inventivetalent.pluginannotations.message.MessageLoader;

import java.util.List;

public class PlayerCommands {

	public static final String        PERM_BASE      = "murder.player.";
	public              MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.command", null);

	private Murder plugin;

	public PlayerCommands(Murder plugin) {
		this.plugin = plugin;
	}

	@Command(name = "murderJoin",
			 description = "Join a game",
			 aliases = {
					 "mJoin",
					 "mj" },
			 usage = "<Arena Name | Arena ID>",
			 min = 1,
			 //			 max = 1,
			 errorHandler = MurderErrorHandler.class)
	@Permission(PERM_BASE + "join")
	public void join(Player sender, @OptionalArg String name, @OptionalArg Integer id) {
		if (name == null && id == null) {
			throw new InvalidLengthException(1, 0);
		}

		PlayerData playerData = plugin.playerManager.getData(sender.getUniqueId());
		if (playerData != null && playerData.gameId != null) {
			sender.sendMessage(MESSAGE_LOADER.getMessage("game.error.ingame", "game.error.ingame"));
			return;
		}

		Arena arena = null;
		if (name != null) { arena = plugin.arenaManager.getArenaByName(name); }
		if (id != null) { arena = plugin.arenaManager.getArenaById(id); }
		if (arena == null) {
			sender.sendMessage(MESSAGE_LOADER.getMessage("arena.error.notFound", "arena.error.notFound"));
			return;
		}

		if (arena.disabled) {
			sender.sendMessage(MESSAGE_LOADER.getMessage("arena.error.disabled", "arena.error.disabled"));
			return;
		}

		Game game = plugin.gameManager.getOrCreateGame(arena);
		if (!game.gameState.isJoinable()) {
			sender.sendMessage(MESSAGE_LOADER.getMessage("game.error.notJoinable", "game.error.notJoinable"));
			return;
		}
		game.addPlayer(sender);
	}

	@Completion
	public void join(List<String> list, Player player, String name, @OptionalArg Integer id) {
		if (name == null) {
			list.addAll(plugin.arenaManager.nameMap.keySet());
		}
	}

	@Command(name = "murderLeave",
			 description = "Leave the current game",
			 aliases = {
					 "mLeave",
					 "ml" },
			 max = 0,
			 errorHandler = MurderErrorHandler.class)
	@Permission(PERM_BASE + "leave")
	public void leave(Player sender) {
		PlayerData playerData = plugin.playerManager.getData(sender.getUniqueId());
		if (playerData == null || playerData.gameId == null) {
			sender.sendMessage(MESSAGE_LOADER.getMessage("game.error.notIngame", "game.error.notIngame"));
			return;
		}

		Game game = plugin.gameManager.getGame(playerData.gameId);
		if (game != null) { game.removePlayer(sender); }
	}

}
