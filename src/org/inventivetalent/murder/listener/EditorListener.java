package org.inventivetalent.murder.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.inventivetalent.murder.Murder;

public class EditorListener implements Listener {

	private Murder plugin;

	public EditorListener(Murder plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerInteractEvent event) {
//		if (event.isCancelled()) { return; }
		if (plugin.arenaEditorManager.isEditing(event.getPlayer().getUniqueId())) {
			plugin.arenaEditorManager.getEditor(event.getPlayer().getUniqueId()).handleInteract(event);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(BlockPlaceEvent event) {
		if (event.isCancelled() || !event.canBuild()) { return; }
		if (plugin.arenaEditorManager.isEditing(event.getPlayer().getUniqueId())) {
			plugin.arenaEditorManager.getEditor(event.getPlayer().getUniqueId()).handleBlockPlace(event);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(BlockBreakEvent event) {
		if (event.isCancelled()) { return; }
		if (plugin.arenaEditorManager.isEditing(event.getPlayer().getUniqueId())) {
			plugin.arenaEditorManager.getEditor(event.getPlayer().getUniqueId()).handleBlockBreak(event);
		}
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		plugin.arenaEditorManager.removeEditor(event.getPlayer().getUniqueId());
	}

}
