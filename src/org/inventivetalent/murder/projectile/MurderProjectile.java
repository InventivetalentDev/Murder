package org.inventivetalent.murder.projectile;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.player.PlayerData;

import java.util.List;

public class MurderProjectile {

	public final Type       type;
	public final Game       game;
	public final Player     shooter;
	public final Vector     direction;
	public       Projectile projectile;

	protected int timeout = 300;

	public MurderProjectile(Type type, Game game, Player shooter, Vector direction) {
		this.type = type;
		this.game = game;
		this.shooter = shooter;
		this.direction = direction.clone();
	}

	void initProjectile(Projectile projectile) {
		this.projectile = projectile;
		this.projectile.setMetadata("MURDER", new FixedMetadataValue(Murder.instance, this));
	}

	public void tick() {
		if (timeout-- <= 0) {
			projectile.remove();
		}
		if (game.gameState.ordinal() >= GameState.ENDED.ordinal()) {
			projectile.remove();
		}
		if (!game.arena.contains(projectile.getLocation().toVector())) {
			projectile.remove();
		}
	}

	public boolean finished() {
		if (projectile.isOnGround()) {
			projectile.remove();
			return true;
		}
		if (projectile.isDead()) {
			projectile.remove();
			return true;
		}

		boolean hitPlayer = false;
		List<Entity> nearby = projectile.getNearbyEntities(.5, 1, .5);
		for (Entity entity : nearby) {
			if (entity.getLocation().distance(projectile.getLocation()) > 1.51) { continue; }
			if (entity.getUniqueId().equals(shooter.getUniqueId())) { continue; }
			if (entity.getType() != EntityType.PLAYER) { continue; }
			PlayerData playerData = Murder.instance.playerManager.getData(entity.getUniqueId());
			if (playerData != null) {
				if (playerData.isInGame()) {
					if (playerData.isSpectator) {
						Player spectator = playerData.getPlayer();
						if (spectator.getEyeLocation().add(0, 1, 0).getBlock().getType() == Material.AIR) {
							playerData.getPlayer().setVelocity(new Vector(0, 2, 0).add(projectile.getVelocity().clone().multiply(-1)));
						} else if (spectator.getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {
							playerData.getPlayer().setVelocity(new Vector(0, -2, 0).add(projectile.getVelocity().clone().multiply(-1)));
						}

						continue;
					}
					playerData.killer = shooter.getUniqueId();
					playerData.killed = true;
					game.killedPlayers.add(playerData.uuid);

					this.projectile.remove();
					hitPlayer = true;
				}
			}
		}
		return hitPlayer;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		MurderProjectile that = (MurderProjectile) o;

		if (type != that.type) { return false; }
		if (game != null ? !game.equals(that.game) : that.game != null) { return false; }
		return shooter != null ? shooter.getUniqueId().equals(that.shooter.getUniqueId()) : that.shooter == null;

	}

	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + (game != null ? game.hashCode() : 0);
		result = 31 * result + (shooter != null ? shooter.getUniqueId().hashCode() : 0);
		return result;
	}

	public enum Type {
		GUN,
		KNIFE;
	}

}
