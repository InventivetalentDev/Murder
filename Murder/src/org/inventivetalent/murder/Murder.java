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

package org.inventivetalent.murder;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.inventivetalent.apihelper.APIManager;
import org.inventivetalent.bossbar.BossBarAPI;
import org.inventivetalent.murder.arena.ArenaManager;
import org.inventivetalent.murder.arena.editor.ArenaEditorManager;
import org.inventivetalent.murder.command.ArenaCommands;
import org.inventivetalent.murder.command.GameCommands;
import org.inventivetalent.murder.command.PlayerCommands;
import org.inventivetalent.murder.game.GameManager;
import org.inventivetalent.murder.item.ItemManager;
import org.inventivetalent.murder.listener.DataListener;
import org.inventivetalent.murder.listener.EditorListener;
import org.inventivetalent.murder.name.NameManager;
import org.inventivetalent.murder.player.PlayerManager;
import org.inventivetalent.murder.skin.SkinManager;
import org.inventivetalent.murder.task.ArenaOutlineTask;
import org.inventivetalent.nicknamer.api.INickNamer;
import org.inventivetalent.nicknamer.api.NickManager;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.playerversion.PlayerVersion;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.config.ConfigValue;
import org.inventivetalent.title.TitleAPI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;

public class Murder extends JavaPlugin {

	public static Murder instance;

	public ArenaManager       arenaManager;
	public GameManager        gameManager;
	public NameManager        nameManager;
	public SkinManager        skinManager;
	public PlayerManager      playerManager;
	public ItemManager        itemManager;
	public ArenaEditorManager arenaEditorManager;

	public EditorListener editorListener;
	public DataListener   dataListener;

	public ArenaCommands  arenaCommands;
	public GameCommands   gameCommands;
	public PlayerCommands playerCommands;

	public ArenaOutlineTask arenaOutlineTask;

	public File arenaFile = new File(getDataFolder(), "arenas.json");
	boolean firstStart = !arenaFile.exists();

	@ConfigValue(path = "debug") public boolean debug;

	@ConfigValue(path = "countdown.lobby") public int lobbyTime;
	@ConfigValue(path = "countdown.start") public int startTime;

	@ConfigValue(path = "loot.delay") public    int lootDelay;
	@ConfigValue(path = "loot.interval") public int lootInterval;

	@ConfigValue(path = "end.delay") public int endDelay;

	@ConfigValue(path = "resourcepack.game.url") public   String gamePackUrl;
	@ConfigValue(path = "resourcepack.game.hash") public  String gamePackHash;
	@ConfigValue(path = "resourcepack.reset.url") public  String resetPackUrl;
	@ConfigValue(path = "resourcepack.reset.hash") public String resetPackHash;

	@ConfigValue(path = "players.min") public int minPlayers;

	@Override
	public void onLoad() {
		APIManager.require(PacketListenerAPI.class, this);
		APIManager.require(BossBarAPI.class, this);
		APIManager.require(PlayerVersion.class, this);
		APIManager.require(TitleAPI.class, this);
	}

	@Override
	public void onEnable() {
		//		if (!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
		//			getLogger().severe("****************************************");
		//			getLogger().severe(" ");
		//			getLogger().severe("         Please install WorldEdit        ");
		//			getLogger().severe("http://dev.bukkit.org/bukkit-plugins/worldedit");
		//			getLogger().severe(" ");
		//			getLogger().severe("****************************************");
		//			Bukkit.getPluginManager().disablePlugin(this);
		//			return;
		//		} else {
		//			getLogger().info("Found WorldEdit");
		//		}
		if (!Bukkit.getPluginManager().isPluginEnabled("NickNamer")) {
			getLogger().severe("****************************************");
			getLogger().severe(" ");
			getLogger().severe("    This plugin depends on NickNamer    ");
			getLogger().severe("        https://r.spiget.org/5341        ");
			getLogger().severe(" ");
			getLogger().severe("****************************************");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		} else {
			getLogger().info("Found NickNamer");
		}
		if (!Bukkit.getPluginManager().isPluginEnabled("ResourcePackApi") || !Bukkit.getPluginManager().isPluginEnabled("NPCLib")) {
			getLogger().warning("**************************************");
			getLogger().warning(" ");
			getLogger().warning("     It is recommended to install     ");
			getLogger().warning("       ResourcePackApi & NPCLib       ");
			getLogger().warning("       https://r.spiget.org/2397      ");
			getLogger().warning("       https://r.spiget.org/5853      ");
			getLogger().warning(" ");
			getLogger().warning("**************************************");
		} else {
			getLogger().info("Found ResourcePackApi & NPCLib");
		}
		instance = this;

		APIManager.initAPI(PacketListenerAPI.class);
		APIManager.initAPI(BossBarAPI.class);
		APIManager.initAPI(PlayerVersion.class);
		APIManager.initAPI(TitleAPI.class);

		saveDefaultConfig();
		PluginAnnotations.CONFIG.loadValues(this, this);
		if (debug) {
			getLogger().setLevel(Level.ALL);
			getLogger().info("Debug enabled.");
		}

		arenaManager = new ArenaManager(this);
		gameManager = new GameManager(this);
		nameManager = new NameManager(this);
		skinManager = new SkinManager(this);
		playerManager = new PlayerManager(this);
		itemManager = new ItemManager(this);
		arenaEditorManager = new ArenaEditorManager(this);

		Bukkit.getPluginManager().registerEvents(editorListener = new EditorListener(this), this);
		Bukkit.getPluginManager().registerEvents(dataListener = new DataListener(this), this);

		PluginAnnotations.COMMAND.registerCommands(this, arenaCommands = new ArenaCommands(this));
		PluginAnnotations.COMMAND.registerCommands(this, gameCommands = new GameCommands(this));
		PluginAnnotations.COMMAND.registerCommands(this, playerCommands = new PlayerCommands(this));

		arenaOutlineTask = new ArenaOutlineTask(this);
		arenaOutlineTask.runTaskTimer(this, 10, 10);

		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				getLogger().info("Loading data...");
				loadData();
			}
		}, 20);
	}

	@Override
	public void onDisable() {
		getLogger().info("Saving data...");
		saveData();
	}

	//	public WorldEdit getWorldEdit() {
	//		return ((WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit")).getWorldEdit();
	//	}

	public NickManager getNickManager() {
		return ((INickNamer) Bukkit.getPluginManager().getPlugin("NickNamer")).getAPI();
	}

	public void saveData() {
		writeJson(arenaManager.toJson(), arenaFile);
	}

	public void loadData() {
		try {
			arenaManager.loadJson(new JsonParser().parse(new FileReader(arenaFile)).getAsJsonArray());
		} catch (Exception e) {
			if (!firstStart) {
				throw new RuntimeException(e);
			}
		}
	}

	public void writeJson(JsonElement jsonElement, File file) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement));
			writer.flush();
			writer.close();
		} catch (Exception e) {
			throw new RuntimeException("Failed to write json", e);
		}
	}

	public Vector minVector(Vector a, Vector b) {
		return new Vector(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
	}

	public Vector maxVector(Vector a, Vector b) {
		return new Vector(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
	}

}
