package com.groboot.pushapps;


class RegistrationChecker {

	static boolean needToSendData(String utcoffset, String regKey,
			String deviceId, int sdkInt, String manufacturer, String token,
			String sdkVersion, String appVersion) {
		boolean result = true;
/*		SharedData sharedData = SharedData.getInstance();
		result = sharedData.getPrefBoolean(NEED_TO_REGISTER_AGAIN, true);
		if (!sharedData.getPrefString(UTC_OFFSET, "").equals(utcoffset)) {
			result = true;
			sharedData.setPrefString(UTC_OFFSET, utcoffset);
		}
		if (!sharedData.getPrefString(CURRENT_REGISTRATION_KEY, "").equals(
				regKey)) {
			result = true;
			sharedData.setPrefString(CURRENT_REGISTRATION_KEY, regKey);
		}
		if (!sharedData.getPrefString(DEVICE_ID, "").equals(deviceId)) {
			result = true;
			sharedData.setPrefString(DEVICE_ID, deviceId);
		}
		if (sharedData.getPrefInt(SDK_INT, 0) != sdkInt) {
			result = true;
			sharedData.setPrefString(DEVICE_ID, deviceId);
		}
		if (!sharedData.getPrefString(MANUFACTURER, "").equals(manufacturer)) {
			result = true;
			sharedData.setPrefString(MANUFACTURER, manufacturer);
		}
		if (!sharedData.getPrefString(APP_TOKEN, "").equals(token)) {
			result = true;
			sharedData.setPrefString(APP_TOKEN, token);
		}
		if (!sharedData.getPrefString(SDK_VERSION, "").equals(sdkVersion)) {
			result = true;
			sharedData.setPrefString(SDK_VERSION, sdkVersion);
		}
		if (!sharedData.getPrefString(APP_VERSION, "").equals(appVersion)) {
			result = true;
			sharedData.setPrefString(APP_VERSION, sdkVersion);
		}
*/		return result;
	}

	final static String UTC_OFFSET = "current_utc";
	final static String CURRENT_REGISTRATION_KEY = "current_reg_key";
	final static String DEVICE_ID = "current_device_id";
	final static String SDK_INT = "current_os_version";
	final static String MANUFACTURER = "MANUFACTURER";
	final static String APP_TOKEN = "APP_TOKEN";
	final static String SDK_VERSION = "current_push_sdk_version";
	final static String APP_VERSION = "APP_VERSION";
	final static String NEED_TO_REGISTER_AGAIN = "need_to_register_again";
}
