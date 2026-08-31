import SwiftUI
import Shared
import FirebaseCore
import FirebaseMessaging

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    
    var orientationLock: UIInterfaceOrientationMask = .portrait
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        KoinInitializer.shared.initialize()
        
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
        
        if let model = parseNotification(userInfo) {
            KoinInitializer.shared.handleNotificationTap(notification: model)
        }
        
        completionHandler()
    }
    
    func parseNotification(_ userInfo: [AnyHashable: Any]) -> NotificationListModel? {
        do {
            let data = try JSONSerialization.data(withJSONObject: userInfo, options: [])
            return try JSONDecoder().decode(NotificationListModel.self, from: data)
        } catch {
            print("Decode error:", error)
            return nil
        }
    }
    
    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        return self.orientationLock
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

extension NotificationListModel: Decodable {
    public convenience init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let id = try container.decodeIfPresent(Int32.self, forKey: .id) ?? 0
        var typeVal: Int32 = 0
        if let tStr = try? container.decodeIfPresent(String.self, forKey: .type), let t = Int32(tStr) {
            typeVal = t
        } else {
            typeVal = try container.decodeIfPresent(Int32.self, forKey: .type) ?? 0
        }
        var contentIdVal: Int32 = 0
        if let cStr = try? container.decodeIfPresent(String.self, forKey: .contentId), let c = Int32(cStr) {
            contentIdVal = c
        } else {
            contentIdVal = try container.decodeIfPresent(Int32.self, forKey: .contentId) ?? 0
        }
        let title = try container.decodeIfPresent(String.self, forKey: .title)
        let time = try container.decodeIfPresent(String.self, forKey: .time)
        let date = try container.decodeIfPresent(String.self, forKey: .date)
        let description = try container.decodeIfPresent(String.self, forKey: .description)
        let groupCode = try container.decodeIfPresent(String.self, forKey: .groupCode)
        let isRead = try container.decodeIfPresent(Bool.self, forKey: .isRead) ?? true
        self.init(id: id, type: typeVal, contentId: contentIdVal, title: title, time: time, date: date, description: description, groupCode: groupCode, isRead: isRead)
    }
    
    enum CodingKeys: String, CodingKey {
        case id, type, contentId, title, time, date, description, groupCode, isRead
    }
}
