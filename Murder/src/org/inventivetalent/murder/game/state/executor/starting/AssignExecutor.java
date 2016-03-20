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

package org.inventivetalent.murder.game.state.executor.starting;

import net.md_5.bungee.api.chat.TextComponent;
import org.inventivetalent.bossbar.BossBarAPI;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.Role;
import org.inventivetalent.murder.game.CountdownType;
import org.inventivetalent.murder.game.Game;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.murder.game.state.executor.CountdownExecutor;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.title.TitleAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AssignExecutor extends CountdownExecutor {

	int    ticks  = 0;
	Random random = new Random();

	public AssignExecutor(Game game) {
		super(game, CountdownType.START);
	}

	@Override
	public void tick() {
		super.tick();
		if (ticks == 0) {
			updatePlayerStates(GameState.ASSIGN, null);
		}
		if (ticks == 1) {//Assign
			List<UUID> toAssign = new ArrayList<>(game.players);

			//Pick a murderer
			updateRole(toAssign.remove(random.nextInt(toAssign.size())), Role.MURDERER);

			//Pick a weapon bystander
			updateRole(toAssign.remove(random.nextInt(toAssign.size())), Role.WEAPON);

			//Make all other players bystanders
			while (!toAssign.isEmpty()) {
				updateRole(toAssign.remove(0), Role.DEFAULT);
			}
		}
		if (ticks == 2) {//Send role messages
			for (UUID uuid : game.players) {
				PlayerData data = Murder.instance.playerManager.getData(uuid);
				if (data != null) {
					//Titles
					TitleAPI.reset(data.getPlayer());
					TitleAPI.sendTimings(data.getPlayer(), 10, Murder.instance.startTime, 10);
					TitleAPI.sendSubTitle(data.getPlayer(), new TextComponent(data.role.getSubTitle()));
					TitleAPI.sendTitle(data.getPlayer(), new TextComponent(data.role.getTitle()));

					//BossBar
					TextComponent textComponent = new TextComponent(String.format(data.role.getBarText(), data.nameTag));
					data.bossBar = BossBarAPI.addBar(data.getPlayer(), textComponent, data.role.getBarColor(), data.role.getBarStyle(), 1);
				}
			}
		}
		ticks++;
	}

	void updateRole(UUID uuid, Role role) {
		PlayerData data = Murder.instance.playerManager.getData(uuid);
		if (data == null) { throw new IllegalStateException("Missing player data for " + uuid); }
		data.role = role;
	}

	@Override
	public boolean finished() {
		return ticks >= 3;
	}
}
