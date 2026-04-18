import SwiftUI

struct MainTabView: View {
    @EnvironmentObject private var container: AppContainer

    var body: some View {
        TabView {
            MoodScreen()
                .tabItem {
                    Label("Mood", systemImage: "face.smiling")
                }

            JournalListScreen()
                .tabItem {
                    Label("Journal", systemImage: "note.text")
                }

            InsightsScreen()
                .tabItem {
                    Label("Insights", systemImage: "chart.line.uptrend.xyaxis")
                }
        }
    }
}
