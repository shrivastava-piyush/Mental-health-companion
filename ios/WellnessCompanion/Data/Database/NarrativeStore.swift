import Foundation
import SQLite3

struct NarrativeThread: Identifiable {
    let id: Int64
    let createdAt: Int64
    var updatedAt: Int64
    var label: String
    var keywords: String
    var status: String
    var entryCount: Int
}

struct EntryKeywords {
    let entryId: Int64
    let keywords: String
}

struct KeywordedEntry {
    let id: Int64
    let createdAt: Int64
    let title: String
    let body: String
    let keywords: String
}

final class NarrativeStore {
    private let db: WellnessDatabase

    init(db: WellnessDatabase) {
        self.db = db
    }

    func insertThread(_ thread: NarrativeThread) -> Int64 {
        db.onQueue {
            let sql = "INSERT INTO narrative_threads (createdAt, updatedAt, label, keywords, status, entryCount) VALUES (?, ?, ?, ?, ?, ?)"
            guard let stmt = db.prepare(sql) else { return -1 }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, thread.createdAt)
            sqlite3_bind_int64(stmt, 2, thread.updatedAt)
            sqlite3_bind_text(stmt, 3, (thread.label as NSString).utf8String, -1, nil)
            sqlite3_bind_text(stmt, 4, (thread.keywords as NSString).utf8String, -1, nil)
            sqlite3_bind_text(stmt, 5, (thread.status as NSString).utf8String, -1, nil)
            sqlite3_bind_int(stmt, 6, Int32(thread.entryCount))
            sqlite3_step(stmt)
            return sqlite3_last_insert_rowid(db.handle)
        }
    }

    func updateThread(_ thread: NarrativeThread) {
        db.onQueue {
            let sql = "UPDATE narrative_threads SET updatedAt=?, label=?, keywords=?, status=?, entryCount=? WHERE id=?"
            guard let stmt = db.prepare(sql) else { return }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, thread.updatedAt)
            sqlite3_bind_text(stmt, 2, (thread.label as NSString).utf8String, -1, nil)
            sqlite3_bind_text(stmt, 3, (thread.keywords as NSString).utf8String, -1, nil)
            sqlite3_bind_text(stmt, 4, (thread.status as NSString).utf8String, -1, nil)
            sqlite3_bind_int(stmt, 5, Int32(thread.entryCount))
            sqlite3_bind_int64(stmt, 6, thread.id)
            sqlite3_step(stmt)
        }
    }

    func activeThreads(limit: Int = 10) -> [NarrativeThread] {
        db.onQueue {
            var results: [NarrativeThread] = []
            let sql = "SELECT id, createdAt, updatedAt, label, keywords, status, entryCount FROM narrative_threads WHERE status = 'ongoing' ORDER BY updatedAt DESC LIMIT ?"
            guard let stmt = db.prepare(sql) else { return [] }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int(stmt, 1, Int32(limit))
            while sqlite3_step(stmt) == SQLITE_ROW {
                results.append(readThread(stmt))
            }
            return results
        }
    }

    func allThreads() -> [NarrativeThread] {
        db.onQueue {
            var results: [NarrativeThread] = []
            guard let stmt = db.prepare("SELECT id, createdAt, updatedAt, label, keywords, status, entryCount FROM narrative_threads ORDER BY updatedAt DESC") else { return [] }
            defer { sqlite3_finalize(stmt) }
            while sqlite3_step(stmt) == SQLITE_ROW {
                results.append(readThread(stmt))
            }
            return results
        }
    }

    func insertRef(threadId: Int64, entryId: Int64) {
        db.onQueue {
            db.execute("INSERT OR IGNORE INTO thread_entry_refs (threadId, entryId) VALUES (\(threadId), \(entryId))")
        }
    }

    func entriesForThread(_ threadId: Int64) -> [JournalSummary] {
        db.onQueue {
            var results: [JournalSummary] = []
            let sql = """
            SELECT je.id, je.createdAt, je.updatedAt, je.title, je.wordCount
            FROM journal_entries je
            INNER JOIN thread_entry_refs r ON r.entryId = je.id
            WHERE r.threadId = ?
            ORDER BY je.createdAt DESC
            """
            guard let stmt = db.prepare(sql) else { return [] }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, threadId)
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

    func insertKeywords(entryId: Int64, keywords: String) {
        db.onQueue {
            let sql = "INSERT OR REPLACE INTO entry_keywords (entryId, keywords) VALUES (?, ?)"
            guard let stmt = db.prepare(sql) else { return }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, entryId)
            sqlite3_bind_text(stmt, 2, (keywords as NSString).utf8String, -1, nil)
            sqlite3_step(stmt)
        }
    }

    func keywordsForEntry(_ entryId: Int64) -> String? {
        db.onQueue {
            let sql = "SELECT keywords FROM entry_keywords WHERE entryId = ? LIMIT 1"
            guard let stmt = db.prepare(sql) else { return nil }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, entryId)
            guard sqlite3_step(stmt) == SQLITE_ROW else { return nil }
            return String(cString: sqlite3_column_text(stmt, 0))
        }
    }

    func recentEntriesWithKeywords(before: Int64, limit: Int = 30) -> [KeywordedEntry] {
        db.onQueue {
            var results: [KeywordedEntry] = []
            let sql = """
            SELECT je.id, je.createdAt, je.title, je.body, ek.keywords
            FROM journal_entries je
            INNER JOIN entry_keywords ek ON ek.entryId = je.id
            WHERE je.createdAt < ?
            ORDER BY je.createdAt DESC LIMIT ?
            """
            guard let stmt = db.prepare(sql) else { return [] }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, before)
            sqlite3_bind_int(stmt, 2, Int32(limit))
            while sqlite3_step(stmt) == SQLITE_ROW {
                results.append(KeywordedEntry(
                    id: sqlite3_column_int64(stmt, 0),
                    createdAt: sqlite3_column_int64(stmt, 1),
                    title: String(cString: sqlite3_column_text(stmt, 2)),
                    body: String(cString: sqlite3_column_text(stmt, 3)),
                    keywords: String(cString: sqlite3_column_text(stmt, 4))
                ))
            }
            return results
        }
    }

    func entriesNearSameDayOfMonth(now: Int64, limit: Int = 5) -> [JournalEntry] {
        db.onQueue {
            var results: [JournalEntry] = []
            let sql = """
            SELECT id, createdAt, updatedAt, title, body, wordCount FROM journal_entries
            WHERE abs((createdAt / 86400000) % 30 - (? / 86400000) % 30) <= 1
            ORDER BY createdAt DESC LIMIT ?
            """
            guard let stmt = db.prepare(sql) else { return [] }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, now)
            sqlite3_bind_int(stmt, 2, Int32(limit))
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

    private func readThread(_ stmt: OpaquePointer?) -> NarrativeThread {
        NarrativeThread(
            id: sqlite3_column_int64(stmt, 0),
            createdAt: sqlite3_column_int64(stmt, 1),
            updatedAt: sqlite3_column_int64(stmt, 2),
            label: String(cString: sqlite3_column_text(stmt, 3)),
            keywords: String(cString: sqlite3_column_text(stmt, 4)),
            status: String(cString: sqlite3_column_text(stmt, 5)),
            entryCount: Int(sqlite3_column_int(stmt, 6))
        )
    }
}
