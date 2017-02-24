package org.inventivetalent.murder.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.murder.Murder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StoredData extends BasicPlayerData {

	public ItemStack[] storedInventory;
	public ItemStack[] storedExtra;//TODO: figure out what this is for
	public ItemStack[] storedArmor;
	public Location    storedLocation;
	public GameMode    storedGameMode;
	public float       storedExp;
	public int         storedLevel;
	public int         storedFireTicks;
	public double      storedMaxHealth;
	public double      storedHealth;
	public int         storedFood;
	public float       storedSaturation;
	public float       storedExhaustion;
	public float       storedFlySpeed;
	public float       storedWalkSpeed;
	public boolean     storedAllowFlight;
	public boolean     storedFlying;

	public boolean stored = false;

	public StoredData(UUID uuid) {
		super(uuid);
	}

	public void storeData(boolean clear) {
		if (stored) { throw new IllegalStateException("already stored"); }

		Player player = getPlayer();
		storedInventory = player.getInventory().getContents();
		//		storedExtra = player.getInventory().getExtraContents();
		storedArmor = player.getInventory().getArmorContents();
		storedLocation = player.getLocation();
		storedGameMode = player.getGameMode();
		storedExp = player.getExp();
		storedLevel = player.getLevel();
		storedFireTicks = player.getFireTicks();
		storedMaxHealth = player.getMaxHealth();
		storedHealth = player.getHealth();
		storedFood = player.getFoodLevel();
		storedSaturation = player.getSaturation();
		storedExhaustion = player.getExhaustion();
		storedFlySpeed = player.getFlySpeed();
		storedWalkSpeed = player.getWalkSpeed();
		storedAllowFlight = player.getAllowFlight();
		storedFlying = player.isFlying();

		if (clear) {
			player.getInventory().clear();
			player.setExp(0);
			player.setLevel(0);
			player.setFireTicks(0);
			player.setMaxHealth(20);
			player.setHealth(20);
			player.setFoodLevel(20);
			player.setFlying(false);
		}

		stored = true;
	}

	public void storeData() {
		storeData(false);
	}

	public void restoreData() {
		if (!stored) { throw new IllegalStateException("not stored yet"); }

		Player player = getPlayer();
		player.getInventory().setContents(storedInventory);
		//		player.getInventory().setExtraContents(storedExtra);
		player.getInventory().setArmorContents(storedArmor);
		player.teleport(storedLocation);
		player.setGameMode(storedGameMode);
		player.setExp(storedExp);
		player.setLevel(storedLevel);
		player.setFireTicks(storedFireTicks);
		player.setMaxHealth(storedMaxHealth);
		player.setHealth(storedHealth);
		player.setFoodLevel(storedFood);
		player.setSaturation(storedSaturation);
		player.setExhaustion(storedExhaustion);
		player.setFlySpeed(storedFlySpeed);
		player.setWalkSpeed(storedWalkSpeed);
		player.setAllowFlight(storedAllowFlight);
		player.setFlying(storedFlying);

		storedInventory = null;
		storedExtra = null;
		storedArmor = null;
		storedLocation = null;
		storedGameMode = null;
		storedExp = 0;
		storedLevel = 0;
		storedFireTicks = 0;
		storedMaxHealth = 0;
		storedHealth = 0;
		storedFood = 0;
		storedSaturation = 0;
		storedExhaustion = 0;
		storedFlySpeed = 0;
		storedWalkSpeed = 0;
		storedAllowFlight = false;
		storedFlying = false;

		stored = false;

		Murder.instance.playerManager.deleteDataFile(uuid);
	}

	public void saveToFile(File file) {
		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

		configuration.set("inventory", new ArrayList<ItemStack>(Arrays.asList(storedInventory)));
		//		configuration.set("extra", new ArrayList<>(Arrays.asList(storedExtra)));
		configuration.set("armor", new ArrayList<ItemStack>(Arrays.asList(storedArmor)));
		configuration.set("world", storedLocation != null ? storedLocation.getWorld().getName() : null);
		configuration.set("location", storedLocation.toVector());
		configuration.set("gamemode", storedGameMode.name());
		configuration.set("exp", storedExp);
		configuration.set("level", storedLevel);
		configuration.set("fireticks", storedFireTicks);
		configuration.set("maxhealth", storedMaxHealth);
		configuration.set("health", storedHealth);
		configuration.set("food", storedFood);
		configuration.set("saturation", storedSaturation);
		configuration.set("exhaustion", storedExhaustion);
		configuration.set("flyspeed", storedFlySpeed);
		configuration.set("walkspeed", storedWalkSpeed);
		configuration.set("allowflight", storedAllowFlight);
		configuration.set("flying", storedFlying);

		try {
			configuration.save(file);
		} catch (Exception e) {
			throw new RuntimeException("Failed to save data for " + uuid);
		}
	}

	public void loadFromFile(File file) {
		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

		storedInventory = ((List<ItemStack>) configuration.getList("inventory")).toArray(new ItemStack[0]);
		//		storedExtra = (ItemStack[]) configuration.getList("extra", new ArrayList<ItemStack>()).toArray();
		storedArmor = ((List<ItemStack>) configuration.getList("armor")).toArray(new ItemStack[0]);
		storedLocation = configuration.getVector("location").toLocation(Bukkit.getWorld(configuration.getString("world")));
		storedGameMode = GameMode.valueOf(configuration.getString("gamemode"));
		storedExp = (float) configuration.getDouble("exp");
		storedLevel = configuration.getInt("level");
		storedFireTicks = configuration.getInt("fireticks");
		storedMaxHealth = configuration.getDouble("maxhealth");
		storedHealth = configuration.getDouble("health");
		storedFood = configuration.getInt("food");
		storedSaturation = (float) configuration.getDouble("saturation");
		storedExhaustion = (float) configuration.getDouble("exhaustion");
		storedFlySpeed = (float) configuration.getDouble("flyspeed");
		storedWalkSpeed = (float) configuration.getDouble("walkspeed");
		storedAllowFlight = configuration.getBoolean("allowflight");
		storedFlying = configuration.getBoolean("flying");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		if (!super.equals(o)) { return false; }

		StoredData that = (StoredData) o;

		if (Float.compare(that.storedExp, storedExp) != 0) { return false; }
		if (storedLevel != that.storedLevel) { return false; }
		if (storedFireTicks != that.storedFireTicks) { return false; }
		if (Double.compare(that.storedMaxHealth, storedMaxHealth) != 0) { return false; }
		if (Double.compare(that.storedHealth, storedHealth) != 0) { return false; }
		if (storedFood != that.storedFood) { return false; }
		if (Float.compare(that.storedSaturation, storedSaturation) != 0) { return false; }
		if (Float.compare(that.storedExhaustion, storedExhaustion) != 0) { return false; }
		if (Float.compare(that.storedFlySpeed, storedFlySpeed) != 0) { return false; }
		if (Float.compare(that.storedWalkSpeed, storedWalkSpeed) != 0) { return false; }
		if (storedAllowFlight != that.storedAllowFlight) { return false; }
		if (storedFlying != that.storedFlying) { return false; }
		if (stored != that.stored) { return false; }
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if (!Arrays.equals(storedInventory, that.storedInventory)) { return false; }
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if (!Arrays.equals(storedExtra, that.storedExtra)) { return false; }
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if (!Arrays.equals(storedArmor, that.storedArmor)) { return false; }
		if (storedLocation != null ? !storedLocation.equals(that.storedLocation) : that.storedLocation != null) { return false; }
		return storedGameMode == that.storedGameMode;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		long temp;
		result = 31 * result + Arrays.hashCode(storedInventory);
		result = 31 * result + Arrays.hashCode(storedExtra);
		result = 31 * result + Arrays.hashCode(storedArmor);
		result = 31 * result + (storedLocation != null ? storedLocation.hashCode() : 0);
		result = 31 * result + (storedGameMode != null ? storedGameMode.hashCode() : 0);
		result = 31 * result + (storedExp != +0.0f ? Float.floatToIntBits(storedExp) : 0);
		result = 31 * result + storedLevel;
		result = 31 * result + storedFireTicks;
		temp = Double.doubleToLongBits(storedMaxHealth);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(storedHealth);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + storedFood;
		result = 31 * result + (storedSaturation != +0.0f ? Float.floatToIntBits(storedSaturation) : 0);
		result = 31 * result + (storedExhaustion != +0.0f ? Float.floatToIntBits(storedExhaustion) : 0);
		result = 31 * result + (storedFlySpeed != +0.0f ? Float.floatToIntBits(storedFlySpeed) : 0);
		result = 31 * result + (storedWalkSpeed != +0.0f ? Float.floatToIntBits(storedWalkSpeed) : 0);
		result = 31 * result + (storedAllowFlight ? 1 : 0);
		result = 31 * result + (storedFlying ? 1 : 0);
		result = 31 * result + (stored ? 1 : 0);
		return result;
	}
}