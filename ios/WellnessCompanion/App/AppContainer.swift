import Foundation
import Combine

final class AppContainer: ObservableObject {
    let database: WellnessDatabase
    let moodStore: MoodStore
    let journalStore: JournalStore
    let metricStore: MetricStore
    let narrativeStore: NarrativeStore

    lazy var threadDetector = ThreadDetector(store: narrativeStore)
    lazy var coldOpenGenerator = ColdOpenGenerator(narrativeStore: narrativeStore, moodStore: moodStore)
    lazy var mirrorGenerator = MirrorGenerator(moodStore: moodStore, journalStore: journalStore, narrativeStore: narrativeStore)

    let modelManager: ModelManager

    var reflectionEngine: ReflectionEngine? {
        guard modelManager.isDownloaded else { return nil }
        return ReflectionEngine(engine: llamaEngine)
    }

    private lazy var llamaEngine = LlamaEngine(modelPath: modelManager.modelPath)

    init() {
        database = WellnessDatabase()
        moodStore = MoodStore(db: database)
        journalStore = JournalStore(db: database)
        metricStore = MetricStore(db: database)
        narrativeStore = NarrativeStore(db: database)
        modelManager = ModelManager()
    }
}
