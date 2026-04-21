import SwiftUI

struct LiquidEntryRow: View {
    let summary: JournalSummary
    
    var body: some View {
        HStack(spacing: 24) {
            VStack(alignment: .leading, spacing: 4) {
                Text(formatDay(summary.createdAt))
                    .font(.system(size: 28, weight: .black, design: .rounded))
                    .foregroundStyle(.white)
                Text(formatMonth(summary.createdAt))
                    .font(.system(size: 10, weight: .black, design: .rounded))
                    .textCase(.uppercase)
                    .tracking(2.0)
                    .foregroundStyle(.white.opacity(0.4))
            }
            .frame(width: 60)
            
            VStack(alignment: .leading, spacing: 6) {
                Text(summary.title)
                    .font(.system(size: 20, weight: .medium, design: .serif))
                    .foregroundStyle(.white)
                    .lineLimit(1)
                
                HStack {
                    Text("\(summary.wordCount) words")
                    Text("•")
                    Text(formatTime(summary.createdAt))
                }
                .font(.caption.bold())
                .foregroundStyle(.white.opacity(0.4))
            }
            
            Spacer()
            
            Image(systemName: "arrow.right")
                .font(.subheadline.bold())
                .foregroundStyle(.white.opacity(0.2))
        }
        .padding(24)
        .background(.white.opacity(0.05))
        .clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
        .overlay(RoundedRectangle(cornerRadius: 32, style: .continuous).stroke(.white.opacity(0.05), lineWidth: 1))
    }
    
    private func formatDay(_ m: Int64) -> String { let f = DateFormatter(); f.dateFormat = "dd"; return f.string(from: Date(timeIntervalSince1970: Double(m)/1000)) }
    private func formatMonth(_ m: Int64) -> String { let f = DateFormatter(); f.dateFormat = "MMM"; return f.string(from: Date(timeIntervalSince1970: Double(m)/1000)) }
    private func formatTime(_ m: Int64) -> String { let f = DateFormatter(); f.timeStyle = .short; return f.string(from: Date(timeIntervalSince1970: Double(m)/1000)) }
}
