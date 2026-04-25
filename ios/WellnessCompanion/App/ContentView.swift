import SwiftUI

struct ContentView: View {
    @EnvironmentObject private var container: AppContainer
    @State private var isUnlocked = false

    var body: some View {
        Group {
            if !container.isReady {
                // High-fidelity launch/splash screen
                VStack(spacing: 24) {
                    Image(systemName: "sparkles")
                        .font(.system(size: 40))
                        .foregroundStyle(Color.cyan.opacity(0.6))
                    
                    Text("SYNCHRONIZING...")
                        .font(.system(size: 10, weight: .black, design: .rounded))
                        .kerning(3.0)
                        .foregroundStyle(Color.white.opacity(0.3))
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color.liquidDeep)
                .ignoresSafeArea()
            } else if isUnlocked {
                AppRootView()
                    .environmentObject(container.backgroundManager) // Injecting here
                    .onAppear { container.atmosphereManager.start() }
            } else {
                BiometricGateView(onUnlocked: { 
                    withAnimation(.spring(response: 0.8, dampingFraction: 0.8)) {
                        isUnlocked = true 
                    }
                })
            }
        }
        .preferredColorScheme(.dark)
    }
}
