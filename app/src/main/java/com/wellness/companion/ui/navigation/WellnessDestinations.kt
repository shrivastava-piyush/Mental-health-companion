package com.wellness.companion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.ui.graphics.vector.ImageVector

sealed class WellnessDestination(val route: String) {
    data object Mood     : WellnessDestination("mood")
    data object Journal  : WellnessDestination("journal")
    data object Insights : WellnessDestination("insights")

    /** In-journal editor; `new` if creating. */
    data object JournalEditor : WellnessDestination("journal/editor/{id}") {
        fun build(id: Long?): String = "journal/editor/${id ?: -1L}"
        const val ARG = "id"
    }

    data object ThreadDetail : WellnessDestination("journal/thread/{threadId}/{label}") {
        fun build(threadId: Long, label: String): String =
            "journal/thread/$threadId/${java.net.URLEncoder.encode(label, "UTF-8")}"
        const val ARG_ID = "threadId"
        const val ARG_LABEL = "label"
    }

    data object BiometricGate : WellnessDestination("gate")
}

data class BottomTab(
    val destination: WellnessDestination,
    val label: String,
    val icon: ImageVector,
)

val BottomTabs: List<BottomTab> = listOf(
    BottomTab(WellnessDestination.Mood,     "Mood",     Icons.Outlined.Mood),
    BottomTab(WellnessDestination.Journal,  "Journal",  Icons.Outlined.EditNote),
    BottomTab(WellnessDestination.Insights, "Insights", Icons.Outlined.Insights),
)

// Kept for future "Actions" tab; pre-wired so we can add without reshuffling.
@Suppress("unused")
private val FutureIcon = Icons.Outlined.Bolt
