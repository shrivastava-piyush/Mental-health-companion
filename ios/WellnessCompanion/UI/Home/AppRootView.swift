import SwiftUI

/// Environment key to trigger reflection or mood from children in-place (Fragment pattern).
private struct GlobalNavKey: EnvironmentKey {
    static let defaultValue: (NavTarget) -> Void = { _ in }
}

enum NavTarget {
    case home
    case reflection(id: Int64?, prompt: String?)
    case mood
}

extension EnvironmentValues {
    var globalNav: (NavTarget) -> Void {
        get { self[GlobalNavKey.self] }
        set { self[GlobalNavKey.self] = newValue }
    }
}

struct AppRootView: View {
    @EnvironmentObject private var container: AppContainer
    @State private var selectedTab: Int = 0
    @State private var scrollOffset: CGFloat = 0
    
    // Global Navigation State for "Fragments"
    @State private var activeFragment: NavTarget = .home

    var body: some View {
        ZStack(alignment: .bottom) {
            // 1. The Dynamic Living Background
            LiquidAura(scrollOffset: scrollOffset)
                .ignoresSafeArea()
            
            // 2. Content Stack (The "Fragments")
            ZStack {
                switch activeFragment {
                case .reflection(let id, let prompt):
                    JournalEditorScreen(entryId: id, initialBody: prompt) {
                        withAnimation(.spring(response: 0.6, dampingFraction: 0.8)) {
                            activeFragment = .home
                        }
                    }
                    .transition(.asymmetric(insertion: .move(edge: .trailing), removal: .move(edge: .leading)))
                    .zIndex(1)
                    
                case .mood:
                    MoodScreen {
                        withAnimation(.spring(response: 0.6, dampingFraction: 0.8)) {
                            activeFragment = .home
                        }
                    }
                    .transition(.asymmetric(insertion: .move(edge: .trailing), removal: .move(edge: .leading)))
                    .zIndex(1)
                    
                case .home:
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
            .environment(\.globalNav, { target in
                withAnimation(.spring()) {
                    activeFragment = target
                }
            })
            
            // 3. Floating Navigation (Only show if on main tabs)
            if case .home = activeFragment {
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
