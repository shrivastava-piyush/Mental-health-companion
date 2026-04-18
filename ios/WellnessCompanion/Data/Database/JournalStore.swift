import Foundation
import Combine
import SQLite3

struct JournalEntry: Identifiable {
    let id: Int64
    let createdAt: Int64
    let updatedAt: Int64
    let title: String
    let body: String
    let wordCount: Int
}

struct JournalSummary: Identifiable {
    let id: Int64
    let createdAt: Int64
    let updatedAt: Int64
    let title: String
    let wordCount: Int
}

final class JournalStore: ObservableObject {
    private let db: WellnessDatabase
    @Published var count: Int = 0

    init(db: WellnessDatabase) {
        self.db = db
        refreshCount()
    }

    func save(_ entry: JournalEntry) -> Int64 {
        db.onQueue {
            if entry.id > 0 {
                let sql = "UPDATE journal_entries SET updatedAt=?, title=?, body=?, wordCount=? WHERE id=?"
                guard let stmt = db.prepare(sql) else { return entry.id }
                defer { sqlite3_finalize(stmt) }
                sqlite3_bind_int64(stmt, 1, entry.updatedAt)
                sqlite3_bind_text(stmt, 2, (entry.title as NSString).utf8String, -1, nil)
                sqlite3_bind_text(stmt, 3, (entry.body as NSString).utf8String, -1, nil)
                sqlite3_bind_int(stmt, 4, Int32(entry.wordCount))
                sqlite3_bind_int64(stmt, 5, entry.id)
                sqlite3_step(stmt)
                DispatchQueue.main.async { self.refreshCount() }
                return entry.id
            } else {
                let sql = "INSERT INTO journal_entries (createdAt, updatedAt, title, body, wordCount) VALUES (?, ?, ?, ?, ?)"
                guard let stmt = db.prepare(sql) else { return -1 }
                defer { sqlite3_finalize(stmt) }
                sqlite3_bind_int64(stmt, 1, entry.createdAt)
                sqlite3_bind_int64(stmt, 2, entry.updatedAt)
                sqlite3_bind_text(stmt, 3, (entry.title as NSString).utf8String, -1, nil)
                sqlite3_bind_text(stmt, 4, (entry.body as NSString).utf8String, -1, nil)
                sqlite3_bind_int(stmt, 5, Int32(entry.wordCount))
                sqlite3_step(stmt)
                let rowId = sqlite3_last_insert_rowid(db.handle)
                DispatchQueue.main.async { self.refreshCount() }
                return rowId
            }
        }
    }

    func fetchById(_ id: Int64) -> JournalEntry? {
        db.onQueue {
            let sql = "SELECT id, createdAt, updatedAt, title, body, wordCount FROM journal_entries WHERE id = ? LIMIT 1"
            guard let stmt = db.prepare(sql) else { return nil }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, id)
            guard sqlite3_step(stmt) == SQLITE_ROW else { return nil }
            return JournalEntry(
                id: sqlite3_column_int64(stmt, 0),
                createdAt: sqlite3_column_int64(stmt, 1),
                updatedAt: sqlite3_column_int64(stmt, 2),
                title: String(cString: sqlite3_column_text(stmt, 3)),
                body: String(cString: sqlite3_column_text(stmt, 4)),
                wordCount: Int(sqlite3_column_int(stmt, 5))
            )
        }
    }

    func fetchSummaries(limit: Int = 200) -> [JournalSummary] {
        db.onQueue {
            var results: [JournalSummary] = []
            let sql = "SELECT id, createdAt, updatedAt, title, wordCount FROM journal_entries ORDER BY createdAt DESC LIMIT ?"
            guard let stmt = db.prepare(sql) else { return [] }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int(stmt, 1, Int32(limit))
            while sqlite3_step(stmt) == SQLITE_ROW {
                results.append(JournalSummary(
                    id: sqlite3_column_int64(stmt, 0),
                    createdAt: sqlite3_column_int64(stmt, 1),
                    updatedAt: sqlite3_column_int64(stmt, 2),
                    title: String(cString: sqlite3_column_text(stmt, 3)),
                    wordCount: Int(sqlite3_column_int(stmt, 4))
                ))
            }
            return results
        }
    }

    func delete(id: Int64) {
        db.onQueue {
            db.execute("DELETE FROM journal_entries WHERE id = \(id)")
            DispatchQueue.main.async { self.refreshCount() }
        }
    }

    func recentEntriesWithBody(limit: Int = 200) -> [JournalEntry] {
        db.onQueue {
            var results: [JournalEntry] = []
            let sql = "SELECT id, createdAt, updatedAt, title, body, wordCount FROM journal_entries ORDER BY createdAt DESC LIMIT ?"
            guard let stmt = db.prepare(sql) else { return [] }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int(stmt, 1, Int32(limit))
            while sqlite3_step(stmt) == SQLITE_ROW {
                results.append(JournalEntry(
                    id: sqlite3_column_int64(stmt, 0),
                    createdAt: sqlite3_column_int64(stmt, 1),
                    updatedAt: sqlite3_column_int64(stmt, 2),
                    title: String(cString: sqlite3_column_text(stmt, 3)),
                    body: String(cString: sqlite3_column_text(stmt, 4)),
                    wordCount: Int(sqlite3_column_int(stmt, 5))
                ))
            }
            return results
        }
    }

    private func refreshCount() {
        let c: Int = db.onQueue {
            guard let stmt = db.prepare("SELECT COUNT(*) FROM journal_entries") else { return 0 }
            defer { sqlite3_finalize(stmt) }
            sqlite3_step(stmt)
            return Int(sqlite3_column_int(stmt, 0))
        }
        DispatchQueue.main.async { self.count = c }
    }
}
