import SwiftUI

struct AppRootView: View {
    @EnvironmentObject private var container: AppContainer
    @State private var selectedTab: Int = 0
    @State private var scrollOffset: CGFloat = 0
    
    var body: some View {
        ZStack {
            LiquidAura(scrollOffset: scrollOffset).ignoresSafeArea()
            
            VStack(spacing: 0) {
                // Morphing Content Area
                ZStack {
                    if selectedTab == 0 {
                        HomeScreen(scrollOffset: $scrollOffset)
                            .transition(.asymmetric(insertion: .opacity.combined(with: .scale(scale: 0.95)), removal: .opacity))
                    } else if selectedTab == 1 {
                        JournalListScreen()
                            .transition(.asymmetric(insertion: .opacity.combined(with: .move(edge: .trailing)), removal: .opacity))
                    } else {
                        InsightsScreen()
                            .transition(.asymmetric(insertion: .opacity.combined(with: .move(edge: .trailing)), removal: .opacity))
                    }
                }
                
                Spacer(minLength: 0)
                
                // Floating Pill Navigation
                LiquidNavBar(selectedTab: $selectedTab)
                    .padding(.bottom, 34)
            }
        }
    }
}

struct LiquidNavBar: View {
    @Binding var selectedTab: Int
    @Namespace private var navNamespace
    
    var body: some View {
        HStack(spacing: 0) {
            navItem(index: 0, icon: "circle.hexagongrid.fill", label: "Today")
            navItem(index: 1, icon: "text.quote", label: "Library")
            navItem(index: 2, icon: "bolt.ring.closed", label: "Pulse")
        }
        .padding(8)
        .background(.ultraThinMaterial.opacity(0.6))
        .clipShape(Capsule())
        .overlay(Capsule().stroke(.white.opacity(0.1), lineWidth: 1))
        .shadow(color: .black.opacity(0.3), radius: 20, y: 10)
    }
    
    private func navItem(index: Int, icon: String, label: String) -> some View {
        Button {
            withAnimation(.spring(response: 0.4, dampingFraction: 0.8)) {
                selectedTab = index
            }
            UIImpactFeedbackGenerator(style: .soft).impactOccurred()
        } label: {
            VStack(spacing: 4) {
                Image(systemName: icon)
                    .font(.system(size: 20, weight: .bold))
                Text(label)
                    .font(.system(size: 9, weight: .black, design: .rounded))
                    .textCase(.uppercase)
            }
            .frame(width: 80, height: 60)
            .foregroundStyle(selectedTab == index ? .white : .white.opacity(0.4))
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
