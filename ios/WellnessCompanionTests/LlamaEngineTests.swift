import XCTest
@testable import WellnessCompanion

final class LlamaEngineTests: XCTestCase {
    
    func testLlamaInitialization() async {
        // This test requires a valid model file to be present.
        // Since we can't guarantee that in the test environment, we'll just test 
        // that it doesn't crash on init and that isReady is false initially.
        let engine = LlamaEngine(modelPath: "/tmp/non_existent.gguf")
        XCTAssertFalse(engine.isReady, "Engine should not be ready with non-existent model")
    }
}
