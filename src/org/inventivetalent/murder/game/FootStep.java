package org.inventivetalent.murder.game;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class FootStep {

	public final UUID   uuid;
	public final Vector vector;
	public final Color  color;

	int fadeTimeout = 20;

	public FootStep(UUID uuid, Vector vector, Color color) {
		this.uuid = uuid;
		this.vector = vector;
		this.color = color;
	}

	public void display(Player receiver) {
		if (receiver == null || !receiver.isOnline()) { return; }

		Collection<Player> singleton = Collections.singleton(receiver);
		ParticleEffect.REDSTONE.sendColor(singleton, vector.getX(), vector.getY() + .025, vector.getZ(), color);
		if (fadeTimeout++ >= 16) {
			fadeTimeout=0;
			ParticleEffect.FOOTSTEP.send(singleton, vector.getX(), vector.getY() + .01, vector.getZ(), 0, 0, 0, 0, 1);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		FootStep footStep = (FootStep) o;

		if (vector != null ? !vector.equals(footStep.vector) : footStep.vector != null) { return false; }
		return color != null ? color.equals(footStep.color) : footStep.color == null;

	}

	@Override
	public int hashCode() {
		int result = vector != null ? vector.hashCode() : 0;
		result = 31 * result + (color != null ? color.hashCode() : 0);
		return result;
	}
}
