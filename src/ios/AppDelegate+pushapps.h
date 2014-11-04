//
//  AppDelegate+notification.h
//  PushAppsPhonegap
//
//  Created by Geoffrey Bauduin <bauduin.geo@gmail.com> on 04/11/2014.
//
//

#ifndef PushAppsPhonegap_AppDelegate_pushapps_h
#define PushAppsPhonegap_AppDelegate_pushapps_h

#import "AppDelegate.h"

@interface AppDelegate (pushapps)
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error;
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo;
#   ifdef __IPHONE_8_0
- (void)application:(UIApplication *)application handleActionWithIdentifier:(NSString *)identifier forRemoteNotification:(NSDictionary *)userInfo completionHandler:(void(^)())completionHandler;
- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings;
#   endif

@end

#endif
