import SwiftUI

struct JournalEditorScreen: View {
    @EnvironmentObject private var container: AppContainer
    @Environment(\.dismiss) private var dismiss
    let entryId: Int64?
    let initialBody: String?
    
    init(entryId: Int64?, initialBody: String? = nil) {
        self.entryId = entryId
        self.initialBody = initialBody
    }

    @State private var title = ""
    @State private var body_ = ""
    @State private var savedId: Int64 = 0
    @State private var originalCreatedAt: Int64? = nil
    
    @FocusState private var isFocused: Bool
    @State private var guidedMode = false

    var body: some View {
        ZStack {
            LiquidAura(scrollOffset: 0).ignoresSafeArea()
            
            if guidedMode {
                GuidedEntryView(onComplete: { compiledBody, suggestedTitle in
                    self.body_ = compiledBody
                    if let t = suggestedTitle { self.title = t }
                    withAnimation(.spring()) { guidedMode = false }
                }, onCancel: {
                    withAnimation(.spring()) { guidedMode = false }
                })
            } else {
                editorContent
            }
        }
        .onAppear {
            load()
            if let initial = initialBody, body_.isEmpty {
                body_ = initial
            }
        }
    }
    
    private var editorContent: some View {
        VStack(spacing: 0) {
            HStack {
                Button { save(); dismiss() } label: {
                    Text("Done").bold().foregroundStyle(.white)
                }
                Spacer()
                Text("REFLECTION").miniCaps().foregroundStyle(Color.white.opacity(0.4))
                Spacer()
                if savedId > 0 {
                    Button(role: .destructive) {
                        container.journalStore.delete(id: savedId)
                        dismiss()
                    } label: {
                        Image(systemName: "trash").foregroundStyle(Color.white.opacity(0.4))
                    }
                } else {
                    Spacer().frame(width: 40)
                }
            }
            .padding(.horizontal, 28)
            .padding(.top, 20)
            
            ScrollView {
                VStack(alignment: .leading, spacing: 32) {
                    if entryId == nil && body_.isEmpty {
                        guidedNudge
                    }
                    
                    VStack(alignment: .leading, spacing: 20) {
                        TextField("UNTITLED", text: $title)
                            .font(.system(size: 28, weight: .black, design: .rounded))
                            .foregroundStyle(Color.white)
                        
                        TextEditor(text: $body_)
                            .font(.system(size: 20, weight: .medium, design: .serif))
                            .lineSpacing(10)
                            .frame(minHeight: 500)
                            .scrollContentBackground(.hidden)
                            .focused($isFocused)
                            .foregroundStyle(Color.white)
                            .overlay(alignment: .topLeading) {
                                if body_.isEmpty {
                                    Text("Speak your truth…")
                                        .font(.system(size: 20, weight: .medium, design: .serif))
                                        .foregroundStyle(Color.white.opacity(0.2))
                                        .padding(.top, 8)
                                        .allowsHitTesting(false)
                                }
                            }
                    }
                }
                .padding(28)
            }
        }
    }
    
    private var guidedNudge: some View {
        Button {
            withAnimation(.spring()) { guidedMode = true }
        } label: {
            HStack(spacing: 16) {
                Image(systemName: "sparkles")
                    .font(.title2)
                    .foregroundStyle(.cyan)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text("Analytic Reflection").font(.headline).foregroundStyle(.white)
                    Text("Expose hidden tensions").font(.caption).foregroundStyle(.white.opacity(0.5))
                }
                Spacer()
                Image(systemName: "arrow.right.circle.fill")
                    .font(.title2)
                    .foregroundStyle(.white.opacity(0.2))
            }
            .padding(20)
            .background(.white.opacity(0.08))
            .clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
        }
        .buttonStyle(.plain)
    }

    private func load() {
        if let id = entryId, let entry = container.journalStore.fetchById(id) {
            title = entry.title; body_ = entry.body; savedId = entry.id
            originalCreatedAt = entry.createdAt
        }
    }

    private func save() {
        guard !title.isEmpty || !body_.isEmpty else { return }
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let entry = JournalEntry(
            id: savedId, 
            createdAt: originalCreatedAt ?? now, 
            updatedAt: now,
            title: title.isEmpty ? "Reflection" : title, 
            body: body_, 
            wordCount: body_.split(separator: " ").count
        )
        let id = container.journalStore.save(entry)
        container.threadDetector.process(entryId: id, title: title, body: body_)
    }
}

