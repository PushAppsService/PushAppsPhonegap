//
//  AppDelegate+pushapps.m
//  PushAppsPhonegap
//
//  Created by Geoffrey Bauduin <bauduin.geo@gmail.com> on 04/11/2014.
//
//

#import "AppDelegate+pushapps.h"
#import "PushApps.h"
#import <objc/runtime.h>

@implementation AppDelegate (pushapps)

#ifdef __IPHONE_8_0
- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings
{
    [[PushAppsManager sharedInstance] didRegisterUserNotificationSettings:notificationSettings];
}

- (void)application:(UIApplication *)application handleActionWithIdentifier:(NSString *)identifier forRemoteNotification:(NSDictionary *)userInfo completionHandler:(void(^)())completionHandler
{
    [[PushAppsManager sharedInstance] handleActionWithIdentifier:identifier forRemoteNotification:userInfo
                                               completionHandler:completionHandler];
}
#endif
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    // Notify PushApps of a successful registration.
    NSLog(@"Received push token : %@", deviceToken);
    [[PushAppsManager sharedInstance] updatePushToken:deviceToken];
}

// Gets called when a remote notification is received while app is in the foreground.
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
    NSLog(@"Received remote notification while app was in foreground : %@", userInfo);
    [[PushAppsManager sharedInstance] handlePushMessageOnForeground:userInfo];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
    // keeps you up to date with any errors during push setup.
    NSLog(@"Cannot register for push notifications : %@", error.userInfo[@"NSLocalizedDescription"]);
    [[PushAppsManager sharedInstance] updatePushError:error];
}

@end

