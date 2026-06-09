import SwiftUI
import Shared

@main
struct iOSApp: App {
    
    init() {
        KoinInitializer.shared.initialize()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
