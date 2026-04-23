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
                        statCard(label: "Logs", value: "\(totalMoods)", icon: "face.smiling", color: .liquidTeal)
                        statCard(label: "Notes", value: "\(totalJournals)", icon: "text.quote", color: .liquidRose)
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
                    
                    // High-Fidelity Download Card
                    ModelDownloadCardView(manager: container.modelManager)
                    
                    Spacer(minLength: 150)
                }.padding(.horizontal, 28)
            }
        }.onAppear(perform: refresh)
    }
    
    @ViewBuilder
    private func statCard(label: String, value: String, icon: String, color: Color) -> some View {
        VStack(alignment: .leading, spacing: 14) {
            ZStack {
                Circle().fill(color.opacity(0.2)).frame(width: 44, height: 44)
                Image(systemName: icon).font(.headline).foregroundStyle(color)
            }
            VStack(alignment: .leading, spacing: 0) {
                Text(value).font(.system(size: 32, weight: .black, design: .rounded)).foregroundStyle(.white)
                Text(label).miniCaps().foregroundStyle(Color.white.opacity(0.4))
            }
        }.frame(maxWidth: CGFloat.infinity, alignment: .leading).padding(24).background(.white.opacity(0.08)).clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
    }
    
    private func refresh() {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let thirtyDaysAgo = now - 30 * 86_400_000
        totalMoods = container.moodStore.fetchRange(from: thirtyDaysAgo, to: now).count
        totalJournals = container.journalStore.fetchSummaries().count
        trend = container.moodStore.dailyAggregate(from: thirtyDaysAgo, to: now)
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
    @ObservedObject var manager: ModelManager
    
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("THE MIRROR").miniCaps().foregroundStyle(Color.white.opacity(0.4))
                    Text("Offline Intelligence").font(.headline).foregroundStyle(.white)
                }
                Spacer(); Image(systemName: "brain.head.profile").font(.title2).foregroundStyle(.cyan)
            }
            
            Group {
                switch manager.status {
                case .notDownloaded:
                    Button {
                        UIImpactFeedbackGenerator(style: .medium).impactOccurred()
                        manager.download(url: "https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf")
                    } label: {
                        Text("Download Assistant")
                            .font(.subheadline.bold())
                            .foregroundStyle(.white)
                            .frame(maxWidth: CGFloat.infinity)
                            .frame(height: 54)
                            .background(Color.liquidIndigo, in: Capsule())
                            .overlay(Capsule().stroke(.white.opacity(0.1), lineWidth: 1))
                    }
                    .buttonStyle(.plain)
                    
                case .downloading(let progress):
                    VStack(spacing: 12) {
                        HStack {
                            Text("Synchronizing…").font(.caption.bold()).foregroundStyle(.cyan)
                            Spacer()
                            Text("\(Int(progress * 100))%").font(.system(size: 10, weight: .black, design: .monospaced)).foregroundStyle(.white)
                        }
                        
                        // High-Fidelity Progress Bar
                        GeometryReader { proxy in
                            ZStack(alignment: .leading) {
                                Capsule().fill(.white.opacity(0.1))
                                Capsule()
                                    .fill(LinearGradient(colors: [.cyan, .liquidTeal], startPoint: .leading, endPoint: .trailing))
                                    .frame(width: proxy.size.width * CGFloat(progress))
                                    .shadow(color: .cyan.opacity(0.3), radius: 5)
                            }
                        }
                        .frame(height: 8)
                        
                        Text("Initializing adversarial logic").font(.system(size: 8)).foregroundStyle(.white.opacity(0.3)).miniCaps()
                    }
                    .padding(.vertical, 10)
                    
                case .ready:
                    HStack {
                        Label("Mirror Ready", systemImage: "checkmark.seal.fill").font(.subheadline.bold()).foregroundStyle(.cyan)
                        Spacer()
                        Button("Delete") { manager.deleteModel() }.font(.caption.bold()).foregroundStyle(Color.white.opacity(0.4))
                    }
                    
                case .error(let msg):
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Handshake Failed").font(.subheadline.bold()).foregroundStyle(.red)
                        Text(msg).font(.caption).foregroundStyle(.white.opacity(0.4))
                        Button("Retry Connection") {
                             manager.download(url: "https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf")
                        }.font(.subheadline.bold()).foregroundStyle(.white)
                    }
                }
            }
            .animation(.spring(response: 0.4, dampingFraction: 0.8), value: manager.status)
        }
        .padding(28)
        .background(.white.opacity(0.05))
        .clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
        .overlay(RoundedRectangle(cornerRadius: 32, style: .continuous).stroke(.white.opacity(0.1), lineWidth: 1))
    }
}
