package com.groboot.pushapps;

import android.util.Log;

class Logger {
	public static final boolean DEBUG = false;

	public static void log(String msg) {
		if (DEBUG) {
			Log.i("PushAppsSDK", msg);
		}
	}
}
