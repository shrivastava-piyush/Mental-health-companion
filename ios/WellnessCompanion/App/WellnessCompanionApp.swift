import SwiftUI

@main
struct WellnessCompanionApp: App {
    @StateObject private var container = AppContainer()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(container)
                .preferredColorScheme(.dark) // Liquid Glass is designed for a deep, immersive dark aesthetic
        }
    }
}
