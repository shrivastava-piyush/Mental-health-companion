import SwiftUI
import LocalAuthentication

struct BiometricGateView: View {
    let onUnlocked: () -> Void
    @State private var errorMessage = ""
    @State private var showError = false

    var body: some View {
        ZStack {
            Color.wellnessBackground.ignoresSafeArea()
            LiquidAura(scrollOffset: 0).ignoresSafeArea()
            
            VStack(spacing: 48) {
                Spacer()
                
                VStack(spacing: 24) {
                    ZStack {
                        Circle()
                            .fill(Color.wellnessAccent.opacity(0.1))
                            .frame(width: 160, height: 160)
                        
                        let icon = Image(systemName: "lock.shield.fill")
                            .font(.system(size: 80))
                            .foregroundStyle(Color.wellnessAccent)
                        
                        if #available(iOS 17.0, *) {
                            icon.symbolEffect(.bounce, value: showError)
                        } else {
                            icon
                        }
                    }
                    
                    VStack(spacing: 12) {
                        Text("Wellness Companion")
                            .font(.system(size: 32, weight: .bold, design: .rounded))
                            .foregroundStyle(Color.wellnessText)
                        
                        Text("Your private sanctuary.")
                            .font(.system(.subheadline, design: .serif))
                            .italic()
                            .foregroundStyle(Color.wellnessSecondaryText)
                    }
                }
                
                Spacer()

                VStack(spacing: 20) {
                    Button(action: authenticate) {
                        HStack(spacing: 12) {
                            Image(systemName: "faceid")
                                .font(.title3)
                            Text("Unlock with FaceID")
                        }
                        .font(.headline)
                        .foregroundStyle(.white)
                        .frame(maxWidth: CGFloat.infinity)
                        .frame(height: 64)
                        .background(Color.wellnessAccent, in: Capsule())
                        .shadow(color: Color.wellnessAccent.opacity(0.3), radius: 20, y: 10)
                    }
                    
                    if showError {
                        Text(errorMessage)
                            .font(.caption.bold())
                            .foregroundStyle(.red.opacity(0.8))
                            .transition(.move(edge: .top).combined(with: .opacity))
                    }
                }
                .padding(.horizontal, 40)
                
                Spacer()
                
                HStack(spacing: 8) {
                    Image(systemName: "checkmark.shield.fill")
                    Text("End-to-end local encryption")
                }
                .font(.system(size: 10, weight: .bold, design: .rounded))
                .textCase(.uppercase)
                .tracking(1.0)
                .foregroundStyle(Color.wellnessSecondaryText.opacity(0.6))
                .padding(.bottom, 20)
            }
            .padding(24)
        }
        .onAppear(perform: authenticate)
    }

    private func authenticate() {
        let context = LAContext()
        var error: NSError?
        if context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) {
            context.evaluatePolicy(.deviceOwnerAuthenticationWithBiometrics,
                                   localizedReason: "Unlock your wellness journal") { success, authError in
                DispatchQueue.main.async {
                    if success {
                        withAnimation(.spring(response: 0.6, dampingFraction: 0.8)) { onUnlocked() }
                    } else {
                        errorMessage = authError?.localizedDescription ?? "Authentication failed"
                        withAnimation { showError = true }
                    }
                }
            }
        } else {
            withAnimation { onUnlocked() }
        }
    }
}
