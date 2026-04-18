import Foundation

enum TextAnalyzer {

    static func keywords(_ text: String, limit: Int = 20) -> [String] {
        guard !text.isEmpty else { return [] }
        let tokens = tokenize(text)
        var counts: [String: Int] = [:]
        for token in tokens { counts[token, default: 0] += 1 }
        return counts.sorted { $0.value > $1.value }.prefix(limit).map(\.key)
    }

    static func topWords(_ text: String, limit: Int = 5) -> [(String, Int)] {
        guard !text.isEmpty else { return [] }
        let tokens = tokenize(text)
        var counts: [String: Int] = [:]
        for token in tokens { counts[token, default: 0] += 1 }
        return counts.sorted { $0.value > $1.value }.prefix(limit).map { ($0.key, $0.value) }
    }

    static func similarity(_ a: [String], _ b: [String]) -> Float {
        guard !a.isEmpty, !b.isEmpty else { return 0 }
        let sa = Set(a), sb = Set(b)
        let intersection = sa.intersection(sb).count
        let union = sa.union(sb).count
        return union == 0 ? 0 : Float(intersection) / Float(union)
    }

    private static func tokenize(_ text: String) -> [String] {
        text.lowercased()
            .components(separatedBy: splitPattern)
            .map { $0.trimmingCharacters(in: .punctuationCharacters) }
            .filter { $0.count > 2 && !stopWords.contains($0) }
            .map { naiveStem($0) }
    }

    private static func naiveStem(_ word: String) -> String {
        var w = word
        for (suffix, replacement) in suffixRules {
            if w.hasSuffix(suffix), w.count - suffix.count + replacement.count >= 3 {
                w = String(w.dropLast(suffix.count)) + replacement
                break
            }
        }
        return w
    }

    private static let splitPattern = CharacterSet.whitespacesAndNewlines
        .union(CharacterSet(charactersIn: ",.;:!?()[]{}\"'—–-"))

    private static let suffixRules: [(String, String)] = [
        ("ying", "y"), ("ies", "y"), ("ious", "y"), ("ness", ""), ("ment", ""),
        ("tion", "t"), ("sion", "s"), ("ling", ""), ("ting", "t"), ("ning", "n"),
        ("ring", "r"), ("king", "k"), ("ping", "p"), ("bing", "b"), ("ding", "d"),
        ("ging", "g"), ("ming", "m"), ("ving", "ve"), ("zing", "z"), ("ing", ""),
        ("ful", ""), ("ous", ""), ("ive", ""), ("able", ""), ("ible", ""),
        ("ated", "ate"), ("ened", "en"), ("ised", "ise"), ("ized", "ize"),
        ("lled", "ll"), ("tted", "t"), ("pped", "p"), ("bbed", "b"),
        ("dded", "d"), ("gged", "g"), ("mmed", "m"), ("nned", "n"),
        ("rred", "r"), ("ssed", "ss"), ("ed", ""), ("ly", ""), ("er", ""),
        ("est", ""), ("'s", ""), ("s", ""),
    ]

    private static let stopWords: Set<String> = [
        "the", "and", "for", "are", "but", "not", "you", "all", "can", "had",
        "her", "was", "one", "our", "out", "has", "have", "been", "some",
        "them", "than", "its", "over", "such", "that", "this", "with", "will",
        "each", "make", "like", "from", "just", "into", "about", "could",
        "would", "there", "their", "what", "which", "when", "who", "how",
        "were", "your", "more", "also", "did", "these", "then", "those",
        "very", "after", "before", "being", "does", "doing", "during", "got",
        "get", "getting", "going", "gone", "way", "much", "really", "thing",
        "things", "think", "know", "even", "back", "through", "well", "still",
        "where", "too", "only", "she", "him", "his", "they", "most", "other",
        "don", "didn", "isn", "wasn", "doesn", "won", "shouldn", "couldn",
        "wouldn", "haven", "hasn", "aren", "weren", "let", "may", "might",
        "shall", "should", "need", "use", "say", "said", "because", "same",
        "own", "here", "while", "both", "between", "any", "few", "many",
        "now", "today", "yesterday", "tomorrow", "bit",
    ]
}
