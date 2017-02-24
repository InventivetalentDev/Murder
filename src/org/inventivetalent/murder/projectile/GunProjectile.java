package org.inventivetalent.murder.projectile;

import org.bukkit.Color;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.particle.ParticleEffect;

import java.util.Collections;

public class GunProjectile extends MurderProjectile {

	public GunProjectile(Game game, Player shooter, Vector direction) {
		super(Type.GUN, game, shooter, direction.clone());
		initProjectile(shooter.launchProjectile(Arrow.class, this.direction.multiply(3)));
	}

	@Override
	public void tick() {
		super.tick();
		projectile.setVelocity(direction);

		ParticleEffect.REDSTONE.sendColor(Collections.singleton(shooter), projectile.getLocation().getX(), projectile.getLocation().getY(), projectile.getLocation().getZ(), Color.BLUE);
	}

	@Override
	public boolean finished() {
		return super.finished();
	}
}
