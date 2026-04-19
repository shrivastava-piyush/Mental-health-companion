package com.wellness.companion.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wellness.companion.di.AppContainer
import com.wellness.companion.ui.auth.BiometricGateScreen
import com.wellness.companion.ui.insights.InsightsScreen
import com.wellness.companion.ui.journal.JournalEditorScreen
import com.wellness.companion.ui.journal.JournalListScreen
import com.wellness.companion.ui.journal.ThreadDetailScreen
import com.wellness.companion.ui.mood.MoodScreen
import com.wellness.companion.ui.navigation.BottomTabs
import com.wellness.companion.ui.navigation.WellnessDestination
import com.wellness.companion.ui.theme.WellnessTheme

/**
 * Top-level composable. Renders the biometric gate first, then the main shell.
 *
 * The shell picks between a bottom NavigationBar (phones) and a NavigationRail
 * (tablets / unfolded foldables) using a width breakpoint (600 dp). A manual
 * check is used instead of the window-size-class artifact to avoid pulling
 * another transitive dep for a one-shot computation.
 */
@Composable
fun WellnessAppRoot(container: AppContainer) {
    WellnessTheme {
        val rootNav = rememberNavController()
        NavHost(
            navController = rootNav,
            startDestination = WellnessDestination.BiometricGate.route,
        ) {
            composable(WellnessDestination.BiometricGate.route) {
                BiometricGateScreen(onUnlocked = {
                    rootNav.navigate("shell") {
                        popUpTo(WellnessDestination.BiometricGate.route) { inclusive = true }
                    }
                })
            }
            composable("shell") { MainShell(container) }
        }
    }
}

@Composable
private fun MainShell(container: AppContainer) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp
        val nav = rememberNavController()
        val entry by nav.currentBackStackEntryAsState()
        val activeTab = entry?.destination?.route

        if (useRail) {
            Row(Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    BottomTabs.forEach { tab ->
                        NavigationRailItem(
                            selected = activeTab?.startsWith(tab.destination.route) == true,
                            onClick = { switchTab(nav, tab.destination.route) },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
                ShellNavHost(container, nav, contentPadding = PaddingValues(0.dp))
            }
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        BottomTabs.forEach { tab ->
                            NavigationBarItem(
                                selected = activeTab?.startsWith(tab.destination.route) == true,
                                onClick = { switchTab(nav, tab.destination.route) },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                },
            ) { inner ->
                ShellNavHost(container, nav, inner)
            }
        }
    }
}

@Composable
private fun ShellNavHost(
    container: AppContainer,
    nav: NavHostController,
    contentPadding: PaddingValues,
) {
    NavHost(
        navController = nav,
        startDestination = WellnessDestination.Mood.route,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(WellnessDestination.Mood.route) {
            MoodScreen(container, contentPadding)
        }
        composable(WellnessDestination.Journal.route) {
            JournalListScreen(
                container = container,
                onOpen = { id -> nav.navigate(WellnessDestination.JournalEditor.build(id)) },
                onOpenThread = { id, label ->
                    nav.navigate(WellnessDestination.ThreadDetail.build(id, label))
                },
                contentPadding = contentPadding,
            )
        }
        composable(
            route = WellnessDestination.JournalEditor.route,
            arguments = listOf(
                navArgument(WellnessDestination.JournalEditor.ARG) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(WellnessDestination.JournalEditor.ARG_PROMPT) {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) { entry ->
            val id = entry.arguments?.getLong(WellnessDestination.JournalEditor.ARG) ?: -1L
            val prompt = java.net.URLDecoder.decode(
                entry.arguments?.getString(WellnessDestination.JournalEditor.ARG_PROMPT).orEmpty(),
                "UTF-8",
            )
            JournalEditorScreen(
                container = container,
                entryId = if (id > 0L) id else 0L,
                onBack = { nav.popBackStack() },
                contentPadding = contentPadding,
                prefilledPrompt = prompt,
                onOpenJournalWithPrompt = { question ->
                    nav.navigate(WellnessDestination.JournalEditor.build(null, question))
                },
            )
        }
        composable(
            route = WellnessDestination.ThreadDetail.route,
            arguments = listOf(
                navArgument(WellnessDestination.ThreadDetail.ARG_ID) {
                    type = NavType.LongType
                },
                navArgument(WellnessDestination.ThreadDetail.ARG_LABEL) {
                    type = NavType.StringType
                },
            ),
        ) { entry ->
            val threadId = entry.arguments?.getLong(WellnessDestination.ThreadDetail.ARG_ID) ?: 0L
            val label = java.net.URLDecoder.decode(
                entry.arguments?.getString(WellnessDestination.ThreadDetail.ARG_LABEL).orEmpty(),
                "UTF-8",
            )
            ThreadDetailScreen(
                container = container,
                threadId = threadId,
                threadLabel = label,
                onOpenEntry = { id -> nav.navigate(WellnessDestination.JournalEditor.build(id)) },
                onBack = { nav.popBackStack() },
                contentPadding = contentPadding,
            )
        }
        composable(WellnessDestination.Insights.route) {
            InsightsScreen(container, contentPadding)
        }
    }
}

private fun switchTab(nav: NavHostController, route: String) {
    nav.navigate(route) {
        popUpTo(nav.graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
