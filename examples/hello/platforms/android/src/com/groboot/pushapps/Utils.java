package com.groboot.pushapps;

import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.content.pm.PackageManager;

class Utils {
	static String getUtcOffset() {
		TimeZone tz = TimeZone.getDefault();
		Date now = new Date();
		int offsetFromUtc = tz.getOffset(now.getTime()) / 1000 / 60;
		return "" + offsetFromUtc;
	}

/*	*//**
	 * 
	 * @param context
	 * @return device id used for PushApps
	 *//*
	static String getDeviceId(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = tm.getDeviceId();
			if (deviceId != null && deviceId.length() != 0) {
				SharedData.getInstance(context).setPrefString("DEVICE_ID", deviceId);
				return deviceId;
			}
		} catch (Exception e) {
		}
		try {
			String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			if (androidId != null) {
				SharedData.getInstance(context).setPrefString("DEVICE_ID", androidId);
				return androidId;
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}
*/
	static boolean checkForLocationPermission(Context context) {
		int hasFineLocation = context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
		int hasCoarseLocation = context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION");
		return (hasFineLocation == PackageManager.PERMISSION_GRANTED || hasCoarseLocation == PackageManager.PERMISSION_GRANTED);
	}

}
