import SwiftUI

struct InsightsScreen: View {
    @EnvironmentObject private var container: AppContainer
    @State private var totalMoods = 0
    @State private var totalJournals = 0
    @State private var trend: [DailyMoodBucket] = []
    @State private var metrics: [MetricSnapshot] = []
    @State private var mirror: Mirror? = nil
    @State private var patternNarrative = ""

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 18) {
                    Text("Gentle patterns, not judgements.")
                        .foregroundStyle(.secondary)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    HStack(spacing: 12) {
                        StatCard(label: "Mood logs", value: "\(totalMoods)")
                        StatCard(label: "Journal notes", value: "\(totalJournals)")
                    }

                    if let m = mirror {
                        MirrorCardView(mirror: m)
                    }

                    if !patternNarrative.isEmpty {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("What your month is saying")
                                .font(.headline)
                            Text(patternNarrative)
                                .italic()
                                .foregroundStyle(.secondary)
                        }
                        .wellnessCard()
                    }

                    if !trend.isEmpty {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("30-day valence").font(.headline)
                            MoodTrendChartView(buckets: trend)
                                .frame(height: 140)
                        }
                        .wellnessCard()
                    }

                    if !metrics.isEmpty {
                        VStack(alignment: .leading, spacing: 12) {
                            Text("Today's metrics").font(.headline)
                            ForEach(metrics) { metric in
                                HStack {
                                    Text(metric.type.displayName)
                                        .font(.subheadline)
                                    Spacer()
                                    ProgressView(value: metric.value, total: metric.type.maxValue)
                                        .frame(width: 120)
                                    Text(String(format: "%.0f", metric.value))
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                        .frame(width: 40)
                                }
                            }
                        }
                        .wellnessCard()
                    }

                    ModelDownloadCardView()
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 12)
            }
            .navigationTitle("Insights")
            .onAppear(perform: refresh)
        }
    }

    private func refresh() {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let thirtyDaysAgo = now - 30 * 86_400_000
        totalMoods = container.moodStore.count
        totalJournals = container.journalStore.count
        trend = container.moodStore.dailyAggregate(from: thirtyDaysAgo, to: now)
        metrics = container.metricStore.latestPerType()

        Task {
            let df = DateFormatter()
            df.dateFormat = "MMM d"
            let label = "Your month — \(df.string(from: Date(timeIntervalSince1970: Double(thirtyDaysAgo) / 1000))) to \(df.string(from: Date()))"
            let m = container.mirrorGenerator.generate(from: thirtyDaysAgo, to: now, periodLabel: label)
            await MainActor.run { mirror = m }

            if let m, let engine = container.reflectionEngine {
                let narrative = await engine.narrateMirror(m)
                await MainActor.run { patternNarrative = narrative ?? "" }
            }
        }
    }
}

struct StatCard: View {
    let label: String
    let value: String

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(value)
                .font(.title)
                .fontWeight(.bold)
            Text(label)
                .font(.caption)
                .fontWeight(.medium)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .wellnessCard()
    }
}

struct MirrorCardView: View {
    let mirror: Mirror

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(mirror.periodLabel)
                .font(.title3)
                .fontWeight(.medium)
            Text("\(mirror.totalMoods) mood logs · \(mirror.totalEntries) journal entries")
                .font(.caption)
                .foregroundStyle(.secondary)

            if mirror.moodArc.count >= 2 {
                MoodTrendChartView(buckets: mirror.moodArc)
                    .frame(height: 80)
            }

            if !mirror.topWords.isEmpty {
                Text("Words on your mind")
                    .font(.caption)
                    .fontWeight(.medium)
                HStack(spacing: 12) {
                    ForEach(mirror.topWords, id: \.0) { word, count in
                        Text("\(word) (\(count))")
                            .font(.subheadline)
                            .fontWeight(.semibold)
                    }
                }
            }

            if !mirror.highlightSnippet.isEmpty {
                Text("Most invested entry").font(.caption).fontWeight(.medium)
                Text("\u{201C}\(mirror.highlightSnippet)…\u{201D}")
                    .italic()
                    .lineLimit(3)
                Text("— \(mirror.highlightTitle)")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }

            if let cb = mirror.callback {
                Text(cb).foregroundStyle(.secondary)
            }
        }
        .padding(20)
        .background(Color.lavenderMist.opacity(0.2), in: RoundedRectangle(cornerRadius: 22))
    }
}

struct ModelDownloadCardView: View {
    @EnvironmentObject private var container: AppContainer

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Reflection engine").font(.headline)
            Text("A small on-device model that asks you deeper questions about your entries. Nothing leaves your phone.")
                .font(.subheadline)
                .foregroundStyle(.secondary)

            switch container.modelManager.status {
            case .notDownloaded:
                Button("Download (~600 MB)") {
                    container.modelManager.download(
                        url: "https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf"
                    )
                }
                .buttonStyle(.borderedProminent)
            case .downloading(let progress):
                ProgressView(value: progress)
                Text("\(Int(progress * 100))%")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            case .ready:
                HStack {
                    Text("Ready").foregroundStyle(.green).fontWeight(.medium)
                    Spacer()
                    Button("Remove", role: .destructive) {
                        container.modelManager.deleteModel()
                    }
                    .buttonStyle(.bordered)
                }
            case .error(let msg):
                Text(msg).foregroundStyle(.red).font(.caption)
                Button("Retry") {
                    container.modelManager.download(
                        url: "https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf"
                    )
                }
                .buttonStyle(.bordered)
            }
        }
        .wellnessCard()
    }
}
