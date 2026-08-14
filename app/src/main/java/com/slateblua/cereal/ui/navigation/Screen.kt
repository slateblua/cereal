package com.slateblua.cereal.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    data object Home : Screen("home", "home", Icons.Default.Home)
    data object Lessons : Screen("lessons", "lessons", Icons.Default.AutoStories)
    data object Settings : Screen("settings", "settings", Icons.Default.Settings)
    data object Lesson : Screen("lesson/{lessonId}", "Lesson") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }

    companion object {
        val bottomNavItems: List<Screen>
            get() = listOf(Home, Lessons, Settings)
    }
}
