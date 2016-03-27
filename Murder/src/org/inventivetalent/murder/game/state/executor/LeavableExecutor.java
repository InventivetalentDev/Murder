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

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.StateExecutor;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.rpapi.ResourcePackAPI;

import java.util.UUID;

public class LeavableExecutor extends StateExecutor {

	public LeavableExecutor(Game game) {
		super(game);
	}

	@Override
	public void tick() {
		if (!game.leavingPlayers.isEmpty()) {
			for (UUID uuid : game.leavingPlayers) {
				game.players.remove(uuid);
				game.broadcastLeave(uuid);

				PlayerData data = Murder.instance.playerManager.removeData(uuid);
				if (data != null) {
					//Remove black screen
					data.getPlayer().getInventory().setHelmet(null);

					//Clear effects
					for (PotionEffect effect : data.getPlayer().getActivePotionEffects()) {
						data.getPlayer().removePotionEffect(effect.getType());
					}

					if (data.getOfflinePlayer().isOnline()) {
						data.restoreData();
						Murder.instance.playerManager.resetPlayer(data.getOfflinePlayer());
					}

					for (UUID uuid1 : game.players) {
						Player player = game.getPlayer(uuid1);
						if (player != null) { player.showPlayer(data.getPlayer()); }
					}

					//Reset resource pack
					ResourcePackAPI.setResourcepack(data.getPlayer(), Murder.instance.resetPackUrl, Murder.instance.resetPackHash);

					//Reset BossBar
					if (data.bossBar != null) {
						data.bossBar.removePlayer(data.getPlayer());
					}
				}
			}
			game.leavingPlayers.clear();
		}
	}

	@Override
	public boolean finished() {
		return super.finished();
	}
}
