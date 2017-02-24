package org.inventivetalent.murder.task;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.arena.editor.ArenaEditor;
import org.inventivetalent.particle.ParticleEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class ArenaOutlineTask extends BukkitRunnable {

	private Murder plugin;

	public ArenaOutlineTask(Murder plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		for (Iterator<ArenaEditor> iterator = plugin.arenaEditorManager.editorMap.values().iterator(); iterator.hasNext(); ) {
			ArenaEditor next = iterator.next();

			Vector aCorner = next.minCorner != null ? toBlock(next.minCorner) : null;
			Vector bCorner = next.maxCorner != null ? toBlock(next.maxCorner) : null;
			if (aCorner == null && bCorner == null) { continue; }

			if (aCorner != null) {
				blockOutline(next.getPlayer(), aCorner, Color.LIME);
			}
			if (bCorner != null) {
				blockOutline(next.getPlayer(), bCorner, Color.RED);
			}
			if (aCorner != null && bCorner != null) {
				Vector minCorner = Murder.instance.minVector(aCorner, bCorner);
				Vector maxCorner = Murder.instance.maxVector(aCorner, bCorner);

				int i = 0;
				for (int x = minCorner.getBlockX(); x <= maxCorner.getBlockX(); x++) {
					for (int z = minCorner.getBlockZ(); z <= maxCorner.getBlockZ(); z++) {
						for (int y = minCorner.getBlockY(); y <= maxCorner.getBlockY(); y++) {
							Vector vector = new Vector(x, y, z);
							int edgeIntersectionCount = 0;

							//https://bukkit.org/threads/calculating-the-edges-of-a-rectangle-cuboid-discussion.78267/#post-1142330
							if (x == minCorner.getBlockX() || x == maxCorner.getBlockX()) { edgeIntersectionCount++; }
							if (y == minCorner.getBlockY() || y == maxCorner.getBlockY()) { edgeIntersectionCount++; }
							if (z == minCorner.getBlockZ() || z == maxCorner.getBlockZ()) { edgeIntersectionCount++; }
							if (edgeIntersectionCount >= 2) {
								Vector vector1 = vector.clone().add(new Vector(.5, .5, .5));
								if (vector.distance(next.getPlayer().getLocation().toVector()) > 32) { continue; }
								Color color = Color.BLUE;
								if (++i % 2 == 0) { color = Color.AQUA; }
								ParticleEffect.REDSTONE.sendColor(Collections.singleton(next.getPlayer()), vector1.getX(), vector1.getY(), vector1.getZ(), color);
							}
						}
					}
				}

				Vector midpoint = minCorner.clone().midpoint(maxCorner.clone());
				blockOutline(next.getPlayer(), toBlock(midpoint), Color.PURPLE);
			}
		}
	}

	Vector toBlock(Vector vector) {
		return new Vector(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}

	void blockOutline(Player receiver, Vector location, Color color) {
		Collection<Player> singleton = Collections.singleton(receiver);

		//Center
		ParticleEffect.REDSTONE.sendColor(singleton, location.getX(), location.getY() + .5, location.getZ(), color);

		//Outline
		ParticleEffect.REDSTONE.sendColor(singleton, location.getX(), location.getY(), location.getZ(), color);
		ParticleEffect.REDSTONE.sendColor(singleton, location.getX(), location.getY(), location.getZ() + 1, color);
		ParticleEffect.REDSTONE.sendColor(singleton, location.getX() + 1, location.getY(), location.getZ(), color);
		ParticleEffect.REDSTONE.sendColor(singleton, location.getX() + 1, location.getY(), location.getZ() + 1, color);
		ParticleEffect.REDSTONE.sendColor(singleton, location.getX(), location.getY() + 1, location.getZ(), color);
		ParticleEffect.REDSTONE.sendColor(singleton, location.getX(), location.getY() + 1, location.getZ() + 1, color);
		ParticleEffect.REDSTONE.sendColor(singleton, location.getX() + 1, location.getY() + 1, location.getZ(), color);
		ParticleEffect.REDSTONE.sendColor(singleton, location.getX() + 1, location.getY() + 1, location.getZ() + 1, color);
	}
}
