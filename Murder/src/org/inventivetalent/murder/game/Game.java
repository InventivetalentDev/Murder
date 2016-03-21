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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.Role;
import org.inventivetalent.murder.arena.Arena;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.game.state.StateExecutor;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.message.MessageFormatter;
import org.inventivetalent.pluginannotations.message.MessageLoader;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Game {

	public static final MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.game", null);

	public final UUID  gameId;
	public final Arena arena;

	public GameState     gameState     = GameState.WAITING;
	public StateExecutor stateExecutor = gameState.newExecutor(this);

	public final Set<UUID> joiningPlayers         = new HashSet<>();
	public final Set<UUID> leavingPlayers         = new HashSet<>();
	public final Set<UUID> players                = new HashSet<>();//This set should only be accessed by the state executors
	public final Set<UUID> waitingForResourcepack = new HashSet<>();

	public int ticks = 0;

	public int lobbyCountdown;
	public int startCountdown;

	public Role winner = Role.WEAPON;// WEAPON = bystanders, MURDERER = murderer

	public boolean droppingLoot;//Set to true after the drop-delay is over

	public Game(Arena arena) {
		this.gameId = UUID.randomUUID();
		this.arena = arena;
	}

	public void addPlayer(Player player) {
		if (players.contains(player.getUniqueId())) { throw new IllegalStateException("player " + player + " is already in the game"); }
		if (!gameState.isJoinable()) { throw new IllegalStateException(gameState + " is not joinable"); }
		if (joiningPlayers.contains(player.getUniqueId())) { return; }

		PlayerData data = Murder.instance.playerManager.getOrCreateData(player.getUniqueId());
		data.storeData(true);

		leavingPlayers.remove(player.getUniqueId());
		joiningPlayers.add(player.getUniqueId());
	}

	public void removePlayer(OfflinePlayer player) {
		if (!players.contains(player.getUniqueId())) { throw new IllegalStateException("player " + player + " is not in the game"); }
		if (leavingPlayers.contains(player.getUniqueId())) { return; }

		//		PlayerData data = Murder.instance.playerManager.getData(player.getUniqueId());
		//		if (data != null) { data.restoreData(); }

		joiningPlayers.remove(player.getUniqueId());
		leavingPlayers.add(player.getUniqueId());
	}

	public void broadcastJoin(UUID uuid) {
		final Player player = getPlayer(uuid);
		broadcastMessage(MESSAGE_LOADER.getMessage("join", "join", true, '&', new MessageFormatter() {
			@Override
			public String format(String key, String message) {
				return String.format(message, player.getDisplayName(), players.size(), arena.maxPlayers);
			}
		}));
	}

	public void broadcastLeave(UUID uuid) {
		final Player player = getPlayer(uuid);
		broadcastMessage(MESSAGE_LOADER.getMessage("leave", "leave", true, '&', new MessageFormatter() {
			@Override
			public String format(String key, String message) {
				return String.format(message, player.getDisplayName(), players.size(), arena.maxPlayers);
			}
		}));
	}

	public void broadcastMessage(String message) {
		for (UUID uuid : players) {
			getPlayer(uuid).sendMessage(message);
		}
	}

	public int countBystanders(boolean includeDefault, boolean includeWeapon) {
		int count = 0;
		for (UUID uuid : players) {
			PlayerData data = Murder.instance.playerManager.getData(uuid);
			if (data != null) {
				if (data.role != null) {
					if (includeDefault && data.role == Role.DEFAULT) { count++; }
					if (includeWeapon && data.role == Role.WEAPON) { count++; }
				}
			}
		}
		return count;
	}

	public Set<UUID> getBystanders(boolean includeDefault, boolean includeWeapon) {
		Set<UUID> uuids = new HashSet<>();
		for (UUID uuid : players) {
			PlayerData data = Murder.instance.playerManager.getData(uuid);
			if (data != null) {
				if (data.role != null) {
					if (includeDefault && data.role == Role.DEFAULT) {
						uuids.add(uuid);
					}
					if (includeWeapon && data.role == Role.WEAPON) {
						uuids.add(uuid);
					}
				}
			}
		}
		return uuids;
	}

	public UUID getMurderer() {
		for (UUID uuid : players) {
			PlayerData data = Murder.instance.playerManager.getData(uuid);
			if (data != null) {
				if (data.role != null) {
					if (data.role == Role.MURDERER) { return uuid; }
				}
			}
		}
		return null;
	}

	public Player getPlayer(UUID uuid) {
		return Bukkit.getPlayer(uuid);
	}

	public void tick() {
		if (this.stateExecutor != null) {
			this.stateExecutor.tick();
			if (this.stateExecutor.finished()) {
				Murder.instance.getLogger().finer("[" + gameId + "] State " + gameState + " finished, switching to " + gameState.next());
				gameState = gameState.next();
				stateExecutor = gameState.newExecutor(this);
			} else if (this.stateExecutor.revert()) {
				Murder.instance.getLogger().fine("[" + gameId + "] State " + gameState + " did not finish, going back to " + gameState.previous());
				gameState = gameState.previous();
				stateExecutor = gameState.newExecutor(this);
			}
		}
	}

}
