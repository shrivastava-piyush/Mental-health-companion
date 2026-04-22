package com.wellness.companion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class WellnessDestination(val route: String) {
    data object Home     : WellnessDestination("home")
    data object Mood     : WellnessDestination("mood")
    data object Journal  : WellnessDestination("journal")
    data object Insights : WellnessDestination("insights")

    data object JournalEditor : WellnessDestination("journal/editor/{id}?prompt={prompt}") {
        fun build(id: Long?, prompt: String = ""): String {
            val base = "journal/editor/${id ?: -1L}"
            return if (prompt.isBlank()) "$base?prompt="
            else "$base?prompt=${java.net.URLEncoder.encode(prompt, "UTF-8")}"
        }
        const val ARG = "id"
        const val ARG_PROMPT = "prompt"
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
