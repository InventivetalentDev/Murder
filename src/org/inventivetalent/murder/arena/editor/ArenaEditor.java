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

package org.inventivetalent.murder.arena.editor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.VectorFormatter;
import org.inventivetalent.murder.arena.Arena;
import org.inventivetalent.murder.arena.spawn.SpawnPoint;
import org.inventivetalent.murder.arena.spawn.SpawnType;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.message.MessageFormatter;
import org.inventivetalent.pluginannotations.message.MessageLoader;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

public class ArenaEditor {

	public static MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.editor.arena", null);

	public UUID uuid;

	//Arena settings
	public String name;
	public Vector minCorner;
	public Vector maxCorner;

	public SpawnPoint lobbySpawnPoint;
	public Set<SpawnPoint> playerSpawnPoints = new HashSet<>();
	public Set<SpawnPoint> lootSpawnPoints   = new HashSet<>();

	boolean wasDisabled;

	MessageFormatter minCornerFormatter = new VectorFormatter(new Callable<Vector>() {
		@Override
		public Vector call() throws Exception {
			return minCorner;
		}
	});
	MessageFormatter maxCornerFormatter = new VectorFormatter(new Callable<Vector>() {
		@Override
		public Vector call() throws Exception {
			return maxCorner;
		}
	});

	public ArenaEditor(UUID uuid) {
		this.uuid = uuid;
	}

