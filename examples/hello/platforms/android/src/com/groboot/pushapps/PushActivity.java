package com.groboot.pushapps;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class PushActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.log("onCreate");
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			handleMessage(bundle);
		} else {
			handleMessage(new Bundle());
		}
	}

	private void handleMessage(Bundle bundle) {
		try {
			String notifId = getIntent().getStringExtra("notificationId");
			Logger.log("the notification id is " + notifId);
			PushAppsHelper.handleNotificationRead(notifId, getApplicationContext(), this.getIntent());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// Check if link exists
			String link = bundle.getString(PushManager.NOTIFICATION_LINK_KEY);
			if (link != null && link.trim().length() > 0) {
				try {
					Intent i = new Intent();
					i.setAction(Intent.ACTION_VIEW);
					i.setData(Uri.parse(link));
					startActivity(i);
				} catch (Exception e) {
					// Do nothing
				}
			} else {
				Intent customIntent;

				// For PHONEGAP - if not set custom intent, take the launch
				// intent
				String intentNameFromPref = SharedData.getInstance(getApplicationContext()).getPrefString("IntentName", "");
				if (intentNameFromPref.length() > 0) {
					try {
						customIntent = new Intent();
						customIntent.putExtras(bundle);
						customIntent.setClass(getApplicationContext(), Class.forName(intentNameFromPref));
					} catch (Exception e) {
						e.printStackTrace();
						String packageName = getPackageName();
						customIntent = getPackageManager().getLaunchIntentForPackage(packageName);
						customIntent.putExtras(bundle);
					}
				} else {
					String packageName = getPackageName();
					customIntent = getPackageManager().getLaunchIntentForPackage(packageName);
					customIntent.putExtras(bundle);
				}
				if (!SharedData.getInstance().getPrefBoolean(Constants.NEW_TASK, false)) {
					customIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				}
				startActivity(customIntent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Logger.log("onNewIntent");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			handleMessage(bundle);
		}
	}

}
