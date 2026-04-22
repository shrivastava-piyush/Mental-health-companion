package com.wellness.companion.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.wellness.companion.di.AppContainer
import com.wellness.companion.di.ViewModelFactories
import com.wellness.companion.ui.auth.BiometricGateScreen
import com.wellness.companion.ui.home.HomeScreen
import com.wellness.companion.ui.insights.InsightsScreen
import com.wellness.companion.ui.journal.JournalEditorScreen
import com.wellness.companion.ui.journal.JournalListScreen
import com.wellness.companion.ui.journal.ThreadDetailScreen
import com.wellness.companion.ui.mood.MoodScreen
import com.wellness.companion.ui.navigation.BottomTabs
import com.wellness.companion.ui.navigation.WellnessDestination
import com.wellness.companion.ui.theme.WellnessTheme

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
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val activeTab = entry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                BottomTabs.forEach { tab ->
                    NavigationBarItem(
                        selected = activeTab?.startsWith(tab.destination.route) == true,
                        onClick = { switchTab(nav, tab.destination.route) },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.4f),
                            unselectedTextColor = Color.White.copy(alpha = 0.4f),
                            indicatorColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        },
    ) { inner ->
        ShellNavHost(container, nav, inner)
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
        startDestination = WellnessDestination.Home.route,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { fadeIn() + scaleIn(initialScale = 0.95f) },
        exitTransition = { fadeOut() + scaleOut(targetScale = 0.95f) }
    ) {
        composable(WellnessDestination.Home.route) {
            HomeScreen(
                viewModel = viewModel(factory = ViewModelFactories.home(container)),
                onOpenMood = { nav.navigate(WellnessDestination.Mood.route) },
                onOpenJournal = { nav.navigate(WellnessDestination.Journal.route) },
                onOpenReflection = { id -> nav.navigate(WellnessDestination.JournalEditor.build(id)) },
                contentPadding = contentPadding
            )
        }
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
            arguments = listOf(navArgument(WellnessDestination.JournalEditor.ARG) {
                type = NavType.LongType
                defaultValue = -1L
            }),
        ) { entry ->
            val id = entry.arguments?.getLong(WellnessDestination.JournalEditor.ARG) ?: -1L
            JournalEditorScreen(
                container = container,
                entryId = if (id > 0L) id else 0L,
                onBack = { nav.popBackStack() },
                contentPadding = contentPadding,
            )
        }
        composable(
            route = WellnessDestination.ThreadDetail.route,
            arguments = listOf(
                navArgument(WellnessDestination.ThreadDetail.ARG_ID) { type = NavType.LongType },
                navArgument(WellnessDestination.ThreadDetail.ARG_LABEL) { type = NavType.StringType },
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
