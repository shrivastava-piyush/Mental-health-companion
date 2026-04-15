package com.wellness.companion.domain

import java.util.concurrent.TimeUnit

/**
 * Tiny, side-effect-free time helpers. Kept in the domain layer so the UI and
 * data layers share the same definition of "last 30 days" rather than each
 * computing a slightly different epoch boundary.
 */
object Time {

    fun daysAgoMillis(days: Int, now: Long = System.currentTimeMillis()): Long =
        now - TimeUnit.DAYS.toMillis(days.toLong())

    fun startOfDayMillis(now: Long = System.currentTimeMillis()): Long {
        val dayMs = TimeUnit.DAYS.toMillis(1)
        return (now / dayMs) * dayMs
    }
}
