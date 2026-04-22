package com.wellness.companion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class WellnessDestination(val route: String) {
    data object Home     : WellnessDestination("home")
    data object Mood     : WellnessDestination("mood")
    data object Journal  : WellnessDestination("journal")
    data object Insights : WellnessDestination("insights")

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
    BottomTab(WellnessDestination.Home,     "Today",    Icons.Outlined.Home),
    BottomTab(WellnessDestination.Journal,  "Library",  Icons.Outlined.AutoStories),
    BottomTab(WellnessDestination.Insights, "Pulse",    Icons.Outlined.AutoGraph),
)
