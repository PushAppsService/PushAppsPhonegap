package com.groboot.pushapps;

import android.content.Context;
import android.content.Intent;

public final class GCMBaseIntentServiceImpl extends GCMBaseIntentService {

	@Override
	protected void onMessage(Context paramContext, Intent paramIntent) {
		Logger.log("GCMBaseIntentServiceImpl - onMessage");		
		PushAppsHelper.handleOnMessage(paramIntent, paramContext);
	}

	@Override
	protected void onError(Context paramContext, String paramString) {
		Logger.log("GCMBaseIntentServiceImpl - onError");
	}

	@Override
	protected void onRegistered(Context paramContext, String registrationId) {
		Logger.log("GCMBaseIntentServiceImpl - onRegistered");
		PushAppsHelper.handleRegistration(registrationId, paramContext);
	}

	@Override
	protected void onUnregistered(Context paramContext, String paramString) {
		Logger.log("GCMBaseIntentServiceImpl - onUnregistered");
	}

}
