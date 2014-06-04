package com.groboot.pushapps;

class Constants {

	static final String BASE_URL = "https://ws.pushapps.mobi";

	static final String REGISTER_URL = BASE_URL + "/User/Register";
	static final String UNREGISTER_URL = BASE_URL + "/User/Unregister";
	static final String READ = BASE_URL + "/User/NotificationOpened";
	static final String REPORT_EVENT = BASE_URL + "/User/ReportEvent";
	static final String SEND_TAG = BASE_URL + "/User/SetTag";
	static final String REMOVE_TAG = BASE_URL + "/User/RemoveTag";
	static final String VERSION = "1.6.0";
	static final String SENDER_ID = "senderId";
	static final String APP_TOKEN = "token";
	static final String PUSH_ENABLED = "push_enabled";
	static final String INTENT = "intent";
	static final int NOTIFICATION_ID = 32;
	static final int RESULT_OK = 1;
	static final int RESULT_ERROR = 0;
	static final String NEW_TASK = "newTask";
	static final int NATIVE = 1;
	static final int PHONE_GAP = 3;
	static final String SDK_TYPE = "_sdktype";
	static final String ID_TYPE = "idtype";

	static final int SUCCESS = 100;

	static class TYPES {
		static final int DATE = 1;
		static final int NUMBER = 2;
		static final int STRING = 3;
		static final int BOOLEAN = 5;
	}
}
