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
