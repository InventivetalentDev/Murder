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

package org.inventivetalent.murder.item;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.itembuilder.ItemBuilder;
import org.inventivetalent.murder.Murder;

public class ItemManager {

	private Murder               plugin;
	public  ConfigurationSection itemSection;

	public ItemManager(Murder plugin) {
		this.plugin = plugin;
		itemSection = plugin.getConfig().getConfigurationSection("items");
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
