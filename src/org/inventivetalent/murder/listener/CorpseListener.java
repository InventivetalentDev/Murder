package org.inventivetalent.murder.listener;

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
import org.inventivetalent.npclib.NPCLib;
import org.inventivetalent.npclib.event.NPCCollisionEvent;
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
	public void on(final NPCCollisionEvent event) {
		if (event.getEntity() != null && event.getEntity().getType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();
			PlayerData data = plugin.playerManager.getData(player.getUniqueId());
			if (data != null) {
				if (data.isInGame() && data.getGame() != null) {
					if (!data.isSpectator && data.role == Role.MURDERER) {
						TitleAPI.sendTimings(player, 0, 10, 5);
						if (data.lootCount > 0) {
							TitleAPI.sendSubTitle(player, new TextComponent(MESSAGE_LOADER.getMessage("disguise.info", "disguise.info", new MessageFormatter() {
								@Override
								public String format(String key, String message) {
									return String.format(message, event.getNpc().getName());
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
