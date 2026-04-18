import SwiftUI

extension Color {
    static let sageGreen = Color(red: 0.69, green: 0.78, blue: 0.71)
    static let roseBlush = Color(red: 0.91, green: 0.76, blue: 0.76)
    static let lavenderMist = Color(red: 0.80, green: 0.76, blue: 0.87)
    static let warmCream = Color(red: 0.98, green: 0.96, blue: 0.93)
    static let darkSurface = Color(red: 0.11, green: 0.11, blue: 0.13)
    static let darkCard = Color(red: 0.16, green: 0.16, blue: 0.18)
}

extension ShapeStyle where Self == Color {
    static var cardBackground: Color {
        Color(.systemBackground).opacity(0.95)
    }
}

struct WellnessCardStyle: ViewModifier {
    func body(content: Content) -> some View {
        content
            .padding(16)
            .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 22))
    }
}

extension View {
    func wellnessCard() -> some View {
        modifier(WellnessCardStyle())
    }
}
