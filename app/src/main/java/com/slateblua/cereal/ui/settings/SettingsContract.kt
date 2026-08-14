package com.slateblua.cereal.ui.settings

import com.slateblua.cereal.domain.model.AppSettings
import com.slateblua.cereal.domain.model.UserStats

data class SettingsState(
    val isLoading: Boolean = true,
    val settings: AppSettings = AppSettings(),
    val userStats: UserStats = UserStats(),
    val showDisclaimer: Boolean = false
)

sealed interface SettingsSideEffect {
    data class ShowToast(val message: String) : SettingsSideEffect
}
