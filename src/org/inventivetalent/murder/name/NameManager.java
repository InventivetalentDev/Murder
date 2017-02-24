package org.inventivetalent.murder.name;

import org.bukkit.OfflinePlayer;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.config.ConfigValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NameManager {

	private Murder plugin;

	public final Random random = new Random();

	@ConfigValue(path = "name.tags") public                    List<String> tags   = new ArrayList<>();
	@ConfigValue(path = "name.colors", colorChar = '&') public List<String> colors = new ArrayList<>();

	public NameManager(Murder plugin) {
		this.plugin = plugin;
		PluginAnnotations.CONFIG.loadValues(plugin, this);
	}

	public String randomTag() {
		return tags.get(random.nextInt(tags.size()));
	}

	public String randomColor() {
		return colors.get(random.nextInt(colors.size()));
	}

	public String randomName() {
		return randomColor() + randomTag();
	}

	public void setNameTag(OfflinePlayer player, String nameTag) {
		if (nameTag == null || nameTag.isEmpty()) {
			plugin.getNickManager().removeNick(player.getUniqueId());
		} else {
			plugin.getNickManager().setNick(player.getUniqueId(), nameTag);
		}
		PlayerData playerData = plugin.playerManager.getData(player.getUniqueId());
		if (nameTag == null) {
			if (playerData == null) { return; }
		}
		if (playerData != null) {
			playerData.nameTag = nameTag;
		}
	}

}
