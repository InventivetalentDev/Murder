package org.inventivetalent.murder.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.arena.Arena;
import org.inventivetalent.murder.game.state.GameState;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.message.MessageLoader;

public class SignListener implements Listener {

	static MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages", null);

	private Murder plugin;

	public SignListener(Murder plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void on(final SignChangeEvent event) {
		if (event.getLine(0) != null && event.getLine(0).toLowerCase().contains("[murder]")) {
			if (event.getPlayer().hasPermission("murder.sign.edit")) {
				if (event.getLine(plugin.signLineLeave) != null) {
					if (event.getLine(plugin.signLineLeave).toLowerCase().contains(plugin.signKeyLeave)) {
						event.setLine(plugin.signLineTitle, plugin.signTitle);
						event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("sign.create.leave", "sign.create.leave"));
						return;
					}
				}
				if (event.getLine(1) != null) {
					Arena arena;
					try {
						arena = plugin.arenaManager.getArenaById(Integer.parseInt(event.getLine(1)));
					} catch (NumberFormatException e) {
						arena = plugin.arenaManager.getArenaByName(event.getLine(1));
					}
					if (arena == null) {
						event.getPlayer().sendMessage(MESSAGE_LOADER.getMessage("command.arena.error.notFound", "command.arena.error.notFound"));
						event.setCancelled(true);
						return;
					}
					event.setLine(plugin.signLineTitle, plugin.signTitle);
					event.setLine(plugin.signLineArena, arena.name);
					event.setLine(plugin.signLineState, GameState.WAITING.getSignText());
					event.setLine(plugin.signLinePlayers, String.format(plugin.signFormatPlayers, 0, arena.maxPlayers));

					final int arenaId = arena.id;
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							if (event.getBlock().getState() instanceof Sign) {
								plugin.arenaManager.addArenaSign(arenaId, (Sign) event.getBlock().getState());
							}
						}
					}, 1);
				}
			}
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block != null) {
				if (block.getState() instanceof Sign) {
					Sign sign = (Sign) block.getState();
					if (plugin.signTitle.equals(sign.getLine(plugin.signLineTitle))) {
						event.setCancelled(true);

						if (sign.getLine(plugin.signLineLeave) != null) {
							if (sign.getLine(plugin.signLineLeave).toLowerCase().contains(plugin.signKeyLeave)) {
								event.getPlayer().chat("/murderLeave");
								return;
							}
						}

						event.getPlayer().chat("/murderJoin " + sign.getLine(plugin.signLineArena));
						Arena arena = plugin.arenaManager.getArenaByName(sign.getLine(plugin.signLineArena));
						if (arena != null) { plugin.arenaManager.addArenaSign(arena.id, sign); }
					}
				}
			}
		}
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		Block block=event.getBlock();
		if (block != null && block.getState() instanceof Sign) {
			Sign sign=(Sign)block.getState();
			if (sign.getLine(0) != null && sign.getLine(0).toLowerCase().contains("[murder]")) {
				if (!event.getPlayer().hasPermission("murder.sign.edit")) {
					event.setCancelled(true);
				}
			}
		}
	}

}
