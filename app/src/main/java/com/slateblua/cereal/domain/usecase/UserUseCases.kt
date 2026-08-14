package com.slateblua.cereal.domain.usecase

import com.slateblua.cereal.domain.model.AppSettings
import com.slateblua.cereal.domain.model.UserStats
import com.slateblua.cereal.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class GetUserStatsUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<UserStats> {
        return userPreferencesRepository.getUserStats()
    }
}

class GetAppSettingsUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<AppSettings> {
        return userPreferencesRepository.getAppSettings()
    }
}

class UpdateSettingsUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(transform: (AppSettings) -> AppSettings) {
        userPreferencesRepository.updateAppSettings(transform)
    }
}
