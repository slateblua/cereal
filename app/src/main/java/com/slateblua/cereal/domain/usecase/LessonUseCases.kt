package com.slateblua.cereal.domain.usecase

import com.slateblua.cereal.domain.model.Lesson
import com.slateblua.cereal.domain.repository.LessonRepository
import com.slateblua.cereal.domain.repository.UserPreferencesRepository

class GetLessonUseCase(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(lessonId: String): Result<Lesson> {
        return lessonRepository.getLesson(lessonId)
    }
}

data class LessonCompletionResult(
    val xpEarned: Int,
)

class CompleteLessonUseCase(
    private val lessonRepository: LessonRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(
        lessonId: String,
        score: Int = 100,
        xpEarned: Int = 50
    ): LessonCompletionResult {
        lessonRepository.markLessonCompleted(lessonId, score, xpEarned)
        userPreferencesRepository.completeLesson(lessonId, xpEarned)
        return LessonCompletionResult(
            xpEarned = xpEarned,
        )
    }

    suspend operator fun invoke(
        lessonId: String,
        xpEarned: Int
    ): LessonCompletionResult {
        return invoke(lessonId = lessonId, score = 100, xpEarned = xpEarned)
    }
}
