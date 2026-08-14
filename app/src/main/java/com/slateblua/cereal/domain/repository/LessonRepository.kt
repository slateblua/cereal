package com.slateblua.cereal.domain.repository

import com.slateblua.cereal.domain.model.Lesson
import com.slateblua.cereal.domain.model.RoadmapUnit
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun getRoadmap(): Flow<List<RoadmapUnit>>
    suspend fun getLesson(lessonId: String): Result<Lesson>
    suspend fun markLessonCompleted(lessonId: String, score: Int, xpEarned: Int)
}
