package org.inventivetalent.murder.command;

import org.bukkit.entity.Player;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.arena.Arena;
import org.inventivetalent.murder.command.error.MurderErrorHandler;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.command.Command;
import org.inventivetalent.pluginannotations.command.JoinedArg;
import org.inventivetalent.pluginannotations.command.Permission;
import org.inventivetalent.pluginannotations.message.MessageLoader;

public class ToggleCommands {

	static MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.command.arena.editor", null);

	private Murder plugin;

	public ToggleCommands(Murder plugin) {
		this.plugin = plugin;
	}

	@Command(name = "murderEnable",
			 description = "Enable an arena",
			 aliases = {
					 "mEnable",
					 "me" },
			 usage = "<Arena Name>",
			 errorHandler = MurderErrorHandler.class)
	@Permission("murder.arena.toggle")
	public void enable(Player sender, @JoinedArg String name) {
		Arena arena = plugin.arenaManager.getArenaByName(name);
		if (arena == null) {
			sender.sendMessage(MESSAGE_LOADER.getMessage("error.notFound", "error.notFound"));
			return;
		}
		plugin.arenaManager.removeArena(arena);
		arena.disabled = false;
		plugin.arenaManager.addArena(arena);

		sender.sendMessage(MESSAGE_LOADER.getMessage("toggle.enable", "toggle.enable"));
	}

	@Command(name = "murderDisable",
			 description = "Disable an arena",
			 aliases = {
					 "mDisable",
					 "md" },
			 usage = "<Arena Name>",
			 errorHandler = MurderErrorHandler.class)
	@Permission("murder.arena.toggle")
	public void disable(Player sender, @JoinedArg String name) {
		Arena arena = plugin.arenaManager.getArenaByName(name);
		if (arena == null) {
			sender.sendMessage(MESSAGE_LOADER.getMessage("error.notFound", "error.notFound"));
			return;
		}
		Game game = plugin.gameManager.getGameForArenaId(arena.id);
		if (game != null) { game.kickAllPlayers(); }

		plugin.arenaManager.removeArena(arena);
		arena.disabled = true;
		plugin.arenaManager.addArena(arena);
		sender.sendMessage(MESSAGE_LOADER.getMessage("toggle.disable", "toggle.disable"));
	}

}
