import SwiftUI

struct ContentView: View {
    @EnvironmentObject private var container: AppContainer
    @State private var isUnlocked = false

    var body: some View {
        if isUnlocked {
            MainTabView()
        } else {
            BiometricGateView(onUnlocked: { isUnlocked = true })
        }
    }
}
