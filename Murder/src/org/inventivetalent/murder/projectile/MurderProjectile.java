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

		List<Entity> nearby = projectile.getNearbyEntities(.5, 1, .5);
		for (Entity entity : nearby) {
			if (entity.getUniqueId().equals(shooter.getUniqueId())) { continue; }
			if (entity.getType() != EntityType.PLAYER) { continue; }
			PlayerData playerData = Murder.instance.playerManager.getData(entity.getUniqueId());
			if (playerData != null) {
				if (playerData.isInGame()) {
					if (playerData.isSpectator) {
						Player spectator = playerData.getPlayer();
						if (spectator.getEyeLocation().add(0, 1, 0).getBlock().getType() == Material.AIR) {
							playerData.getPlayer().setVelocity(new Vector(0, 2, 0));
						} else if (spectator.getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {
							playerData.getPlayer().setVelocity(new Vector(0, -2, 0));
						}

						continue;
					}
					playerData.killer = shooter.getUniqueId();
					playerData.killed = true;
					game.killedPlayers.add(playerData.uuid);

					this.projectile.remove();
					return true;
				}
			}
		}
		return false;
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
