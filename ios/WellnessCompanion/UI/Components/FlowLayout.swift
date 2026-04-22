import SwiftUI

struct FlowLayout: Layout {
    var spacing: CGFloat = 8
    
    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let width = proposal.width ?? .infinity
        var x: CGFloat = 0; var y: CGFloat = 0; var lineH: CGFloat = 0; var maxW: CGFloat = 0
        for view in subviews {
            let s = view.sizeThatFits(.unspecified)
            if x + s.width > width { x = 0; y += lineH + spacing; lineH = 0 }
            x += s.width + spacing; lineH = max(lineH, s.height); maxW = max(maxW, x)
        }
        return CGSize(width: maxW, height: y + lineH)
    }
    
    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        var x = bounds.minX; var y = bounds.minY; var lineH: CGFloat = 0
        for view in subviews {
            let s = view.sizeThatFits(.unspecified)
            if x + s.width > bounds.maxX { x = bounds.minX; y += lineH + spacing; lineH = 0 }
            view.place(at: CGPoint(x: x, y: y), proposal: .unspecified)
            x += s.width + spacing; lineH = max(lineH, s.height)
        }
    }
}
