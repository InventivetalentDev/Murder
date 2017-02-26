package org.inventivetalent.murder.skin;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.inventivetalent.murder.Murder;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class SkinManager {

	private Murder plugin;

	public File localSkinStorage;
	JsonObject skinIndex;

	public SkinManager(Murder plugin) {
		this.plugin = plugin;
		localSkinStorage = new File(plugin.getDataFolder(), "skins");

		plugin.getLogger().info("Downloading skin index...");
		try {
			skinIndex = new JsonParser().parse(new java.io.InputStreamReader(new URL("https://raw.githubusercontent.com/InventivetalentDev/Murder/master/resources/mineskin-index.json").openStream())).getAsJsonObject();
		} catch (IOException e) {
			throw new RuntimeException("Failed to download skin index", e);
		}

		try {
			load(plugin.nameManager.colors);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load skin data", e);
		}
	}

	public void setSkin(OfflinePlayer player, String skinId) {
		if (skinId == null || skinId.isEmpty()) {
			plugin.getNickManager().removeSkin(player.getUniqueId());
		} else {
			plugin.getNickManager().setCustomSkin(player.getUniqueId(), skinId);
		}
	}

	public void load(Iterable<String> colorCodes) throws ParseException, IOException {
		if (!localSkinStorage.exists()) {
			localSkinStorage.mkdirs();
			plugin.getLogger().info("Local Skin-Storage doesn't exist. Downloading original skins...");
			for (final String color : colorCodes) {
				load(skinIndex, color.substring(1, 2));
				load(skinIndex, color.substring(1, 2) + "b");
			}
		} else {
			for (String color : colorCodes) {
				loadLocal(color.substring(1, 2));
				loadLocal(color.substring(1, 2) + "b");
			}
		}
	}

	void loadLocal(String name) throws IOException, ParseException {
		File localFile = new File(localSkinStorage, name + ".skin");
		if (!localFile.exists()) { load(skinIndex, name); } else {
			BufferedReader reader = new BufferedReader(new FileReader(localFile));
			String string = "";
			String line = null;
			while ((line = reader.readLine()) != null) {
				string += line;
			}
			reader.close();
			plugin.getNickManager().loadCustomSkin(name, new JsonParser().parse(string).getAsJsonObject());
		}
	}

	void load(JsonObject skinIndex, final String name) throws ParseException, IOException {
		if (!skinIndex.has(name)) {
			plugin.getLogger().warning("Could not find '" + name + "' in skin index");
			return;
		}
		final int mineskinId = skinIndex.get(name).getAsInt();

		String string = "";
		try {
			string = CharStreams.toString(new InputSupplier<InputStreamReader>() {
				@Override
				public InputStreamReader getInput() throws IOException {
					URLConnection connection = new URL(String.format("http://api.mineskin.org/get/id/%s", mineskinId)).openConnection();
					connection.addRequestProperty("User-Agent", "Murder/" + plugin.getDescription().getVersion());
					return new InputStreamReader(connection.getInputStream(), Charsets.UTF_8);
				}
			});
			JsonObject rawJson = new JsonParser().parse(string).getAsJsonObject();

			// Parse
			JsonObject json = new JsonObject();
			json.addProperty("name", rawJson.get("name").getAsString());
			json.addProperty("id", rawJson.get("data").getAsJsonObject().get("uuid").getAsString());

			JsonObject property = new JsonObject();
			property.addProperty("name", "textures");
			property.addProperty("value", rawJson.get("data").getAsJsonObject().get("texture").getAsJsonObject().get("value").getAsString());
			property.addProperty("signature", rawJson.get("data").getAsJsonObject().get("texture").getAsJsonObject().get("signature").getAsString());

			JsonArray properties = new JsonArray();
			properties.add(property);
			json.add("properties", properties);

			// save
			plugin.getLogger().info("Loaded skin data for " + name);
			plugin.getNickManager().loadCustomSkin(name, json);
			File localFile = new File(localSkinStorage, name + ".skin");
			localFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(localFile));
			writer.write(json.toString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			throw new RuntimeException("Failed to download skin '" + name + "'. Received String: '" + string + "'", e);
		}
	}

}
