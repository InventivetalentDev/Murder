package org.inventivetalent.murder.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BasicPlayerData {

	public final UUID uuid;

	public BasicPlayerData(UUID uuid) {
		if (uuid == null) { throw new IllegalArgumentException("uuid cannot be null"); }
		this.uuid = uuid;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		BasicPlayerData that = (BasicPlayerData) o;

		return uuid.equals(that.uuid);

	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
}
