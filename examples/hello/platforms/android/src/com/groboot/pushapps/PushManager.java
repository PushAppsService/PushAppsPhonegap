package com.groboot.pushapps;

import java.util.Arrays;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.groboot.pushapps.v4.NotificationCompat;

public class PushManager {

	// Public vars

	public static String NOTIFICATION_MESSAGE_KEY = "Message";
	public static String NOTIFICATION_TITLE_KEY = "Title";
	public static String NOTIFICATION_LINK_KEY = "L";
	public static String NOTIFICATION_SOUND_KEY = "S";
	public static String EXTRA_DATA = "D";

	private static PushManager mManager = null;

	private String mSenderId, mAppToken;

	Context mAppContext;

	private PushAppsRegistrationInterface mRegistrationInterface;
	private PushAppsMessageInterface mMessageInterface;

	public static void init(Context context, String googleProjectId, String appToken) {
		mManager = new PushManager(context, googleProjectId, appToken);
		boolean enabled = SharedData.getInstance(context.getApplicationContext()).getPrefBoolean("push_enabled", true);
		if (enabled) {
			mManager.register();
		}
		PushAppsHelper.handleVersionChanges(context, appToken);
		mManager.sendTag(new SendTagResponseListener() {

			@Override
			public void response(boolean success, String message) {
				// TODO Auto-generated method stub

			}
		}, new Tag("Last Activation", Calendar.getInstance().getTime()));
	}

	public static PushManager getInstance(Context context) {
		if (mManager == null) {
			mManager = new PushManager(context);
		}
		return mManager;
	};

	private PushManager(Context context) {
		this.mSenderId = SharedData.getInstance(context).getPrefString(Constants.SENDER_ID, "");
		this.mAppToken = SharedData.getInstance(context).getPrefString(Constants.APP_TOKEN, "");
		mAppContext = context.getApplicationContext();
	}

	private PushManager(Context context, String senderId, String applicationToken) {
		this.mSenderId = senderId;
		this.mAppToken = applicationToken;
		mAppContext = context.getApplicationContext();
		SharedData.getInstance(context).setPrefString(Constants.SENDER_ID, this.mSenderId);
		SharedData.getInstance(context).setPrefString(Constants.APP_TOKEN, this.mAppToken);
	}

	public void setDeviceIDType(int type) {
		SharedData.getInstance().setPrefInt(Constants.ID_TYPE, type);
	}

	/*
	 * public static void setSDKType(int sdkType) { switch (sdkType) { case
	 * Constants.NATIVE: SharedData.getInstance().setPrefInt(Constants.SDK_TYPE,
	 * sdkType); break;
	 * 
	 * case Constants.PHONE_GAP:
	 * SharedData.getInstance().setPrefInt(Constants.SDK_TYPE, sdkType); break;
	 * } }
	 */
	/**
	 * Unregister the device from getting push notifications
	 */
	public void unregister() {
		Logger.log("public static void unregister");
		PushAppsHelper.handleUnregistration(mAppContext);
	}

	/**
	 * Register the device in order to get push notifications
	 * 
	 */
	public void register() {
		Logger.log("public static void register");
		SharedData.getInstance(mAppContext).setPrefBoolean("push_enabled", true);
		GCMRegistrar.checkDevice(mAppContext);
		GCMRegistrar.checkManifest(mAppContext);
		String senderId = SharedData.getInstance(mAppContext).getPrefString(Constants.SENDER_ID, "");
		GCMRegistrar.register(mAppContext, new String[] { senderId });
	}

	/**
	 * Set the intent to launch on notification opened
	 * 
	 * @param intentName
	 *            full intent name (including package name) to launch on
	 *            notification opened
	 */
	public void setIntentNameToLaunch(String intentName) {
		SharedData.getInstance().setPrefString("IntentName", intentName);
	}

	@Deprecated
	public void reportEvent(String event) {
		PushAppsHelper.reportEvent(event, mAppContext, mAppToken);
	}

	public void sendTag(SendTagResponseListener responseListener, Tag... tags) {
		PostSender.sendTags(responseListener, mAppToken, mAppContext, tags);
	}

	public void removeTag(SendTagResponseListener responseListener, String... tagsToRemove) {
		PostSender.removeTags(responseListener, mAppToken, mAppContext, Arrays.asList(tagsToRemove));
	}

	public void setNotificationIcon(int iconResource) {
		SharedData.getInstance().setPrefInt("NotificationIcon", iconResource);
	}

	/**
	 * set if the intent fired when
	 * 
	 * @param flag
	 */
	public void setShouldStartIntentAsNewTask(boolean flag) {
		SharedData.getInstance().setPrefBoolean(Constants.NEW_TASK, flag);
	}

