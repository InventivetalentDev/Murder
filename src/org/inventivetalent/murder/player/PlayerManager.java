package org.inventivetalent.murder.player;

import org.bukkit.OfflinePlayer;
import org.inventivetalent.murder.Murder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

	private Murder plugin;

	//	@ConfigValue(path = "storePlayerData") public boolean storePlayerData;
	public File dataFolder;

	public final Map<UUID, PlayerData> dataMap = new HashMap<>();

	public PlayerManager(Murder plugin) {
		this.plugin = plugin;
		dataFolder = new File(plugin.getDataFolder(), "playerdata");
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}
	}

	public boolean contains(UUID uuid) {
		return dataMap.containsKey(uuid);
	}

	@Nullable
	public PlayerData getData(@Nullable UUID uuid) {
		if (uuid == null) { return null; }
		if (dataMap.containsKey(uuid)) { return dataMap.get(uuid); }
		return loadFromFile(uuid);
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
		deleteDataFile(uuid);
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
	public PlayerData loadFromFile(@Nonnull UUID uuid) {
		try {
			File file = new File(dataFolder, uuid.toString());
			PlayerData data = getOrCreateData(uuid);
			if (!file.exists()) { return data; }
			data.loadFromFile(file);
			data.stored = true;
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
