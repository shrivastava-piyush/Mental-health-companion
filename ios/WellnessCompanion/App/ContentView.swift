import SwiftUI

struct ContentView: View {
    @EnvironmentObject private var container: AppContainer
    @State private var isUnlocked = false

    var body: some View {
        Group {
            if isUnlocked {
                AppRootView()
                    .onAppear { container.atmosphereManager.start() }
            } else {
                BiometricGateView(onUnlocked: { 
                    withAnimation(.spring(response: 0.8, dampingFraction: 0.8)) {
                        isUnlocked = true 
                    }
                })
            }
        }
    }
}
