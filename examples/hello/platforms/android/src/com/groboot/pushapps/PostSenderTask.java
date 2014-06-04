package com.groboot.pushapps;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;

class PostSenderTask extends AsyncTask<Void, Void, String> {

	String url, data;
	SendTagResponseListener listener;

	PostSenderTask(String url, String data, SendTagResponseListener listener) {
		this.url = url;
		this.data = data;
		this.listener = listener;
	}

	PostSenderTask(String url, String data) {
		this.url = url;
		this.data = data;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (listener != null) {
			try {
				JSONObject obj = new JSONObject(result);
				int code = obj.getInt("Code");
				String message = obj.getString("Message");
				if (code == Constants.SUCCESS) {
					listener.response(true, message);
				} else {
					listener.response(false, message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				listener.response(false, e.getMessage());
			}
		}
	}

	@Override
	protected String doInBackground(Void... params) {
		Logger.log("the url is " + url);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		request.addHeader("Content-Type", "application/json");
		HttpResponse response;
		String responseString = null;
		try {
			if (data != null)
				request.setEntity(new ByteArrayEntity(data.toString().getBytes("UTF8")));
			Logger.log("data " + data);
			response = httpClient.execute(request);
			responseString = EntityUtils.toString(response.getEntity());
			Logger.log("response " + responseString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseString;
	}

}
