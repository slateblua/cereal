package com.slateblua.cereal.ui.lessons

import com.slateblua.cereal.domain.model.RoadmapUnit
import com.slateblua.cereal.domain.model.UserStats

data class LessonsState(
    val isLoading: Boolean = true,
    val units: List<RoadmapUnit> = emptyList(),
    val userStats: UserStats = UserStats(),
    val errorMessage: String? = null
)

sealed interface LessonsSideEffect {
    data class NavigateToLesson(val lessonId: String) : LessonsSideEffect
    data class ShowStreakDialog(val streakDays: Int) : LessonsSideEffect
    data class ShowLockedNodeMessage(val message: String) : LessonsSideEffect
}
