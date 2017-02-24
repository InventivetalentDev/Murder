package org.inventivetalent.murder.game.state.executor;

import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.CountdownType;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.pluginannotations.message.MessageFormatter;

public class CountdownExecutor extends LeavableExecutor {

	public CountdownType type;

	public CountdownExecutor(Game game, CountdownType type) {
		super(game);
		this.type = type;
		//		resetTime();
	}

	protected void resetTime() {
		if (type == CountdownType.LOBBY) {
			game.lobbyCountdown = Murder.instance.lobbyTime;
		}
		if (type == CountdownType.START) {
			game.startCountdown = Murder.instance.startTime;
		}
	}

	@Override
	public void tick() {
		super.tick();
		game.ticks++;
		if (game.ticks >= 20) {
			if (type == CountdownType.LOBBY) {
				if (game.players.size() >= game.arena.minPlayers) {
					game.lobbyCountdown--;
					if ((game.lobbyCountdown + 1) % 10 == 0 || game.lobbyCountdown < 5) {
						game.broadcastMessage(Game.MESSAGE_LOADER.getMessage("countdown.lobby.time", "countdown.lobby.time", new MessageFormatter() {
							@Override
							public String format(String key, String message) {
								return String.format(message, game.lobbyCountdown + 1);
							}
						}));
					}
				} else {
					//Not enough players
					if (game.lobbyCountdown < Murder.instance.lobbyTime) {//We actually started the countdown
						resetTime();
						game.broadcastMessage(Game.MESSAGE_LOADER.getMessage("countdown.lobby.cancel", "countdown.lobby.cancel"));
					}
				}
			}
			if (type == CountdownType.START) {
				game.startCountdown--;
				if ((game.startCountdown + 1) % 10 == 0 || game.startCountdown < 5) {
					game.broadcastMessage(Game.MESSAGE_LOADER.getMessage("countdown.start.time", "countdown.start.time", new MessageFormatter() {
						@Override
						public String format(String key, String message) {
							return String.format(message, game.startCountdown + 1);
						}
					}));
				}
			}
			game.ticks = 0;
		}
	}

	@Override
	public boolean finished() {
		if (type == CountdownType.LOBBY) { return game.lobbyCountdown <= 0; }
		if (type == CountdownType.START) { return game.startCountdown <= 0; }
		return super.finished();
	}

	@Override
	public boolean revert() {
		return false;
	}
}
