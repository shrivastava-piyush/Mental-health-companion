import SwiftUI

/// A high-fidelity animated quote view that presents text with a "Liquid Glass" aesthetic.
/// Features a sequential word reveal and a soft shimmer effect.
struct AnimatedQuoteView: View {
    let quote: String
    let author: String
    let category: MoodCategory
    
    @State private var revealProgress: Double = 0
    @State private var shimmerPhase: Double = 0
    
    private var words: [String] {
        quote.components(separatedBy: " ")
    }
    
    var body: some View {
        VStack(spacing: 32) {
            // Sequential Word Reveal
            FlowLayout(spacing: 12) {
                ForEach(Array(words.enumerated()), id: \.offset) { index, word in
                    Text(word)
                        .font(.system(size: 36, weight: .bold, design: .serif))
                        .italic()
                        .foregroundStyle(textGradient)
                        .opacity(wordOpacity(for: index))
                        .blur(radius: wordBlur(for: index))
                        .scaleEffect(wordScale(for: index))
                }
            }
            .multilineTextAlignment(.center)
            .padding(.horizontal, 28)
            
            // Author Reveal
            Text(author.uppercased())
                .font(.system(size: 11, weight: .black, design: .rounded))
                .tracking(4.0)
                .foregroundStyle(.white.opacity(0.4))
                .opacity(revealProgress > 0.8 ? 1 : 0)
                .offset(y: revealProgress > 0.8 ? 0 : 10)
        }
        .onAppear {
            withAnimation(.easeOut(duration: 2.5)) {
                revealProgress = 1.0
            }
            withAnimation(.linear(duration: 4.0).repeatForever(autoreverses: false)) {
                shimmerPhase = 1.0
            }
        }
        // Re-animate when quote changes
        .id(quote)
    }
    
    // MARK: - Calculations
    
    private var textGradient: LinearGradient {
        let baseColor: Color = {
            switch category {
            case .positive: return .white
            case .neutral: return Color.white.opacity(0.9)
            case .negative: return Color.sagePastel.opacity(0.8)
            }
        }()
        
        return LinearGradient(
            colors: [baseColor, baseColor.opacity(0.7), baseColor],
            startPoint: .init(x: shimmerPhase - 0.4, y: 0.5),
            endPoint: .init(x: shimmerPhase + 0.4, y: 0.5)
        )
    }
    
    private func wordOpacity(for index: Int) -> Double {
        let step = 1.0 / Double(words.count)
        let start = Double(index) * step
        return max(0, min(1, (revealProgress - start) / step))
    }
    
    private func wordBlur(for index: Int) -> CGFloat {
        let opacity = wordOpacity(for: index)
        return (1.0 - opacity) * 10
    }
    
    private func wordScale(for index: Int) -> CGFloat {
        let opacity = wordOpacity(for: index)
        return 0.95 + (opacity * 0.05)
    }
}
