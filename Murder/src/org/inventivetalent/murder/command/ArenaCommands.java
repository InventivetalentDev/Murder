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

package org.inventivetalent.murder.command;

import org.bukkit.entity.Player;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.arena.Arena;
import org.inventivetalent.murder.arena.editor.ArenaEditor;
import org.inventivetalent.murder.command.error.MurderErrorHandler;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.command.*;
import org.inventivetalent.pluginannotations.command.exception.InvalidLengthException;
import org.inventivetalent.pluginannotations.message.MessageFormatter;
import org.inventivetalent.pluginannotations.message.MessageLoader;

import java.util.List;
import java.util.Map;

public class ArenaCommands {

	public static final String        PERM_BASE      = "murder.arena.";
	static              MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.command.arena.editor", null);

	private Murder plugin;

	public ArenaCommands(Murder plugin) {
		this.plugin = plugin;
	}

	@Command(name = "murderArena",
			 aliases = {
					 "mArena",
					 "ma" },
			 usage = "<create|edit|remove|finish|cancel> [Arena Name]",
			 min = 1,
			 max = -1,
			 errorHandler = MurderErrorHandler.class)
	@Permission(PERM_BASE + "edit")
	public void arena(Player sender, ArenaAction action, @OptionalArg @JoinedArg String name) {
		System.out.println("arena( " + sender + ", " + action + ", " + name + " )");

		Arena arena = null;
		ArenaEditor editor = null;
		if (action == ArenaAction.CREATE || action == ArenaAction.EDIT) {
			editor = plugin.arenaEditorManager.newEditor(sender.getUniqueId());
		}
		if (action == ArenaAction.FINISH || action == ArenaAction.CANCEL) {
			editor = plugin.arenaEditorManager.getEditor(sender.getUniqueId());
		}
		if (action == ArenaAction.CREATE || action == ArenaAction.EDIT || action == ArenaAction.REMOVE) {
			if (name == null) {
				throw new InvalidLengthException(2, 1);
			}
			arena = plugin.arenaManager.getArenaByName(name);
		}

		if (action == ArenaAction.LIST) {
			for (Map.Entry<String, Integer> entry : plugin.arenaManager.nameMap.entrySet()) {
				sender.sendMessage("§e- #" + entry.getValue() + " | " + entry.getKey());
			}
			return;
		}

		if (action == ArenaAction.FINISH || action == ArenaAction.CANCEL) {
			if (editor == null) {
				sender.sendMessage(MESSAGE_LOADER.getMessage("error.notEditing", "error.notEditing"));
				return;
			}
		}

		if (action == ArenaAction.FINISH) {
			if (!editor.isComplete()) {
				sender.sendMessage(MESSAGE_LOADER.getMessage("error.notComplete", "error.notComplete"));
				return;
			}

			Arena oldArena = plugin.arenaManager.getArenaByName(editor.name);
			if (oldArena != null) {
				plugin.arenaManager.removeArena(oldArena);
			}
			final Arena newArena = oldArena != null ? editor.update(oldArena) : editor.create();
			plugin.arenaManager.addArena(newArena);
			sender.sendMessage(MESSAGE_LOADER.getMessage("finished", "finished", new MessageFormatter() {
				@Override
				public String format(String key, String message) {
					return String.format(message, newArena.id, newArena.name);
				}
			}));

			editor.resetSpawnBlocks();
			plugin.arenaEditorManager.removeEditor(sender.getUniqueId());

			PlayerData playerData = plugin.playerManager.loadFromFile(sender.getUniqueId());
			if (playerData.stored) {
				sender.getInventory().clear();
				playerData.restoreData();
				//				Murder.instance.playerManager.deleteDataFile(playerData.uuid);
			}
			return;
		}
		if (action == ArenaAction.CANCEL) {
			editor.resetSpawnBlocks();
			plugin.arenaEditorManager.removeEditor(sender.getUniqueId());

			PlayerData playerData = plugin.playerManager.loadFromFile(sender.getUniqueId());
			if (playerData.stored) {
				sender.getInventory().clear();
				playerData.restoreData();
				//				Murder.instance.playerManager.deleteDataFile(playerData.uuid);
			}

			sender.sendMessage(MESSAGE_LOADER.getMessage("canceled", "canceled"));
			return;
		}

		if (action == ArenaAction.CREATE) {
			if (plugin.arenaManager.getArenaByName(name) != null) {
				sender.sendMessage(MESSAGE_LOADER.getMessage("error.duplicated", "error.duplicate"));
				plugin.arenaEditorManager.removeEditor(sender.getUniqueId());
				return;
			}

			editor.name = name;

			PlayerData playerData = plugin.playerManager.getOrCreateData(sender.getUniqueId());
			if (!playerData.stored) {
				playerData.storeData();
				//				plugin.playerManager.saveDataToFile(playerData);
			}

			giveEditorItems(sender);

			sender.sendMessage(MESSAGE_LOADER.getMessage("started", "started"));
			return;
		}

		if (arena == null) {
			sender.sendMessage(MESSAGE_LOADER.getMessage("error.notFound", "error.notFound"));
			return;
		}

		if (action == ArenaAction.REMOVE) {
			plugin.arenaManager.removeArena(arena);
			sender.sendMessage(MESSAGE_LOADER.getMessage("removed", "removed"));
			return;
		}
		if (action == ArenaAction.EDIT) {
			editor.name = arena.name;
			editor.load(arena);
			editor.setSpawnBlocks();

			PlayerData playerData = plugin.playerManager.getOrCreateData(sender.getUniqueId());
			if (!playerData.stored) {
				playerData.storeData();
				//				plugin.playerManager.saveDataToFile(playerData);
			}

			giveEditorItems(sender);

			sender.sendMessage(MESSAGE_LOADER.getMessage("started", "started"));
			return;
		}
	}

	void giveEditorItems(Player sender) {
		sender.getInventory().clear();
		sender.getInventory().setItem(0, plugin.itemManager.getBoundsSelector());
		sender.getInventory().setItem(2, plugin.itemManager.getLobbySpawnSelector());
		sender.getInventory().setItem(4, plugin.itemManager.getPlayerSpawnSelector());
		sender.getInventory().setItem(5, plugin.itemManager.getLootSpawnSelector());
		sender.getInventory().setHeldItemSlot(0);
	}

	@Completion
	public void arena(List<String> list, Player player, ArenaAction action, String name) {
		if (action == null) {
			for (ArenaAction action1 : ArenaAction.values())
				list.add(action1.name().toLowerCase());
		} else if (name == null) {
			list.addAll(plugin.arenaManager.nameMap.keySet());
		}
	}

	public enum ArenaAction {
		CREATE,
		EDIT,
		REMOVE,
		LIST,

		FINISH,
		CANCEL
	}

}
