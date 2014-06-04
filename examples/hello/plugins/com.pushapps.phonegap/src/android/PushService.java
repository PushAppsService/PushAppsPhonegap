package com.groboot.pushapps;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

abstract class PushService extends IntentService {
	private static WakeLock wakeLock;
	private static final Object LOCK = PushService.class;
	private static String sRetryReceiverClassName;

	public PushService(String name) {
		super(name);
	}

	public PushService() {
		super("PushService");
	}

	static synchronized void setRetryBroadcastReceiver(Context context) {
	}

	static String getRetryReceiverClassName() {
		return sRetryReceiverClassName;
	}
	
	static void setRetryReceiverClassName(String className) {
		sRetryReceiverClassName = className;
	}

	protected abstract void onMessage(String msg, Context context);

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			if (intent == null) {
				return;
				// the intent is null, returning
			}
			Context context = getApplicationContext();
			onMessage("we got something!", context);
			String action = intent.getAction();
			Logger.log("the action is " + action);
			if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
				try {
					// TODO: handle the registration
					Logger.log("registered");
					handleRegistrationIntent(intent);
					// GCMRegistrar.handleRegistrationIntent(intent);
				} finally {
					if ((wakeLock != null) && (wakeLock.isHeld()))
						wakeLock.release();
				}
			} else if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
				try {
					// TODO: handle msg
					// GCMMessageHandler.handleIntentMessage(paramIntent);
					onMessage("we got something!", context);
				} finally {
					if ((wakeLock != null) && (wakeLock.isHeld()))
						wakeLock.release();
				}
			} 
		} finally {
			synchronized (LOCK) {
				if (wakeLock != null) {
					// Log.v("GCMBaseIntentService", "Releasing wakelock");
					wakeLock.release();
				} else {
					// Log.e("GCMBaseIntentService",
					// "Wakelock reference is null");
				}
			}
		}
	}

	static void handleRegistrationIntent(Intent paramIntent) {
		// boolean bool = PushManager.shared().getPreferences().isPushEnabled();
		String str1 = paramIntent.getStringExtra("registration_id");
		String str2 = paramIntent.getStringExtra("error");
		String str3 = paramIntent.getStringExtra("unregistered");
		if (str2 != null) {
			// TODO: error
			// Logger.error("Received GCM error: " + str2);
			/*
			 * if (bool) { //Logger.error("Failed to register with GCM.");
			 * //registrationFailed(str2); } else { //
			 * Logger.error("Failed to unregister with GCM."); }
			 */
		} else if (str3 != null) {
			// Logger.info("Unregistered from GCM: " + str3);
			// backoffTime = 10000L;
		} else if (str1 != null) {
			Logger.log("the id is" + str3);
			// Logger.info("Received GCM Registration ID:" + str1);
			/*
			 * if (bool) { UAirship.shared().getAnalytics().addEvent(new
			 * PushServiceStartedEvent()); PushManager.shared().setGcmId(str1);
			 * } backoffTime = 10000L;
			 */
		}
	}

	static void runIntentInService(Context context, Intent intent,
			String className) {
		synchronized (LOCK) {
			if (wakeLock == null) {
				PowerManager pm = (PowerManager) context
						.getSystemService("power");

				wakeLock = pm.newWakeLock(1, "GCM_LIB");
			}
		}

		// Log.v("GCMBaseIntentService", "Acquiring wakelock");
		wakeLock.acquire();
		intent.setClassName(context, className);
		context.startService(intent);
	}

}
