import SwiftUI

struct MoodScreen: View {
    @EnvironmentObject private var container: AppContainer
    let onDone: () -> Void
    
    @State private var valence: Double = 0
    @State private var label = ""
    @State private var energy: Double = 50
    
    var body: some View {
        VStack(spacing: 0) {
            // 1. Sharp Header
            HStack {
                Button("Discard") { onDone() }.foregroundStyle(.white.opacity(0.4))
                Spacer()
                Text("THE CHECK-IN").miniCaps().foregroundStyle(.white.opacity(0.4))
                Spacer()
                Button("Commit") { save() }.bold().foregroundStyle(.white)
            }
            .padding(28)
            
            ScrollView(showsIndicators: false) {
                VStack(spacing: 80) {
                    
                    // 2. Confrontational Prompt
                    VStack(spacing: 20) {
                        Text("Locate your center.")
                            .font(.system(size: 40, weight: .black, design: .serif))
                            .foregroundStyle(.white)
                            .multilineTextAlignment(.center)
                        
                        Text("Avoid the easy answer.")
                            .font(.subheadline.bold())
                            .foregroundStyle(.cyan.opacity(0.6))
                    }
                    .padding(.top, 40)
                    
                    // 3. Precise Valence Input (The Tension)
                    VStack(spacing: 40) {
                        Text(valenceDescription)
                            .font(.headline)
                            .foregroundStyle(.white)
                            .id(valence)
                        
                        Slider(value: $valence, in: -100...100, step: 1)
                            .tint(.white)
                            .padding(.horizontal, 40)
                        
                        HStack {
                            Text("HEAVY").miniCaps()
                            Spacer()
                            Text("LIGHT").miniCaps()
                        }
                        .foregroundStyle(.white.opacity(0.3))
                        .padding(.horizontal, 44)
                    }
                    
                    // 4. Energy Dimension
                    VStack(spacing: 32) {
                        Text("Energy Level")
                            .font(.subheadline.bold())
                            .foregroundStyle(.white.opacity(0.4))
                        
                        HStack(spacing: 12) {
                            ForEach(0..<10) { i in
                                Rectangle()
                                    .fill(energy >= Double(i * 10) ? Color.white : Color.white.opacity(0.1))
                                    .frame(height: 40)
                                    .onTapGesture {
                                        energy = Double(i * 10 + 10)
                                        UIImpactFeedbackGenerator(style: .light).impactOccurred()
                                    }
                            }
                        }
                        .padding(.horizontal, 40)
                    }
                    
                    // 5. The One Word
                    VStack(spacing: 20) {
                        TextField("One word for this tension…", text: $label)
                            .font(.system(size: 24, weight: .medium, design: .serif))
                            .multilineTextAlignment(.center)
                            .foregroundStyle(.white)
                        
                        Rectangle().fill(.white.opacity(0.2)).frame(width: 200, height: 1)
                    }
                }
                .padding(.bottom, 100)
            }
        }
        .background(LiquidAura(scrollOffset: 0).ignoresSafeArea())
    }
    
    private var valenceDescription: String {
        if valence < -80 { return "Deeply Burdened" }
        if valence < -40 { return "Difficult / Heavy" }
        if valence < -10 { return "Subtly Clouded" }
        if valence < 10 { return "Neutral / Observation" }
        if valence < 40 { return "Clear / Calm" }
        if valence < 80 { return "Vibrant / Sharp" }
        return "High Radiance"
    }
    
    private func save() {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let entry = MoodEntry(
            id: 0,
            createdAt: now,
            valence: Int(valence),
            arousal: Int(energy),
            label: label.trimmingCharacters(in: .whitespaces),
            note: ""
        )
        _ = container.moodStore.insert(entry)
        
        // Correct MetricStore usage: insert(type:value:)
        container.metricStore.insert(type: .meditationMinutes, value: energy) 
        
        UIImpactFeedbackGenerator(style: .heavy).impactOccurred()
        onDone()
    }
}
