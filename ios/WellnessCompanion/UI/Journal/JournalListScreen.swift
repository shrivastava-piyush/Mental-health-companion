import SwiftUI

struct JournalListScreen: View {
    @EnvironmentObject private var container: AppContainer
    @State private var entries: [JournalSummary] = []
    @State private var threads: [NarrativeThread] = []
    @State private var activeReflectionId: Int64? = nil
    
    var body: some View {
        ZStack {
            if let id = activeReflectionId {
                // Fragment: Replaces the Library screen content in-place
                JournalEditorScreen(entryId: id) {
                    withAnimation(.spring()) {
                        activeReflectionId = nil
                        refresh()
                    }
                }
                .transition(.asymmetric(insertion: .move(edge: .trailing), removal: .move(edge: .leading)))
                .zIndex(1)
            } else {
                libraryContent
                    .transition(.asymmetric(insertion: .move(edge: .leading), removal: .move(edge: .trailing)))
                    .zIndex(0)
            }
        }
        .onAppear(perform: refresh)
    }
    
    private var libraryContent: some View {
        ZStack(alignment: .bottomTrailing) {
            LiquidAura(scrollOffset: 0).ignoresSafeArea()
            
            ScrollView {
                VStack(alignment: .leading, spacing: 50) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("REFLECTIONS").miniCaps().foregroundStyle(Color.white.opacity(0.4))
                        Text("Library").font(.system(size: 44, weight: .black, design: .rounded)).foregroundStyle(.white)
                        Text("\(entries.count) notes captured in sanctuary").font(.subheadline.bold()).foregroundStyle(.white.opacity(0.5))
                    }
                    .padding(.top, 40)
                    
                    if !threads.isEmpty {
                        VStack(alignment: .leading, spacing: 24) {
                            Text("Themes").sectionHeader()
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 20) {
                                    ForEach(threads) { thread in
                                        NavigationLink {
                                            ThreadDetailScreen(thread: thread)
                                        } label: {
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
                                    withAnimation(.spring()) {
                                        activeReflectionId = summary.id
                                    }
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
            
            // Floating Plus Button
            Button {
                withAnimation(.spring()) {
                    activeReflectionId = -1 // Trigger new
                }
            } label: {
                Circle()
                    .fill(Color.wellnessAccent)
                    .frame(width: 72, height: 72)
                    .shadow(color: Color.wellnessAccent.opacity(0.3), radius: 20, y: 10)
                    .overlay(Image(systemName: "plus").font(.system(size: 30, weight: .bold)).foregroundStyle(.white))
            }
            .padding(28)
        }
    }

    private func refresh() {
        entries = container.journalStore.fetchSummaries()
        threads = container.narrativeStore.fetchActiveThreads()
    }
}
