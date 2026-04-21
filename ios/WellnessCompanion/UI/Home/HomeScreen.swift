import SwiftUI

struct HomeScreen: View {
    @EnvironmentObject private var container: AppContainer
    @Binding var scrollOffset: CGFloat
    
    @State private var recentMood: MoodEntry?
    @State private var recentJournal: JournalSummary?
    @State private var showMoodLog = false
    @State private var showJournal = false
    @State private var selectedSpark: String? = nil
    
    private let quotes = [
        ("The only way to make sense out of change is to plunge into it, move with it, and join the dance.", "Alan Watts"),
        ("Knowing yourself is the beginning of all wisdom.", "Aristotle"),
        ("The wound is the place where the Light enters you.", "Rumi"),
        ("Your vision will become clear only when you can look into your own heart.", "Carl Jung")
    ]
    @State private var selectedQuote = ("Focus on the present moment.", "Breathe")
    @State private var aiSpark: String? = nil

    var body: some View {
        ScrollView(showsIndicators: false) {
            VStack(alignment: .center, spacing: 60) {
                
                // 1. Zero-Border Quote Hero
                VStack(spacing: 32) {
                    Image(systemName: "sparkles")
                        .font(.system(size: 30))
                        .foregroundStyle(.white.opacity(0.4))
                    
                    Text(selectedQuote.0)
                        .font(.system(size: 36, weight: .bold, design: .serif))
                        .italic()
                        .multilineTextAlignment(.center)
                        .lineSpacing(10)
                        .foregroundStyle(.white)
                        .padding(.horizontal, 20)
                        .shadow(color: .black.opacity(0.2), radius: 10)
                    
                    Text(selectedQuote.1.uppercased())
                        .font(.system(size: 12, weight: .black, design: .rounded))
                        .tracking(3.0)
                        .foregroundStyle(.white.opacity(0.5))
                }
                .frame(maxWidth: .infinity)
                .padding(.top, 80)
                
                // 2. Immersive "Enter Sanctuary" Trigger
                Button {
                    UIImpactFeedbackGenerator(style: .heavy).impactOccurred()
                    showMoodLog = true
                } label: {
                    VStack(spacing: 16) {
                        ZStack {
                            Circle()
                                .fill(.white.opacity(0.1))
                                .frame(width: 120, height: 120)
                                .blur(radius: 10)
                            
                            Circle()
                                .fill(LinearGradient(colors: [.liquidTeal, .liquidIndigo], startPoint: .topLeading, endPoint: .bottomTrailing))
                                .frame(width: 100, height: 100)
                                .shadow(color: .liquidTeal.opacity(0.5), radius: 20, y: 10)
                            
                            Image(systemName: "touchid")
                                .font(.system(size: 40))
                                .foregroundStyle(.white)
                        }
                        
                        Text("Enter Sanctuary")
                            .font(.system(size: 20, weight: .black, design: .rounded))
                            .foregroundStyle(.white)
                    }
                }
                .buttonStyle(.plain)
                
                // 3. Floating Reflection Sparks
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
        .fullScreenCover(isPresented: $showMoodLog) {
            MoodScreen()
        }
        .sheet(isPresented: $showJournal, onDismiss: loadData) {
            JournalEditorScreen(entryId: nil, initialBody: selectedSpark)
        }
    }
    
    @ViewBuilder
    private var latestActivitySection: some View {
        if recentMood != nil || recentJournal != nil {
            VStack(alignment: .leading, spacing: 20) {
                Text("Recent Activity").sectionHeader().foregroundStyle(.white.opacity(0.5))
                
                VStack(spacing: 16) {
                    if let mood = recentMood {
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
                    
                    if let journal = recentJournal {
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
                }
            }
        }
    }
    
    private func sparkPill(text: String, isAi: Bool = false) -> some View {
        Button { 
            selectedSpark = text
            showJournal = true 
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
        selectedQuote = quotes.randomElement()!
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        recentMood = container.moodStore.fetchRange(from: now - 86400000, to: now).last
        recentJournal = container.journalStore.fetchSummaries(limit: 1).first
        
        if let engine = container.reflectionEngine {
            Task {
                let hour = Calendar.current.component(.hour, from: Date())
                let tod = hour < 6 ? "late night" : hour < 12 ? "morning" : hour < 17 ? "afternoon" : hour < 21 ? "evening" : "night"
                aiSpark = await engine.contextualStarter(moodLabel: recentMood?.label, timeOfDay: tod)
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

struct ScrollOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}
