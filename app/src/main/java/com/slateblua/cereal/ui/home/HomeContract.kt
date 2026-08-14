package com.slateblua.cereal.ui.home

import com.slateblua.cereal.domain.model.AppSettings
import com.slateblua.cereal.domain.model.Lesson
import com.slateblua.cereal.domain.model.UserStats

data class HomeState(
    val isLoading: Boolean = true,
    val userStats: UserStats = UserStats(),
    val settings: AppSettings = AppSettings(),
    val nextAvailableLesson: Lesson? = null,
    val totalLessonsCount: Int = 6,
    val completedLessonsCount: Int = 0,
    val nextLevelTarget: Int = 2,
    val errorMessage: String? = null
)

sealed interface HomeSideEffect {
    data class NavigateToLesson(val lessonId: String) : HomeSideEffect
    data class NavigateToLessonsTab(val unitId: String? = null) : HomeSideEffect
}
