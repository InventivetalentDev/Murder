package org.inventivetalent.murder;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.inventivetalent.apihelper.APIManager;
import org.inventivetalent.entityanimation.AnimationAPI;
import org.inventivetalent.murder.arena.ArenaManager;
import org.inventivetalent.murder.arena.editor.ArenaEditorManager;
import org.inventivetalent.murder.command.*;
import org.inventivetalent.murder.corpse.CorpseManager;
import org.inventivetalent.murder.game.GameManager;
import org.inventivetalent.murder.item.ItemManager;
import org.inventivetalent.murder.listener.*;
import org.inventivetalent.murder.name.NameListener;
import org.inventivetalent.murder.name.NameManager;
import org.inventivetalent.murder.packet.PacketListener;
import org.inventivetalent.murder.player.PlayerManager;
import org.inventivetalent.murder.skin.SkinManager;
import org.inventivetalent.murder.spectate.SpectateManager;
import org.inventivetalent.murder.task.ArenaOutlineTask;
import org.inventivetalent.nicknamer.api.INickNamer;
import org.inventivetalent.nicknamer.api.NickManager;
import org.inventivetalent.npclib.NPCLib;
import org.inventivetalent.npclib.registry.NPCRegistry;
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
	public SpectateManager    spectateManager;
	public CorpseManager      corpseManager;

	public EditorListener     editorListener;
	public DataListener       dataListener;
	public GameListener       gameListener;
	public SpectatorListener  spectatorListener;
	public NameListener       nameListener;
	public ProtectionListener protectionListener;
	public CorpseListener     corpseListener;
	public SignListener       signListener;

	public PacketListener packetListener;
	public NPCRegistry npcRegistry;

	public ArenaCommands     arenaCommands;
	public GameCommands      gameCommands;
	public PlayerCommands    playerCommands;
	public ToggleCommands    toggleCommands;
	public CountdownCommands countdownCommands;

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

	@ConfigValue(path = "sign.title",
				 colorChar = '&') public             String signTitle;
	@ConfigValue(path = "sign.lines.title") public   int    signLineTitle;
	@ConfigValue(path = "sign.lines.arena") public   int    signLineArena;
	@ConfigValue(path = "sign.lines.state") public   int    signLineState;
	@ConfigValue(path = "sign.lines.players") public int    signLinePlayers;
	@ConfigValue(path = "sign.lines.leave") public   int    signLineLeave;
	@ConfigValue(path = "sign.format.players",
				 colorChar = '&') public             String signFormatPlayers;
	@ConfigValue(path = "sign.key.leave",
				 colorChar = '&') public             String signKeyLeave;

	@Override
	public void onLoad() {
		APIManager.require(PacketListenerAPI.class, this);
		APIManager.require(PlayerVersion.class, this);
		APIManager.require(TitleAPI.class, this);
		APIManager.require(AnimationAPI.class, this);
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
		if (!Bukkit.getPluginManager().isPluginEnabled("MenuBuilder")) {
			getLogger().severe("****************************************");
			getLogger().severe(" ");
			getLogger().severe("    This plugin depends on MenuBuilder    ");
			getLogger().severe("        https://r.spiget.org/12995        ");
			getLogger().severe(" ");
			getLogger().severe("****************************************");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		} else {
			getLogger().info("Found MenuBuilder");
		}
		if (!Bukkit.getPluginManager().isPluginEnabled("NPCLib")) {
			getLogger().severe("****************************************");
			getLogger().severe(" ");
			getLogger().severe("    This plugin depends on NPCLib    ");
			getLogger().severe("        https://r.spiget.org/5853        ");
			getLogger().severe(" ");
			getLogger().severe("****************************************");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		} else {
			getLogger().info("Found MenuBuilder");
		}
		instance = this;

		APIManager.initAPI(PacketListenerAPI.class);
		APIManager.initAPI(PlayerVersion.class);
		APIManager.initAPI(TitleAPI.class);
		APIManager.initAPI(AnimationAPI.class);

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
		spectateManager = new SpectateManager(this);
		corpseManager = new CorpseManager(this);

		Bukkit.getPluginManager().registerEvents(editorListener = new EditorListener(this), this);
		Bukkit.getPluginManager().registerEvents(dataListener = new DataListener(this), this);
		Bukkit.getPluginManager().registerEvents(gameListener = new GameListener(this), this);
		Bukkit.getPluginManager().registerEvents(spectatorListener = new SpectatorListener(this), this);
		Bukkit.getPluginManager().registerEvents(nameListener = new NameListener(this), this);
		if (classExists("org.bukkit.event.player.PlayerPickupArrowEvent")) { Bukkit.getPluginManager().registerEvents(new GameListenerArrow(this), this); }
		Bukkit.getPluginManager().registerEvents(protectionListener = new ProtectionListener(this), this);
		Bukkit.getPluginManager().registerEvents(corpseListener = new CorpseListener(this), this);
		Bukkit.getPluginManager().registerEvents(signListener = new SignListener(this), this);

		packetListener = new PacketListener(this);
		npcRegistry = NPCLib.createRegistry(this);

		PluginAnnotations.COMMAND.registerCommands(this, arenaCommands = new ArenaCommands(this));
		PluginAnnotations.COMMAND.registerCommands(this, gameCommands = new GameCommands(this));
		PluginAnnotations.COMMAND.registerCommands(this, playerCommands = new PlayerCommands(this));
		PluginAnnotations.COMMAND.registerCommands(this, toggleCommands = new ToggleCommands(this));
		PluginAnnotations.COMMAND.registerCommands(this, countdownCommands = new CountdownCommands(this));

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

		if (packetListener != null) {
			packetListener.disable();
		}
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

	public boolean contains(Vector min, Vector max, Vector point) {
		return ((point.getX() >= min.getX()) && (point.getX() <= max.getX())) &&//
				((point.getY() >= min.getY()) && (point.getY() <= max.getY())) &&//
				((point.getZ() >= min.getZ()) && (point.getZ() <= max.getZ()));
	}

	boolean classExists(String name) {
		try {
			return Class.forName(name) != null;
		} catch (Exception e) {
			return false;
		}
	}

}
