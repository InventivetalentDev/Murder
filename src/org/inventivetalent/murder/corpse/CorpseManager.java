package org.inventivetalent.murder.corpse;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.npclib.npc.living.human.NPCPlayer;

import java.util.UUID;

public class CorpseManager {

	private Murder plugin;

	public CorpseManager(Murder plugin) {
		this.plugin = plugin;
	}

	public NPCPlayer spawnCorpse(Game game, PlayerData data, Location l) {
		if (data.isInGame() && data.nameTag != null) {
			Location location = l.clone();

			Block block = location.getBlock();
			while (block.getType() != Material.AIR) {
				block = block.getRelative(BlockFace.UP);
			}
			location.setY(block.getY() + .5);

			//Make sure there's enough room
			block = location.getBlock().getRelative(BlockFace.NORTH);
			while (block.getRelative(BlockFace.NORTH).getType() != Material.AIR) {
				block = block.getRelative(BlockFace.SOUTH);
			}
			location = block.getLocation();

			NPCPlayer npc = plugin.npcRegistry.spawnPlayerNPC(location, NPCPlayer.class, UUID.randomUUID(), data.nameTag);
			npc.setSkin(data.nameTag.substring(1, 2) + "b");
			npc.setLaying(true);
			npc.setPersistent(false);
			//TODO: collision
			//TODO: gravity
			game.corpses.add(npc);
			return npc;
		}
		return null;
	}

	public void removeCorpses(Game game) {
		for (NPCPlayer npc : game.corpses) {
			npc.despawn();
		}
	}

}
