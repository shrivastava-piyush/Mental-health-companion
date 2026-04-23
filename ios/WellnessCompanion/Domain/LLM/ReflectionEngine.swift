import Foundation

final class ReflectionEngine {
    private let engine: LlamaEngine

    init(engine: LlamaEngine) {
        self.engine = engine
    }

    var isReady: Bool { engine.isReady }

    func reflect(title: String, body: String) async -> [String]? {
        guard engine.isReady, !body.isEmpty else { return nil }
        let raw = await engine.generate(
            system: LlmPrompts.socraticReflection,
            user: "Journal entry titled \"\(title)\":\n\n\(String(body.prefix(1200)))",
            maxTokens: 200, temperature: 0.8
        )
        let questions = raw.split(separator: "\n")
            .map { $0.trimmingCharacters(in: .whitespaces) }
            .filter { !$0.isEmpty && $0.hasSuffix("?") }
        return questions.isEmpty ? nil : Array(questions.prefix(3))
    }

    func reframe(title: String, body: String) async -> String? {
        guard engine.isReady, !body.isEmpty else { return nil }
        let raw = await engine.generate(
            system: LlmPrompts.reframeLens,
            user: "Journal entry titled \"\(title)\":\n\n\(String(body.prefix(1200)))",
            maxTokens: 120, temperature: 0.7
        )
        return raw.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? nil : raw.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    func narrateMirror(_ mirror: Mirror) async -> String? {
        guard engine.isReady else { return nil }
        let user = buildMirrorPrompt(mirror)
        let raw = await engine.generate(
            system: LlmPrompts.patternNarrator, user: user,
            maxTokens: 140, temperature: 0.75
        )
        return raw.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? nil : raw.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    func contextualStarter(moodLabel: String?, timeOfDay: String) async -> String? {
        guard engine.isReady else { return nil }
        var user = "Context:\n"
        if let mood = moodLabel { user += "- Recent mood: \(mood)\n" }
        user += "- Time of day: \(timeOfDay)\nGenerate a journaling prompt."
        let raw = await engine.generate(
            system: LlmPrompts.contextualStarter, user: user,
            maxTokens: 40, temperature: 0.9
        )
        let trimmed = raw.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmed.isEmpty ? nil : trimmed
    }

    func goDeeper(bodySoFar: String) async -> String? {
        guard engine.isReady, !bodySoFar.isEmpty else { return nil }
        let raw = await engine.generate(
            system: LlmPrompts.goDeeper,
            user: "Entry so far:\n\n\(String(bodySoFar.prefix(800)))\n\nWhat question would pull them deeper?",
            maxTokens: 50, temperature: 0.8
        )
        let trimmed = raw.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmed.hasSuffix("?") ? trimmed : nil
    }

    func suggestTitle(body: String) async -> String? {
        guard engine.isReady, !body.isEmpty else { return nil }
        let raw = await engine.generate(
            system: LlmPrompts.autoTitle,
            user: "Journal entry:\n\n\(String(body.prefix(600)))\n\nGenerate a title.",
            maxTokens: 20, temperature: 0.7
        )
        let trimmed = raw.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmed.isEmpty ? nil : String(trimmed.prefix(80))
    }

    func guidedQuestion(exchanges: [(String, String)], context: String = "") async -> String? {
        guard engine.isReady else { return nil }
        var user = ""
        if !context.isEmpty {
            user += "Broader Context for this session:\n\(context)\n\n"
        }
        
        if exchanges.isEmpty {
            user += "This is the start of a guided journaling session. Ask the opening question based on the context provided."
        } else {
            user += "Previous exchanges:\n"
            for (q, a) in exchanges {
                user += "Q: \(q)\nA: \(a)\n\n"
            }
            user += "Using the persona, acknowledge the user's last answer and ask the next creative, deep-dive follow-up question."
        }
        
        let raw = await engine.generate(
            system: LlmPrompts.guidedQuestion, user: user,
            maxTokens: 100, temperature: 0.8
        )
        let trimmed = raw.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmed.isEmpty ? nil : trimmed
    }

    func compileGuided(exchanges: [(String, String)]) async -> String? {
        guard engine.isReady, !exchanges.isEmpty else { return nil }
        var user = "Compile these exchanges into a journal entry:\n\n"
        for (q, a) in exchanges {
            user += "Q: \(q)\nA: \(a)\n\n"
        }
        let raw = await engine.generate(
            system: LlmPrompts.guidedCompile, user: user,
            maxTokens: 512, temperature: 0.65
        )
        let trimmed = raw.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmed.isEmpty ? nil : trimmed
    }

    private func buildMirrorPrompt(_ mirror: Mirror) -> String {
        var s = "Month summary:\n"
        s += "- \(mirror.totalEntries) journal entries, \(mirror.totalMoods) mood logs\n"
        let tone: String
        switch mirror.avgValence {
        case 1.0...: tone = "predominantly positive"
        case 0.3..<1.0: tone = "leaning positive"
        case -0.3..<0.3: tone = "mixed, near neutral"
        case -1.0 ..< -0.3: tone = "leaning difficult"
        default: tone = "predominantly heavy"
        }
        s += "- Overall emotional tone: \(tone) (valence \(String(format: "%.1f", mirror.avgValence)))\n"
        if !mirror.topWords.isEmpty {
            s += "- Words most on their mind: \(mirror.topWords.map { "\($0.0) (\($0.1)x)" }.joined(separator: ", "))\n"
        }
        if let cb = mirror.callback { s += "- Shift observed: \(cb)\n" }
        if !mirror.highlightSnippet.isEmpty {
            s += "- Most invested entry excerpt: \"\(String(mirror.highlightSnippet.prefix(200)))\"\n"
        }
        return s
    }
}
