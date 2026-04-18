import Foundation
import SQLite3

enum MetricType: String, CaseIterable {
    case sleepHours = "SLEEP_HOURS"
    case activityMinutes = "ACTIVITY_MINUTES"
    case hydrationLitres = "HYDRATION_LITRES"
    case steps = "STEPS"
    case meditationMinutes = "MEDITATION_MINUTES"

    var displayName: String {
        switch self {
        case .sleepHours: return "Sleep"
        case .activityMinutes: return "Activity"
        case .hydrationLitres: return "Hydration"
        case .steps: return "Steps"
        case .meditationMinutes: return "Meditation"
        }
    }

    var maxValue: Double {
        switch self {
        case .sleepHours: return 12
        case .activityMinutes: return 120
        case .hydrationLitres: return 4
        case .steps: return 15000
        case .meditationMinutes: return 60
        }
    }
}

struct MetricSnapshot: Identifiable {
    var id: String { type.rawValue }
    let type: MetricType
    let value: Double
    let createdAt: Int64
}

final class MetricStore {
    private let db: WellnessDatabase

    init(db: WellnessDatabase) {
        self.db = db
    }

    func insert(type: MetricType, value: Double) {
        db.onQueue {
            let now = Int64(Date().timeIntervalSince1970 * 1000)
            let sql = "INSERT INTO metric_entries (createdAt, type, value) VALUES (?, ?, ?)"
            guard let stmt = db.prepare(sql) else { return }
            defer { sqlite3_finalize(stmt) }
            sqlite3_bind_int64(stmt, 1, now)
            sqlite3_bind_text(stmt, 2, (type.rawValue as NSString).utf8String, -1, nil)
            sqlite3_bind_double(stmt, 3, value)
            sqlite3_step(stmt)
        }
    }

    func latestPerType() -> [MetricSnapshot] {
        db.onQueue {
            var results: [MetricSnapshot] = []
            let sql = """
            SELECT type, value, MAX(createdAt) as createdAt
            FROM metric_entries GROUP BY type
            """
            guard let stmt = db.prepare(sql) else { return [] }
            defer { sqlite3_finalize(stmt) }
            while sqlite3_step(stmt) == SQLITE_ROW {
                let typeStr = String(cString: sqlite3_column_text(stmt, 0))
                guard let type = MetricType(rawValue: typeStr) else { continue }
                results.append(MetricSnapshot(
                    type: type,
                    value: sqlite3_column_double(stmt, 1),
                    createdAt: sqlite3_column_int64(stmt, 2)
                ))
            }
            return results
        }
    }
}
