import SwiftUI

enum MoodCategory {
    case positive, neutral, negative
    
    init(valence: Int) {
        if valence > 20 { self = .positive }
        else if valence < -20 { self = .negative }
        else { self = .neutral }
    }
}

struct WellnessContentProvider {
    static func quote(for category: MoodCategory) -> (String, String) {
        let positive = [
            ("Happiness is not something readymade. It comes from your own actions.", "Dalai Lama"),
            ("The most important thing is to enjoy your life—to be happy—it's all that matters.", "Audrey Hepburn"),
            ("Let your joy be in your journey, not in some distant goal.", "Tim Cook")
        ]
        let neutral = [
            ("Focus on the present moment. Breathe.", "Zen Proverb"),
            ("Knowing yourself is the beginning of all wisdom.", "Aristotle"),
            ("Be still. The world will uncover itself to you.", "Franz Kafka")
        ]
        let negative = [
            ("This too shall pass. Be gentle with yourself.", "Persian Proverb"),
            ("The wound is the place where the Light enters you.", "Rumi"),
            ("Out of difficulties grow miracles.", "Jean de la Bruyère")
        ]
        
        switch category {
        case .positive: return positive.randomElement()!
        case .neutral: return neutral.randomElement()!
        case .negative: return negative.randomElement()!
        }
    }
}
