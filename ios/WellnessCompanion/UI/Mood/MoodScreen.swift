import SwiftUI

struct MoodScreen: View {
    @EnvironmentObject private var container: AppContainer
    @Environment(\.dismiss) private var dismiss
    
    @State private var valence: Double = 0
    @State private var arousal: Double = 0
    @State private var selectedQualities: Set<String> = []
    
    private let qualities = ["Peaceful", "Joyful", "Balanced", "Empowered", "Grateful", "Tired", "Worried", "Frustrated", "Overwhelmed", "Numb"]

    var body: some View {
        ZStack {
            // Immersive background that reacts to valence
            LiquidAura(scrollOffset: 0)
                .hueRotation(.degrees(valence * 30))
                .ignoresSafeArea()
            
            VStack(spacing: 0) {
                // 1. Minimal Header
                HStack {
                    Button { dismiss() } label: {
                        Image(systemName: "chevron.down")
                            .font(.title2.bold())
                            .foregroundStyle(.white.opacity(0.6))
                    }
                    Spacer()
                    Text("REFLECT").miniCaps().foregroundStyle(.white.opacity(0.4))
                    Spacer()
                    Button(action: save) {
                        Text("Save").bold().foregroundStyle(.white)
                    }
                }
                .padding(.horizontal, 28)
                .padding(.top, 20)
                
                ScrollView(showsIndicators: false) {
                    VStack(spacing: 60) {
                        // 2. The Liquid Wheel
                        VStack(spacing: 32) {
                            Text("How is your energy?")
                                .font(.system(size: 28, weight: .bold, design: .rounded))
                                .foregroundStyle(.white)
                            
                            LiquidMoodWheel(valence: $valence, arousal: $arousal)
                                .frame(width: 320, height: 320)
                        }
                        .padding(.top, 40)
                        
                        // 3. Apple Health style quality picker
                        VStack(alignment: .leading, spacing: 24) {
                            Text("Qualities").sectionHeader()
                            
                            FlowLayout(spacing: 12) {
                                ForEach(qualities, id: \.self) { quality in
                                    Button {
                                        if selectedQualities.contains(quality) { selectedQualities.remove(quality) }
                                        else { selectedQualities.insert(quality) }
                                        UIImpactFeedbackGenerator(style: .light).impactOccurred()
                                    } label: {
                                        Text(quality)
                                            .font(.subheadline.bold())
                                            .padding(.horizontal, 20)
                                            .padding(.vertical, 14)
                                            .background(selectedQualities.contains(quality) ? .white : .white.opacity(0.1))
                                            .foregroundStyle(selectedQualities.contains(quality) ? .black : .white)
                                            .clipShape(Capsule())
                                    }
                                    .buttonStyle(.plain)
                                }
                            }
                        }
                        
                        Spacer(minLength: 100)
                    }
                    .padding(28)
                }
            }
        }
    }
    
    private func save() {
        UINotificationFeedbackGenerator().notificationOccurred(.success)
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let label = selectedQualities.sorted().joined(separator: ", ")
        _ = container.moodStore.insert(MoodEntry(
            id: 0, createdAt: now,
            valence: Int(valence * 100), arousal: Int(arousal * 100),
            label: label, note: ""
        ))
        dismiss()
    }
}

struct LiquidMoodWheel: View {
    @Binding var valence: Double
    @Binding var arousal: Double
    
    var body: some View {
        GeometryReader { geo in
            let center = CGPoint(x: geo.size.width / 2, y: geo.size.height / 2)
            let radius = min(geo.size.width, geo.size.height) / 2
            
            ZStack {
                // Glowing borderless base
                Circle()
                    .fill(.white.opacity(0.05))
                    .blur(radius: 2)
                
                // Animated Segments
                Canvas { context, size in
                    let center = CGPoint(x: size.width / 2, y: size.height / 2)
                    let radius = min(size.width, size.height) / 2
                    
                    let colors: [Color] = [.liquidTeal, .liquidIndigo, .liquidRose, .liquidAmber]
                    
                    for i in 0..<4 {
                        var path = Path()
                        path.move(to: center)
                        path.addArc(center: center, radius: radius,
                                    startAngle: .degrees(Double(i) * 90 - 90),
                                    endAngle: .degrees(Double(i+1) * 90 - 90),
                                    clockwise: false)
                        path.closeSubpath()
                        context.fill(path, with: .color(colors[i].opacity(0.3)))
                    }
                }
                .mask(Circle())
                
                // Axis labels
                Group {
                    Text("energised").position(x: center.x, y: -10)
                    Text("calm").position(x: center.x, y: geo.size.height + 10)
                    Text("low").position(x: -20, y: center.y)
                    Text("high").position(x: geo.size.width + 20, y: center.y)
                }
                .font(.system(size: 10, weight: .black, design: .rounded))
                .textCase(.uppercase)
                .foregroundStyle(.white.opacity(0.3))
                
                // The Orb
                Circle()
                    .fill(.white)
                    .frame(width: 48, height: 48)
                    .shadow(color: .white.opacity(0.5), radius: 20)
                    .position(currentPos(in: geo.size))
                    .gesture(
                        DragGesture(minimumDistance: 0)
                            .onChanged { value in
                                updateState(from: value.location, in: geo.size)
                            }
                    )
            }
        }
    }
    
    private func currentPos(in size: CGSize) -> CGPoint {
        let center = CGPoint(x: size.width / 2, y: size.height / 2)
        let radius = min(size.width, size.height) / 2
        return CGPoint(
            x: center.x + CGFloat(valence) * radius,
            y: center.y - CGFloat(arousal) * radius
        )
    }
    
    private func updateState(from pos: CGPoint, in size: CGSize) {
        let center = CGPoint(x: size.width / 2, y: size.height / 2)
        let radius = min(size.width, size.height) / 2
        
        let dx = pos.x - center.x
        let dy = center.y - pos.y
        let dist = min(sqrt(dx*dx + dy*dy), radius)
        let angle = atan2(dy, dx)
        
        valence = Double(cos(angle) * dist / radius)
        arousal = Double(sin(angle) * dist / radius)
        
        UIImpactFeedbackGenerator(style: .soft).impactOccurred()
    }
}
