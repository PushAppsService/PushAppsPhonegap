package com.groboot.pushapps;

import java.util.Calendar;
import java.util.Random;

import android.content.Context;
import android.content.Intent;

class PushAppsHelper {
	static void handleRegistration(String registrationId, Context paramContext) {
		SharedData sharedData = SharedData.getInstance(paramContext);
		String appToken = sharedData.getPrefString(Constants.APP_TOKEN, "");
		PostSender.register(registrationId, appToken, paramContext);
		sharedData.setPrefString("PushToken", registrationId);
		// checkAndHandleLoc(null, paramContext, true);
		PushAppsRegistrationInterface regInterface = PushManager.getInstance(paramContext).getRegistrationInterface();
		if (regInterface != null) {
			regInterface.onRegistered(paramContext, registrationId);
		}
	}

	static void handleUnregistration(Context paramContext) {
		GCMRegistrar.unregister(paramContext);
		SharedData sharedData = SharedData.getInstance(paramContext);
		sharedData.setPrefBoolean(Constants.PUSH_ENABLED, false);
		sharedData.setPrefBoolean(RegistrationChecker.NEED_TO_REGISTER_AGAIN, true);
		String appToken = sharedData.getPrefString(Constants.APP_TOKEN, "");
		PostSender.unregister(paramContext, appToken);
		PushAppsRegistrationInterface regInterface = PushManager.getInstance(paramContext).getRegistrationInterface();
		if (regInterface != null) {
			regInterface.onUnregistered(paramContext, "");
		}
	}

	static void handleVersionChanges(Context context, String appToken) {
		try {
			int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			SharedData sharedData = SharedData.getInstance();
			int lastVersionCode = SharedData.getInstance().getPrefInt("ver_code", -1);
			if (lastVersionCode == -1) {
				PostSender.sendTags(null, appToken, context, new Tag("App_First_Install", Calendar.getInstance().getTime()));
				sharedData.setPrefInt("ver_code", versionCode);
			} else if (lastVersionCode != versionCode) {
				PostSender.sendTags(null, appToken, context, new Tag("App_Version_Update", Calendar.getInstance().getTime()));
				sharedData.setPrefInt("ver_code", versionCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void handleNotificationRead(String notifId, Context paramContext, Intent intent) {
		SharedData sharedData = SharedData.getInstance(paramContext);
		String appToken = sharedData.getPrefString(Constants.APP_TOKEN, "");
		PostSender.setNotificationRead(notifId, appToken, PushAppsUserManager.getDeviceId(paramContext));
	}

	static void handleOnMessage(Intent intent, Context context) {
		PushAppsMessageInterface mesInterface = PushManager.getInstance(context).getMessageInterface();
		if (mesInterface != null) {
			mesInterface.onMessage(context, intent);
		} else {
			if (!PushManager.getInstance(context).shouldStackNotifications()) {
				PushManager.buildNotification(intent.getExtras(), context, Constants.NOTIFICATION_ID);
			} else {
				PushManager.buildNotification(intent.getExtras(), context, (new Random()).nextInt(290) + 1);
			}
		}
	}

	@Deprecated
	static void reportEvent(String event, Context context, String appToken) {
		PostSender.sendEvent(event, context, appToken);
	}

}
