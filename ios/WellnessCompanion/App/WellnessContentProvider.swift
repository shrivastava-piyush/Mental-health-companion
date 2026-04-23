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
            ("Let your joy be in your journey, not in some distant goal.", "Tim Cook"),
            ("The sun shines not on us but in us.", "John Muir"),
            ("Radiate boundless love towards the entire world.", "Buddha")
        ]
        let neutral = [
            ("Focus on the present moment. Breathe.", "Zen Proverb"),
            ("Knowing yourself is the beginning of all wisdom.", "Aristotle"),
            ("Be still. The world will uncover itself to you.", "Franz Kafka"),
            ("Nature does not hurry, yet everything is accomplished.", "Lao Tzu"),
            ("The present moment is filled with joy and happiness. If you are attentive, you will see it.", "Thich Nhat Hanh")
        ]
        let negative = [
            ("This too shall pass. Be gentle with yourself.", "Persian Proverb"),
            ("The wound is the place where the Light enters you.", "Rumi"),
            ("Out of difficulties grow miracles.", "Jean de la Bruyère"),
            ("You are loved just as you are.", "Ram Dass"),
            ("Softly, navigate the storm. Your heart is an anchor.", "Unknown")
        ]
        
        switch category {
        case .positive: return positive.randomElement()!
        case .neutral: return neutral.randomElement()!
        case .negative: return negative.randomElement()!
        }
    }
}
