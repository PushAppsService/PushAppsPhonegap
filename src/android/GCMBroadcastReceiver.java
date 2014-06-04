package com.groboot.pushapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GCMBroadcastReceiver extends BroadcastReceiver {

	public final void onReceive(Context context, Intent intent) {
		String className = getGCMIntentServiceClassName(context);
		GCMBaseIntentService.runIntentInService(context, intent, className);
		setResult(-1, null, null);
	}

	protected String getGCMIntentServiceClassName(Context context) {
		return getDefaultIntentServiceClassName(context);
	}

	static final String getDefaultIntentServiceClassName(Context context) {
		return "com.groboot.pushapps.GCMBaseIntentServiceImpl";
	}

}
