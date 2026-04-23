import SwiftUI

struct FlowLayout: Layout {
    var spacing: CGFloat = 8
    
    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        // Respect the proposed width from the parent (e.g. the screen width minus padding)
        let width = proposal.width ?? 300
        var x: CGFloat = 0; var y: CGFloat = 0; var lineH: CGFloat = 0; var maxW: CGFloat = 0
        
        for view in subviews {
            let s = view.sizeThatFits(.unspecified)
            // If this word exceeds the available width, wrap to next line
            if x + s.width > width && x > 0 {
                x = 0
                y += lineH + spacing
                lineH = 0
            }
            x += s.width + spacing
            lineH = max(lineH, s.height)
            maxW = max(maxW, x)
        }
        return CGSize(width: min(width, maxW), height: y + lineH)
    }
    
    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        var x = bounds.minX; var y = bounds.minY; var lineH: CGFloat = 0
        for view in subviews {
            let s = view.sizeThatFits(.unspecified)
            // Wrap to next line if needed
            if x + s.width > bounds.maxX && x > bounds.minX {
                x = bounds.minX
                y += lineH + spacing
                lineH = 0
            }
            view.place(at: CGPoint(x: x, y: y), proposal: .unspecified)
            x += s.width + spacing
            lineH = max(lineH, s.height)
        }
    }
}
