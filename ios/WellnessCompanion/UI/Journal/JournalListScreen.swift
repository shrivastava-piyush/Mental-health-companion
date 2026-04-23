import SwiftUI

struct JournalListScreen: View {
    @EnvironmentObject private var container: AppContainer
    @State private var entries: [JournalSummary] = []
    @State private var threads: [NarrativeThread] = []
    @State private var showEditor = false
    @State private var editingId: Int64? = nil
    @State private var showThread: NarrativeThread? = nil

    var body: some View {
        ZStack {
            // Main Content
            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: 50) {
                    
                    // 1. Hero Header
                    VStack(alignment: .leading, spacing: 8) {
                        Text("JOURNAL").miniCaps().foregroundStyle(Color.white.opacity(0.4))
                        Text("Reflections")
                            .font(.system(size: 44, weight: .black, design: .rounded))
                            .foregroundStyle(.white)
                        Text("\(entries.count) notes captured in sanctuary")
                            .font(.subheadline.bold())
                            .foregroundStyle(.white.opacity(0.5))
                    }
                    .padding(.top, 40)
                    
                    // 2. Thematic Bubbles
                    if !threads.isEmpty {
                        VStack(alignment: .leading, spacing: 20) {
                            Text("Themes").sectionHeader()
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 16) {
                                    ForEach(threads) { thread in
                                        ThreadBubble(thread: thread) { showThread = thread }
                                    }
                                }
                            }
                        }
                    }
                    
                    // 3. Immersive Timeline
                    VStack(alignment: .leading, spacing: 32) {
                        Text("Timeline").sectionHeader()
                        
                        if entries.isEmpty {
                            VStack(spacing: 20) {
                                Image(systemName: "leaf")
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
                                    editingId = summary.id
                                    showEditor = true
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
            
            // 4. Glowing FAB
            VStack {
                Spacer()
                HStack {
                    Spacer()
                    Button {
                        editingId = nil
                        showEditor = true
                        UIImpactFeedbackGenerator(style: .medium).impactOccurred()
                    } label: {
                        ZStack {
                            Circle()
                                .fill(.white.opacity(0.2))
                                .frame(width: 80, height: 80)
                                .blur(radius: 10)
                            
                            Circle()
                                .fill(LinearGradient(colors: [.liquidRose, .liquidAmber], startPoint: .topLeading, endPoint: .bottomTrailing))
                                .frame(width: 70, height: 70)
                                .shadow(color: .liquidRose.opacity(0.4), radius: 15, y: 8)
                            
                            Image(systemName: "plus")
                                .font(.title.bold())
                                .foregroundStyle(.white)
                        }
                    }
                    .padding(28)
                }
            }
        }
        .sheet(isPresented: $showEditor, onDismiss: refresh) {
            JournalEditorScreen(entryId: editingId)
        }
        .sheet(item: $showThread) { thread in
            ThreadDetailScreen(thread: thread)
        }
        .onAppear(perform: refresh)
    }
    
    private func refresh() {
        withAnimation(.spring()) {
            entries = container.journalStore.fetchSummaries()
            threads = container.narrativeStore.activeThreads()
        }
    }
}

struct ThreadBubble: View {
    let thread: NarrativeThread
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(alignment: .leading, spacing: 14) {
                Image(systemName: "sparkles")
                    .font(.title3)
                    .foregroundStyle(.cyan)
                
                Text(thread.label)
                    .font(.subheadline.bold())
                    .foregroundStyle(.white)
                    .lineLimit(2)
                    .multilineTextAlignment(.leading)
            }
            .padding(20)
            .frame(width: 150, height: 130, alignment: .topLeading)
            .background(.white.opacity(0.08))
            .clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
        }
        .buttonStyle(.plain)
    }
}
