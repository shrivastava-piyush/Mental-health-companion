import SwiftUI

struct ThreadDetailScreen: View {
    @EnvironmentObject private var container: AppContainer
    @Environment(\.dismiss) private var dismiss
    let thread: NarrativeThread

    @State private var entries: [JournalSummary] = []
    @State private var activeReflectionId: Int64? = nil

    var body: some View {
        ZStack {
            if let id = activeReflectionId {
                // Fragment: Reflection Editor (In-place)
                JournalEditorScreen(entryId: id) {
                    withAnimation(.spring()) {
                        activeReflectionId = nil
                        refresh()
                    }
                }
                .transition(.asymmetric(insertion: .move(edge: .trailing), removal: .move(edge: .leading)))
                .zIndex(1)
            } else {
                detailContent
                    .transition(.asymmetric(insertion: .move(edge: .leading), removal: .move(edge: .trailing)))
                    .zIndex(0)
            }
        }
        .onAppear(perform: refresh)
    }
    
    private var detailContent: some View {
        VStack(spacing: 0) {
            // Header
            HStack {
                Button { dismiss() } label: {
                    Image(systemName: "chevron.down")
                        .font(.title2.bold())
                        .foregroundStyle(.white.opacity(0.6))
                }
                Spacer()
                Text("THEME").miniCaps().foregroundStyle(Color.white.opacity(0.4))
                Spacer()
                Spacer().frame(width: 40)
            }
            .padding(.horizontal, 28)
            .padding(.top, 20)
            
            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: 40) {
                    
                    // Hero Header
                    VStack(alignment: .leading, spacing: 12) {
                        ZStack(alignment: .center) {
                            Circle().fill(.white.opacity(0.1)).frame(width: 48, height: 48)
                            Image(systemName: "sparkles").font(.title3).foregroundStyle(.cyan)
                        }
                        
                        Text(thread.label)
                            .font(.system(size: 34, weight: .black, design: .rounded))
                            .foregroundStyle(.white)
                        
                        Text("\(entries.count) connected reflections")
                            .font(.subheadline.bold())
                            .foregroundStyle(.white.opacity(0.5))
                    }
                    
                    // Entries Timeline
                    VStack(spacing: 16) {
                        ForEach(entries) { entry in
                            Button {
                                withAnimation(.spring()) {
                                    activeReflectionId = entry.id
                                }
                            } label: {
                                LiquidEntryRow(summary: entry)
                            }
                            .buttonStyle(.plain)
                        }
                    }
                    
                    Spacer(minLength: 100)
                }
                .padding(28)
            }
        }
    }

    private func refresh() {
        entries = container.narrativeStore.entriesForThread(thread.id)
    }
}
