import XCTest
import SwiftUI
@testable import WellnessCompanion

final class AppContainerTests: XCTestCase {
    
    @MainActor
    func testAsyncInitialization() async {
        let container = AppContainer()
        
        // Wait for up to 5 seconds for initialization
        let expectation = XCTestExpectation(description: "Container becomes ready")
        
        var isReady = false
        for _ in 0..<50 {
            if container.isReady {
                isReady = true
                expectation.fulfill()
                break
            }
            try? await Task.sleep(nanoseconds: 100_000_000) // 0.1s
        }
        
        XCTAssertTrue(isReady, "AppContainer should become ready asynchronously")
        XCTAssertNotNil(container.database)
        XCTAssertNotNil(container.moodStore)
        XCTAssertNotNil(container.journalStore)
    }
    
    @MainActor
    func testBackgroundManagerPersistence() async {
        let manager = BackgroundManager()
        let testImage = UIImage(systemName: "star.fill")!
        
        manager.saveImage(testImage)
        
        // BackgroundManager uses Task.detached for saving, but property assignment is MainActor.
        XCTAssertEqual(manager.selectedImage, testImage)
    }
}
