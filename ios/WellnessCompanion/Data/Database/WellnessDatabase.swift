import Foundation
import SQLite3

final class WellnessDatabase {
    private var db: OpaquePointer?
    private let queue = DispatchQueue(label: "com.wellness.db", qos: .userInitiated)

    init() {
        let url = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
            .appendingPathComponent("wellness.db")
        guard sqlite3_open(url.path, &db) == SQLITE_OK else {
            fatalError("Cannot open database")
        }
        execute("PRAGMA journal_mode = WAL")
        execute("PRAGMA synchronous = NORMAL")
        execute("PRAGMA temp_store = MEMORY")
        execute("PRAGMA mmap_size = 67108864")
        createTables()
    }

    deinit {
        sqlite3_close(db)
    }

    func execute(_ sql: String) {
        var err: UnsafeMutablePointer<CChar>?
        sqlite3_exec(db, sql, nil, nil, &err)
        if let err = err {
            let msg = String(cString: err)
            sqlite3_free(err)
            print("SQL error: \(msg)")
        }
    }

    func prepare(_ sql: String) -> OpaquePointer? {
        var stmt: OpaquePointer?
        guard sqlite3_prepare_v2(db, sql, -1, &stmt, nil) == SQLITE_OK else {
            print("Prepare error: \(String(cString: sqlite3_errmsg(db)!))")
            return nil
        }
        return stmt
    }

    func onQueue<T>(_ work: () throws -> T) rethrows -> T {
        try queue.sync { try work() }
    }

    var handle: OpaquePointer? { db }

    private func createTables() {
        execute("""
        CREATE TABLE IF NOT EXISTS mood_entries (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            createdAt INTEGER NOT NULL,
            valence INTEGER NOT NULL,
            arousal INTEGER NOT NULL,
            label TEXT NOT NULL DEFAULT '',
            note TEXT NOT NULL DEFAULT ''
        )
        """)
        execute("CREATE INDEX IF NOT EXISTS idx_mood_createdAt ON mood_entries(createdAt)")
        execute("CREATE INDEX IF NOT EXISTS idx_mood_valence ON mood_entries(valence)")

        execute("""
        CREATE TABLE IF NOT EXISTS journal_entries (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            createdAt INTEGER NOT NULL,
            updatedAt INTEGER NOT NULL,
            title TEXT NOT NULL DEFAULT '',
            body TEXT NOT NULL DEFAULT '',
            wordCount INTEGER NOT NULL DEFAULT 0
        )
        """)
        execute("CREATE INDEX IF NOT EXISTS idx_journal_createdAt ON journal_entries(createdAt)")

        execute("""
        CREATE TABLE IF NOT EXISTS metric_entries (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            createdAt INTEGER NOT NULL,
            type TEXT NOT NULL,
            value REAL NOT NULL
        )
        """)
        execute("CREATE INDEX IF NOT EXISTS idx_metric_type_date ON metric_entries(type, createdAt)")

        execute("""
        CREATE TABLE IF NOT EXISTS narrative_threads (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            createdAt INTEGER NOT NULL,
            updatedAt INTEGER NOT NULL,
            label TEXT NOT NULL DEFAULT '',
            keywords TEXT NOT NULL DEFAULT '',
            status TEXT NOT NULL DEFAULT 'ongoing',
            entryCount INTEGER NOT NULL DEFAULT 0
        )
        """)

        execute("""
        CREATE TABLE IF NOT EXISTS thread_entry_refs (
            threadId INTEGER NOT NULL,
            entryId INTEGER NOT NULL,
            PRIMARY KEY (threadId, entryId),
            FOREIGN KEY (threadId) REFERENCES narrative_threads(id) ON DELETE CASCADE,
            FOREIGN KEY (entryId) REFERENCES journal_entries(id) ON DELETE CASCADE
        )
        """)

        execute("""
        CREATE TABLE IF NOT EXISTS entry_keywords (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            entryId INTEGER NOT NULL,
            keywords TEXT NOT NULL DEFAULT ''
        )
        """)
        execute("CREATE UNIQUE INDEX IF NOT EXISTS idx_kw_entry ON entry_keywords(entryId)")
    }
}
