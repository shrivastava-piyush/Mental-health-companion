import Foundation
import Combine
import SQLite3

struct MoodEntry: Identifiable {
    let id: Int64
    let createdAt: Int64
    let valence: Int
    let arousal: Int
    let label: String
    let note: String
}

struct DailyMoodBucket {
    let dayBucket: Int64
    let avgValence: Double
    let avgArousal: Double
    let sampleCount: Int
}

final class MoodStore: ObservableObject {
    private let db: WellnessDatabase
    @Published var entries: [MoodEntry] = []
    @Published var count: Int = 0

    init(db: WellnessDatabase) {
        self.db = db
        refreshCount()
    }

    func insert(_ entry: MoodEntry) -> Int64 {
        db.onQueue {
            let sql = "INSERT INTO mood_entries (createdAt, valence, arousal, label, note) VALUES (?, ?, ?, ?, ?)"
            guard let stmt = db.prepare(sql) else { return -1 }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, entry.createdAt)
            sqlite3_bind_int(stmt, 2, Int32(entry.valence))
            sqlite3_bind_int(stmt, 3, Int32(entry.arousal))
            sqlite3_bind_text(stmt, 4, (entry.label as NSString).utf8String, -1, nil)
            sqlite3_bind_text(stmt, 5, (entry.note as NSString).utf8String, -1, nil)
            sqlite3_step(stmt)
            let rowId = sqlite3_last_insert_rowid(db.handle)
            DispatchQueue.main.async { self.refreshCount() }
            return rowId
        }
    }

    func delete(id: Int64) {
        db.onQueue {
            db.execute("DELETE FROM mood_entries WHERE id = \(id)")
            DispatchQueue.main.async { self.refreshCount() }
        }
    }

    func fetchRange(from: Int64, to: Int64) -> [MoodEntry] {
        db.onQueue {
            var results: [MoodEntry] = []
            let sql = "SELECT id, createdAt, valence, arousal, label, note FROM mood_entries WHERE createdAt BETWEEN ? AND ? ORDER BY createdAt DESC"
            guard let stmt = db.prepare(sql) else { return [] }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, from)
            sqlite3_bind_int64(stmt, 2, to)
            while sqlite3_step(stmt) == SQLITE_ROW {
                results.append(MoodEntry(
                    id: sqlite3_column_int64(stmt, 0),
                    createdAt: sqlite3_column_int64(stmt, 1),
                    valence: Int(sqlite3_column_int(stmt, 2)),
                    arousal: Int(sqlite3_column_int(stmt, 3)),
                    label: String(cString: sqlite3_column_text(stmt, 4)),
                    note: String(cString: sqlite3_column_text(stmt, 5))
                ))
            }
            return results
        }
    }

    func dailyAggregate(from: Int64, to: Int64) -> [DailyMoodBucket] {
        db.onQueue {
            var results: [DailyMoodBucket] = []
            let sql = """
            SELECT (createdAt / 86400000) AS dayBucket, AVG(valence), AVG(arousal), COUNT(*)
            FROM mood_entries WHERE createdAt BETWEEN ? AND ?
            GROUP BY dayBucket ORDER BY dayBucket ASC
            """
            guard let stmt = db.prepare(sql) else { return [] }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, from)
            sqlite3_bind_int64(stmt, 2, to)
            while sqlite3_step(stmt) == SQLITE_ROW {
                results.append(DailyMoodBucket(
                    dayBucket: sqlite3_column_int64(stmt, 0),
                    avgValence: sqlite3_column_double(stmt, 1),
                    avgArousal: sqlite3_column_double(stmt, 2),
                    sampleCount: Int(sqlite3_column_int(stmt, 3))
                ))
            }
            return results
        }
    }

    func recentLabels(limit: Int) -> [String] {
        db.onQueue {
            var results: [String] = []
            let sql = "SELECT label FROM mood_entries ORDER BY createdAt DESC LIMIT ?"
            guard let stmt = db.prepare(sql) else { return [] }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int(stmt, 1, Int32(limit))
            while sqlite3_step(stmt) == SQLITE_ROW {
                results.append(String(cString: sqlite3_column_text(stmt, 0)))
            }
            return results
        }
    }

    func avgValence(from: Int64, to: Int64) -> Double? {
        db.onQueue {
            let sql = "SELECT AVG(valence) FROM mood_entries WHERE createdAt BETWEEN ? AND ?"
            guard let stmt = db.prepare(sql) else { return nil }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, from)
            sqlite3_bind_int64(stmt, 2, to)
            guard sqlite3_step(stmt) == SQLITE_ROW else { return nil }
            return sqlite3_column_type(stmt, 0) != SQLITE_NULL ? sqlite3_column_double(stmt, 0) : nil
        }
    }

    private func refreshCount() {
        let c: Int = db.onQueue {
            let sql = "SELECT COUNT(*) FROM mood_entries"
            guard let stmt = db.prepare(sql) else { return 0 }
            defer { sqlite3_finalize(stmt) }
            sqlite3_step(stmt)
            return Int(sqlite3_column_int(stmt, 0))
        }
        DispatchQueue.main.async { self.count = c }
    }
}
