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
            ("Radiate boundless love.", "Buddha"),
            ("The sun shines within.", "John Muir"),
            ("Joy is the journey.", "Tim Cook"),
            ("Be the light.", "Unknown"),
            ("Choose happiness now.", "Dalai Lama")
        ]
        let neutral = [
            ("Breathe. You are here.", "Zen"),
            ("Be still. Know thyself.", "Aristotle"),
            ("Nature does not hurry.", "Lao Tzu"),
            ("Focus on this breath.", "Unknown"),
            ("The present is enough.", "Thich Nhat Hanh")
        ]
        let negative = [
            ("This too shall pass.", "Persian"),
            ("The wound is the light.", "Rumi"),
            ("Softly navigate the storm.", "Unknown"),
            ("You are enough.", "Ram Dass"),
            ("Peace is an anchor.", "Unknown")
        ]
        
        switch category {
        case .positive: return positive.randomElement()!
        case .neutral: return neutral.randomElement()!
        case .negative: return negative.randomElement()!
        }
    }
    
    // Curated high-fidelity atmospheric images
    static let libraryHero = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80" // Ocean
    static let insightsHero = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80" // Mountains
    static let attribution = "Photography by Unsplash (Public Domain / CC0)"
}
