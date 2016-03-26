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

import org.inventivetalent.bossbar.BossBarAPI;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.message.MessageLoader;

public enum Role {

	/* Default Bystander */
	DEFAULT("default.name", "default.title", "default.subtitle", "default.objective", "default.bossbar.text", "default.bossbar.color", "default.bossbar.style"),
	MURDERER("murderer.name", "murderer.title", "murderer.subtitle", "murderer.objective", "murderer.bossbar.text", "murderer.bossbar.color", "murderer.bossbar.style"),
	WEAPON("weapon.name", "weapon.title", "weapon.subtitle", "weapon.objective", "weapon.bossbar.text", "weapon.bossbar.color", "weapon.bossbar.style")
	/*,SPECTATOR("spectator.name", null, null, null, null, null, null)*/;

	private String nameKey;
	private String titleKey;
	private String subTitleKey;
	private String objectiveKey;
	private String barTextKey;
	private String barColorKey;
	private String barStyleKey;

	private static final MessageLoader MESSAGE_LOADER = PluginAnnotations.MESSAGE.newMessageLoader(Murder.instance, "config.yml", "messages.role", null);

	Role(String nameKey, String titleKey, String subTitleKey, String objectiveKey, String barTextKey, String barColorKey, String barStyleKey) {
		this.nameKey = nameKey;
		this.titleKey = titleKey;
		this.subTitleKey = subTitleKey;
		this.objectiveKey = objectiveKey;
		this.barTextKey = barTextKey;
		this.barColorKey = barColorKey;
		this.barStyleKey = barStyleKey;
	}

	public String getName() {
		return MESSAGE_LOADER.getMessage(nameKey, nameKey);
	}

	public String getTitle() {
		return MESSAGE_LOADER.getMessage(titleKey, titleKey);
	}

	public String getSubTitle() {
		return MESSAGE_LOADER.getMessage(subTitleKey, subTitleKey);
	}

	public String getObjective() {
		return MESSAGE_LOADER.getMessage(objectiveKey, objectiveKey);
	}

	public String getBarText() {
		return MESSAGE_LOADER.getMessage(barTextKey, barTextKey);
	}

	public BossBarAPI.Color getBarColor() {
		String value = MESSAGE_LOADER.getMessage(barColorKey, null);
		if (value == null) { return BossBarAPI.Color.WHITE; }
		return BossBarAPI.Color.valueOf(value.toUpperCase());
	}

	public BossBarAPI.Style getBarStyle() {
		String value = MESSAGE_LOADER.getMessage(barStyleKey, null);
		if (value == null) { return BossBarAPI.Style.PROGRESS; }
		return BossBarAPI.Style.valueOf(value.toUpperCase());
	}

}
