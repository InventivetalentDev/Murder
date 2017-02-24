package org.inventivetalent.murder.arena.editor;

import org.inventivetalent.murder.Murder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArenaEditorManager {

	private Murder plugin;

	public Map<UUID, ArenaEditor> editorMap = new HashMap<>();

	public ArenaEditorManager(Murder plugin) {
		this.plugin = plugin;
	}

	public ArenaEditor getEditor(UUID uuid) {
		if (editorMap.containsKey(uuid)) {
			return editorMap.get(uuid);
		}
		return null;
	}

	public boolean isEditing(UUID uuid) {
		return editorMap.containsKey(uuid);
	}

	public ArenaEditor newEditor(UUID uuid) {
		removeEditor(uuid);

		ArenaEditor editor = new ArenaEditor(uuid);
		editorMap.put(uuid, editor);
		return editor;
	}

	public ArenaEditor removeEditor(UUID uuid) {
		return editorMap.remove(uuid);
	}

}
