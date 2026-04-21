import SwiftUI

extension Color {
    // Timely-inspired Vibrant Palette
    static let liquidDeep = Color(hex: "0D0D12")
    static let liquidIndigo = Color(hex: "312E81")
    static let liquidTeal = Color(hex: "0D9488")
    static let liquidAmber = Color(hex: "D97706")
    static let liquidRose = Color(hex: "E11D48")
    
    // Semantic aliases
    static let wellnessBackground = liquidDeep
    static let wellnessSurface = Color.white.opacity(0.12)
    static let wellnessAccent = Color(hex: "6F9A74") // Sage from original spec
    static let wellnessSecondary = liquidTeal
    static let wellnessSecondaryText = Color.white.opacity(0.6)
    static let wellnessText = Color.white
    
    static let sagePastel = Color(hex: "D7E6D8")
    static let rosePastel = Color(hex: "FAE2E2")
    static let lavenderPastel = Color(hex: "E9E5F5")
    
    // UI Colors
    static let liquidText = Color.white
    static let liquidSecondaryText = Color.white.opacity(0.6)
    
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default: (a, r, g, b) = (1, 1, 1, 0)
        }
        self.init(.sRGB, red: Double(r) / 255, green: Double(g) / 255, blue: Double(b) / 255, opacity: Double(a) / 255)
    }
}

struct LiquidGlassModifier: ViewModifier {
    var radius: CGFloat = 32
    
    func body(content: Content) -> some View {
        content
            .padding(24)
            .background(.ultraThinMaterial.opacity(0.8))
            .clipShape(RoundedRectangle(cornerRadius: radius, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: radius, style: .continuous)
                    .stroke(.white.opacity(0.15), lineWidth: 1)
            )
            .shadow(color: .black.opacity(0.25), radius: 20, y: 10)
    }
}

extension View {
    func liquidGlass(radius: CGFloat = 32) -> some View {
        modifier(LiquidGlassModifier(radius: radius))
    }
    
    func boldTitle() -> some View {
        self.font(.system(size: 38, weight: .black, design: .rounded))
            .tracking(-1.0)
            .foregroundStyle(.white)
    }
    
    func sectionHeader() -> some View {
        self.font(.system(size: 11, weight: .black, design: .rounded))
            .textCase(.uppercase)
            .tracking(2.0)
            .foregroundStyle(.white.opacity(0.5))
    }
    
    func miniCaps() -> some View {
        self.font(.system(size: 10, weight: .black, design: .rounded))
            .textCase(.uppercase)
            .tracking(2.0)
    }
}

struct LiquidFont {
    static func hero() -> Font { .system(size: 34, weight: .bold, design: .rounded) }
    static func serifSub() -> Font { .system(size: 20, weight: .medium, design: .serif) }
}
