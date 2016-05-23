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


