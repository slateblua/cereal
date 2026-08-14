package com.slateblua.cereal.data.datasource

import android.content.Context
import com.slateblua.cereal.data.model.LessonDto
import com.slateblua.cereal.domain.model.Lesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class LessonLocalDataSource(
    private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val lessonCache = mutableMapOf<String, Lesson>()

    private val lessonFiles = listOf(
        "lessons/lesson_1_money_basics.json",
        "lessons/lesson_2_budgeting_503020.json",
        "lessons/lesson_3_compound_interest.json",
        "lessons/lesson_4_credit_debt.json",
        "lessons/lesson_5_investing_starter.json",
        "lessons/lesson_6_emergency_fund.json"
    )

    suspend fun getAllLessons(): List<Lesson> = withContext(Dispatchers.IO) {
        if (lessonCache.size == lessonFiles.size) {
            return@withContext lessonCache.values.toList()
        }

        lessonFiles.forEach { filePath ->
            try {
                val jsonString = context.assets.open(filePath).bufferedReader().use { it.readText() }
                val dto = json.decodeFromString<LessonDto>(jsonString)
                val domainLesson = dto.toDomain()
                lessonCache[domainLesson.id] = domainLesson
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        lessonCache.values.toList()
    }

    suspend fun getLessonById(lessonId: String): Result<Lesson> = withContext(Dispatchers.IO) {
        lessonCache[lessonId]?.let { return@withContext Result.success(it) }

        // If not in cache, try loading all
        getAllLessons()
        lessonCache[lessonId]?.let {
            Result.success(it)
        } ?: Result.failure(IllegalArgumentException("Lesson not found with id: $lessonId"))
    }
}
