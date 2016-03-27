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

package org.inventivetalent.murder.packet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.murder.Murder;
import org.inventivetalent.murder.player.PlayerData;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;
import org.inventivetalent.reflection.minecraft.Minecraft;
import org.inventivetalent.reflection.resolver.MethodResolver;
import org.inventivetalent.reflection.resolver.ResolverQuery;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;
import org.inventivetalent.reflection.resolver.minecraft.OBCClassResolver;

import java.util.logging.Level;

public class PacketListener {

	static NMSClassResolver nmsClassResolver = new NMSClassResolver();
	static OBCClassResolver obcClassResolver = new OBCClassResolver();

	static Class<?> ItemStack = nmsClassResolver.resolveSilent("ItemStack");

	static Class<?> CraftItemStack = obcClassResolver.resolveSilent("inventory.CraftItemStack");

	static MethodResolver CraftItemStackMethodResolver = new MethodResolver(CraftItemStack);

	static final ItemStack AIR_ITEM = new ItemStack(Material.AIR);
	static Object NMS_AIR_ITEM;

	private Murder        plugin;
	public  PacketHandler packetHandler;

	public PacketListener(final Murder plugin) {
		this.plugin = plugin;
		PacketHandler.addHandler(packetHandler = new PacketHandler(plugin) {
					@Override
					public void onSend(SentPacket sentPacket) {
						if (sentPacket.hasPlayer()) {
							Player player = sentPacket.getPlayer();
							if ("PacketPlayOutEntityEquipment".equals(sentPacket.getPacketName())) {
								PlayerData playerData = plugin.playerManager.getData(player.getUniqueId());
								if (playerData != null && playerData.isInGame()) {
									int slot = -1;
									if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
										slot = (int) sentPacket.getPacketValue("b");
									} else {
										slot = ((Enum) sentPacket.getPacketValue("b")).ordinal();
									}
									if (slot == 0/*main hand*/ || (Minecraft.VERSION.newerThan(Minecraft.Version.v1_9_R1) && slot == 1/*offhand*/)) {
										try {
											ItemStack heldItem = (ItemStack) CraftItemStackMethodResolver.resolve(new ResolverQuery("asBukkitCopy", ItemStack)).invoke(null, sentPacket.getPacketValue("c"));
											if (heldItem == null || heldItem.getType() == Material.AIR) { return; }
											if (!plugin.itemManager.getKnife().equals(heldItem) && !plugin.itemManager.getGun().equals(heldItem)) {
												if (NMS_AIR_ITEM == null) {
													NMS_AIR_ITEM = CraftItemStackMethodResolver.resolveSilent(new ResolverQuery("asNMSCopy", org.bukkit.inventory.ItemStack.class)).invoke(null, AIR_ITEM);
												}
												sentPacket.setPacketValue("c", NMS_AIR_ITEM);
											}
										} catch (ReflectiveOperationException e) {
											plugin.getLogger().log(Level.SEVERE, "Exception while hiding items", e);
										}
									}
								}
							}
						}
					}

					@Override
					public void onReceive(ReceivedPacket receivedPacket) {
						if (receivedPacket.hasPlayer()) {
							if ("PacketPlayInBlockDig".equals(receivedPacket.getPacketName())) {
								if (Minecraft.VERSION.newerThan(Minecraft.Version.v1_9_R1)) {
									PlayerData playerData = plugin.playerManager.getData(receivedPacket.getPlayer().getUniqueId());
									if (playerData != null && playerData.isInGame()) {
										if (((Enum) receivedPacket.getPacketValue("c")).ordinal() == 6) {
											//Cancel hand-swap
											receivedPacket.setCancelled(true);
										}
									}
								}
							}
						}
					}
				}

		);
	}

	public void disable() {
		PacketHandler.removeHandler(packetHandler);
	}

}
