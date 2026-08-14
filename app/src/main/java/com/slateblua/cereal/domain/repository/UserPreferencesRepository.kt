package com.slateblua.cereal.domain.repository

import com.slateblua.cereal.domain.model.AppSettings
import com.slateblua.cereal.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getUserStats(): Flow<UserStats>
    fun getAppSettings(): Flow<AppSettings>
    suspend fun addXp(amount: Int)
    suspend fun completeLesson(lessonId: String, xpEarned: Int)
    suspend fun updateAppSettings(transform: (AppSettings) -> AppSettings)
}
