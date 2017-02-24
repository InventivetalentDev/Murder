package org.inventivetalent.murder.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.Role;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.murder.projectile.GunProjectile;
import org.inventivetalent.murder.projectile.KnifeProjectile;
import org.inventivetalent.murder.projectile.MurderProjectile;
import org.inventivetalent.npclib.NPCLib;

import java.util.List;

public class GameListener implements Listener {

	private Murder plugin;

	public GameListener(Murder plugin) {
		this.plugin = plugin;
	}

	// Move

	@EventHandler
	public void on(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.playerManager.getData(player.getUniqueId());
		if (data != null) {
			if (data.isInGame() && data.getGame() != null) {
				if (data.gameState.ordinal() > GameState.LOBBY.ordinal()) {
					if (!data.getGame().arena.contains(event.getTo().toVector())) {
						Vector middle = data.getGame().arena.maxCorner.clone().midpoint(data.getGame().arena.minCorner.clone());
						player.setVelocity(middle.clone().subtract(event.getTo().toVector()).normalize());
					}
				}
			}
		}
	}

	//Entity Damage

	@EventHandler
	public void on(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();
			PlayerData data = plugin.playerManager.getData(player.getUniqueId());
			if (data != null) {
				if (data.isInGame()) {
					if (data.gameState.isInvulnerable() || data.isSpectator) {
						event.setCancelled(true);
					} else {
						if (player.getHealth() - event.getFinalDamage() <= 0.0) {
							event.setCancelled(true);
							//							data.damageAmount = 0;
							data.killed = true;
							data.getGame().killedPlayers.add(data.uuid);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();
			PlayerData data = plugin.playerManager.getData(player.getUniqueId());
			if (data != null) {
				if (data.isInGame() && data.getGame() != null) {
					if (data.gameState.isInvulnerable() || data.isSpectator) {
						event.setCancelled(true);
					} else {
						Entity damager = event.getDamager();
						if (damager != null) {
							PlayerData damagerData = null;

							if (damager.getType() == EntityType.PLAYER) {
								//								event.setCancelled(true);

								damagerData = Murder.instance.playerManager.getData(damager.getUniqueId());
								if (damagerData.role != Role.MURDERER) {
									event.setCancelled(true);
								}
							}
							if (damager.getType() == EntityType.ARROW) {
								if (damager.hasMetadata("MURDER")) {
									//									event.setCancelled(true);
									List<MetadataValue> metaList = damager.getMetadata("MURDER");
									if (metaList != null && !metaList.isEmpty()) {
										MurderProjectile projectile = (MurderProjectile) metaList.get(0).value();
										if (projectile != null) {
											damagerData = Murder.instance.playerManager.getData(projectile.shooter.getUniqueId());
											//Instant kill
											event.setDamage(40);
										}
									}
								}
							}

							if (damagerData != null) {
								if (damagerData.isInGame() && damagerData.getGame() != null) {
									if (//
											(damagerData.role == Role.MURDERER && (damager.getType() == EntityType.ARROW || plugin.itemManager.getKnife().equals(damagerData.getPlayer().getInventory().getItemInHand()))) ||// It's the murderer AND it's a direct attack OR the thrown knife
													(damagerData.role == Role.WEAPON && damager.getType() == EntityType.ARROW)// It's a weapon-bystander AND it's a shot bullet
											) {
										//Set the potential killer
										data.killer = damagerData.uuid;

										if (player.getHealth() - event.getFinalDamage() <= 0.0) {
											event.setCancelled(true);
											data.killed = true;
											data.getGame().killedPlayers.add(data.uuid);
										}
									} else {
										event.setCancelled(true);
									}
									//									if (damagerData.getPlayer().getItemInHand().equals(Murder.instance.itemManager.getKnife())) {
									//										//Set the potential killer
									//										data.killer = damagerData.uuid;
									//										if (data.damageAmount++ >= 2) {
									//											data.damageAmount = 0;
									//											data.killed = true;
									//											data.getGame().killedPlayers.add(data.uuid);
									//										}
									//
									//										for (UUID uuid : data.getGame().players) {
									//											AnimationAPI.playAnimation(player, data.getGame().getPlayer(uuid), AnimationAPI.Animation.TAKE_DAMGE);
									//										}
									//									}
								} else {
									event.setCancelled(true);
								}
							}
						}
					}
					/*else {
						Entity damager = event.getDamager();
						if (damager != null && damager.getType() == EntityType.PLAYER) {
							Player damagerPlayer = (Player) damager;
							PlayerData damagerData = plugin.playerManager.getData(damagerPlayer.getUniqueId());
							if (damagerData != null) {
								if (damagerData.isInGame() && !damagerData.killed) {
									//Set the potential killer
									data.killer = damagerPlayer.getUniqueId();
								}
							}
						}
					}*/
				}
			}
		}
		//		if (event.getEntityType() == EntityType.ARROW) {
		//			if (event.getEntity().hasMetadata("MURDER")) {
		//				event.setCancelled(true);
		//				event.setDamage(0);
		//			}
		//		}
	}

	@EventHandler
	public void on(PlayerDeathEvent event) {
		//		Player player = event.getEntity();
		//		PlayerData data = plugin.playerManager.getData(player.getUniqueId());
		//		if (data != null) {
		//			if (data.isInGame()) {
		//				if (data.gameState.isInvulnerable() || data.role == Role.SPECTATOR) {
		//					event.getEntity().setHealth(event.getEntity().getMaxHealth());
		//					event.setDeathMessage(null);
		//					event.getDrops().clear();
		//					event.getEntity().teleport(event.getEntity().getLocation());
		//				} else {
		//					data.killed = true;
		//					//noinspection ConstantConditions
		//					data.getGame().killedPlayers.add(player.getUniqueId());
		//				}
		//			}
		//		}
	}

	// Gun shot / Knife throw / Speed Boost

	@EventHandler
	public void on(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.playerManager.getData(player.getUniqueId());
		if (data != null) {
			if (data.isInGame()) {
				if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) { return; }

				if (data.role == Role.DEFAULT) {
					if (Murder.instance.itemManager.getSpeedBoost().equals(player.getItemInHand())) {
						player.getInventory().setItem(8, null);
						player.setFoodLevel(20);
						player.setWalkSpeed(0.3f);
						data.speedTimeout = 100;
						data.getGame().timeoutPlayers.add(data.uuid);
						return;
					}
				}

				MurderProjectile projectile = null;
				if (data.role == Role.WEAPON) {
					if (Murder.instance.itemManager.getGun().equals(player.getItemInHand())) {
						if (Murder.instance.itemManager.getBullet().equals(player.getInventory().getItem(8))) {
							player.getInventory().setItem(8, null);
							projectile = new GunProjectile(data.getGame(), player, player.getLocation().getDirection());
							data.reloadTimer = 80;
							//noinspection ConstantConditions
							data.getGame().timeoutPlayers.add(data.uuid);

							//							data.getPlayer().getWorld().playSound(data.getPlayer().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.2f, 1.5f);
							//							data.getPlayer().getWorld().playSound(data.getPlayer().getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.1f, 2f);
							data.getPlayer().getWorld().playSound(data.getPlayer().getLocation(), "murder.gun.shot", 0.5f, 1f);
						}
					}
				}
				if (data.role == Role.MURDERER) {
					if (Murder.instance.itemManager.getKnife().equals(player.getItemInHand())) {
						player.getInventory().setItem(4, null);
						projectile = new KnifeProjectile(data.getGame(), player, player.getLocation().getDirection());
						data.knifeTimout = 600;
						//noinspection ConstantConditions
						data.getGame().timeoutPlayers.add(data.uuid);

						//						data.getPlayer().getWorld().playSound(data.getPlayer().getLocation(), Sound.ENTITY_SNOWMAN_SHOOT, 0.1f, 1f);
						data.getPlayer().getWorld().playSound(data.getPlayer().getLocation(), Sound.BLOCK_METAL_HIT, 1f, 3f);
					}
				}

				if (projectile != null) {
					//noinspection ConstantConditions
					data.getGame().projectiles.add(projectile);
				}

			}
		}
	}

	// Pickup

	@EventHandler
	public void on(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.playerManager.getData(player.getUniqueId());
		if (data != null) {
			if (data.isInGame()) {
				ItemStack itemStack = event.getItem() != null ? event.getItem().getItemStack() : null;
				if (itemStack != null) {
					if (data.isSpectator) {
						event.setCancelled(true);
					} else {
						if (Murder.instance.itemManager.getKnife().equals(itemStack)) {
							event.setCancelled(true);
							if (data.role == Role.MURDERER) {
								event.getItem().remove();
								data.getPlayer().getInventory().setItem(4, Murder.instance.itemManager.getKnife());
								//noinspection ConstantConditions
								data.getGame().timeoutPlayers.remove(data.uuid);
								data.knifeTimout = 0;

								data.getPlayer().playSound(data.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2f, 1f);
							}
						} else if (Murder.instance.itemManager.getGun().equals(itemStack)) {
							event.setCancelled(true);
							if (data.role == Role.DEFAULT || data.role == Role.WEAPON) {
								if (data.gunTimeout <= 0) {
									data.role = Role.WEAPON;
									event.getItem().remove();
									data.getPlayer().getInventory().setItem(4, Murder.instance.itemManager.getGun());
									data.getPlayer().getInventory().setItem(8, Murder.instance.itemManager.getBullet());

									data.getPlayer().playSound(data.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2f, 1f);
								}
							}
						} else if (Murder.instance.itemManager.getLoot().equals(itemStack)) {
							event.setCancelled(true);
							data.lootCount += event.getItem().getItemStack().getAmount();
							event.getItem().remove();

							data.getPlayer().playSound(data.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2f, 1f);

							if (data.lootCount >= 5) {
								if (data.role == Role.DEFAULT) {
									data.lootCount -= 5;
									data.role = Role.WEAPON;
									data.getPlayer().getInventory().setItem(4, Murder.instance.itemManager.getGun());
									data.getPlayer().getInventory().setItem(8, Murder.instance.itemManager.getBullet());

									data.getPlayer().playSound(data.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
								}
							}

							data.getPlayer().setLevel(data.lootCount);
						} else {
							event.setCancelled(true);
						}
					}
				}
			}
		}

		if (NPCLib.isNPC(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	// Cancelled events

	@EventHandler
	public void on(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.playerManager.getData(player.getUniqueId());
		if (data != null) {
			if (data.isInGame()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void on(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerData data = plugin.playerManager.getData(player.getUniqueId());
		if (data != null) {
			if (data.isInGame()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void on(PlayerItemDamageEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.playerManager.getData(player.getUniqueId());
		if (data != null) {
			if (data.isInGame()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void on(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		PlayerData data = plugin.playerManager.getData(player.getUniqueId());
		if (data != null) {
			if (data.isInGame()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void on(ProjectileHitEvent event) {
		//		if (event.getEntity().hasMetadata("MURDER")) {
		//			event.getEntity().remove();
		//		}
	}
}
