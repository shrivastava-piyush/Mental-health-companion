import SwiftUI

struct JournalListScreen: View {
    @EnvironmentObject private var container: AppContainer
    @State private var entries: [JournalSummary] = []
    @State private var threads: [NarrativeThread] = []
    @State private var showEditor = false
    @State private var editingId: Int64? = nil
    @State private var showThread: NarrativeThread? = nil

    var body: some View {
        NavigationStack {
            List {
                Section {
                    Text("On-device only. Not backed up, not shared.")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                        .listRowBackground(Color.clear)
                }

                if !threads.isEmpty {
                    Section("Your threads") {
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 10) {
                                ForEach(threads) { thread in
                                    ThreadChipView(thread: thread) {
                                        showThread = thread
                                    }
                                }
                            }
                        }
                        .listRowInsets(EdgeInsets())
                        .listRowBackground(Color.clear)
                    }
                }

                if entries.isEmpty && threads.isEmpty {
                    Section {
                        Text("No entries yet.\nWrite freely — it stays on this device.")
                            .multilineTextAlignment(.center)
                            .foregroundStyle(.secondary)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 40)
                    }
                }

                if !entries.isEmpty {
                    Section("All entries") {
                        ForEach(entries) { summary in
                            Button {
                                editingId = summary.id
                                showEditor = true
                            } label: {
                                HStack {
                                    VStack(alignment: .leading, spacing: 4) {
                                        Text(summary.title)
                                            .font(.headline)
                                            .lineLimit(1)
                                        Text("\(summary.wordCount) words")
                                            .font(.caption)
                                            .foregroundStyle(.secondary)
                                    }
                                    Spacer()
                                    Text(formatDate(summary.updatedAt))
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                }
                            }
                            .tint(.primary)
                        }
                    }
                }
            }
            .navigationTitle("Journal")
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button {
                        editingId = nil
                        showEditor = true
                    } label: {
                        Image(systemName: "plus")
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
    }

    private func refresh() {
        entries = container.journalStore.fetchSummaries()
        threads = container.narrativeStore.activeThreads()
    }

    private func formatDate(_ millis: Int64) -> String {
        let f = DateFormatter()
        f.dateFormat = "MMM d"
        return f.string(from: Date(timeIntervalSince1970: Double(millis) / 1000))
    }
}

struct ThreadChipView: View {
    let thread: NarrativeThread
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            VStack(alignment: .leading, spacing: 4) {
                Text(thread.label)
                    .font(.subheadline)
                    .fontWeight(.medium)
                    .lineLimit(1)
                Text("\(thread.entryCount) entries")
                    .font(.caption2)
                    .foregroundStyle(.secondary)
                Text(thread.status.capitalized)
                    .font(.caption2)
                    .foregroundStyle(thread.status == "ongoing" ? .blue : .secondary)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .frame(width: 140)
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .fill(thread.status == "ongoing" ? Color.lavenderMist.opacity(0.3) : Color(.systemGray5))
            )
        }
        .tint(.primary)
    }
}

extension NarrativeThread: @retroactive Identifiable {}
