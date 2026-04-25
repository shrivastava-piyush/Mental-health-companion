import Foundation
import Combine

/// The dependency injection container for the application.
/// Optimized for ultra-fast startup by offloading heavy init to background tasks.
final class AppContainer: ObservableObject {
    @Published var isReady = false
    
    // Explicitly optional or implicitly unwrapped with careful management
    private(set) var database: WellnessDatabase!
    private(set) var moodStore: MoodStore!
    private(set) var journalStore: JournalStore!
    private(set) var metricStore: MetricStore!
    private(set) var narrativeStore: NarrativeStore!
    private(set) var modelManager: ModelManager!
    
    // Global Managers (Lightweight)
    let backgroundManager = BackgroundManager()
    let atmosphereManager = AtmosphereManager()

    private var _llamaEngine: LlamaEngine?
    var reflectionEngine: ReflectionEngine? {
        guard isReady, modelManager.isDownloaded, let engine = _llamaEngine else { return nil }
        return ReflectionEngine(engine: engine)
    }

    init() {
        // AppContainer itself remains lightweight
        Task {
            await initializeCore()
        }
    }
    
    @MainActor
    private func initializeCore() async {
        // Offload heavy DB and FS work to background
        await Task.detached(priority: .userInitiated) {
            let db = WellnessDatabase()
            let mStore = MoodStore(db: db)
            let jStore = JournalStore(db: db)
            let metStore = MetricStore(db: db)
            let nStore = NarrativeStore(db: db)
            let mManager = ModelManager()
            
            let engine = LlamaEngine(modelPath: mManager.modelPath)
            
            await MainActor.run {
                self.database = db
                self.moodStore = mStore
                self.journalStore = jStore
                self.metricStore = metStore
                self.narrativeStore = nStore
                self.modelManager = mManager
                self._llamaEngine = engine
                
                // Final signal that UI can unlock
                self.isReady = true
            }
        }.value
    }
    
    /// Thread Detector and Generators (Lazy loaded when needed)
    lazy var threadDetector: ThreadDetector = { ThreadDetector(store: narrativeStore) }()
    lazy var coldOpenGenerator: ColdOpenGenerator = { ColdOpenGenerator(narrativeStore: narrativeStore, moodStore: moodStore) }()
    lazy var mirrorGenerator: MirrorGenerator = { MirrorGenerator(moodStore: moodStore, journalStore: journalStore, narrativeStore: narrativeStore) }()
}
