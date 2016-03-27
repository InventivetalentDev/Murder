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

package org.inventivetalent.murder.listener;

import de.inventivegames.npc.NPCLib;
import de.inventivegames.npc.event.NPCCollideEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.Role;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.message.MessageFormatter;
import org.inventivetalent.pluginannotations.message.MessageLoader;
import org.inventivetalent.title.TitleAPI;

public class CorpseListener implements Listener {

	static MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.game", null);

	private Murder plugin;

	public CorpseListener(Murder plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void on(final NPCCollideEvent event) {
		if (event.getWith() != null && event.getWith().getType() == EntityType.PLAYER) {
			Player player = (Player) event.getWith();
			PlayerData data = plugin.playerManager.getData(player.getUniqueId());
			if (data != null) {
				if (data.isInGame() && data.getGame() != null) {
					if (!data.isSpectator && data.role == Role.MURDERER) {
						TitleAPI.sendTimings(player, 0, 10, 5);
						if (data.lootCount > 0) {
							TitleAPI.sendSubTitle(player, new TextComponent(MESSAGE_LOADER.getMessage("disguise.info", "disguise.info", new MessageFormatter() {
								@Override
								public String format(String key, String message) {
									return String.format(message, event.getNPC().getName());
								}
							})));
						} else {
							TitleAPI.sendSubTitle(player, new TextComponent(MESSAGE_LOADER.getMessage("disguise.error", "disguise.error")));
						}
						TitleAPI.sendTitle(player, new TextComponent(""));
					}
				}
			}
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.playerManager.getData(player.getUniqueId());
		if (data != null && data.isInGame() && data.getGame() != null) {
			if (!data.isSpectator && data.role == Role.MURDERER) {
				for (final Entity entity : player.getNearbyEntities(1, 2, 1)) {
					if (NPCLib.isNPC(entity)) {
						if (data.lootCount > 0) {
							data.lootCount--;
							player.setLevel(data.lootCount);

							String originalNametag = data.nameTag;
							data.disguiseTag = entity.getName();
							plugin.playerManager.disguisePlayer(player, entity.getName());
							data.nameTag = originalNametag;
							player.sendMessage(MESSAGE_LOADER.getMessage("disguise.disguised", "disguise.disguised", new MessageFormatter() {
								@Override
								public String format(String key, String message) {
									return String.format(message, entity.getName());
								}
							}));
						}
						break;
					}
				}
			}
		}
	}

}
