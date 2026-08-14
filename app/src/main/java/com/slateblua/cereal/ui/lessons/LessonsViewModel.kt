package com.slateblua.cereal.ui.lessons

import androidx.lifecycle.ViewModel
import com.slateblua.cereal.domain.model.NodeStatus
import com.slateblua.cereal.domain.model.RoadmapNode
import com.slateblua.cereal.domain.usecase.GetRoadmapUseCase
import com.slateblua.cereal.domain.usecase.GetUserStatsUseCase
import kotlinx.coroutines.flow.combine
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class LessonsViewModel(
    private val getRoadmapUseCase: GetRoadmapUseCase,
    private val getUserStatsUseCase: GetUserStatsUseCase
) : ViewModel(), ContainerHost<LessonsState, LessonsSideEffect> {

    override val container: Container<LessonsState, LessonsSideEffect> = container(LessonsState()) {
        observeRoadmapAndStats()
    }

    private fun observeRoadmapAndStats() = intent {
        combine(
            getRoadmapUseCase(),
            getUserStatsUseCase()
        ) { units, stats ->
            Pair(units, stats)
        }.collect { (units, stats) ->
            reduce {
                state.copy(
                    isLoading = false,
                    units = units,
                    userStats = stats,
                    errorMessage = null
                )
            }
        }
    }

    fun onNodeClicked(node: RoadmapNode) = intent {
        when (node.status) {
            NodeStatus.LOCKED -> {
                postSideEffect(
                    LessonsSideEffect.ShowLockedNodeMessage("Complete previous lessons to unlock \"${node.title}\".")
                )
            }
            NodeStatus.AVAILABLE, NodeStatus.COMPLETED -> {
                postSideEffect(
                    LessonsSideEffect.NavigateToLesson(node.lessonId)
                )
            }
        }
    }
}
