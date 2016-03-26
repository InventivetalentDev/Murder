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

package org.inventivetalent.murder.spectate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.itembuilder.ItemBuilder;
import org.inventivetalent.itembuilder.SkullMetaBuilder;
import org.inventivetalent.menubuilder.inventory.InventoryMenuBuilder;
import org.inventivetalent.menubuilder.inventory.ItemListener;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.message.MessageLoader;

import java.util.Set;
import java.util.UUID;

public class SpectateManager {

	static MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.game.spectate", null);

	private Murder plugin;

	public SpectateManager(Murder plugin) {
		this.plugin = plugin;
	}

	public boolean teleportToClosestPlayer(PlayerData who) {
		Game game = plugin.gameManager.getGame(who.gameId);
		if (game != null) {
			PlayerData closest = null;
			double closestDistance = Double.MAX_VALUE;
			for (UUID uuid : game.players) {
				if (uuid.equals(who.uuid)) { continue; }
				PlayerData data = plugin.playerManager.getData(uuid);
				if (data == null) { continue; }
				double distance = data.getPlayer().getLocation().distanceSquared(who.getPlayer().getLocation());
				if (distance < closestDistance) {
					closestDistance = distance;
					closest = data;
				}
			}
			if (closest != null) {
				who.getPlayer().teleport(closest.getPlayer().getLocation());
				return true;
			}
		}
		return false;
	}

	public void openSpectatorMenu(PlayerData data) {
		Game game = plugin.gameManager.getGame(data.gameId);
		if (game != null) {
			Set<UUID> aliveIds = game.getAlivePlayers();
			int aliveCount = aliveIds.size();
			InventoryMenuBuilder menuBuilder = new InventoryMenuBuilder(aliveCount <= 9 ? 9 : aliveCount <= 18 ? 18 : aliveCount <= 27 ? 27 : 36, MESSAGE_LOADER.getMessage("menu.title", "menu.title"));

			int slot = 0;
			for (UUID uuid : aliveIds) {
				final PlayerData data1 = plugin.playerManager.getData(uuid);
				if (data1 != null) {
					ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM, 1, 3);
					SkullMetaBuilder metaBuilder = itemBuilder.buildMeta().withDisplayName(data1.nameTag).convertBuilder(SkullMetaBuilder.class);
					metaBuilder.withOwner(data1.getPlayer().getName());
					metaBuilder.withLore("§7(" + data1.getPlayer().getName() + "§7)");
					itemBuilder = metaBuilder.item();

					menuBuilder.withItem(slot++, itemBuilder.build(), new ItemListener() {
						@Override
						public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
							if (player == null || data1.getPlayer() == null) { return; }
							player.teleport(data1.getPlayer().getLocation());
						}
					}, InventoryMenuBuilder.ALL_CLICK_TYPES);
				}
			}

			menuBuilder.show(data.getPlayer());
		}
	}

}
