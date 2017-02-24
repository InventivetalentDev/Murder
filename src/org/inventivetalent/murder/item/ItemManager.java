package org.inventivetalent.murder.item;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.inventivetalent.itembuilder.ItemBuilder;
import org.inventivetalent.murder.Murder;

public class ItemManager {

	private Murder               plugin;
	public  ConfigurationSection itemSection;

	public ItemManager(Murder plugin) {
		this.plugin = plugin;
		itemSection = plugin.getConfig().getConfigurationSection("items");
	}

	public boolean lazyEqual(ItemStack a, ItemStack b) {
		if (a == null || b == null) { return false; }
		if (a.getType() != b.getType()) { return false; }
		ItemMeta aMeta = a.getItemMeta();
		ItemMeta bMeta = b.getItemMeta();
		if (!aMeta.getDisplayName().equals(bMeta.getDisplayName())) { return false; }
		return true;
	}

	//Game Items

	public ItemStack getKnife() {
		return new ItemBuilder(Material.DIAMOND_AXE).fromConfig(itemSection.getConfigurationSection("knife")).build();
	}

	public ItemStack getGun() {
		return new ItemBuilder(Material.DIAMOND_HOE).fromConfig(itemSection.getConfigurationSection("gun")).build();
	}

	public ItemStack getBullet() {
		return new ItemBuilder(Material.ARROW).fromConfig(itemSection.getConfigurationSection("bullet")).build();
	}

	public ItemStack getLoot() {
		return new ItemBuilder(Material.DIAMOND).fromConfig(itemSection.getConfigurationSection("loot")).build();
	}

	public ItemStack getNameInfo(String name) {
		//noinspection ConstantConditions
		return new ItemBuilder(Material.NAME_TAG).fromConfig(itemSection.getConfigurationSection("nameInfo")).buildMeta().withFormat("%s", name).item().build();
	}

	public ItemStack getSpeedBoost() {
		return new ItemBuilder(Material.SUGAR).fromConfig(itemSection.getConfigurationSection("speedBoost")).build();
	}

	public ItemStack getTeleporter() {
		return new ItemBuilder(Material.COMPASS).fromConfig(itemSection.getConfigurationSection("teleporter")).build();
	}

	//Editor Items

	public ItemStack getBoundsSelector() {
		return new ItemBuilder(Material.GOLD_AXE).fromConfig(itemSection.getConfigurationSection("editor.arena.boundsSelector")).build();
	}

	public ItemStack getLobbySpawnSelector() {
		return new ItemBuilder(Material.REDSTONE_BLOCK).fromConfig(itemSection.getConfigurationSection("editor.arena.spawnSelector.lobby")).build();
	}

	public ItemStack getPlayerSpawnSelector() {
		return new ItemBuilder(Material.EMERALD_BLOCK).fromConfig(itemSection.getConfigurationSection("editor.arena.spawnSelector.player")).build();
	}

	public ItemStack getLootSpawnSelector() {
		return new ItemBuilder(Material.DIAMOND_BLOCK).fromConfig(itemSection.getConfigurationSection("editor.arena.spawnSelector.loot")).build();
	}

}
