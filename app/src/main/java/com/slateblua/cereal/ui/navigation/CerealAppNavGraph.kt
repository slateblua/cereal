package com.slateblua.cereal.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.slateblua.cereal.ui.home.HomeScreen
import com.slateblua.cereal.ui.home.HomeViewModel
import com.slateblua.cereal.ui.lesson.LessonScreen
import com.slateblua.cereal.ui.lesson.LessonViewModel
import com.slateblua.cereal.ui.lessons.LessonsScreen
import com.slateblua.cereal.ui.lessons.LessonsViewModel
import com.slateblua.cereal.ui.settings.SettingsScreen
import com.slateblua.cereal.ui.settings.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CerealAppNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Lessons.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                CerealBottomBar(navController = navController)
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = koinViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToLesson = { lessonId ->
                        navController.navigate(Screen.Lesson.createRoute(lessonId))
                    },
                    onNavigateToLessonsTab = {
                        navController.navigate(Screen.Lessons.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Lessons.route) {
                val viewModel: LessonsViewModel = koinViewModel()
                LessonsScreen(
                    viewModel = viewModel,
                    onNavigateToLesson = { lessonId ->
                        navController.navigate(Screen.Lesson.createRoute(lessonId))
                    }
                )
            }

            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = koinViewModel()
                SettingsScreen(viewModel = viewModel)
            }

            composable(
                route = Screen.Lesson.route,
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
                val viewModel: LessonViewModel = koinViewModel(parameters = { parametersOf(lessonId) })
                LessonScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
