import SwiftUI
import PhotosUI

struct InsightsScreen: View {
    @EnvironmentObject private var container: AppContainer
    @EnvironmentObject private var bgManager: BackgroundManager
    
    @State private var totalMoods = 0; @State private var totalJournals = 0
    @State private var trend: [DailyMoodBucket] = []
    @State private var patternNarrative = ""
    
    @State private var photoPickerItem: PhotosPickerItem? = nil

    var body: some View {
        ScrollView(showsIndicators: false) {
            VStack(alignment: .leading, spacing: 50) {
                
                // 1. Personalization Header
                VStack(alignment: .leading, spacing: 20) {
                    Text("PULSE").miniCaps().foregroundStyle(Color.white.opacity(0.4))
                    Text("Patterns").font(.system(size: 44, weight: .black, design: .rounded)).foregroundStyle(.white)
                    
                    // The Personalize Trigger (Faded memory engine)
                    PhotosPicker(selection: $photoPickerItem, matching: .images) {
                        HStack(spacing: 12) {
                            Image(systemName: "photo.stack.fill").font(.caption)
                            Text(bgManager.selectedImage == nil ? "Personalize Background" : "Update Memory")
                                .font(.system(size: 12, weight: .black)).miniCaps()
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(.white.opacity(0.1), in: Capsule())
                        .foregroundStyle(.white)
                    }
                    .onChange(of: photoPickerItem) { newItem in
                        handlePickerChange(newItem)
                    }
                }
                .padding(.top, 40)
                
                HStack(spacing: 20) {
                    statCard(label: "Logs", value: "\(totalMoods)", icon: "face.smiling", color: .liquidTeal)
                    statCard(label: "Notes", value: "\(totalJournals)", icon: "text.quote", color: .liquidRose)
                }
                
                if !patternNarrative.isEmpty {
                    VStack(alignment: .leading, spacing: 20) {
                        Text("Perspective").sectionHeader()
                        VStack(alignment: .leading, spacing: 20) {
                            Image(systemName: "quote.opening").font(.title).foregroundStyle(.white.opacity(0.2))
                            Text(patternNarrative).font(.system(size: 20, weight: .medium, design: .serif)).italic().lineSpacing(8).foregroundStyle(.white)
                        }
                        .padding(32).background(.white.opacity(0.08)).clipShape(RoundedRectangle(cornerRadius: 40, style: .continuous))
                    }
                }
                
                VStack(alignment: .leading, spacing: 24) {
                    Text("Emotional Flow").sectionHeader()
                    MoodTrendChartView(buckets: trend).frame(height: 180).padding(24).background(.white.opacity(0.05)).clipShape(RoundedRectangle(cornerRadius: 40, style: .continuous))
                }
                
                downloadCard(manager: container.modelManager)
                
                Spacer(minLength: 150)
            }
            .padding(.horizontal, 28) // Strict Boundary Protection
        }
        .onAppear(perform: refresh)
    }
    
    private func handlePickerChange(_ item: PhotosPickerItem?) {
        Task {
            if let data = try? await item?.loadTransferable(type: Data.self),
               let image = UIImage(data: data) {
                await MainActor.run {
                    bgManager.saveImage(image)
                    UIImpactFeedbackGenerator(style: .medium).impactOccurred()
                }
            }
        }
    }
    
    @ViewBuilder
    private func statCard(label: String, value: String, icon: String, color: Color) -> some View {
        VStack(alignment: .leading, spacing: 14) {
            ZStack {
                Circle().fill(color.opacity(0.2)).frame(width: 44, height: 44)
                Image(systemName: icon).font(.headline).foregroundStyle(color)
            }
            VStack(alignment: .leading, spacing: 0) {
                Text(value).font(.system(size: 32, weight: .black, design: .rounded)).foregroundStyle(.white)
                Text(label).miniCaps().foregroundStyle(Color.white.opacity(0.4))
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(24)
        .background(.white.opacity(0.08))
        .clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
    }

    @ViewBuilder
    private func downloadCard(manager: ModelManager) -> some View {
        VStack(alignment: .leading, spacing: 20) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("THE MIRROR").miniCaps().foregroundStyle(Color.white.opacity(0.4))
                    Text("Offline Intelligence").font(.headline).foregroundStyle(.white)
                }
                Spacer(); Image(systemName: "brain.head.profile").font(.title2).foregroundStyle(.cyan)
            }
            
            Group {
                switch manager.status {
                case .notDownloaded:
                    Button {
                        UIImpactFeedbackGenerator(style: .medium).impactOccurred()
                        manager.download(url: "https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf")
                    } label: {
                        Text("Download Assistant")
                            .font(.subheadline.bold())
                            .foregroundStyle(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 54)
                            .background(Color.liquidIndigo, in: Capsule())
                            .overlay(Capsule().stroke(.white.opacity(0.1), lineWidth: 1))
                    }
                    .buttonStyle(.plain)
                    
                case .downloading(let progress):
                    VStack(spacing: 12) {
                        HStack {
                            Text("Synchronizing…").font(.caption.bold()).foregroundStyle(.cyan)
                            Spacer()
                            Text("\(Int(progress * 100))%").font(.system(size: 10, weight: .black, design: .monospaced)).foregroundStyle(.white)
                        }
                        ZStack(alignment: .leading) {
                            Capsule().fill(.white.opacity(0.1)).frame(height: 8)
                            Capsule()
                                .fill(LinearGradient(colors: [.cyan, .liquidTeal], startPoint: .leading, endPoint: .trailing))
                                .frame(width: max(0, min(1, CGFloat(progress))) * 200, height: 8) // Approximating width relative to padding
                        }
                    }
                    
                case .ready:
                    Label("Mirror Ready", systemImage: "checkmark.seal.fill").font(.subheadline.bold()).foregroundStyle(.cyan)
                case .error(let msg):
                    Text(msg).font(.caption).foregroundStyle(.red)
                }
            }
        }
        .padding(28)
        .background(.white.opacity(0.05))
        .clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
        .overlay(RoundedRectangle(cornerRadius: 32, style: .continuous).stroke(.white.opacity(0.1), lineWidth: 1))
    }
    
    private func refresh() {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let thirtyDaysAgo = now - 30 * 86_400_000
        totalMoods = container.moodStore.fetchRange(from: thirtyDaysAgo, to: now).count
        totalJournals = container.journalStore.fetchSummaries().count
        trend = container.moodStore.dailyAggregate(from: thirtyDaysAgo, to: now)
        Task {
            let df = DateFormatter(); df.dateFormat = "MMM d"
            let label = "\(df.string(from: Date(timeIntervalSince1970: Double(thirtyDaysAgo) / 1000))) — \(df.string(from: Date()))"
            let m = container.mirrorGenerator.generate(from: thirtyDaysAgo, to: now, periodLabel: label)
            if let m, let engine = container.reflectionEngine {
                let narrative = await engine.narrateMirror(m)
                await MainActor.run { withAnimation(.spring()) { patternNarrative = narrative ?? "" } }
            }
        }
    }
}
