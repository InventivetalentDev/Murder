package org.inventivetalent.murder.game.state.executor.starting;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarFlag;
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

				//Titles
				TitleAPI.reset(data.getPlayer());
				TitleAPI.sendTimings(data.getPlayer(), 10, (Murder.instance.startTime * 20) - 5, 10);
				TitleAPI.sendSubTitle(data.getPlayer(), new TextComponent(data.role.getSubTitle()));
				TitleAPI.sendTitle(data.getPlayer(), new TextComponent(data.role.getTitle()));

				//BossBar
				TextComponent textComponent = new TextComponent(String.format(data.role.getBarText(), data.nameTag));
				data.bossBar = Bukkit.createBossBar(textComponent.getText(), data.role.getBarColor(), data.role.getBarStyle(), BarFlag.DARKEN_SKY);
				data.bossBar.addPlayer(data.getPlayer());
			}
		}
		ticks++;
	}

	void updateRole(UUID uuid, Role role) {
		PlayerData data = Murder.instance.playerManager.getData(uuid);
		data.role = role;
	}

	@Override
	public boolean finished() {
		return super.finished() || ticks >= 3;
	}
}
