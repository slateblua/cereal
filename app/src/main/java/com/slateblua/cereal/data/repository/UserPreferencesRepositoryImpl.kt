package com.slateblua.cereal.data.repository

import com.slateblua.cereal.data.datasource.PreferencesDataSource
import com.slateblua.cereal.domain.model.AppSettings
import com.slateblua.cereal.domain.model.UserStats
import com.slateblua.cereal.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepositoryImpl(
    private val preferencesDataSource: PreferencesDataSource
) : UserPreferencesRepository {

    override fun getUserStats(): Flow<UserStats> {
        return preferencesDataSource.userStatsFlow
    }

    override fun getAppSettings(): Flow<AppSettings> {
        return preferencesDataSource.appSettingsFlow
    }

    override suspend fun addXp(amount: Int) {
        preferencesDataSource.addXp(amount)
    }

    override suspend fun completeLesson(lessonId: String, xpEarned: Int) {
        preferencesDataSource.markLessonCompleted(lessonId, xpEarned)
    }

    override suspend fun updateAppSettings(transform: (AppSettings) -> AppSettings) {
        preferencesDataSource.updateSettings(transform)
    }
}
