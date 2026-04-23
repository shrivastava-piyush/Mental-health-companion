import SwiftUI

/// Preference key for tracking scroll offset in high-fidelity screens.
struct ScrollOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}

struct HomeScreen: View {
    @EnvironmentObject private var container: AppContainer
    @Environment(\.globalNav) private var globalNav
    @Binding var scrollOffset: CGFloat
    
    @State private var recentMood: MoodEntry?
    @State private var recentJournal: JournalSummary?
    
    @State private var selectedQuote = ("Focus on the present moment.", "Breathe")
    @State private var currentCategory: MoodCategory = .neutral
    @State private var aiSpark: String? = nil

    var body: some View {
        ScrollView(showsIndicators: false) {
            VStack(alignment: .center, spacing: 60) {
                
                // 1. Animated Quote Hero
                AnimatedQuoteView(
                    quote: selectedQuote.0,
                    author: selectedQuote.1,
                    category: currentCategory
                )
                .frame(maxWidth: CGFloat.infinity)
                .padding(.top, 100)
                
                // 2. Floating Reflection Sparks
                VStack(alignment: .leading, spacing: 24) {
                    HStack {
                        Text("Sparks").sectionHeader().foregroundStyle(.white.opacity(0.5))
                        Spacer()
                    }
                    .padding(.horizontal, 28)
                    
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 20) {
                            if let spark = aiSpark {
                                sparkPill(text: spark, isAi: true)
                            }
                            sparkPill(text: "What does peace look like for you right now?")
                            sparkPill(text: "Describe a color that matches your energy.")
                        }
                        .padding(.horizontal, 28)
                    }
                }
                
                // 3. Redesigned Check-in (In-place Fragment Trigger)
                Button {
                    UIImpactFeedbackGenerator(style: .heavy).impactOccurred()
                    globalNav(.mood)
                } label: {
                    HStack(spacing: 20) {
                        ZStack {
                            Circle().fill(.white.opacity(0.1)).frame(width: 54, height: 54)
                            Image(systemName: "leaf.fill")
                                .font(.system(size: 24))
                                .foregroundStyle(LinearGradient(colors: [.liquidTeal, .liquidIndigo], startPoint: .top, endPoint: .bottom))
                        }
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Sanctuary Check-in").font(.headline).foregroundStyle(.white)
                            Text("Observe your inner state").font(.caption).foregroundStyle(.white.opacity(0.5))
                        }
                        
                        Spacer()
                        
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundStyle(.white.opacity(0.3))
                    }
                    .padding(24)
                    .background(.white.opacity(0.08))
                    .clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
                    .overlay(RoundedRectangle(cornerRadius: 32, style: .continuous).stroke(.white.opacity(0.1), lineWidth: 1))
                }
                .padding(.horizontal, 28)
                .buttonStyle(.plain)
                
                latestActivitySection
                    .padding(.horizontal, 28)
                
                Spacer(minLength: 150)
            }
            .background(
                GeometryReader { proxy in
                    Color.clear.preference(key: ScrollOffsetKey.self, value: proxy.frame(in: .global).minY)
                }
            )
        }
        .onPreferenceChange(ScrollOffsetKey.self) { value in
            scrollOffset = value
        }
        .onAppear(perform: loadData)
    }
    
    @ViewBuilder
    private var latestActivitySection: some View {
        if recentMood != nil || recentJournal != nil {
            VStack(alignment: .leading, spacing: 20) {
                Text("Recent Activity").sectionHeader().foregroundStyle(.white.opacity(0.5))
                
                VStack(spacing: 16) {
                    if let mood = recentMood {
                        Button {
                            globalNav(.mood)
                        } label: {
                            HStack(spacing: 16) {
                                Text(moodEmoji(for: mood.valence)).font(.title2)
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(mood.label.isEmpty ? "Mood Logged" : mood.label).font(.subheadline.bold())
                                    Text(formatRelativeDate(mood.createdAt)).font(.caption).foregroundStyle(.white.opacity(0.4))
                                }
                                Spacer()
                            }
                            .padding(20).background(.white.opacity(0.05)).clipShape(RoundedRectangle(cornerRadius: 24))
                        }
                        .buttonStyle(.plain)
                    }
                    
                    if let journal = recentJournal {
                        Button {
                            globalNav(.reflection(id: journal.id, prompt: nil))
                        } label: {
                            HStack(spacing: 16) {
                                Image(systemName: "text.quote").foregroundStyle(.cyan)
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(journal.title).font(.subheadline.bold()).lineLimit(1)
                                    Text(formatRelativeDate(journal.createdAt)).font(.caption).foregroundStyle(.white.opacity(0.4))
                                }
                                Spacer()
                            }
                            .padding(20).background(.white.opacity(0.05)).clipShape(RoundedRectangle(cornerRadius: 24))
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
        }
    }
    
    private func sparkPill(text: String, isAi: Bool = false) -> some View {
        Button { 
            globalNav(.reflection(id: nil, prompt: text))
        } label: {
            VStack(alignment: .leading, spacing: 10) {
                if isAi {
                    HStack {
                        Image(systemName: "sparkles")
                        Text("AI Musing").font(.system(size: 8, weight: .black)).textCase(.uppercase)
                    }
                    .foregroundStyle(.cyan)
                }
                Text(text)
                    .font(.system(.subheadline, design: .serif))
                    .foregroundStyle(.white)
                    .lineLimit(3)
            }
            .padding(20)
            .frame(width: 220, height: 130, alignment: .topLeading)
            .background(.white.opacity(0.08))
            .clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
            .overlay(RoundedRectangle(cornerRadius: 32, style: .continuous).stroke(.white.opacity(0.1), lineWidth: 1))
        }
        .buttonStyle(.plain)
    }

    private func loadData() {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        recentMood = container.moodStore.fetchRange(from: now - 86400000 * 7, to: now).last
        recentJournal = container.journalStore.fetchSummaries(limit: 1).first
        
        let valence = recentMood?.valence ?? 0
        currentCategory = MoodCategory(valence: valence)
        selectedQuote = WellnessContentProvider.quote(for: currentCategory)
        container.atmosphereManager.adaptTo(valence: valence)

        if let engine = container.reflectionEngine {
            Task {
                let hour = Calendar.current.component(.hour, from: Date())
                let tod = hour < 6 ? "late night" : hour < 12 ? "morning" : hour < 17 ? "afternoon" : hour < 21 ? "evening" : "night"
                let spark = await engine.contextualStarter(moodLabel: recentMood?.label, timeOfDay: tod)
                await MainActor.run { aiSpark = spark }
            }
        }
    }
    
    private func moodEmoji(for v: Int) -> String {
        if v > 60 { return "✨" }
        if v > 20 { return "🙂" }
        if v > -20 { return "😐" }
        if v > -60 { return "🙁" }
        return "😔"
    }
    
    private func formatRelativeDate(_ millis: Int64) -> String {
        let date = Date(timeIntervalSince1970: Double(millis) / 1000)
        let formatter = RelativeDateTimeFormatter(); formatter.unitsStyle = .full
        return formatter.localizedString(for: date, relativeTo: Date())
    }
}
