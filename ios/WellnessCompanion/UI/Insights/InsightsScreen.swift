import SwiftUI

struct InsightsScreen: View {
    @EnvironmentObject private var container: AppContainer
    @State private var totalMoods = 0; @State private var totalJournals = 0
    @State private var trend: [DailyMoodBucket] = []; @State private var metrics: [MetricSnapshot] = []
    @State private var mirror: Mirror? = nil; @State private var patternNarrative = ""

    var body: some View {
        ZStack {
            LiquidAura(scrollOffset: 0).ignoresSafeArea()
            
            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: 50) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("PULSE").miniCaps().foregroundStyle(Color.white.opacity(0.4))
                        Text("Patterns").font(.system(size: 44, weight: .black, design: .rounded)).foregroundStyle(.white)
                        Text("Gentle observations of your journey").font(.subheadline.bold()).foregroundStyle(.white.opacity(0.5))
                    }.padding(.top, 40)
                    
                    HStack(spacing: 20) {
                        LiquidStatCard(label: "Logs", value: "\(totalMoods)", icon: "face.smiling", color: .liquidTeal)
                        LiquidStatCard(label: "Notes", value: "\(totalJournals)", icon: "text.quote", color: .liquidRose)
                    }
                    
                    if !patternNarrative.isEmpty {
                        VStack(alignment: .leading, spacing: 20) {
                            Text("Perspective").sectionHeader()
                            VStack(alignment: .leading, spacing: 20) {
                                Image(systemName: "quote.opening").font(.title).foregroundStyle(.white.opacity(0.2))
                                Text(patternNarrative).font(.system(size: 20, weight: .medium, design: .serif)).italic().lineSpacing(8).foregroundStyle(.white)
                            }.padding(32).background(.white.opacity(0.08)).clipShape(RoundedRectangle(cornerRadius: 40, style: .continuous))
                        }
                    }
                    
                    VStack(alignment: .leading, spacing: 24) {
                        Text("Emotional Flow").sectionHeader()
                        MoodTrendChartView(buckets: trend).frame(height: 180).padding(24).background(.white.opacity(0.05)).clipShape(RoundedRectangle(cornerRadius: 40, style: .continuous))
                    }
                    
                    ModelDownloadCardView(modelManager: container.modelManager)
                    Spacer(minLength: 150)
                }.padding(.horizontal, 28)
            }
        }.onAppear(perform: refresh)
    }
    
    private func refresh() {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let thirtyDaysAgo = now - 30 * 86_400_000
        totalMoods = container.moodStore.count; totalJournals = container.journalStore.count
        trend = container.moodStore.dailyAggregate(from: thirtyDaysAgo, to: now)
        metrics = container.metricStore.latestPerType()
        Task {
            let df = DateFormatter(); df.dateFormat = "MMM d"
            let label = "\(df.string(from: Date(timeIntervalSince1970: Double(thirtyDaysAgo) / 1000))) — \(df.string(from: Date()))"
            let m = container.mirrorGenerator.generate(from: thirtyDaysAgo, to: now, periodLabel: label)
            await MainActor.run { mirror = m }
            if let m, let engine = container.reflectionEngine {
                let narrative = await engine.narrateMirror(m)
                await MainActor.run { withAnimation(.spring()) { patternNarrative = narrative ?? "" } }
            }
        }
    }
}

struct ModelDownloadCardView: View {
    @ObservedObject var modelManager: ModelManager
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("REFLECTION ENGINE").miniCaps().foregroundStyle(Color.white.opacity(0.4))
                    Text("Offline Intelligence").font(.headline).foregroundStyle(.white)
                }
                Spacer(); Image(systemName: "brain.head.profile").font(.title2).foregroundStyle(.cyan)
            }
            switch modelManager.status {
            case .notDownloaded:
                Button(action: { modelManager.download(url: "https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf") }) {
                    Text("Download Assistant").font(.subheadline.bold()).foregroundStyle(.white).frame(maxWidth: .infinity).frame(height: 54).background(Color.liquidIndigo, in: Capsule())
                }
            case .downloading(let p):
                HStack(spacing: 20) {
                    ZStack {
                        Circle().stroke(Color.white.opacity(0.1), lineWidth: 4)
                        Circle().trim(from: 0, to: CGFloat(p)).stroke(.cyan, style: StrokeStyle(lineWidth: 4, lineCap: .round)).rotationEffect(.degrees(-90))
                        Text("\(Int(p*100))%").font(.system(size: 8, weight: .black)).foregroundStyle(.white)
                    }.frame(width: 44, height: 44)
                    Text("Loading Intelligence…").font(.subheadline.bold()).foregroundStyle(Color.white.opacity(0.6))
                }
            case .ready:
                HStack {
                    Label("Engine Ready", systemImage: "checkmark.seal.fill").font(.subheadline.bold()).foregroundStyle(.cyan)
                    Spacer(); Button("Remove") { modelManager.deleteModel() }.font(.caption.bold()).foregroundStyle(Color.white.opacity(0.4))
                }
            case .error(let msg): 
                VStack(alignment: .leading, spacing: 10) {
                    Text(msg).foregroundStyle(.red).font(.caption)
                    Button("Retry") { modelManager.download(url: "https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf") }
                }
            }
        }.padding(28).background(.white.opacity(0.05)).clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
    }
}
