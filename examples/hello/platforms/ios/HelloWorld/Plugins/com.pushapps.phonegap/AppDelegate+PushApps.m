//
//  AppDelegate+notification.m
//  pushtest
//
//  Created by Robert Easterday on 10/26/12.
//
//

#import "AppDelegate+PushApps.h"
#import "PushApps.h"
#import <objc/runtime.h>

static char launchNotificationKey;

@implementation AppDelegate (PushApps)

- (id) getCommandInstance:(NSString*)className
{
	return [self.viewController getCommandInstance:className];
}

+ (void)load
{
    Method original, swizzled;
    
    original = class_getInstanceMethod(self, @selector(init));
    swizzled = class_getInstanceMethod(self, @selector(swizzled_init));
    method_exchangeImplementations(original, swizzled);
}

- (AppDelegate *)swizzled_init
{
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(createNotificationChecker:)
               name:@"UIApplicationDidFinishLaunchingNotification" object:nil];
	
	return [self swizzled_init];
}

- (void)createNotificationChecker:(NSNotification *)notification
{
	if (notification)
	{
		NSDictionary *launchOptions = [notification userInfo];
		if (launchOptions)
			self.launchNotification = [launchOptions objectForKey: @"UIApplicationLaunchOptionsRemoteNotificationKey"];
	}
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    // Notify PushApps of a successful registration.
    [[PushAppsManager sharedInstance] updatePushToken:deviceToken];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
    // Notify PushApps of an unsuccessful registration.
    [[PushAppsManager sharedInstance] updatePushError:error];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    
    UIApplicationState appState = application.applicationState;
    
    if (appState == UIApplicationStateActive) {
        [[PushAppsManager sharedInstance] handlePushMessageOnForeground:userInfo];
    } else {
        self.launchNotification = userInfo;
    }
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    
    //zero badge
    application.applicationIconBadgeNumber = 0;

    if (![self.viewController.webView isLoading] && self.launchNotification) {
        NSDictionary *dictionary = [NSDictionary dictionaryWithDictionary:self.launchNotification];
        [[PushAppsManager sharedInstance] performSelectorOnMainThread:@selector(handlePushMessageOnForeground:) withObject:dictionary waitUntilDone:NO];
        self.launchNotification = nil;
    }
}

- (NSMutableArray *)launchNotification
{
   return objc_getAssociatedObject(self, &launchNotificationKey);
}

- (void)setLaunchNotification:(NSDictionary *)aDictionary
{
    objc_setAssociatedObject(self, &launchNotificationKey, aDictionary, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (void)dealloc
{
    self.launchNotification	= nil;
}

@end
