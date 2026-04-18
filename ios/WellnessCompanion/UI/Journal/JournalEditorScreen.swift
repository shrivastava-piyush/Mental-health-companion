import SwiftUI

struct JournalEditorScreen: View {
    @EnvironmentObject private var container: AppContainer
    @Environment(\.dismiss) private var dismiss
    let entryId: Int64?

    @State private var title = ""
    @State private var body_ = ""
    @State private var savedId: Int64 = 0
    @State private var coldOpen: ColdOpen? = nil
    @State private var starterPrompt = ""

    @State private var reflectionQuestions: [String] = []
    @State private var reframeText = ""
    @State private var goDeeperNudge = ""
    @State private var titleSuggestion = ""
    @State private var isReflecting = false
    @State private var isNudging = false
    @State private var isSuggestingTitle = false

    @State private var guidedMode = false
    @State private var guidedExchanges: [(String, String)] = []
    @State private var guidedCurrentQuestion = ""
    @State private var guidedAnswer = ""
    @State private var guidedGenerating = false

    private var hasLlm: Bool { container.reflectionEngine != nil }
    private var wordCount: Int { body_.split(separator: " ").count }

    var body: some View {
        if guidedMode {
            guidedView
        } else {
            editorView
        }
    }

    private var editorView: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    if let co = coldOpen {
                        ColdOpenCardView(coldOpen: co) { coldOpen = nil }
                    }

                    if hasLlm && entryId == nil && body_.isEmpty {
                        Button {
                            guidedMode = true
                            startGuided()
                        } label: {
                            Label("Guide me", systemImage: "sparkles")
                        }
                        .buttonStyle(.bordered)
                    }

                    TextField("Title", text: $title)
                        .font(.title2)
                        .fontWeight(.medium)

                    let placeholder = starterPrompt.isEmpty ? "Write freely…" : starterPrompt
                    ZStack(alignment: .topLeading) {
                        if body_.isEmpty {
                            Text(placeholder)
                                .foregroundStyle(.secondary)
                                .italic()
                                .padding(.top, 8)
                        }
                        TextEditor(text: $body_)
                            .frame(minHeight: 200)
                            .scrollContentBackground(.hidden)
                    }

                    if hasLlm && wordCount >= 15 {
                        HStack(spacing: 12) {
                            if goDeeperNudge.isEmpty && !isNudging {
                                Button("Go deeper") { requestGoDeeper() }
                                    .buttonStyle(.bordered)
                            }
                            if title.isEmpty && titleSuggestion.isEmpty && !isSuggestingTitle {
                                Button("Suggest title") { requestTitleSuggestion() }
                                    .buttonStyle(.bordered)
                            }
                            if isNudging || isSuggestingTitle {
                                ProgressView().controlSize(.small)
                            }
                        }
                    }

                    if !goDeeperNudge.isEmpty {
                        HStack(alignment: .top) {
                            Text(goDeeperNudge)
                                .italic()
                                .foregroundStyle(.secondary)
                            Spacer()
                            Button { goDeeperNudge = "" } label: {
                                Image(systemName: "xmark")
                            }
                        }
                        .wellnessCard()
                    }

