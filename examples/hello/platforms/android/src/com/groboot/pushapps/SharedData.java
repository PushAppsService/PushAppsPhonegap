package com.groboot.pushapps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedData {

	static final String PREFS_NAME = "pushappsdata";

	private SharedPreferences appSharedPrefs;
	private Editor prefsEditor;
	private static SharedData instance;

	private SharedData(Context context) {
		this.appSharedPrefs = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
		this.prefsEditor = appSharedPrefs.edit();
	}

	static SharedData getInstance(Context context) {
		if (instance == null) {
			instance = new SharedData(context);
		}
		return instance;
	}

	static SharedData getInstance() {
		return instance;
	}

	int getPrefInt(String key, int defaultVal) {
		return appSharedPrefs.getInt(key, defaultVal);
	}

	void setPrefInt(String key, int value) {
		prefsEditor.putInt(key, value);
		prefsEditor.commit();
	}

	String getPrefString(String key, String defaultVal) {
		return appSharedPrefs.getString(key, defaultVal);
	}

	boolean getPrefBoolean(String key, boolean defaultVal) {
		return appSharedPrefs.getBoolean(key, defaultVal);
	}

	void setPrefString(String key, String value) {
		prefsEditor.putString(key, value);
		prefsEditor.commit();
	}

	void setPrefBoolean(String key, boolean value) {
		prefsEditor.putBoolean(key, value);
		prefsEditor.commit();
	}

	long getPrefLong(String key, Long defaultValue) {
		return appSharedPrefs.getLong(key, defaultValue);
	}

	void setPrefLong(String key, long value) {
		prefsEditor.putLong(key, value);
		prefsEditor.commit();
	}

}