struct GuidedEntryView: View {
    @EnvironmentObject private var container: AppContainer
    let onComplete: (String, String?) -> Void
    let onCancel: () -> Void
    
    @State private var exchanges: [(String, String)] = []
    @State private var currentQuestion = ""
    @State private var answer = ""
    @State private var isGenerating = false
    
    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button("Cancel", action: onCancel).foregroundStyle(Color.white.opacity(0.6))
                Spacer()
                Text("THE MIRROR").miniCaps().foregroundStyle(Color.white.opacity(0.4))
                Spacer()
                if !exchanges.isEmpty {
                    Button("Finish") { finish() }.bold().foregroundStyle(Color.cyan)
                } else {
                    Spacer().frame(width: 50)
                }
            }
            .padding(28)
            
            ScrollViewReader { proxy in
                ScrollView {
                    VStack(spacing: 40) {
                        ForEach(Array(exchanges.enumerated()), id: \.offset) { _, pair in
                            VStack(alignment: .leading, spacing: 20) {
                                Text(pair.0).font(.system(size: 18, weight: .bold, design: .serif)).foregroundStyle(.cyan.opacity(0.8))
                                Text(pair.1).font(.system(size: 18, weight: .medium, design: .default)).foregroundStyle(.white)
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                        }
                        
                        if !currentQuestion.isEmpty {
                            Text(currentQuestion)
                                .font(.system(size: 24, weight: .black, design: .serif))
                                .foregroundStyle(.white)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .id("bottom")
                        }
                        
                        if isGenerating {
                            ProgressView().tint(.white).padding()
                        }
                    }
                    .padding(.horizontal, 28)
                }
                .onChange(of: exchanges.count) { _ in
                    withAnimation(.spring()) { proxy.scrollTo("bottom", anchor: .bottom) }
                }
            }
            
            if !currentQuestion.isEmpty && !isGenerating {
                liquidInputArea
            }
        }
        .onAppear(perform: start)
    }
    
    private var liquidInputArea: some View {
        HStack(spacing: 16) {
            TextField("Be precise…", text: $answer, axis: .vertical)
                .lineLimit(1...5)
                .padding(18)
                .background(.white.opacity(0.1))
                .clipShape(RoundedRectangle(cornerRadius: 24))
                .foregroundStyle(.white)
            
            Button(action: next) {
                Image(systemName: "arrow.up.circle.fill")
                    .font(.system(size: 48))
                    .foregroundStyle(answer.isEmpty ? .white.opacity(0.2) : .white)
            }
            .disabled(answer.isEmpty)
        }
        .padding(24)
        .background(.ultraThinMaterial)
    }
    
    private func start() {
        isGenerating = true
        Task {
            let q = await container.reflectionEngine?.guidedQuestion(exchanges: []) ?? "What is the core tension you're feeling today?"
            await MainActor.run { withAnimation { currentQuestion = q; isGenerating = false } }
        }
    }
    
    private func next() {
        let a = answer; answer = ""
        withAnimation { exchanges.append((currentQuestion, a)); currentQuestion = "" }
        
        isGenerating = true
        Task {
            let q = await container.reflectionEngine?.guidedQuestion(exchanges: exchanges) ?? "Why that specific word?"
            await MainActor.run { withAnimation { currentQuestion = q; isGenerating = false } }
        }
    }
    
    private func finish() {
        isGenerating = true
        Task {
            let compiled = await container.reflectionEngine?.compileGuided(exchanges: exchanges) ?? exchanges.map(\.1).joined(separator: "\n\n")
            let title = await container.reflectionEngine?.suggestTitle(body: compiled)
            await MainActor.run { onComplete(compiled, title) }
        }
    }
}
EOF