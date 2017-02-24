package org.inventivetalent.murder.player;

import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.Role;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;

import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerData extends StoredData {

	public UUID gameId;

	public String nameTag;
	public String disguiseTag;// Murderer only

	public GameState gameState = GameState.WAITING;
	public Role    role;
	public boolean isSpectator;

//	public int damageAmount;// Determines when the player is "dead"
	public boolean killed = false;
	public UUID killer;

	public int reloadTimer;// Delay until the gun is reloaded
	public int gunTimeout;// Delay for players who killed innocent bystanders
	public int knifeTimout;// Backup delay for the murderer to get their knife back
	public int speedTimeout;// Speed-Boost timeout

	public int lootCount;

	public BossBar bossBar;

	public PlayerData(UUID uuid) {
		super(uuid);
	}

	@Override
	public void storeData(boolean clear) {
		super.storeData(clear);
		Murder.instance.playerManager.saveDataToFile(this);
	}

	public boolean isInGame() {
		return gameId != null && getGame() != null;
	}

	@Nullable
	public Game getGame() {
		if (gameId == null) { return null; }
		return Murder.instance.gameManager.getGame(gameId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		if (!super.equals(o)) { return false; }

		PlayerData data = (PlayerData) o;

		if (nameTag != null ? !nameTag.equals(data.nameTag) : data.nameTag != null) { return false; }
		return gameState == data.gameState;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (nameTag != null ? nameTag.hashCode() : 0);
		result = 31 * result + (gameState != null ? gameState.hashCode() : 0);
		return result;
	}
}
