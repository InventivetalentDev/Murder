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
