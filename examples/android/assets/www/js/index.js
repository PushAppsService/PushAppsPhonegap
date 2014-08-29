var GOOGLE_PROJECT_ID = "47811378595";
var PUSHAPPS_APP_TOKEN = "e096fbcb-004e-49b9-839a-09011ace9780";

/**
 * Register current device with PushApps
 */
function registerDevice() {
	PushNotification.registerDevice(GOOGLE_PROJECT_ID, PUSHAPPS_APP_TOKEN, function (pushToken) {
                                    console.log("My push token: " + pushToken);
                                    alert('registerDevice, push token' + pushToken);
                                    }, function (error) {
                                    console.log(error);
                                    alert(error);
                                    });
	
	document.removeEventListener('pushapps.message-received');
	document.addEventListener('pushapps.message-received', function(event) {
                              var notification = event.notification;
                              
                              var devicePlatform = device.platform;
                              if (devicePlatform === "iOS") {
                              
                                alert("message-received, Message: " + notification.aps.alert + " , D: " + notification.D);
                              } else {
                                alert("message-received, Message: " + notification.Message + " , Title: " + notification.Title + " , D: " + notification.D);
                              }
                              });
                              
}

/**
 * Unregister current device with PushApps
 */
function unregisterDevice() {
	document.removeEventListener('pushapps.message-received');
	PushNotification.unRegisterDevice(function () {
                                      console.log("success")
                                      alert("Your device was unregistered from PushApps");
                                      }, function () {
                                      console.log("error");
                                      alert("Error unregistering your device");
                                      });
}

/**
 * Send boolean tag
 */
function sendBooleanTag() {
	var d = document.getElementById('booleanTagInput').value === "on" ? "true" : "false";
	var iden = document.getElementById('booleanIdentifierTagInput').value;
    
    PushNotification.setTags([{
                              identifier: iden,
                              value: d
                              }], function () {
                             alert("Your tag was successfully added");
                             }, function (message) {
                             alert("ERROR: " + message);
                             });
}

/**
 * Send number tag
 */
function sendNumberTag() {
	var d = document.getElementById("numberTagInput").value;
	var iden = document.getElementById('numberIdentifierTagInput').value;
    
    PushNotification.setTags([{
                              identifier: iden,
                              value: d
                              }], function () {
                             alert("Your tag was successfully added");
                             }, function (message) {
                             alert("ERROR: " + message);
                             });
}

/**
 * Send string tag
 */
function sendStringTag() {
	var d = document.getElementById("stringTagInput").value;
	var iden = document.getElementById('stringIdentifierTagInput').value;
    
    PushNotification.setTags([{
                              identifier: iden,
                              value: d
                              }], function () {
                             alert("Your tag was successfully added");
                             }, function (message) {
                             alert("ERROR: " + message);
                             });
}

/**
 * Send date tag
 */
function sendDateTag() {
	var d = new Date(document.getElementById("dateTagInput").value);
    var n = d.toISOString();
    var iden = document.getElementById('dateIdentifierTagInput').value;
    
    PushNotification.setTags([{
                              identifier: iden,
                              value: n
                              }], function () {
                             alert("Your tag was successfully added");
                             }, function (message) {
                             alert("ERROR: " + message);
                             });
}

function removeTag() {
	var idens = [ document.getElementById('identifierRemoveTagInput').value ];
	PushNotification.removeTags(idens, function () {
                                alert("Tag removed successfully");
                                }, function (message) {
                                alert("ERROR: " + message);
                                });
}

function getDeviceId() {
    PushNotification.getDeviceId(function (deviceId) {
                                 alert("Your device id is: " + deviceId);
                                 }, function () {
                                 alert("We could not get your device id. Please check your logs or contact our support team");
                                 })
}

var app = {
    // Application Constructor
initialize: function() {
    console.log("initialize app");
    this.bindEvents();
},
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
bindEvents: function() {
    console.log("bind events");
    document.addEventListener('deviceready', this.onDeviceReady, false);
},
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicity call 'app.receivedEvent(...);'
onDeviceReady: function() {
    console.log("on device ready");
    // Enable PushApps after the deviceready event was called
    registerDevice();
}
};