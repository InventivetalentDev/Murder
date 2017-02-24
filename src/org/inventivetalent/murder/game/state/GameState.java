package org.inventivetalent.murder.game.state;

import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.executor.ended.EndedExecutor;
import org.inventivetalent.murder.game.state.executor.ended.ResetExecutor;
import org.inventivetalent.murder.game.state.executor.ingame.DropLootExecutor;
import org.inventivetalent.murder.game.state.executor.ingame.StartedExecutor;
import org.inventivetalent.murder.game.state.executor.init.WaitingExecutor;
import org.inventivetalent.murder.game.state.executor.lobby.LobbyExecutor;
import org.inventivetalent.murder.game.state.executor.starting.*;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.message.MessageLoader;

import static org.inventivetalent.murder.game.state.GameState.Flag.INVULNERABLE;
import static org.inventivetalent.murder.game.state.GameState.Flag.JOINABLE;

public enum GameState {

	/* Initial state when no game instance exists yet */
	WAITING("waiting.sign", WaitingExecutor.class, JOINABLE, INVULNERABLE),

	/* Lobby States */
	LOBBY("lobby.sign", LobbyExecutor.class, JOINABLE, INVULNERABLE),

	/* Starting States */
	STARTING("starting.sign", StartingExecutor.class, INVULNERABLE),
	DISGUISE(DisguiseExecutor.class, INVULNERABLE),
	ASSIGN(AssignExecutor.class, INVULNERABLE),
	TELEPORT(TeleportExecutor.class, INVULNERABLE),
	STARTING_DELAY(StartingDelayExecutor.class, INVULNERABLE),
	GIVE_ITEMS(GiveItemsExecutor.class),

	/* Ingame States */
	STARTED("started.sign", StartedExecutor.class),
	DROP_LOOT(DropLootExecutor.class),

	/* End States */
	ENDED("ended.sign", EndedExecutor.class, INVULNERABLE),
	RESET(ResetExecutor.class, INVULNERABLE),

	/* Dummy state to tell the manager to remove the game instance */
	DISPOSE;

	private static final MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.game.state", null);

	private String signKey;

	private Class<? extends StateExecutor> executorClass;

	private boolean joinable;
	private boolean invulnerable;

	GameState() {
	}

	GameState(String signKey) {
		this.signKey = signKey;
	}

	GameState(Class<? extends StateExecutor> executorClass, Flag... flags) {
		this.signKey = null;
		this.executorClass = executorClass;
		setFlags(flags);
	}

	GameState(String signKey, Class<? extends StateExecutor> executorClass) {
		this.signKey = signKey;
		this.executorClass = executorClass;
	}

	GameState(String signKey, Class<? extends StateExecutor> executorClass, Flag... flags) {
		this.signKey = signKey;
		this.executorClass = executorClass;
		setFlags(flags);
	}

	void setFlags(Flag... flags) {
		for (Flag flag : flags) {
			if (flag == Flag.JOINABLE) { joinable = true; }
			if (flag == Flag.INVULNERABLE) { invulnerable = true; }
		}
	}

	public String getSignText() {
		if (signKey == null) {
			GameState prev = previous();
			while (prev.signKey == null) {
				prev = previous();
			}
			this.signKey = prev.signKey;
		}
		return MESSAGE_LOADER.getMessage(signKey, signKey);
	}

	public StateExecutor newExecutor(Game game) {
		if (executorClass != null) {
			try {
				return executorClass.getConstructor(Game.class).newInstance(game);
			} catch (Exception e) {
				throw new RuntimeException("Failed to instantiate GameExecutor " + executorClass, e);
			}
		}
		return null;
	}

	public boolean isJoinable() {
		return joinable;
	}

	public boolean isInvulnerable() {
		return invulnerable;
	}

	public GameState next() {
		if (ordinal() < values().length - 1) {
			return values()[ordinal() + 1];
		}
		return values()[0];
	}

	public GameState previous() {
		if (ordinal() > 0) {
			return values()[ordinal() - 1];
		}
		return values()[values().length - 1];
	}

	public static GameState forExecutor(Class<? extends StateExecutor> executorClass) {
		if (executorClass == null) { return null; }
		for (GameState state : values()) {
			if (executorClass.equals(state.executorClass)) {
				return state;
			}
		}
		return null;
	}

	public enum Flag {
		JOINABLE,
		INVULNERABLE;
	}

}
