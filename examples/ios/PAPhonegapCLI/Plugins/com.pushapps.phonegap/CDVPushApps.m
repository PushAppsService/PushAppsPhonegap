//
//  CDVPushApps.m
//  PushAppsPhonegapPlugin
//
//  Created by Asaf Ron on 11/21/13.
//
//

#import "CDVPushApps.h"

@interface CDVPushApps()

@end

@implementation CDVPushApps

- (CDVPlugin*)initWithWebView:(UIWebView*)theWebView
{
    self = [super initWithWebView:theWebView];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(checkForLaunchOptions:) name:@"UIApplicationDidFinishLaunchingNotification" object:nil];
    }
    return self;
}

#define LastPushMessageDictionary @"PUSHAPPSSDK_LastPushMessageDictionary"

- (void)checkForLaunchOptions:(NSNotification *)notification
{
    NSDictionary *launchOptions = [notification userInfo] ;
    
    // This code will be called immediately after application:didFinishLaunchingWithOptions:.
    NSDictionary *notifDictionary = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
    if (notifDictionary) {
        [[NSUserDefaults standardUserDefaults] setObject:notifDictionary forKey:LastPushMessageDictionary];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
}

#define Callback_RegisterUser @"PUSHAPPSSDK_Callback_RegisterUser"

- (void)registerUser:(CDVInvokedUrlCommand *)command
{
    NSString *appToken = [[command.arguments objectAtIndex:0] objectForKey:@"appToken"];
    
    if ([appToken isKindOfClass:[NSString class]]) {
        
        // Saving callback to user defaults
        [self saveCallbackWithName:Callback_RegisterUser andId:command.callbackId];
        
        // Starting the push apps manager
        [[PushAppsManager sharedInstance] setDelegate:self];
        [[PushAppsManager sharedInstance] startPushAppsWithAppToken:appToken withLaunchOptions:nil];
        
    } else {
        
        // Clear callback to user defaults
        [self saveCallbackWithName:Callback_RegisterUser andId:@""];
        
        // Throw error to JS
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"App Token must be supplied"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void)unRegisterUser:(CDVInvokedUrlCommand*)command
{
    // Starting the push apps manager
    [[PushAppsManager sharedInstance] setDelegate:self];
    [[PushAppsManager sharedInstance] unregisterFromPushNotificationsByDeviceId:[[PushAppsManager sharedInstance] getDeviceId]];
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@""];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getDeviceId:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[[PushAppsManager sharedInstance] getDeviceId]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)setTags:(CDVInvokedUrlCommand*)command
{
    NSArray *tagsFromJS = [NSArray arrayWithArray:command.arguments];
    NSMutableArray *arrayToServer = [NSMutableArray array];
    
    for (NSInteger i = 0; i < [tagsFromJS count]; i++) {
        
        // Get values from JS
        NSString *identifier = [[tagsFromJS objectAtIndex:i] objectForKey:@"identifier"];
        id value = [[tagsFromJS objectAtIndex:i] objectForKey:@"value"];
        
        // Try parsing a date
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setLocale:[NSLocale systemLocale]];
        [dateFormatter setTimeZone:[NSTimeZone timeZoneWithName:@"GMT"]];
        [dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss.sssZ"];
        NSDate *date = nil;
        
        @try {
             date = [dateFormatter dateFromString:value];
        }
        @catch (NSException *exception) {
            // Do nothing
        }

        
        if (date) {
            [arrayToServer addObject:[NSDictionary dictionaryWithObjectsAndKeys:date, identifier, nil]];
        }
        else if ([value integerValue]) {
                    
            [arrayToServer addObject:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithInteger:[value integerValue]], identifier, nil]];
                    
        } else {
            
            [arrayToServer addObject:[NSDictionary dictionaryWithObjectsAndKeys:[NSString stringWithString:value], identifier, nil]];
            
        }
        
    }
    
    if ([arrayToServer count] > 0) {
        
        [[PushAppsManager sharedInstance] addTags:arrayToServer andOperationStatus:^(BOOL success, NSString *msg) {
           
            if (success) {
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@""];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
            else {
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:msg];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
            
        }];
        
    }
    else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No valid types were found"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }

}

- (void)removeTags:(CDVInvokedUrlCommand*)command
{
    if ([[NSArray arrayWithArray:command.arguments] count] > 0) {
        
        NSMutableArray *array = [NSMutableArray array];
        
        for (NSInteger i = 0; i < [command.arguments count]; i++) {
            
            if ([[command.arguments objectAtIndex:i] isKindOfClass:[NSString class]]) {
                
                [array addObject:[command.arguments objectAtIndex:i]];
                
            }
            
        }
        
        [[PushAppsManager sharedInstance] removeTagsWithIdentifiers:array andOperationStatus:^(BOOL success, NSString *msg) {
            
            if (success) {
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@""];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
            else {
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:msg];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
            
        }];
    }
}

- (void)saveCallbackWithName:(NSString *)name andId:(NSString *)callbackId
{
    // Saving callback to user defaults
    [[NSUserDefaults standardUserDefaults] setObject:callbackId forKey:name];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString *)getCallbackIdForAction:(NSString *)action
{
    return [[NSUserDefaults standardUserDefaults] objectForKey:action];
}

#pragma mark - push apps delegate

- (void)pushApps:(PushAppsManager *)manager didReceiveRemoteNotification:(NSDictionary *)pushNotification whileInForeground:(BOOL)inForeground
{
    NSMutableDictionary *tempDic = [NSMutableDictionary dictionaryWithDictionary:pushNotification];
    [tempDic setObject:[NSNumber numberWithBool:inForeground] forKey:@"inForeground"];
    [self updateWithMessageParams:tempDic];
}

- (void)updateWithMessageParams:(NSDictionary *)pushNotification
{
    // Clear application badge
    [[PushAppsManager sharedInstance] clearApplicationBadge];
    
    NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
    NSError *innerJSONparsingError;
    for (id key in pushNotification) {
        if ([[pushNotification objectForKey:key] isKindOfClass:[NSString class]]) {
            
            NSDictionary *JSON = [NSJSONSerialization JSONObjectWithData: [[pushNotification objectForKey:key] dataUsingEncoding:NSUTF8StringEncoding] options: NSJSONReadingMutableContainers error: &innerJSONparsingError];
            
            if (JSON) {
                [dictionary setObject:JSON forKey:key];
            }
            else {
                [dictionary setObject:[pushNotification objectForKey:key] forKey:key];
            }
            
        }
        else {
            [dictionary setObject:[pushNotification objectForKey:key] forKey:key];
        }
    }
    
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:0 error:&error];
    
    NSString *jsonString = @"{}";
    if (jsonData) {
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
        
    // Update JS
    NSString *javascripCode = [NSString stringWithFormat:@"PushNotification.messageReceive('%@')", jsonString];
    [self performSelectorOnMainThread:@selector(writeJavascript:) withObject:javascripCode waitUntilDone:YES];
}

- (void)pushApps:(PushAppsManager *)manager didUpdateUserToken:(NSString *)pushToken
{
    // Update JS
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:pushToken];
    
    NSString *callbackId = [self getCallbackIdForAction:Callback_RegisterUser];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
    
    NSDictionary *checkForLastMessage = [[NSUserDefaults standardUserDefaults] objectForKey:LastPushMessageDictionary];
    if (checkForLastMessage) {
        [self updateWithMessageParams:checkForLastMessage];
        
        // Clear last message
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:LastPushMessageDictionary];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
}

@end
