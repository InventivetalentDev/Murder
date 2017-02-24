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

package org.inventivetalent.murder.game.state.executor.ingame;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.Role;
import org.inventivetalent.murder.game.FootStep;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.executor.LeavableExecutor;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.murder.projectile.MurderProjectile;
import org.inventivetalent.npclib.npc.living.human.NPCPlayer;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.message.MessageFormatter;
import org.inventivetalent.pluginannotations.message.MessageLoader;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class IngameExecutor extends LeavableExecutor {

	static MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.game", null);

	int           footstepTicks = 0;
	Set<FootStep> footSteps     = new HashSet<>();

	public IngameExecutor(Game game) {
		super(game);
	}

	@Override
	public void tick() {
		super.tick();

		//Update projectiles
		for (Iterator<MurderProjectile> iterator = game.projectiles.iterator(); iterator.hasNext(); ) {
			MurderProjectile next = iterator.next();
			next.tick();
			if (next.finished()) {
				iterator.remove();
			}
		}

		if (!game.killedPlayers.isEmpty()) {
			for (UUID uuid : game.killedPlayers) {
				PlayerData data = Murder.instance.playerManager.getData(uuid);
				if (data != null) {
					final Location deathLocation = data.getPlayer().getLocation();
					NPCPlayer corpse = Murder.instance.corpseManager.spawnCorpse(game, data, deathLocation.clone());

					//Make the player a spectator
					data.isSpectator = true;
					//					data.getPlayer().setGameMode(GameMode.SPECTATOR);
					data.getPlayer().setAllowFlight(true);
					data.getPlayer().setFlying(true);
					data.getPlayer().getInventory().clear();
					data.getPlayer().getInventory().addItem(Murder.instance.itemManager.getTeleporter());
					Murder.instance.spectateManager.teleportToClosestPlayer(data);

					data.getPlayer().getWorld().playSound(data.getPlayer().getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5f, 1f);
					data.getPlayer().playSound(data.getPlayer().getLocation(), Sound.ENTITY_PLAYER_DEATH, 0.5f, 1f);
					data.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("kill.death", "kill.death"));

					data.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
					for (UUID uuid1 : game.players) {
						PlayerData playerData = Murder.instance.playerManager.getData(uuid1);
						if (playerData != null && playerData.isInGame() && !playerData.killed) {
							//Make the spectator invisible to alive players
							playerData.getPlayer().hidePlayer(data.getPlayer());
						}
					}

					//Broadcast special kill messages
					if (data.role == Role.MURDERER) {
						final PlayerData killerData;
						if (data.killer != null && (killerData = Murder.instance.playerManager.getData(data.killer)) != null) {
							game.broadcastMessage(MESSAGE_LOADER.getMessage("kill.murderer.player", "kill.murderer.player", new MessageFormatter() {
								@Override
								public String format(String key, String message) {
									return String.format(message, killerData.nameTag.substring(0, 2) + killerData.getPlayer().getName(), killerData.nameTag);
								}
							}));
						} else {
							game.broadcastMessage(MESSAGE_LOADER.getMessage("kill.murderer.unknown", "kill.murderer.unknown"));
						}
					}
					if (data.role == Role.DEFAULT || data.role == Role.WEAPON) {
						final PlayerData killerData;
						if (data.killer != null && (killerData = Murder.instance.playerManager.getData(data.killer)) != null) {
							if (killerData.role != Role.MURDERER) {
								game.broadcastMessage(MESSAGE_LOADER.getMessage("kill.innocent.player", "kill.innocent.player", new MessageFormatter() {
									@Override
									public String format(String key, String message) {
										return String.format(message, killerData.nameTag.substring(0, 2) + killerData.getPlayer().getName(), killerData.nameTag);
									}
								}));
								killerData.gunTimeout = 200;
								killerData.reloadTimer = 0;
								game.timeoutPlayers.add(killerData.uuid);
								killerData.role = Role.DEFAULT;
								//Drop the gun
								Item dropped = killerData.getPlayer().getWorld().dropItemNaturally(killerData.getPlayer().getLocation(), Murder.instance.itemManager.getGun());
								game.droppedItems.add(dropped);
								killerData.getPlayer().getInventory().setItem(4, null);
								killerData.getPlayer().getInventory().setItem(8, null);
							}
						}
						if (data.role == Role.WEAPON) {
							//Drop the gun
							Item dropped = deathLocation.getWorld().dropItemNaturally(deathLocation, Murder.instance.itemManager.getGun());
							game.droppedItems.add(dropped);
						}
					}

				}
			}
			game.killedPlayers.clear();
		}

		for (Iterator<UUID> iterator = game.timeoutPlayers.iterator(); iterator.hasNext(); ) {
			PlayerData data = Murder.instance.playerManager.getData(iterator.next());
			if (data != null) {
				if (data.gunTimeout > 0) {
					data.gunTimeout--;
				} else if (data.reloadTimer > 0) {
					if (data.reloadTimer == 65) {
						data.getPlayer().getPlayer().playSound(data.getPlayer().getLocation(), "murder.gun.clip.out", 0.8f, 1f);
					}
					if (data.reloadTimer == 10) {
						data.getPlayer().getPlayer().playSound(data.getPlayer().getLocation(), "murder.gun.clip.in", 0.8f, 1f);
					}
					data.reloadTimer--;
					if (data.reloadTimer <= 0) {
						if (data.isInGame()) {
							data.getPlayer().getInventory().setItem(8, Murder.instance.itemManager.getBullet());
						}
						iterator.remove();
					}
				} else if (data.knifeTimout > 0) {
					data.knifeTimout--;
					if (data.knifeTimout <= 0) {
						if (data.isInGame()) {
							data.getPlayer().getInventory().setItem(4, Murder.instance.itemManager.getKnife());
						}
						iterator.remove();
					}
				} else if (data.speedTimeout > 0) {
					data.speedTimeout--;
					if (data.speedTimeout <= 0) {
						if (data.isInGame()) {
							data.getPlayer().setFoodLevel(6);
							data.getPlayer().setWalkSpeed(0.2f);
						}
						iterator.remove();
					}
				} else {
					iterator.remove();
				}
			} else {
				iterator.remove();
			}
		}

		if (footstepTicks % 5 == 0) {
			Player murderer = game.getPlayer(game.getMurderer());
			if (murderer != null) {
				for (Iterator<FootStep> iterator = footSteps.iterator(); iterator.hasNext(); ) {
					FootStep next = iterator.next();
					PlayerData playerData = Murder.instance.playerManager.getData(next.uuid);
					if (playerData == null || !playerData.isInGame() || playerData.killed) {
						iterator.remove();
						continue;
					}
					if (next.vector.distance(murderer.getLocation().toVector()) > 16) { continue; }
					next.display(murderer);
				}
			}
		}
		if (footstepTicks++ >= 20) {
			footstepTicks = 0;
			for (UUID uuid : game.players) {
				PlayerData data = Murder.instance.playerManager.getData(uuid);
				if (data != null && data.isInGame() && !data.isSpectator) {
					Player player = data.getPlayer();
					if (player.isOnGround()) {
						Color color = Color.WHITE;
						switch (ChatColor.getByChar(data.nameTag.substring(1, 2))) {
							case BLACK:
								color = Color.BLACK;
								break;
							case DARK_BLUE:
								color = Color.NAVY;
								break;
							case DARK_GREEN:
								color = Color.GREEN;
								break;
							case DARK_AQUA:
								color = Color.BLUE;
								break;
							case DARK_RED:
								color = Color.MAROON;
								break;
							case DARK_PURPLE:
								color = Color.PURPLE;
								break;
							case GOLD:
								color = Color.ORANGE;
								break;
							case GRAY:
								color = Color.SILVER;
								break;
							case DARK_GRAY:
								color = Color.GRAY;
								break;
							case BLUE:
								color = Color.TEAL;
								break;
							case GREEN:
								color = Color.LIME;
								break;
							case AQUA:
								color = Color.AQUA;
								break;
							case RED:
								color = Color.RED;
								break;
							case LIGHT_PURPLE:
								color = Color.FUCHSIA;
								break;
							case YELLOW:
								color = Color.YELLOW;
								break;
							case WHITE:
								color = Color.WHITE;
								break;
							case MAGIC:
							case BOLD:
							case STRIKETHROUGH:
							case UNDERLINE:
							case ITALIC:
							case RESET:
								break;
						}
						footSteps.add(new FootStep(data.uuid, player.getLocation().toVector(), color));
					}
				}
			}
		}

	}

	@Override
	public boolean finished() {
		if (game.players.size() <= 0) {
			//No winner
			game.winner = null;
			return true;
		}

		UUID murdererId = game.getMurderer();
		PlayerData murdererData = Murder.instance.playerManager.getData(murdererId);
		if (murdererData == null || murdererData.killed || !murdererData.isInGame() || !murdererData.getOfflinePlayer().isOnline()) {
			//Murderer left or was killed - bystanders won
			game.winner = Role.WEAPON;
			return true;
		}

		int aliveCount = 0;
		for (UUID bystanderId : game.getBystanders(true, true)) {
			PlayerData bystanderData = Murder.instance.playerManager.getData(bystanderId);
			if (bystanderData != null && !bystanderData.killed && bystanderData.isInGame() && bystanderData.getOfflinePlayer().isOnline()) {
				aliveCount++;
			}
		}
		if (aliveCount <= 0) {
			//All bystanders left or were killed by the murderer - murderer won
			game.winner = Role.MURDERER;
			return true;
		}

		return false;
	}
}
