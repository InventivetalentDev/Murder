package org.inventivetalent.murder.projectile;

import org.bukkit.Color;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.particle.ParticleEffect;

import java.util.Collections;

public class KnifeProjectile extends MurderProjectile {

	public KnifeProjectile(Game game, Player shooter, Vector direction) {
		super(Type.KNIFE, game, shooter, direction);
		initProjectile(shooter.launchProjectile(Arrow.class, this.direction.multiply(2)));
	}

	@Override
	public void tick() {
		super.tick();

		direction.subtract(new Vector(0, 0.035, 0));
		projectile.setVelocity(direction);

		ParticleEffect.REDSTONE.sendColor(Collections.singleton(shooter), projectile.getLocation().getX(), projectile.getLocation().getY(), projectile.getLocation().getZ(), Color.RED);
	}

	@Override
	public boolean finished() {
		if (super.finished()) {
			Item dropped = projectile.getLocation().getWorld().dropItemNaturally(projectile.getLocation(), Murder.instance.itemManager.getKnife());
			game.droppedItems.add(dropped);
			projectile.remove();
			return true;
		}
		return false;
	}
}
