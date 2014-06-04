package com.groboot.pushapps;

public class PushAppsNotification {
	private String message;
	private String title;
	private int sound;
	private int icon;
	private boolean hasVibrate = false;
	private String link;

	public PushAppsNotification setLink(String link) {
		this.link = link;
		return this;
	}

	public String getLink() {
		return link;
	}

	public boolean isHasVibrate() {
		return hasVibrate;
	}

	public PushAppsNotification setHasVibrate(boolean hasVibrate) {
		this.hasVibrate = hasVibrate;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public PushAppsNotification setMessage(String message) {
		this.message = message;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public PushAppsNotification setTitle(String title) {
		this.title = title;
		return this;
	}

	public int getSound() {
		return sound;
	}

	public PushAppsNotification setSound(int sound) {
		this.sound = sound;
		return this;
	}

	public int getIcon() {
		return icon;
	}

	public PushAppsNotification setIcon(int icon) {
		this.icon = icon;
		return this;
	}

}
