cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/com.pushapps.phonegap/www/pushapps.js",
        "id": "com.pushapps.phonegap.PushApps",
        "clobbers": [
            "PushNotification"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "com.pushapps.phonegap": "1.6.0"
}
// BOTTOM OF METADATA
});