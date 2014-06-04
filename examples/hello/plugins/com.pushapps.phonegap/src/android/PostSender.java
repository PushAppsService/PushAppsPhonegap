package com.groboot.pushapps;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;

class PostSender {
	static void register(String regKey, String token, Context context) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("TimeZone", Utils.getUtcOffset());
			jsonObject.put("PushToken", regKey);
			jsonObject.put("DeviceId", PushAppsUserManager.getDeviceId(context));
			jsonObject.put("OSVersion", Build.VERSION.SDK_INT + "");
			jsonObject.put("DeviceType", Build.MANUFACTURER + " " + Build.DEVICE);
			jsonObject.put("MobileType", 1);
			jsonObject.put("AppToken", token);
			jsonObject.put("SDKVersion", Constants.VERSION);
			jsonObject.put("AppIdentifier", context.getPackageName());
			jsonObject.put("SDK", PushAppsUserManager.getSDKType());
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			String version = pInfo.versionName;
			jsonObject.put("AppVersion", version);
			if (RegistrationChecker.needToSendData(Utils.getUtcOffset(), regKey, PushAppsUserManager.getDeviceId(context),
					Build.VERSION.SDK_INT, Build.MANUFACTURER, token, Constants.VERSION, version)) {
				int resultCode = sendHttpRequest(Constants.REGISTER_URL, jsonObject.toString());
				if (resultCode != Constants.RESULT_OK) {
					SharedData.getInstance().setPrefBoolean(RegistrationChecker.NEED_TO_REGISTER_AGAIN, true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void unregister(Context context, String token) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("DeviceId", PushAppsUserManager.getDeviceId(context));
			jsonObject.put("AppToken", token);
			sendHttpRequestOnDifferentThread(Constants.UNREGISTER_URL, jsonObject.toString());
		} catch (Exception e) {

		}
	}

	static void sendTags(SendTagResponseListener responseListener, String appToken, Context context, Tag... tags) {
		try {
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			for (Tag tag : tags) {
				JSONObject tagJsonObject = new JSONObject();
				tagJsonObject.put("Identifier", tag.Identifier);
				tagJsonObject.put("Value", tag.Value);
				tagJsonObject.put("TagType", tag.TagType);
				jsonArray.put(tagJsonObject);
			}
			jsonObject.put("Tags", jsonArray);
			jsonObject.put("DeviceId", PushAppsUserManager.getDeviceId(context));
			jsonObject.put("AppToken", appToken);
			sendHttpRequestOnDifferentThread(Constants.SEND_TAG, jsonObject.toString(), responseListener);
		} catch (Exception e) {

		}
	}

	static void removeTags(SendTagResponseListener responseListener, String appToken, Context context, List<String> tags) {
		try {
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			for (String tag : tags) {
				jsonArray.put(tag);
			}
			jsonObject.put("Identifiers", jsonArray);
			jsonObject.put("DeviceId", PushAppsUserManager.getDeviceId(context));
			jsonObject.put("AppToken", appToken);
			sendHttpRequestOnDifferentThread(Constants.REMOVE_TAG, jsonObject.toString(), responseListener);
		} catch (Exception e) {

		}
	}

	@Deprecated
	static void sendEvent(String event, Context context, String appToken) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("DeviceId", PushAppsUserManager.getDeviceId(context));
			jsonObject.put("EventIdentifier", event);
			jsonObject.put("AppToken", appToken);
			sendHttpRequestOnDifferentThread(Constants.REPORT_EVENT, jsonObject.toString());
		} catch (Exception e) {

		}
	}

	// API 11
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void executeTask(PostSenderTask task) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			task.execute();
	}

	static void setNotificationRead(String notificationId, String token, String deviceId) {
		try {
			Logger.log("the notification id is " + notificationId);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("NotificationId", notificationId);
			jsonObject.put("AppToken", token);
			jsonObject.put("DeviceId", deviceId);
			PostSenderTask postTask = new PostSenderTask(Constants.READ, jsonObject.toString());
			executeTask(postTask);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	static void sendHttpRequestOnDifferentThread(String url, String json, SendTagResponseListener listener) {
		PostSenderTask task = new PostSenderTask(url, json, listener);
		executeTask(task);
	}

	static void sendHttpRequestOnDifferentThread(String url, String json) {
		PostSenderTask task = new PostSenderTask(url, json);
		executeTask(task);
	}

	static int sendHttpRequest(String url, String json) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		request.addHeader("Content-Type", "application/json");
		HttpResponse response;
		int code = Constants.RESULT_ERROR;
		try {
			if (json != null) {
				Logger.log("the request is " + json);
				request.setEntity(new ByteArrayEntity(json.getBytes("UTF8")));
			}
			Logger.log("the url is " + url);
			response = httpClient.execute(request);
			String jsonString = EntityUtils.toString(response.getEntity());
			try {
				JSONObject jsonObject = new JSONObject(jsonString);
				code = jsonObject.getInt("Code");
			} catch (Exception e) {

			}
			Logger.log("the response is " + jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}

}
