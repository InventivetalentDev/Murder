package org.inventivetalent.murder.arena;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.inventivetalent.murder.Murder;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class ArenaManager {

	private Murder plugin;
	public  File   arenaFolder;

	public final Map<String, Integer>        nameMap = new HashMap<>();
	public final Map<Integer, Set<Location>> signMap = new HashMap<>();

	public ArenaManager(Murder plugin) {
		this.plugin = plugin;
		arenaFolder = new File(Murder.instance.getDataFolder(), "arenas");
		if (!arenaFolder.exists()) {
			arenaFolder.mkdirs();
		}
	}

	public void addArena(Arena arena) {
		File file = new File(arenaFolder, String.valueOf(arena.id));
		if (file.exists()) { throw new IllegalArgumentException("arena file exists"); }
		try {
			file.createNewFile();
			Murder.instance.writeJson(arena.toJson(), file);
			nameMap.put(arena.name.toLowerCase(), arena.id);
			signMap.put(arena.id, new HashSet<Location>());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Arena getArenaById(int id) {
		if (id < 0) { return null; }
		File file = new File(arenaFolder, String.valueOf(id));
		if (!file.exists()) { return null; }
		try {
			return new Arena(new JsonParser().parse(new FileReader(file)).getAsJsonObject());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Arena getArenaByName(String name) {
		if (name == null || name.isEmpty()) { return null; }
		if (nameMap.containsKey(name.toLowerCase())) {
			return getArenaById(nameMap.get(name.toLowerCase()));
		}
		return null;
	}

	public Set<Sign> getArenaSigns(int id) {
		Set<Sign> signs = new HashSet<>();
		if (!signMap.containsKey(id)) { return signs; }
		for (Iterator<Location> iterator = signMap.get(id).iterator(); iterator.hasNext(); ) {
			Location next = iterator.next();
			Block block = next.getBlock();
			if (!(block.getState() instanceof Sign)) {
				iterator.remove();
				continue;
			}
			signs.add((Sign) block.getState());
		}
		return signs;
	}

	public void addArenaSign(int id, Sign sign) {
		Set<Location> signs = signMap.get(id);
		if (signs == null) { signs = new HashSet<>(); }
		if (!signs.contains(sign.getLocation())) { signs.add(sign.getLocation()); }
		signMap.put(id, signs);
	}

	public boolean removeArena(Arena arena) {
		if (arena == null) { return false; }
		nameMap.remove(arena.name);
		File file = new File(arenaFolder, String.valueOf(arena.id));
		return file.delete();
	}

	public void loadJson(JsonArray jsonArray) {
		for (Iterator<JsonElement> iterator = jsonArray.iterator(); iterator.hasNext(); ) {
			JsonObject next = iterator.next().getAsJsonObject();
			nameMap.put(next.get("name").getAsString(), next.get("id").getAsInt());
		}
	}

	//
	public JsonArray toJson() {
		JsonArray jsonArray = new JsonArray();

		for (Map.Entry<String, Integer> entry : nameMap.entrySet()) {
			JsonObject jsonEntry = new JsonObject();
			jsonEntry.addProperty("name", entry.getKey());
			jsonEntry.addProperty("id", entry.getValue());
			jsonArray.add(jsonEntry);
		}

		return jsonArray;
	}

}
