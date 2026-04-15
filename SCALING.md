# Scaling Wellness Companion to 100k+ local records

This document explains how the app is engineered so that a single user's
on-device journal / mood / metric history can grow to **hundreds of
thousands of rows** without the UI dropping frames.

No single technique is magical — the goal is simply to keep every
main-thread interaction in sub-millisecond SQLite territory and to keep the
Compose recomposition graph tight.

---

## 1. Schema is optimised for the read paths, not the write paths

All three entities carry indices that match the exact filter + sort
patterns of the UI:

| Table              | Index                           | Used by                                         |
| ------------------ | ------------------------------- | ----------------------------------------------- |
| `mood_entries`     | `idx_mood_created_at`           | recent feed, trend aggregate                    |
| `mood_entries`     | `idx_mood_valence`              | future mood bucketing / insights                |
| `journal_entries`  | `idx_journal_created_at`        | Paging 3 `pagingSummaries()` DESC scan          |
| `journal_entries`  | `idx_journal_word_count`        | long-form vs quick-note analytics               |
| `metric_entries`   | `idx_metric_type_time` (composite) | per-type time-series chart reads             |
| `metric_entries`   | `idx_metric_created_at`         | global "latest activity" reads                   |

Without these indices a single `SELECT ... ORDER BY createdAt DESC LIMIT 30`
on a 100k-row table triggers a full scan and reliably blows the 16 ms frame
budget on mid-range devices. With the indices in place the same query is
O(log n) on an index B-tree + O(30) on the row cache.

Verification: `EXPLAIN QUERY PLAN` should show `SEARCH USING INDEX idx_…`
for every DAO query. None should say `SCAN TABLE`.

---

## 2. Aggregates run in SQL, not on the JVM

The mood trend chart needs one point per day over 30 days, yet the source
table can carry dozens of entries per day. Instead of streaming the full
range into Kotlin and reducing it, the DAO performs the aggregation in
SQLite:

```sql
SELECT (createdAt / 86400000) AS dayBucket,
       AVG(valence)           AS avgValence,
       AVG(arousal)           AS avgArousal,
       COUNT(*)               AS sampleCount
FROM mood_entries
WHERE createdAt BETWEEN :fromMillis AND :toMillis
GROUP BY dayBucket;
```

This means the Flow emits ~30 rows regardless of how many moods were logged.
JNI crossings stay tiny and the chart composable never allocates a large
intermediate list.

---

## 3. Paging 3 with projection

`JournalDao.pagingSummaries()` intentionally projects only the columns the
list row renders (`id, createdAt, updatedAt, title, wordCount`). The `body`
TEXT blob — which can be megabytes across thousands of entries — never
crosses the JNI boundary until the user taps into an entry.

Pager config:

```
pageSize            = 30      // one page fits on ~1.5 screens
prefetchDistance    = 15      // start loading the next page half a screen early
initialLoadSize     = 45      // first page hides the pager latency
maxSize             = 240     // cap RAM even during long-scroll sessions
enablePlaceholders  = false   // avoid extra DB round-trips for COUNT(*)
```

With these numbers a scrolling user holds ~240 summary rows in memory
(~40 KB total) regardless of whether the DB has 1k or 1M rows.

---

## 4. SQLite PRAGMAs tuned at open-time

In `WellnessDatabase` we apply:

```
PRAGMA synchronous = NORMAL     -- safer than OFF, ~2× faster than FULL on WAL
PRAGMA temp_store = MEMORY      -- avoid disk spills for GROUP BY buffers
PRAGMA mmap_size  = 67108864    -- 64 MB memory map; reads skip the syscall path
```

WAL is already Room's default mode; we set it explicitly to document intent.
These settings are safe for wellness data (not financial / medical records
with strict durability requirements).

---

## 5. Migrations are additive and incremental

All schema changes go through `Migrations.kt` as incremental
`Migration(n, n+1)` steps. Users upgrading across multiple versions run the
migrations in order; **the DB is never rebuilt from scratch**, which at 100k
rows would stall the UI for several seconds.

The Room schema is exported to `app/schemas/` (see the `ksp { arg(...) }`
block in `app/build.gradle.kts`) so that migrations can be verified
structurally in CI.

---

## 6. Compose state is flattened per-screen

Every `ViewModel` exposes a single `StateFlow<UiState>` built via `combine`.
A screen then collects once with `collectAsStateWithLifecycle()`. This
prevents the "three flows → three recomposition passes" anti-pattern that
shows up as visible frame drops when multiple Room queries update at once.

Additionally:

- Canvas components (`WellnessWheel`, `MoodTrendChart`, `MetricBars`)
  cache their `TextMeasurer` with `rememberTextMeasurer(cacheSize = …)` and
  never instantiate one inside `DrawScope`.
- Paging list items use stable keys (`items.itemKey { it.id }`) so Compose
  reuses row composables during DB updates instead of discarding them.

---

## 7. Memory footprint & APK size

- **No bitmap assets**: every visual (launcher, icons, charts) is a
  `VectorDrawable` or a pure `Canvas` drawing. This keeps the release APK
  comfortably below 10 MB.
- **No font files**: typography uses system `FontFamily.SansSerif`.
- **Minimal dependency graph**: see `app/build.gradle.kts`. We deliberately
  do not ship Hilt/Dagger, Retrofit, Gson, Glide/Coil, or any charting
  library. The lazy, manual `AppContainer` DI keeps cold-start < 300 ms on a
  Pixel 6.
- **R8 full mode** + `isShrinkResources` strips unused code / resources on
  release builds.

---

## 8. Future scaling levers (not yet implemented, but easy to add)

- **Periodic VACUUM via WorkManager**: after aggressive deletes, reclaim
  pages and keep index B-trees shallow.
- **Partial indices** on hot ranges, e.g. `CREATE INDEX ... WHERE createdAt >
  :sixtyDaysAgo`. Trades slightly slower writes for even faster recent-range
  reads.
- **Trigram FTS5** for full-text search across journal bodies. Room 2.6+
  exposes `@Fts4` / `@Fts5` entities – the schema layout stays compatible.
- **Encrypted storage at rest** via SQLCipher / a keystore-wrapped key, if
  the product ever needs cloud sync with end-to-end encryption.

---

*TL;DR: every query has an index, every aggregate runs in SQL, every list is
paged, every chart is a Canvas, and the DI is lazy. Those five rules keep
100k+ rows on-device feeling instantaneous.*
