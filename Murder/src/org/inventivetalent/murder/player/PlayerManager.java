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

package org.inventivetalent.murder.player;

import org.bukkit.OfflinePlayer;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.pluginannotations.config.ConfigValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

	private Murder plugin;

	@ConfigValue(path = "storePlayerData") public boolean storePlayerData;
	public                                        File    dataFolder;

	public final Map<UUID, PlayerData> dataMap = new HashMap<>();

	public PlayerManager(Murder plugin) {
		this.plugin = plugin;
		dataFolder = new File(plugin.getDataFolder(), "playerdata");
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}
	}

	@Nullable
	public PlayerData getData(UUID uuid) {
		if (!dataMap.containsKey(uuid)) { return null; }
		return dataMap.get(uuid);
	}

	@Nonnull
	public PlayerData getOrCreateData(UUID uuid) {
		if (!dataMap.containsKey(uuid)) {
			dataMap.put(uuid, new PlayerData(uuid));
		}
		return dataMap.get(uuid);
	}

	@Nullable
	public PlayerData removeData(UUID uuid) {
		if (dataMap.containsKey(uuid)) {
			return dataMap.remove(uuid);
		}
		return null;
	}

	@Nonnull
	public File saveDataToFile(PlayerData toSave) {
		try {
			File file = new File(dataFolder, toSave.uuid.toString());
			if (!file.exists()) {
				file.createNewFile();
			}
			toSave.saveToFile(file);
			return file;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Nonnull
	public PlayerData loadFromFile(UUID uuid) {
		try {
			File file = new File(dataFolder, uuid.toString());
			PlayerData data = getOrCreateData(uuid);
			if (!file.exists()) { return data; }
			data.loadFromFile(file);
			return data;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean deleteDataFile(UUID uuid) {
		File file = new File(dataFolder, uuid.toString());
		return file.delete();
	}

	public void disguisePlayer(@Nonnull OfflinePlayer player, @Nonnull String name) {
		plugin.nameManager.setNameTag(player, name);
		plugin.skinManager.setSkin(player, name.substring(1, 2));
	}

	public void resetPlayer(@Nonnull OfflinePlayer player) {
		plugin.nameManager.setNameTag(player, null);
		plugin.skinManager.setSkin(player, null);
	}
}
