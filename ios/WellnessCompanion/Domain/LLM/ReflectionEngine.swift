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
            user: "Entry: \(String(body.prefix(1000)))",
            maxTokens: 150, temperature: 0.9 
        )
        return raw.split(separator: "\n")
            .map { $0.trimmingCharacters(in: .whitespaces) }
            .filter { !$0.isEmpty && $0.hasSuffix("?") }
    }

    func guidedQuestion(exchanges: [(String, String)], context: String = "") async -> String? {
        guard engine.isReady else { return nil }
        var user = "Context: \(context)\n\n"
        if exchanges.isEmpty {
            user += "Ask the first sharp opening question."
        } else {
            let recent = exchanges.suffix(3)
            for (q, a) in recent { user += "Q: \(q)\nA: \(a)\n" }
            user += "\nAsk the next short, analytic question."
        }
        
        let raw = await engine.generate(
            system: LlmPrompts.guidedQuestion, user: user,
            maxTokens: 60, temperature: 0.8
        )
        return raw.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
    }

    func compileGuided(exchanges: [(String, String)]) async -> String? {
        guard engine.isReady, !exchanges.isEmpty else { return nil }
        var user = "Dialogue for distillation:\n"
        for (q, a) in exchanges { user += "Q: \(q)\nA: \(a)\n" }
        
        let raw = await engine.generate(
            system: LlmPrompts.guidedCompile, user: user,
            maxTokens: 500, temperature: 0.7
        )
        return raw.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
    }

    func narrateMirror(_ mirror: Mirror) async -> String? {
        guard engine.isReady else { return nil }
        let raw = await engine.generate(
            system: LlmPrompts.patternNarrator, 
            user: "Data: Valence \(mirror.avgValence), Keywords: \(mirror.topWords.map { $0.0 }.joined(separator: ", "))",
            maxTokens: 120, temperature: 0.5
        )
        return raw.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
    }

    func contextualStarter(moodLabel: String?, timeOfDay: String) async -> String? {
        guard engine.isReady else { return nil }
        let raw = await engine.generate(
            system: LlmPrompts.contextualStarter, 
            user: "Mood: \(moodLabel ?? "Neutral"), Time: \(timeOfDay)",
            maxTokens: 50, temperature: 1.0
        )
        return raw.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).trimmingCharacters(in: CharacterSet(charactersIn: "\""))
    }
    
    func suggestTitle(body: String) async -> String? {
        guard engine.isReady, !body.isEmpty else { return nil }
        let raw = await engine.generate(
            system: LlmPrompts.autoTitle,
            user: "Text: \(String(body.prefix(500)))",
            maxTokens: 20, temperature: 0.6
        )
        return raw.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).trimmingCharacters(in: CharacterSet(charactersIn: "\"."))
    }

    /// Intelligence: Synthesizes patterns across the last 3 journal entries.
    func synthesizeInsight(entries: [JournalEntry]) async -> String? {
        guard engine.isReady, entries.count >= 2 else { return nil }
        
        let combinedText = entries.map { "\($0.title): \($0.body)" }.joined(separator: "\n---\n")
        let raw = await engine.generate(
            system: "Persona: The Analytic Observer. Task: Find the 'hidden thread' connecting these 3 notes. What is the user not saying directly? Constraint: Under 40 words. Be sharp.",
            user: "Last 3 notes:\n\(combinedText)",
            maxTokens: 100, temperature: 0.75
        )
        return raw.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
    }
}
