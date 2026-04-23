import Foundation
import Quartz

let args = CommandLine.arguments
if args.count < 3 {
    print("Usage: extract.swift <input.pdf> <output.txt>")
    exit(1)
}
let inUrl = URL(fileURLWithPath: args[1])
let outUrl = URL(fileURLWithPath: args[2])

if let pdf = PDFDocument(url: inUrl) {
    if let text = pdf.string {
        do {
            try text.write(to: outUrl, atomically: true, encoding: .utf8)
            print("Extracted text successfully.")
        } catch {
            print("Failed to write to output file: \(error)")
        }
    } else {
        print("Could not extract text from PDF.")
    }
} else {
    print("Could not load PDF.")
}
