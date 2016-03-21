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

package org.inventivetalent.murder.skin;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.inventivetalent.murder.Murder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;

public class SkinManager {

	private Murder plugin;

	public File localSkinStorage;

	public SkinManager(Murder plugin) {
		this.plugin = plugin;
		localSkinStorage = new File(plugin.getDataFolder(), "skins");

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
				load(color.substring(1, 2));
				load(color.substring(1, 2) + "b");
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
		if (!localFile.exists()) { load(name); } else {
			BufferedReader reader = new BufferedReader(new FileReader(localFile));
			String string = "";
			String line = null;
			while ((line = reader.readLine()) != null) {
				string += line;
			}
			reader.close();
			plugin.getNickManager().loadCustomSkin(name, (JSONObject) (new JSONParser().parse(string)));
		}
	}

	void load(final String name) throws ParseException, IOException {
		String string = "";
		try {
			string = CharStreams.toString(new InputSupplier<InputStreamReader>() {
				@Override
				public InputStreamReader getInput() throws IOException {
					return new InputStreamReader(new URL(String.format("http://api.inventivetalent.org/skin/murder/?s=%s", name)).openConnection().getInputStream(), Charsets.UTF_8);
				}
			});
			JSONObject json = (JSONObject) new JSONParser().parse(string);
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