	/**
	 * 
	 * @param data
	 *            bundle from the onMessage() intent callback
	 * @param context
	 * @param notificationId
	 *            id for the notification
	 * @param notificationIcon
	 *            notification icon
	 * @param intent
	 *            intent to be called when the notification is clicked, if a
	 *            link was passed then this intent will be ignored
	 */
	public static void buildNotification(Bundle data, Context context, int notificationId) {
		// Take all needed parameters from notification
		if (data != null) {
			String message = data.getString(NOTIFICATION_MESSAGE_KEY);
			String title = data.getString(NOTIFICATION_TITLE_KEY);
			String sound = data.getString(NOTIFICATION_SOUND_KEY);
			String serverNotificationId = data.getString("Id");

			// Check for default title if needed
			if (title == null) {
				title = getDefaultNotificationTitle(context);
			}

			// Create the notification intent
			Intent notificationIntent = new Intent();
			notificationIntent.setClass(context, PushActivity.class);
			notificationIntent.putExtra("notificationId", serverNotificationId);
			notificationIntent.putExtras(data);
			if (!SharedData.getInstance().getPrefBoolean(Constants.NEW_TASK, false)) {
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			// Create the pending intent
			PendingIntent contentIntent = PendingIntent.getActivity(context, notificationId, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setContentIntent(contentIntent).setSmallIcon(getNotificationIcon("", context)).setTicker(title).setContentTitle(title)
					.setContentText(message).setWhen(System.currentTimeMillis()).setAutoCancel(true)
					.setDefaults(Notification.DEFAULT_VIBRATE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			}
			Notification n = builder.build();
			n.sound = addPushNotificationSound(context, sound);
			nm.notify(notificationId, n);
		}
	}

	/**
	 * 
	 * @param data
	 *            bundle from the onMessage() intent callback
	 * @param context
	 * @param notificationId
	 *            id for the notification
	 * @param notificationIcon
	 *            notification icon
	 * @param intent
	 *            intent to be called when the notification is clicked, if a
	 *            link was passed then this intent will be ignored
	 */
	public static void buildNotification(Bundle extraData, Context context, PushAppsNotification notification, int notificationId) {
		// Take all needed parameters from notification
		Intent notificationIntent = new Intent();
		notificationIntent.setClass(context, PushActivity.class);

		if (extraData != null) {
			notificationIntent.putExtra("notificationId", extraData.getString("Id"));
		}
		if (notification.getLink() != null) {
			notificationIntent.putExtra(NOTIFICATION_LINK_KEY, notification.getLink());
		}
		PendingIntent contentIntent = PendingIntent.getActivity(context, notificationId, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setContentIntent(contentIntent).setSmallIcon(getNotificationIcon("", context)).setTicker(notification.getTitle())
				.setContentTitle(notification.getTitle()).setContentText(notification.getMessage()).setWhen(System.currentTimeMillis())
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_VIBRATE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getMessage()));
		}
		if (!SharedData.getInstance().getPrefBoolean(Constants.NEW_TASK, false)) {
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		}
		Notification n = builder.build();
		if (notification.getSound() > 0) {
			n.sound = getSoungByResId(context, notification.getSound());
		}
		if (notification.isHasVibrate()) {
			n.vibrate = new long[] { 500 };
		}
		nm.notify(notificationId, n);
	}

	public String getDevicePushToken() {
		return PushAppsUserManager.getDevicePushToken(mAppContext);
	}

	public String getAppVersion() {
		return PushAppsUserManager.getAppVersion(mAppContext);
	}

	public String getDeviceId() {
		return PushAppsUserManager.getDeviceId(mAppContext);
	}

	public int getOSVersion() {
		return PushAppsUserManager.getOSVersion();
	}

	public String getSDKVersion() {
		return PushAppsUserManager.getSdkVersion();
	}

	public String getDeviceRegistrationId() {
		return PushAppsUserManager.getRegistrationToken();
	}

	public PushAppsRegistrationInterface getRegistrationInterface() {
		return mRegistrationInterface;
	}

	public PushAppsMessageInterface getMessageInterface() {
		return mMessageInterface;
	}

	public void registerForRegistrationEvents(PushAppsRegistrationInterface regInterface) {
		this.mRegistrationInterface = regInterface;
	}

	public void registerForMessagesEvents(PushAppsMessageInterface mesInterface) {
		this.mMessageInterface = mesInterface;
	}

	private static String getDefaultNotificationTitle(Context context) {
		final PackageManager pm = context.getApplicationContext().getPackageManager();
		ApplicationInfo ai;
		try {
			ai = pm.getApplicationInfo(context.getPackageName(), 0);
		} catch (final Exception e) {
			ai = null;
		}
		return (String) (ai != null ? pm.getApplicationLabel(ai) : "");
	}

	private static Uri addPushNotificationSound(Context context, String sound) {
		if (sound != null && sound.length() != 0) {
			int soundId = context.getResources().getIdentifier(sound, "raw", context.getPackageName());
			if (0 != soundId) {
				return Uri.parse("android.resource://" + context.getPackageName() + "/" + soundId);
			}
		}
		return Settings.System.DEFAULT_NOTIFICATION_URI;
	}

	public static Uri getSoungByResId(Context context, int resId) {
		return Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
	}

	private static int getNotificationIcon(String iconName, Context context) {
		SharedData sharedData = SharedData.getInstance(context);
		int iconId = sharedData.getPrefInt("NotificationIcon", 0);

		if (iconId == 0) {
			iconId = context.getApplicationInfo().icon;

			if (null != iconName) {
				int customId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
				if (0 != customId) {
					iconId = customId;
				}
			}
		}

		return iconId;
	}

	public boolean shouldStackNotifications() {
		return SharedData.getInstance(mAppContext).getPrefBoolean("stack_notifications", false);
	}

	/**
	 * set if on a new message a new notification will be added to the status
	 * bar when there is already a notification on the status bar, when the flag
	 * is false then the existing notification will be replaces by the new
	 * notification.
	 * 
	 * @param flag
	 */
	public void setShouldStackNotifications(boolean flag) {
		SharedData.getInstance(mAppContext).setPrefBoolean("stack_notifications", flag);
	}

}
