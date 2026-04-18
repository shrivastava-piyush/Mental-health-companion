import Foundation

final class ThreadDetector {
    private let store: NarrativeStore
    private let matchThreshold: Float = 0.20
    private let createThreshold: Float = 0.25

    init(store: NarrativeStore) {
        self.store = store
    }

    func process(entryId: Int64, title: String, body: String) {
        let kws = TextAnalyzer.keywords(title + " " + body, limit: 20)
        guard !kws.isEmpty else { return }
        store.insertKeywords(entryId: entryId, keywords: kws.joined(separator: ","))

        let threads = store.allThreads()
        var matched = false

        for var thread in threads {
            let threadKws = thread.keywords.split(separator: ",").map(String.init)
            let sim = TextAnalyzer.similarity(kws, threadKws)
            if sim >= matchThreshold {
                store.insertRef(threadId: thread.id, entryId: entryId)
                thread.entryCount += 1
                thread.updatedAt = Int64(Date().timeIntervalSince1970 * 1000)
                if thread.status == "dormant" { thread.status = "ongoing" }
                let merged = Set(threadKws + kws.prefix(5))
                thread.keywords = merged.joined(separator: ",")
                store.updateThread(thread)
                matched = true
            }
        }

        if !matched {
            tryCreateThread(entryId: entryId, kws: kws)
        }

        markDormant(threads: threads)
    }

    private func tryCreateThread(entryId: Int64, kws: [String]) {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let recent = store.recentEntriesWithKeywords(before: now + 1, limit: 30)

        for entry in recent where entry.id != entryId {
            let entryKws = entry.keywords.split(separator: ",").map(String.init)
            let sim = TextAnalyzer.similarity(kws, entryKws)
            if sim >= createThreshold {
                let shared = Set(kws).intersection(Set(entryKws))
                let label = shared.prefix(3).joined(separator: ", ")
                    .localizedCapitalized

                let threadId = store.insertThread(NarrativeThread(
                    id: 0, createdAt: now, updatedAt: now,
                    label: label.isEmpty ? "New thread" : label,
                    keywords: kws.joined(separator: ","),
                    status: "ongoing", entryCount: 2
                ))
                store.insertRef(threadId: threadId, entryId: entryId)
                store.insertRef(threadId: threadId, entryId: entry.id)
                return
            }
        }
    }

    private func markDormant(threads: [NarrativeThread]) {
        let cutoff = Int64(Date().timeIntervalSince1970 * 1000) - 30 * 86_400_000
        for var thread in threads {
            if thread.status == "ongoing" && thread.updatedAt < cutoff {
                thread.status = "dormant"
                store.updateThread(thread)
            }
        }
    }
}
