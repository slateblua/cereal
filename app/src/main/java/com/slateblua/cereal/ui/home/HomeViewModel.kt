package com.slateblua.cereal.ui.home

import androidx.lifecycle.ViewModel
import com.slateblua.cereal.domain.model.Lesson
import com.slateblua.cereal.domain.model.NodeStatus
import com.slateblua.cereal.domain.repository.LessonRepository
import com.slateblua.cereal.domain.usecase.GetAppSettingsUseCase
import com.slateblua.cereal.domain.usecase.GetRoadmapUseCase
import com.slateblua.cereal.domain.usecase.GetUserStatsUseCase
import kotlinx.coroutines.flow.combine
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class HomeViewModel(
    private val getRoadmapUseCase: GetRoadmapUseCase,
    private val getUserStatsUseCase: GetUserStatsUseCase,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val lessonRepository: LessonRepository
) : ViewModel(), ContainerHost<HomeState, HomeSideEffect> {

    override val container: Container<HomeState, HomeSideEffect> = container(HomeState()) {
        observeData()
    }

    private fun observeData() = intent {
        combine(
            getRoadmapUseCase(),
            getUserStatsUseCase(),
            getAppSettingsUseCase()
        ) { units, stats, settings ->
            val allNodes = units.flatMap { it.nodes }
            val nextNode = allNodes.firstOrNull { it.status == NodeStatus.AVAILABLE }
                ?: allNodes.firstOrNull { !stats.completedLessonIds.contains(it.lessonId) }
                ?: allNodes.firstOrNull()

            val lessonResult = nextNode?.let { lessonRepository.getLesson(it.lessonId) }
            val nextLesson: Lesson? = lessonResult?.getOrNull()

            val completedCount = stats.completedLessonIds.size
            val nextTarget = when {
                completedCount < 1 -> 1
                completedCount < 3 -> 3
                completedCount < 4 -> 4
                completedCount < 5 -> 5
                else -> 6
            }

            HomeState(
                isLoading = false,
                userStats = stats,
                settings = settings,
                nextAvailableLesson = nextLesson,
                totalLessonsCount = allNodes.size.coerceAtLeast(6),
                completedLessonsCount = completedCount,
                nextLevelTarget = nextTarget,
                errorMessage = null
            )
        }.collect { newState ->
            reduce { newState }
        }
    }

    fun onContinueLessonClicked() = intent {
        val nextLessonId = state.nextAvailableLesson?.id
        if (nextLessonId != null) {
            postSideEffect(HomeSideEffect.NavigateToLesson(nextLessonId))
        } else {
            postSideEffect(HomeSideEffect.NavigateToLessonsTab())
        }
    }
}
