package com.groboot.pushapps;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

class PushAppsUserManager {

	/**
	 * 
	 * @param context
	 * @return registered push token for current device
	 */
	static String getDevicePushToken(Context context) {
		SharedData sharedData = SharedData.getInstance(context);
		return sharedData.getPrefString("PushToken", "");
	}

	/**
	 * 
	 * @param context
	 * @return device id used for PushApps
	 */
	static String getDeviceId(Context context) {
		int deviceIdType = SharedData.getInstance(context).getPrefInt(
				Constants.ID_TYPE, DeviceIDTypes.IMEI);
		if (deviceIdType != DeviceIDTypes.ANDROID_ID) {
			try {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				String deviceId = tm.getDeviceId();
				if (deviceId != null && deviceId.length() != 0) {
					SharedData.getInstance(context).setPrefString("DEVICE_ID",
							deviceId);
					return deviceId;
				}
			} catch (Exception e) {
			}
		}
		try {
			String androidId = Settings.Secure.getString(
					context.getContentResolver(), Settings.Secure.ANDROID_ID);
			if (androidId != null) {
				SharedData.getInstance(context).setPrefString("DEVICE_ID",
						androidId);
				return androidId;
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}

	static int getSDKType() {
		try {
			Class.forName( "org.apache.cordova.CordovaActivity" );
			return Constants.PHONE_GAP;
		} catch (ClassNotFoundException e) {
			// Do nothing
		}
		
		return Constants.NATIVE;
	}

	/**
	 * 
	 * @return device OSVersion
	 */
	static int getOSVersion() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 
	 * @return device type used for PushApps
	 */
	static String getDeviceType() {
		return Build.MANUFACTURER + " " + Build.DEVICE;
	}

	/**
	 * 
	 * @return PushApps sdk version
	 */
	static String getSdkVersion() {
		return Constants.VERSION;
	}

	/**
	 * 
	 * @param context
	 * @return current application version
	 */
	static String getAppVersion(Context context) {
		PackageInfo pInfo;
		try {
			pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	static String getRegistrationToken() {
		try {
			return SharedData.getInstance().getPrefString("PushToken", "");
		} catch (Exception e) {
			return "";
		}
	}

}
