import SwiftUI

struct ThreadDetailScreen: View {
    @EnvironmentObject private var container: AppContainer
    @Environment(\.dismiss) private var dismiss
    let thread: NarrativeThread

    @State private var entries: [JournalSummary] = []
    @State private var selectedEntryId: Int64? = nil
    @State private var showEditor = false

    var body: some View {
        NavigationStack {
            List {
                Section {
                    Text("\(entries.count) entries in this thread")
                        .foregroundStyle(.secondary)
                }

                ForEach(entries) { entry in
                    Button {
                        selectedEntryId = entry.id
                        showEditor = true
                    } label: {
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(entry.title)
                                    .font(.headline)
                                    .lineLimit(1)
                                Text("\(entry.wordCount) words")
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                            Spacer()
                            Text(formatDate(entry.createdAt))
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                    }
                    .tint(.primary)
                }
            }
            .navigationTitle(thread.label)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Done") { dismiss() }
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

    private func formatDate(_ millis: Int64) -> String {
        let f = DateFormatter()
        f.dateFormat = "MMM d, yyyy"
        return f.string(from: Date(timeIntervalSince1970: Double(millis) / 1000))
    }
}