                    if !titleSuggestion.isEmpty {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("\u{201C}\(titleSuggestion)\u{201D}")
                                .font(.headline)
                            HStack {
                                Button("Use this") { title = titleSuggestion; titleSuggestion = "" }
                                Button("No thanks") { titleSuggestion = "" }
                            }
                            .buttonStyle(.bordered)
                        }
                        .wellnessCard()
                    }

                    if !reflectionQuestions.isEmpty {
                        VStack(alignment: .leading, spacing: 10) {
                            Text("Sit with this")
                                .font(.headline)
                            ForEach(reflectionQuestions, id: \.self) { q in
                                Text(q).italic().foregroundStyle(.secondary)
                            }
                        }
                        .wellnessCard()
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                    }

                    if !reflectionQuestions.isEmpty && reframeText.isEmpty {
                        Button("See it from another angle") { requestReframe() }
                            .buttonStyle(.bordered)
                    }

                    if !reframeText.isEmpty {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Another angle").font(.headline)
                            Text(reframeText).italic().foregroundStyle(.secondary)
                        }
                        .wellnessCard()
                    }

                    if isReflecting { ProgressView().controlSize(.small) }
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 12)
            }
            .navigationTitle(entryId == nil ? "New entry" : "Edit entry")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Save") { save(); dismiss() }
                }
                ToolbarItem(placement: .primaryAction) {
                    if savedId > 0 {
                        Button(role: .destructive) {
                            container.journalStore.delete(id: savedId)
                            dismiss()
                        } label: {
                            Image(systemName: "trash")
                        }
                    }
                }
            }
            .onAppear(perform: load)
        }
    }

    private var guidedView: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("Answer what feels true. Short is fine.")
                        .foregroundStyle(.secondary)

                    ForEach(Array(guidedExchanges.enumerated()), id: \.offset) { _, pair in
                        VStack(alignment: .leading, spacing: 6) {
                            Text(pair.0).italic().foregroundStyle(.secondary)
                            Text(pair.1)
                        }
                        .wellnessCard()
                    }

                    if guidedGenerating {
                        HStack {
                            ProgressView().controlSize(.small)
                            Text(guidedExchanges.count >= 5 ? "Weaving your entry…" : "Thinking…")
                                .foregroundStyle(.secondary)
                        }
                    }

                    if !guidedGenerating && !guidedCurrentQuestion.isEmpty {
                        Text(guidedCurrentQuestion)
                            .font(.headline)
                            .italic()

                        TextField("Your answer…", text: $guidedAnswer, axis: .vertical)
                            .lineLimit(2...5)
                            .textFieldStyle(.roundedBorder)

                        HStack {
                            Button("Next") { submitGuidedAnswer() }
                                .buttonStyle(.borderedProminent)
                                .disabled(guidedAnswer.trimmingCharacters(in: .whitespaces).isEmpty)

                            if !guidedExchanges.isEmpty {
                                Button("That's enough") { finishGuidedEarly() }
                                    .buttonStyle(.bordered)
                            }
                        }
                    }
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 12)
            }
            .navigationTitle("Guided entry")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { guidedMode = false }
                }
            }
        }
    }

    // MARK: - Actions

    private func load() {
        if let id = entryId, let entry = container.journalStore.fetchById(id) {
            title = entry.title
            body_ = entry.body
            savedId = entry.id
        } else {
            Task {
                coldOpen = container.coldOpenGenerator.generate()
                if let engine = container.reflectionEngine {
                    let labels = container.moodStore.recentLabels(limit: 1)
                    let hour = Calendar.current.component(.hour, from: Date())
                    let tod = hour < 6 ? "late night" : hour < 12 ? "morning" : hour < 17 ? "afternoon" : hour < 21 ? "evening" : "night"
                    starterPrompt = await engine.contextualStarter(moodLabel: labels.first, timeOfDay: tod) ?? ""
                }
            }
        }
    }

    private func save() {
        guard !title.isEmpty || !body_.isEmpty else { return }
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let entry = JournalEntry(
            id: savedId, createdAt: savedId == 0 ? now : now,
            updatedAt: now, title: title.isEmpty ? "Untitled" : title,
            body: body_, wordCount: wordCount
        )
        let id = container.journalStore.save(entry)
        savedId = id
        container.threadDetector.process(entryId: id, title: title, body: body_)
        triggerReflection()
    }

    private func triggerReflection() {
        guard let engine = container.reflectionEngine, !body_.isEmpty else { return }
        isReflecting = true
        Task {
            if let qs = await engine.reflect(title: title.isEmpty ? "Untitled" : title, body: body_) {
                await MainActor.run { reflectionQuestions = qs; isReflecting = false }
            } else {
                await MainActor.run { isReflecting = false }
            }
        }
    }

    private func requestReframe() {
        guard let engine = container.reflectionEngine else { return }
        Task {
            if let text = await engine.reframe(title: title, body: body_) {
                await MainActor.run { reframeText = text }
            }
        }
    }

    private func requestGoDeeper() {
        guard let engine = container.reflectionEngine else { return }
        isNudging = true
        Task {
            let nudge = await engine.goDeeper(bodySoFar: body_)
            await MainActor.run { goDeeperNudge = nudge ?? ""; isNudging = false }
        }
    }

    private func requestTitleSuggestion() {
        guard let engine = container.reflectionEngine else { return }
        isSuggestingTitle = true
        Task {
            let suggestion = await engine.suggestTitle(body: body_)
            await MainActor.run { titleSuggestion = suggestion ?? ""; isSuggestingTitle = false }
        }
    }

    // MARK: - Guided

    private func startGuided() {
        guidedGenerating = true
        Task {
            guard let engine = container.reflectionEngine else { return }
            let q = await engine.guidedQuestion(exchanges: []) ?? "What's on your mind right now?"
            await MainActor.run { guidedCurrentQuestion = q; guidedGenerating = false }
        }
    }

    private func submitGuidedAnswer() {
        let answer = guidedAnswer.trimmingCharacters(in: .whitespaces)
        guard !answer.isEmpty else { return }
        guidedExchanges.append((guidedCurrentQuestion, answer))
        guidedAnswer = ""

        if guidedExchanges.count >= 5 {
            compileGuided()
        } else {
            guidedGenerating = true
            Task {
                guard let engine = container.reflectionEngine else { return }
                let q = await engine.guidedQuestion(exchanges: guidedExchanges) ?? "What else comes to mind?"
                await MainActor.run { guidedCurrentQuestion = q; guidedGenerating = false }
            }
        }
    }

    private func finishGuidedEarly() {
        guard !guidedExchanges.isEmpty else { guidedMode = false; return }
        compileGuided()
    }

    private func compileGuided() {
        guidedGenerating = true
        Task {
            guard let engine = container.reflectionEngine else { return }
            let compiled = await engine.compileGuided(exchanges: guidedExchanges)
                ?? guidedExchanges.map(\.1).joined(separator: "\n\n")
            let suggestedTitle = await engine.suggestTitle(body: compiled)
            await MainActor.run {
                body_ = compiled
                if let t = suggestedTitle { title = t }
                guidedMode = false
                guidedGenerating = false
            }
        }
    }
}

struct ColdOpenCardView: View {
    let coldOpen: ColdOpen
    let onDismiss: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Spacer()
                Button(action: onDismiss) {
                    Image(systemName: "xmark")
                        .foregroundStyle(.secondary)
                }
            }
            Text(coldOpen.reason)
                .font(.caption)
                .fontWeight(.medium)
            Text("\u{201C}\(coldOpen.snippet)\u{201D}")
                .italic()
            Text("— \(coldOpen.title)")
                .font(.caption)
                .foregroundStyle(.secondary)
        }
        .padding(16)
        .background(Color.lavenderMist.opacity(0.2), in: RoundedRectangle(cornerRadius: 18))
    }
}
