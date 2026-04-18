import SwiftUI

struct MoodScreen: View {
    @EnvironmentObject private var container: AppContainer
    @State private var valence: Double = 0
    @State private var arousal: Double = 2
    @State private var label = ""
    @State private var note = ""
    @State private var recentEntries: [MoodEntry] = []
    @State private var trend: [DailyMoodBucket] = []

    private let labels = ["Calm", "Content", "Happy", "Excited", "Anxious",
                          "Frustrated", "Sad", "Tired", "Hopeful", "Grateful"]

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 18) {
                    Text("How are you?")
                        .font(.title2)
                        .fontWeight(.medium)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    VStack(spacing: 12) {
                        Text("Valence")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                        HStack {
                            Text("Low").font(.caption2)
                            Slider(value: $valence, in: -2...2, step: 1)
                            Text("High").font(.caption2)
                        }
                        Text("Energy")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                        HStack {
                            Text("Calm").font(.caption2)
                            Slider(value: $arousal, in: 0...4, step: 1)
                            Text("Energised").font(.caption2)
                        }
                    }
                    .wellnessCard()

                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(labels, id: \.self) { l in
                                Button(l) { label = l }
                                    .buttonStyle(.bordered)
                                    .tint(label == l ? .accentColor : .secondary)
                            }
                        }
                    }

                    TextField("Add a note (optional)", text: $note, axis: .vertical)
                        .lineLimit(3)
                        .wellnessCard()

                    Button(action: saveMood) {
                        Text("Log mood")
                            .font(.headline)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(.tint, in: RoundedRectangle(cornerRadius: 16))
                            .foregroundStyle(.white)
                    }

                    if !trend.isEmpty {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("30-day valence")
                                .font(.headline)
                            MoodTrendChartView(buckets: trend)
                                .frame(height: 140)
                        }
                        .wellnessCard()
                    }

                    if !recentEntries.isEmpty {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Recent").font(.headline)
                            ForEach(recentEntries) { entry in
                                HStack {
                                    Text(entry.label.isEmpty ? "—" : entry.label)
                                        .font(.subheadline)
                                    Spacer()
                                    Text(formatDate(entry.createdAt))
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                }
                                .padding(.vertical, 4)
                            }
                        }
                        .wellnessCard()
                    }
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 12)
            }
            .navigationTitle("Mood")
            .onAppear(perform: refresh)
        }
    }

    private func saveMood() {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        _ = container.moodStore.insert(MoodEntry(
            id: 0, createdAt: now,
            valence: Int(valence), arousal: Int(arousal),
            label: label, note: note
        ))
        label = ""; note = ""; valence = 0; arousal = 2
        refresh()
    }

    private func refresh() {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let thirtyDaysAgo = now - 30 * 86_400_000
        recentEntries = container.moodStore.fetchRange(from: thirtyDaysAgo, to: now)
        trend = container.moodStore.dailyAggregate(from: thirtyDaysAgo, to: now)
    }

    private func formatDate(_ millis: Int64) -> String {
        let date = Date(timeIntervalSince1970: Double(millis) / 1000)
        let f = DateFormatter()
        f.dateFormat = "MMM d"
        return f.string(from: date)
    }
}
