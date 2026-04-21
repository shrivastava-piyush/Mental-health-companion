import SwiftUI

struct ThreadDetailScreen: View {
    @EnvironmentObject private var container: AppContainer
    @Environment(\.dismiss) private var dismiss
    let thread: NarrativeThread

    @State private var entries: [JournalSummary] = []
    @State private var selectedEntryId: Int64? = nil
    @State private var showEditor = false

    var body: some View {
        ZStack {
            LiquidAura(scrollOffset: 0).ignoresSafeArea()
            
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
                                    selectedEntryId = entry.id
                                    showEditor = true
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
        .sheet(isPresented: $showEditor) {
            if let id = selectedEntryId {
                JournalEditorScreen(entryId: id)
            }
        }
        .onAppear {
            entries = container.narrativeStore.entriesForThread(thread.id)
        }
    }
}