	public void handleInteract(PlayerInteractEvent event) {
		ItemStack itemStack = event.getItem();
		if (itemStack != null) {
			if (event.getPlayer().hasPermission("murder.editor.arena.use")) {
				if (itemStack.equals(Murder.instance.itemManager.getBoundsSelector())) {
					if (event.getPlayer().isSneaking()) {//Use the player's location
						if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
							minCorner = event.getPlayer().getLocation().toVector();
							minCorner.setY(Math.max(0, Math.min(255, minCorner.getY())));
							event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("bounds.set.self.min", "bounds.set.self.min", minCornerFormatter));
						} else {
							maxCorner = event.getPlayer().getLocation().toVector();
							maxCorner.setY(Math.max(0, Math.min(255, maxCorner.getY())));
							event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("bounds.set.self.max", "bounds.set.self.max", maxCornerFormatter));
						}
					} else {//Use the clicked block's location
						if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
							minCorner = event.getClickedBlock().getLocation().toVector();
							minCorner.setY(Math.max(0, Math.min(255, minCorner.getY())));
							event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("bounds.set.block.min", "bounds.set.block.min", minCornerFormatter));
						}
						if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
							maxCorner = event.getClickedBlock().getLocation().toVector();
							maxCorner.setY(Math.max(0, Math.min(255, maxCorner.getY())));
							event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("bounds.set.block.max", "bounds.set.block.max", maxCornerFormatter));
						}
					}
					event.setCancelled(true);
				}
			}
		}
	}

	public void handleBlockPlace(BlockPlaceEvent event) {
		ItemStack itemStack = event.getItemInHand();
		if (itemStack != null) {
			if (event.getPlayer().hasPermission("murder.editor.arena.use")) {
				if (itemStack.equals(Murder.instance.itemManager.getLobbySpawnSelector())) {
					lobbySpawnPoint = new SpawnPoint(event.getBlockPlaced().getLocation().toVector(), SpawnType.LOBBY);
					event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("spawn.add.lobby", "spawn.add.lobby", new VectorFormatter(lobbySpawnPoint.getVector())));
				}
				if (itemStack.equals(Murder.instance.itemManager.getPlayerSpawnSelector())) {
					if (checkBounds(event)) {
						Vector vector = event.getBlockPlaced().getLocation().toVector();
						playerSpawnPoints.add(new SpawnPoint(vector, SpawnType.PLAYER));
						event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("spawn.add.player", "spawn.add.player", new VectorFormatter(vector)));
					}
				}
				if (itemStack.equals(Murder.instance.itemManager.getLootSpawnSelector())) {
					if (checkBounds(event)) {
						Vector vector = event.getBlockPlaced().getLocation().toVector();
						playerSpawnPoints.add(new SpawnPoint(vector, SpawnType.LOOT));
						event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("spawn.add.loot", "spawn.add.loot", new VectorFormatter(vector)));
					}
				}
			}
		}
	}

	boolean checkBounds(BlockPlaceEvent event) {
		if (!isInBounds(event.getBlockPlaced().getLocation().toVector())) {
			event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("spawn.error.outOfBounds", "spawn.error.outOfBounds"));
			event.setCancelled(true);
			return false;
		}
		return true;
	}

	public void handleBlockBreak(BlockBreakEvent event) {
		if (event.getBlock() == null) { return; }
		Material material = event.getBlock().getType();
		if (event.getPlayer().hasPermission("murder.editor.arena.use")) {
			if (material.equals(Murder.instance.itemManager.getLobbySpawnSelector().getType())) {
				lobbySpawnPoint = null;
				event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("spawn.remove.lobby", "spawn.remove.lobby"));
			}
			if (material.equals(Murder.instance.itemManager.getPlayerSpawnSelector().getType())) {
				Vector vector = event.getBlock().getLocation().toVector();
				playerSpawnPoints.remove(new SpawnPoint(vector, SpawnType.PLAYER));
				event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("spawn.remove.player", "spawn.remove.player"));
			}
			if (material.equals(Murder.instance.itemManager.getLootSpawnSelector().getType())) {
				Vector vector = event.getBlock().getLocation().toVector();
				playerSpawnPoints.remove(new SpawnPoint(vector, SpawnType.LOOT));
				event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("spawn.remove.loot", "spawn.remove.loot", new VectorFormatter(vector)));
			}
		}
	}

	//Removes placed Spawnpoint blocks
	public void resetSpawnBlocks() {
		Set<SpawnPoint> allSpawnPoints = new HashSet<>();
		if (lobbySpawnPoint != null) { allSpawnPoints.add(lobbySpawnPoint); }
		allSpawnPoints.addAll(playerSpawnPoints);
		allSpawnPoints.addAll(lootSpawnPoints);

		for (SpawnPoint spawnPoint : allSpawnPoints) {
			spawnPoint.getLocation(getPlayer().getWorld()).getBlock().setType(Material.AIR);
		}
	}

	public void setSpawnBlocks() {
		Set<SpawnPoint> allSpawnPoints = new HashSet<>();
		if (lobbySpawnPoint != null) { allSpawnPoints.add(lobbySpawnPoint); }
		allSpawnPoints.addAll(playerSpawnPoints);
		allSpawnPoints.addAll(lootSpawnPoints);

		for (SpawnPoint spawnPoint : allSpawnPoints) {
			Material type = Material.STONE;
			switch (spawnPoint.type) {
				case LOBBY:
					type = Murder.instance.itemManager.getLobbySpawnSelector().getType();
					break;
				case PLAYER:
					type = Murder.instance.itemManager.getPlayerSpawnSelector().getType();
					break;
				case LOOT:
					type = Murder.instance.itemManager.getLootSpawnSelector().getType();
					break;
			}

			spawnPoint.getLocation(getPlayer().getWorld()).getBlock().setType(type);
		}
	}

	boolean isInBounds(Vector vector) {
		Vector min = Murder.instance.minVector(this.minCorner, this.maxCorner);
		Vector max = Murder.instance.maxVector(this.minCorner, this.maxCorner);
		return Murder.instance.contains(min, max, vector);
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public boolean isComplete() {
		if (name == null) { return false; }
		if (minCorner == null || maxCorner == null) { return false; }
		if (lobbySpawnPoint == null) { return false; }
		if (playerSpawnPoints.isEmpty()) { return false; }
		return true;
	}

	public Arena create() {
		if (!isComplete()) { throw new IllegalStateException("not complete"); }

		Arena arena = new Arena(getPlayer().getWorld().getName(), name);
		return update(arena);
	}

	public Arena update(Arena arena) {
		arena.minCorner = Murder.instance.minVector(this.minCorner, this.maxCorner);
		arena.maxCorner = Murder.instance.maxVector(this.minCorner, this.maxCorner);
		arena.spawnPoints.clear();
		arena.spawnPoints.add(lobbySpawnPoint);
		arena.spawnPoints.addAll(playerSpawnPoints);
		arena.spawnPoints.addAll(lootSpawnPoints);

		arena.minPlayers = Math.max(Murder.instance.minPlayers, arena.minPlayers);
		arena.maxPlayers = playerSpawnPoints.size();

		arena.disabled = wasDisabled;

		return arena;
	}

	public void load(Arena arena) {
		this.minCorner = arena.minCorner;
		this.maxCorner = arena.maxCorner;
		this.lobbySpawnPoint = arena.getFirstSpawnPoint(SpawnType.LOBBY);
		this.playerSpawnPoints.clear();
		this.playerSpawnPoints.addAll(arena.getSpawnPoints(SpawnType.PLAYER));
		this.lootSpawnPoints.clear();
		this.lootSpawnPoints.addAll(arena.getSpawnPoints(SpawnType.LOOT));

		this.wasDisabled = arena.disabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		ArenaEditor editor = (ArenaEditor) o;

		return uuid != null ? uuid.equals(editor.uuid) : editor.uuid == null;

	}

	@Override
	public int hashCode() {
		return uuid != null ? uuid.hashCode() : 0;
	}
}
