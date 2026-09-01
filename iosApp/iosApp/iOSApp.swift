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
            let codableNotif = try JSONDecoder().decode(CodableNotification.self, from: data)
            return codableNotif.toKotlinModel()
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

struct CodableNotification: Decodable {
    let id: Int32?
    let type: StringOrInt?
    let contentId: StringOrInt?
    let title: String?
    let time: String?
    let date: String?
    let description: String?
    let groupCode: String?
    let isRead: Bool?
    let pushType: StringOrInt?
    
    enum StringOrInt: Decodable {
        case string(String)
        case int(Int32)
        
        init(from decoder: Decoder) throws {
            let container = try decoder.singleValueContainer()
            if let stringValue = try? container.decode(String.self) {
                self = .string(stringValue)
            } else if let intValue = try? container.decode(Int32.self) {
                self = .int(intValue)
            } else {
                throw DecodingError.typeMismatch(StringOrInt.self, DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Expected String or Int32"))
            }
        }
        
        var int32Value: Int32 {
            switch self {
            case .string(let str):
                return Int32(str) ?? 0
            case .int(let val):
                return val
            }
        }
    }
    
    func toKotlinModel() -> NotificationListModel {
        return NotificationListModel(
            id: id ?? 0,
            type: type?.int32Value ?? 0,
            contentId: contentId?.int32Value ?? 0,
            title: title,
            time: time,
            date: date,
            description: description,
            groupCode: groupCode,
            isRead: isRead ?? true,
            pushType: 1
        )
    }
}
