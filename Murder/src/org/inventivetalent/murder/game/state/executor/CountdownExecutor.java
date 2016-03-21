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
		resetTime();
	}

	void resetTime() {
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
					game.broadcastMessage(Game.MESSAGE_LOADER.getMessage("countdown.lobby.time", "countdown.lobby.time", new MessageFormatter() {
						@Override
						public String format(String key, String message) {
							return String.format(message, game.lobbyCountdown);
						}
					}));
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
				game.broadcastMessage(Game.MESSAGE_LOADER.getMessage("countdown.start.time", "countdown.start.time", new MessageFormatter() {
					@Override
					public String format(String key, String message) {
						return String.format(message, game.startCountdown);
					}
				}));
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
