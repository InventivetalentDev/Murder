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
