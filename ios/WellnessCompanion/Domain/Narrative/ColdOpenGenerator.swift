import Foundation

struct ColdOpen {
    let entryId: Int64
    let title: String
    let snippet: String
    let reason: String
}

final class ColdOpenGenerator {
    private let narrativeStore: NarrativeStore
    private let moodStore: MoodStore

    init(narrativeStore: NarrativeStore, moodStore: MoodStore) {
        self.narrativeStore = narrativeStore
        self.moodStore = moodStore
    }

    func generate() -> ColdOpen? {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let timeEchoes = narrativeStore.entriesNearSameDayOfMonth(now: now)
        let keyworded = narrativeStore.recentEntriesWithKeywords(before: now, limit: 40)

        guard !timeEchoes.isEmpty || !keyworded.isEmpty else { return nil }

        let recentMoods = moodStore.recentLabels(limit: 3)
        let todayKeywords = recentMoods.flatMap { TextAnalyzer.keywords($0, limit: 3) }
            .removingDuplicates()

        var scored: [(Float, ColdOpen)] = []

        for entry in timeEchoes {
            let ageMonths = max(1, (now - entry.createdAt) / (30 * 86_400_000))
            let snippet = firstSentence(entry.body)
            let label = ageMonths == 1 ? "1 month ago you wrote:" : "\(ageMonths) months ago you wrote:"
            scored.append((1.0 / Float(ageMonths), ColdOpen(
                entryId: entry.id, title: entry.title, snippet: snippet, reason: label
            )))
        }

        if !todayKeywords.isEmpty {
            for k in keyworded {
                let entryKws = k.keywords.split(separator: ",").map(String.init).filter { !$0.isEmpty }
                let sim = TextAnalyzer.similarity(todayKeywords, entryKws)
                if sim > 0.15 {
                    let snippet = firstSentence(k.body)
                    scored.append((sim, ColdOpen(
                        entryId: k.id, title: k.title, snippet: snippet,
                        reason: "You were thinking about similar things when you wrote:"
                    )))
                }
            }
        }

        return scored.sorted(by: { $0.0 > $1.0 }).first?.1
    }

    private func firstSentence(_ body: String) -> String {
        if let end = body.firstIndex(where: { ".!?".contains($0) }),
           body.distance(from: body.startIndex, to: end) < 200 {
            return String(body[...end]).trimmingCharacters(in: .whitespaces)
        }
        return String(body.prefix(140)).trimmingCharacters(in: .whitespaces)
    }
}

private extension Array where Element: Hashable {
    func removingDuplicates() -> [Element] {
        var seen: Set<Element> = []
        return filter { seen.insert($0).inserted }
    }
}
