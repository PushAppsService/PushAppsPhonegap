package com.groboot.pushapps;

import android.content.Context;

public interface PushAppsRegistrationInterface {
	
	public void onRegistered(Context paramContext, String paramString);
	
	public void onUnregistered(Context paramContext, String paramString);
	
}
