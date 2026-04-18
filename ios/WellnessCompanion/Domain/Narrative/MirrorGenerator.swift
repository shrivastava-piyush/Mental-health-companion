import Foundation

struct Mirror {
    let periodLabel: String
    let moodArc: [DailyMoodBucket]
    let avgValence: Double
    let topWords: [(String, Int)]
    let highlightTitle: String
    let highlightSnippet: String
    let callback: String?
    let totalEntries: Int
    let totalMoods: Int
}

final class MirrorGenerator {
    private let moodStore: MoodStore
    private let journalStore: JournalStore
    private let narrativeStore: NarrativeStore

    init(moodStore: MoodStore, journalStore: JournalStore, narrativeStore: NarrativeStore) {
        self.moodStore = moodStore
        self.journalStore = journalStore
        self.narrativeStore = narrativeStore
    }

    func generate(from: Int64, to: Int64, periodLabel: String) -> Mirror? {
        let entries = narrativeStore.recentEntriesWithKeywords(before: to, limit: 200)
            .filter { $0.createdAt >= from && $0.createdAt <= to }

        guard !entries.isEmpty else { return nil }

        let arc = moodStore.dailyAggregate(from: from, to: to)
        let avg = moodStore.avgValence(from: from, to: to) ?? 0.0

        let allText = entries.map(\.body).joined(separator: " ")
        let topWords = TextAnalyzer.topWords(allText, limit: 5)

        let highlight = entries.max(by: { $0.body.count < $1.body.count })

        let midpoint = from + (to - from) / 2
        let firstHalf = entries.filter { $0.createdAt < midpoint }
        let secondHalf = entries.filter { $0.createdAt >= midpoint }
        let callback = buildCallback(firstHalf: firstHalf, secondHalf: secondHalf)

        return Mirror(
            periodLabel: periodLabel,
            moodArc: arc,
            avgValence: avg,
            topWords: topWords,
            highlightTitle: highlight?.title ?? "",
            highlightSnippet: String((highlight?.body ?? "").prefix(160)).trimmingCharacters(in: .whitespaces),
            callback: callback,
            totalEntries: entries.count,
            totalMoods: arc.reduce(0) { $0 + $1.sampleCount }
        )
    }

    private func buildCallback(firstHalf: [KeywordedEntry], secondHalf: [KeywordedEntry]) -> String? {
        guard !firstHalf.isEmpty, !secondHalf.isEmpty else { return nil }
        let firstKws = Set(firstHalf.flatMap { $0.keywords.split(separator: ",").map(String.init) }.filter { !$0.isEmpty })
        let secondKws = Set(secondHalf.flatMap { $0.keywords.split(separator: ",").map(String.init) }.filter { !$0.isEmpty })
        let dropped = firstKws.subtracting(secondKws)
        let appeared = secondKws.subtracting(firstKws)

        if dropped.count >= 2 {
            return "You started the month writing about \(Array(dropped.prefix(2)).joined(separator: " and ")). By mid-month, you'd moved on."
        } else if appeared.count >= 2 {
            return "New themes appeared: \(Array(appeared.prefix(2)).joined(separator: " and "))."
        }
        return nil
    }
}
