package org.inventivetalent.murder.game.state.executor.ended;

import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.Role;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.game.state.executor.LeavableExecutor;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.message.MessageFormatter;
import org.inventivetalent.pluginannotations.message.MessageLoader;

public class EndedExecutor extends LeavableExecutor {

	static MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.game", null);

	boolean firstTick  = true;
	int     endSeconds = 0;

	public EndedExecutor(Game game) {
		super(game);
		game.ticks = 0;
	}

	@Override
	public void tick() {
		super.tick();

		if (firstTick) {
			firstTick = false;
			updatePlayerStates(GameState.ENDED, null);

			MessageFormatter murdererFormatter = new MessageFormatter() {
				@Override
				public String format(String key, String message) {
					PlayerData murdererData = Murder.instance.playerManager.getData(game.getMurderer());
					if (murdererData != null) {
						return String.format(message, murdererData.nameTag.substring(0, 2) + murdererData.getPlayer().getName(), murdererData.nameTag);
					}
					return String.format(message, "?", "?");
				}
			};

			if (game.winner == null) {
				game.broadcastMessage(MESSAGE_LOADER.getMessage("winner.draw", "winner.draw"));
			} else if (game.winner == Role.WEAPON) {
				game.broadcastMessage(MESSAGE_LOADER.getMessage("winner.bystander", "winner.bystander", murdererFormatter));
			} else if (game.winner == Role.MURDERER) {
				game.broadcastMessage(MESSAGE_LOADER.getMessage("winner.murderer", "winner.murderer", murdererFormatter));
			} else {
				Murder.instance.getLogger().warning("Invalid Winner: " + game.winner);
			}
			//TODO: print scoreboard
		}

		game.ticks++;
		if (game.ticks >= 20) {
			endSeconds++;
			game.ticks = 0;
		}

	}

	@Override
	public boolean finished() {
		return super.finished() || endSeconds >= Murder.instance.endDelay;
	}
}
