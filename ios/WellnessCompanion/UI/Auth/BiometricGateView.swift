import SwiftUI
import LocalAuthentication

struct BiometricGateView: View {
    let onUnlocked: () -> Void
    @State private var errorMessage = ""
    @State private var showError = false

    var body: some View {
        VStack(spacing: 24) {
            Spacer()
            Image(systemName: "lock.shield")
                .font(.system(size: 64))
                .foregroundStyle(.secondary)
            Text("Wellness Companion")
                .font(.title)
                .fontWeight(.medium)
            Text("Your data stays on this device.")
                .font(.subheadline)
                .foregroundStyle(.secondary)

            Button(action: authenticate) {
                Label("Unlock", systemImage: "faceid")
                    .font(.headline)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(.tint, in: RoundedRectangle(cornerRadius: 16))
                    .foregroundStyle(.white)
            }
            .padding(.horizontal, 40)

            if showError {
                Text(errorMessage)
                    .font(.caption)
                    .foregroundStyle(.red)
            }
            Spacer()
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
                        onUnlocked()
                    } else {
                        errorMessage = authError?.localizedDescription ?? "Authentication failed"
                        showError = true
                    }
                }
            }
        } else {
            onUnlocked()
        }
    }
}
