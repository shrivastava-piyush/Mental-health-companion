import SwiftUI

/// Environment key to trigger reflection from children in-place (Fragment pattern).
private struct OpenReflectionKey: EnvironmentKey {
    static let defaultValue: (Int64?, String?) -> Void = { _, _ in }
}

extension EnvironmentValues {
    var openReflection: (Int64?, String?) -> Void {
        get { self[OpenReflectionKey.self] }
        set { self[OpenReflectionKey.self] = newValue }
    }
}

struct AppRootView: View {
    @EnvironmentObject private var container: AppContainer
    @State private var selectedTab: Int = 0
    @State private var scrollOffset: CGFloat = 0
    
    // Global Navigation State for "Fragments"
    @State private var globalActiveReflectionId: Int64? = nil
    @State private var globalActiveReflectionPrompt: String? = nil

    var body: some View {
        ZStack(alignment: .bottom) {
            // 1. The Dynamic Living Background
            LiquidAura(scrollOffset: scrollOffset)
                .ignoresSafeArea()
            
            // 2. Content Stack (The "Fragments")
            ZStack {
                if let id = globalActiveReflectionId {
                    // Fragment: Reflection Editor (Replaces content in-place)
                    JournalEditorScreen(entryId: id > 0 ? id : nil, initialBody: globalActiveReflectionPrompt) {
                        withAnimation(.spring(response: 0.6, dampingFraction: 0.8)) {
                            globalActiveReflectionId = nil
                            globalActiveReflectionPrompt = nil
                        }
                    }
                    .transition(.asymmetric(insertion: .move(edge: .trailing), removal: .move(edge: .leading)))
                    .zIndex(1)
                } else {
                    // Main Tabs
                    Group {
                        switch selectedTab {
                        case 0: HomeScreen(scrollOffset: $scrollOffset)
                        case 1: JournalListScreen()
                        case 2: InsightsScreen()
                        default: HomeScreen(scrollOffset: $scrollOffset)
                        }
                    }
                    .transition(.asymmetric(insertion: .move(edge: .leading), removal: .move(edge: .trailing)))
                    .zIndex(0)
                }
            }
            .environment(\.openReflection, { id, prompt in
                withAnimation(.spring()) {
                    globalActiveReflectionId = id ?? -1
                    globalActiveReflectionPrompt = prompt
                }
            })
            
            // 3. Floating Navigation (Only show if not in editor)
            if globalActiveReflectionId == nil {
                pillNavigationBar
                    .padding(.bottom, 24)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
    }
    
    private var pillNavigationBar: some View {
        HStack(spacing: 0) {
            navItem(index: 0, icon: "sparkles", label: "Today")
            navItem(index: 1, icon: "book.closed.fill", label: "Library")
            navItem(index: 2, icon: "waveform.path.ecg", label: "Pulse")
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(.ultraThinMaterial)
        .clipShape(Capsule(style: .continuous))
        .overlay(Capsule(style: .continuous).stroke(.white.opacity(0.1), lineWidth: 0.5))
        .shadow(color: .black.opacity(0.3), radius: 20, y: 10)
    }
    
    @Namespace private var navNamespace
    
    private func navItem(index: Int, icon: String, label: String) -> some View {
        Button {
            withAnimation(.spring(response: 0.4, dampingFraction: 0.7)) {
                selectedTab = index
            }
        } label: {
            VStack(spacing: 4) {
                Image(systemName: icon)
                    .font(.system(size: 20, weight: .medium))
                Text(label)
                    .font(.system(size: 10, weight: .bold, design: .rounded))
            }
            .foregroundStyle(selectedTab == index ? .white : .white.opacity(0.4))
            .frame(width: 85, height: 50)
            .background {
                if selectedTab == index {
                    Capsule()
                        .fill(Color.white.opacity(0.15))
                        .matchedGeometryEffect(id: "navBg", in: navNamespace)
                }
            }
        }
        .buttonStyle(.plain)
    }
}
