import SwiftUI

struct JournalListScreen: View {
    @EnvironmentObject private var container: AppContainer
    @Environment(\.openReflection) private var openReflection
    
    @State private var entries: [JournalSummary] = []
    @State private var threads: [NarrativeThread] = []
    @State private var synthesizedInsight: String? = nil
    @State private var isSynthesizing = false
    
    var body: some View {
        libraryContent
            .onAppear(perform: refresh)
    }
    
    private var libraryContent: some View {
        ScrollView(showsIndicators: false) {
            VStack(alignment: .leading, spacing: 50) {
                
                // 1. High-Fidelity Hero Image
                VStack(alignment: .leading, spacing: 20) {
                    AsyncImage(url: URL(string: WellnessContentProvider.libraryHero)) { image in
                        image.resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(height: 240)
                            .clipShape(RoundedRectangle(cornerRadius: 48, style: .continuous))
                            .overlay(alignment: .bottomLeading) {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("REFLECTIONS").miniCaps().foregroundStyle(Color.white.opacity(0.6))
                                    Text("Library").font(.system(size: 44, weight: .black, design: .rounded)).foregroundStyle(.white)
                                }
                                .padding(32)
                            }
                    } placeholder: {
                        RoundedRectangle(cornerRadius: 48).fill(.white.opacity(0.05)).frame(height: 240)
                    }
                    
                    Text(WellnessContentProvider.attribution)
                        .font(.system(size: 8)).miniCaps().foregroundStyle(.white.opacity(0.2))
                        .padding(.leading, 12)
                }
                .padding(.top, 20)
                
                // 2. Intelligence Layer: Synthesis Card
                if !entries.isEmpty {
                    synthesisSection
                }
                
                if !threads.isEmpty {
                    VStack(alignment: .leading, spacing: 24) {
                        Text("Themes").sectionHeader()
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 20) {
                                ForEach(threads) { thread in
                                    VStack(alignment: .leading, spacing: 14) {
                                        Image(systemName: "sparkles").font(.title2).foregroundStyle(.cyan)
                                        VStack(alignment: .leading, spacing: 0) {
                                            Text(thread.label).font(.headline).foregroundStyle(.white)
                                            Text("\(thread.entryCount) entries").font(.caption).foregroundStyle(.white.opacity(0.4))
                                        }
                                    }
                                    .padding(24)
                                    .frame(width: 150, height: 130, alignment: .topLeading)
                                    .background(.white.opacity(0.08))
                                    .clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
                                }
                            }
                        }
                    }
                }
                
                VStack(alignment: .leading, spacing: 24) {
                    Text("Timeline").sectionHeader()
                    if entries.isEmpty {
                        VStack(spacing: 16) {
                            Image(systemName: "book.closed")
                                .font(.system(size: 40))
                                .foregroundStyle(.white.opacity(0.2))
                            Text("Your story begins here.")
                                .font(.system(size: 20, weight: .medium, design: .serif))
                                .foregroundStyle(.white.opacity(0.4))
                        }
                        .frame(maxWidth: CGFloat.infinity)
                        .padding(.top, 60)
                    } else {
                        ForEach(entries) { summary in
                            Button {
                                openReflection(summary.id, nil)
                            } label: {
                                LiquidEntryRow(summary: summary)
                            }
                            .buttonStyle(.plain)
                        }
                    }
                }
                
                Spacer(minLength: 150)
            }
            .padding(.horizontal, 28)
        }
        .overlay(alignment: .bottomTrailing) {
            // Floating Plus Button
            Button {
                openReflection(nil, nil)
            } label: {
                Circle()
                    .fill(Color.wellnessAccent)
                    .frame(width: 72, height: 72)
                    .shadow(color: Color.wellnessAccent.opacity(0.3), radius: 20, y: 10)
                    .overlay(Image(systemName: "plus").font(.system(size: 30, weight: .bold)).foregroundStyle(.white))
            }
            .padding(28)
            .padding(.bottom, 80)
        }
    }
    
    private var synthesisSection: some View {
        VStack(alignment: .leading, spacing: 20) {
            HStack {
                Text("Synthesized Insight").sectionHeader()
                Spacer()
                if isSynthesizing {
                    ProgressView().tint(.cyan).scaleEffect(0.8)
                } else {
                    Button { runSynthesis() } label: {
                        Image(systemName: "arrow.clockwise").font(.caption.bold()).foregroundStyle(.cyan)
                    }
                }
            }
            
            VStack(alignment: .leading, spacing: 16) {
                if let insight = synthesizedInsight {
                    Text(insight)
                        .font(.system(size: 18, weight: .medium, design: .serif))
                        .italic()
                        .foregroundStyle(.white)
                        .lineSpacing(6)
                        .transition(.opacity.combined(with: .move(edge: .top)))
                } else if !isSynthesizing {
                    Text("Deep-dive analysis of your last 3 reflections.")
                        .font(.caption).foregroundStyle(.white.opacity(0.4))
                } else {
                    Text("Finding the hidden thread…")
                        .font(.caption).foregroundStyle(.cyan.opacity(0.6))
                }
            }
            .padding(32)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(.white.opacity(0.08))
            .clipShape(RoundedRectangle(cornerRadius: 40, style: .continuous))
            .overlay(RoundedRectangle(cornerRadius: 40, style: .continuous).stroke(.cyan.opacity(0.2), lineWidth: 1))
        }
    }

    private func refresh() {
        entries = container.journalStore.fetchSummaries()
        threads = container.narrativeStore.activeThreads()
        if synthesizedInsight == nil && entries.count >= 2 {
            runSynthesis()
        }
    }
    
    private func runSynthesis() {
        guard let engine = container.reflectionEngine, entries.count >= 2 else { return }
        isSynthesizing = true
        Task {
            // Fetch the full entries for synthesis
            let fullEntries = entries.prefix(3).compactMap { container.journalStore.fetchById($0.id) }
            let insight = await engine.synthesizeInsight(entries: fullEntries)
            await MainActor.run {
                withAnimation(.spring()) {
                    synthesizedInsight = insight
                    isSynthesizing = false
                }
            }
        }
    }
}
