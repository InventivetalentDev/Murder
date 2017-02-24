package org.inventivetalent.murder.name;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.nicknamer.api.INickNamer;
import org.inventivetalent.nicknamer.api.NickManager;

public class NameListener implements Listener {

	private Murder plugin;

	public NameListener(Murder plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void on(org.inventivetalent.nicknamer.api.event.NickNamerUpdateEvent event) {
//		PlayerData data = plugin.playerManager.getData(event.getPlayer().getUniqueId());
//		if (data != null) {
//			if (data.isInGame()) {
//				if (getNickManager().isNicked(event.getPlayer().getUniqueId())) {
//					event.setNick(getNickManager().getNick(event.getPlayer().getUniqueId()));
//				}
//			}
//		}
	}

	NickManager getNickManager() {
		return ((INickNamer) Bukkit.getPluginManager().getPlugin("NickNamer")).getAPI();
	}

}
