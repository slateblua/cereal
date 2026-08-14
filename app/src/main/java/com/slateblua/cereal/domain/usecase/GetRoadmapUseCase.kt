package com.slateblua.cereal.domain.usecase

import com.slateblua.cereal.domain.model.RoadmapUnit
import com.slateblua.cereal.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow

class GetRoadmapUseCase(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(): Flow<List<RoadmapUnit>> {
        return lessonRepository.getRoadmap()
    }
}
