import SwiftUI
import Shared
import FirebaseCore
import FirebaseMessaging

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    
    var orientationLock: UIInterfaceOrientationMask = .portrait
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        NotificationCenter.default.addObserver(
            forName: NSNotification.Name("OrientationLockChanged"),
            object: nil,
            queue: .main
        ) { notification in
            if let isLandscape = notification.userInfo?["landscape"] as? Bool {
                self.orientationLock = isLandscape ? .landscape : .portrait
                
                if #available(iOS 16.0, *) {
                    let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene
                    windowScene?.requestGeometryUpdate(.iOS(interfaceOrientations: self.orientationLock))
                    if let rootVC = windowScene?.windows.first?.rootViewController {
                        rootVC.setNeedsUpdateOfSupportedInterfaceOrientations()
                    }
                } else {
                    let orientation = isLandscape ? UIInterfaceOrientation.landscapeLeft.rawValue : UIInterfaceOrientation.portrait.rawValue
                    UIDevice.current.setValue(orientation, forKey: "orientation")
                    UIViewController.attemptRotationToDeviceOrientation()
                }
            }
        }
        
        FirebaseApp.configure()
        
        UNUserNotificationCenter.current().delegate = self
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { _, _ in }
        )
        
        application.registerForRemoteNotifications()
        Messaging.messaging().delegate = self
        
        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let tokenString = deviceToken.map { String(format: "%02.2hhx", $0) }.joined()
        print("APNs Token received: \(tokenString)")
        Messaging.messaging().apnsToken = deviceToken
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register for remote notifications: \(error.localizedDescription)")
    }
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("FCM Token: \(String(describing: fcmToken))")
        if let token = fcmToken {
            KoinInitializer.shared.saveFCMToken(token: token)
        }
    }
    
    // Add this to show notifications when app is in the foreground
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .badge])
    }
    
    // Add this to handle notification tap events
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let userInfo = response.notification.request.content.userInfo
        print("Tapped Notification UserInfo: \(userInfo)")
        
        var type: Int32 = 0
        var contentId: Int32 = 0
        let groupCode = userInfo["groupCode"] as? String
        
        if let t = userInfo["type"] as? String {
            type = Int32(t) ?? 0
        } else if let t = userInfo["type"] as? Int32 {
            type = t
        } else if let t = userInfo["type"] as? Int {
            type = Int32(t)
        }
        
        if let c = userInfo["contentId"] as? String {
            contentId = Int32(c) ?? 0
        } else if let c = userInfo["contentId"] as? Int32 {
            contentId = c
        } else if let c = userInfo["contentId"] as? Int {
            contentId = Int32(c)
        }
        
        KoinInitializer.shared.handleNotificationTap(
            type: type,
            contentId: contentId,
            groupCode: groupCode
        )
        
        completionHandler()
    }
    
    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        return self.orientationLock
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        KoinInitializer.shared.initialize()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
