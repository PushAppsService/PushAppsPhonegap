package com.pushapps.phonegap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;

import com.groboot.pushapps.PushAppsRegistrationInterface;
import com.groboot.pushapps.PushManager;
import com.groboot.pushapps.SendTagResponseListener;
import com.groboot.pushapps.Tag;
import com.pushapps.phonegapcli.R;

public class PushAppsPlugin extends CordovaPlugin {

	public static final String ACTION_REGISTER_USER = "registerUser";
	public static final String ACTION_UNREGISTER_USER = "unRegisterUser";
	public static final String ACTION_GET_DEVICE_ID = "getDeviceId";
	public static final String ACTION_SET_TAGS = "setTags";
	public static final String ACTION_REMOVE_TAGS = "removeTags";

	private static final String PREFS_NAME = "pushappsdata";

	boolean receiversRegistered = false;

	HashMap<String, CallbackContext> callbackIds = new HashMap<String, CallbackContext>();
	PushManager manager;

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		checkIntentExtras(intent);
	}

	// Called when there is need to check current messages
	private void checkIntentExtras(Intent intent) {

		if (intent.getExtras() != null) {
			String message = intent.getExtras().getString("Message");
			if (message != null && message.length() > 0) {
				String notificationId = intent.getExtras().getString("Id");

				SharedPreferences appSharedPrefs = cordova
						.getActivity()
						.getApplicationContext()
						.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
				Editor prefsEditor = appSharedPrefs.edit();
				String lastPushRead = appSharedPrefs.getString(
						"LastPushMessageRead", "");

				if (!lastPushRead.equals(notificationId)) {
					prefsEditor
							.putString("LastPushMessageRead", notificationId);
					prefsEditor.commit();
					Bundle params = intent.getExtras();
					internalOnMessage(getJSONStringFromBundle(params));
				}

			}
		}

	}

	private void internalOnMessage(JSONObject message) {

		final String jsStatement = String.format(

		"PushNotification.messageReceive('%s');", message.toString());

		cordova.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				webView.loadUrl("javascript:" + jsStatement);
			}
		});
	}

	PushAppsRegistrationInterface pushAppsRegistrationInterface = new PushAppsRegistrationInterface() {

		@Override
		public void onUnregistered(Context paramContext, String paramString) {
			CallbackContext callback = callbackIds.get("unregisterDevice");
			if (callback == null)
				return;

			callback.success(paramString);
			callbackIds.remove("unregisterDevice");
		}

		@Override
		public void onRegistered(Context paramContext, String paramString) {

			CallbackContext callback = callbackIds.get("registerDevice");
			if (callback == null)
				return;

			callback.success(paramString);
			callbackIds.remove("registerDevice");
		}
	};

	// Utility function. convert bundle into JSONObject
	private static JSONObject getJSONStringFromBundle(Bundle bundle) {

		JSONObject jsonObject = new JSONObject();
		for (String key : bundle.keySet()) {
			Object value = bundle.get(key);
			try {
				// if the value itself is a json we try to parse it
				try {
					JSONObject innerJsonObject = new JSONObject(
							value.toString());
					String escaped = TextUtils.htmlEncode(innerJsonObject
							.toString().replaceAll("\\\\", "\\\\\\\\"));
					jsonObject.put(key, escaped);
					// if we succeeded
				} catch (JSONException e) {
					jsonObject.put(
							key,
							TextUtils.htmlEncode(value.toString().replaceAll(
									"\\\\", "\\\\\\\\")));
				}

			} catch (JSONException e) {
				// Do nothing
			}
		}
		return jsonObject;
	}

	// Main internal function which register a device via the PushApps manager
	private boolean internalRegister(JSONArray data,
			CallbackContext callbackContext) {
		JSONObject params = null;
		try {
			params = data.getJSONObject(0);
		} catch (JSONException e) {
			callbackContext.error(e.getMessage());
			return true;
		}

		callbackIds.put("registerDevice", callbackContext);

		try {
			String googleProjectId = "";
			if (params.has("googleProjectId")) {
				googleProjectId = params.getString("googleProjectId");
			}
			String appToken = "";
			if (params.has("appToken")) {
				appToken = params.getString("appToken");
			}

			// Make sure the push enabled flag is on
			SharedPreferences appSharedPrefs = cordova
					.getActivity()
					.getApplicationContext()
					.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
			Editor prefsEditor = appSharedPrefs.edit();
			prefsEditor.putBoolean("push_enabled", true);
			prefsEditor.commit();
			
			PushManager.init(cordova.getActivity().getApplicationContext(),
					googleProjectId, appToken);
			manager = PushManager.getInstance(cordova.getActivity()
					.getApplicationContext());
			manager.registerForRegistrationEvents(pushAppsRegistrationInterface);
			manager.setShouldStackNotifications(true);
			manager.setNotificationIcon(R.drawable.icon);
			checkIntentExtras(cordova.getActivity().getIntent());

		} catch (JSONException e) {
			callbackIds.remove("registerDevice");
			callbackContext.error(e.getMessage());
			return true;
		}

		return true;
	}

	// Call unregister via PushApps manager
	private boolean internalUnregister(JSONArray data,
			CallbackContext callbackContext) {

		callbackIds.put("unregisterDevice", callbackContext);
		manager.unregister();

		return true;
	}

	private boolean internalDeviceId(CallbackContext callbackContext) {

		callbackContext.success(this.manager.getDeviceId());

		return true;
	}

	private boolean internalSetTags(JSONArray data,
			final CallbackContext callbackContext) {

		List<Tag> tags = new ArrayList<Tag>();

		for (int i = 0; i < data.length(); i++) {

			try {
				JSONObject params = data.getJSONObject(i);
				String identifier = params.getString("identifier");
				String value = params.getString("value");

				try {

					Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'")
							.parse(value);
					tags.add(new Tag(identifier, date));

				} catch (Exception eDate) {

					try {

						Integer number = Integer.parseInt(value);
						tags.add(new Tag(identifier, number));

					} catch (Exception eNumber) {

						if (value.equals("true") || value.equals("false")) {
							boolean bool = value.equals("true");
							tags.add(new Tag(identifier, bool));
						}
						else {
							tags.add(new Tag(identifier, value));
						}
						
					}
				}

			} catch (Exception e) {
				callbackContext.error(e.getMessage());
				return true;
			}

		}

		if (tags.size() > 0) {			
			Tag[] tagsArray = new Tag[tags.size()];
			for (int i = 0; i < tags.size(); i++) {
				tagsArray[i] = tags.get(i);
			}
			
			this.manager.sendTag(new SendTagResponseListener() {
				
				@Override
				public void response(boolean success, String message) {

					if (success) {
						callbackContext.success();
					}
					else {
						callbackContext.error(message);
					}

				}
			}, tagsArray);
		}
		else {
			callbackContext.error("No valid types were found");
		}
		

		return true;
	}
	
	private boolean internalRemoveTags(JSONArray data,
			final CallbackContext callbackContext) {
		
		List<String> identifiers = new ArrayList<String>();
		
		for (int i = 0; i < data.length(); i++) {
			String str = null;
			try {
				str = data.getString(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				return false;
			}
			if (str.length() > 0) {
				identifiers.add(str);
			}
		}
		
		if (identifiers.size() > 0) {
			String[] strings = new String[identifiers.size()];
			for (int i = 0; i < identifiers.size(); i++) {
				strings[i] = identifiers.get(i);
			}
			
			this.manager.removeTag(new SendTagResponseListener() {
				
				@Override
				public void response(boolean success, String message) {
					
					if (success) {
						callbackContext.success();
					}
					else {
						callbackContext.error(message);
					}
					
				}
			}, strings);
		}
		else {
			return true;
		}
		
		return true;
	}

	// Main Phonegap method which convert JS request into native code
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {

		try {
			if (ACTION_REGISTER_USER.equals(action)) {
				return internalRegister(args, callbackContext);
			} else if (ACTION_UNREGISTER_USER.equals(action)) {
				return internalUnregister(args, callbackContext);
			} else if (ACTION_GET_DEVICE_ID.equals(action)) {
				return internalDeviceId(callbackContext);
			} else if (ACTION_SET_TAGS.equals(action)) {
				return internalSetTags(args, callbackContext);
			} else if (ACTION_REMOVE_TAGS.equals(action)) {
				return internalRemoveTags(args, callbackContext);
			}
			callbackContext.error("Invalid action");
			return false;
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
			return false;
		}
	}
}
