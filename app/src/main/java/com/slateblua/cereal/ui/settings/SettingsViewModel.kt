package com.slateblua.cereal.ui.settings

import androidx.lifecycle.ViewModel
import com.slateblua.cereal.domain.model.MascotMood
import com.slateblua.cereal.domain.usecase.GetAppSettingsUseCase
import com.slateblua.cereal.domain.usecase.GetUserStatsUseCase
import com.slateblua.cereal.domain.usecase.UpdateSettingsUseCase
import kotlinx.coroutines.flow.combine
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class SettingsViewModel(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val getUserStatsUseCase: GetUserStatsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel(), ContainerHost<SettingsState, SettingsSideEffect> {

    override val container: Container<SettingsState, SettingsSideEffect> = container(SettingsState()) {
        observeSettingsAndStats()
    }

    private fun observeSettingsAndStats() = intent {
        combine(
            getAppSettingsUseCase(),
            getUserStatsUseCase()
        ) { settings, stats ->
            Pair(settings, stats)
        }.collect { (settings, stats) ->
            reduce {
                state.copy(
                    isLoading = false,
                    settings = settings,
                    userStats = stats
                )
            }
        }
    }

    fun onDarkModeToggled(enabled: Boolean) = intent {
        updateSettingsUseCase { it.copy(isDarkMode = enabled) }
    }

    fun onMascotMoodSelected(mood: MascotMood) = intent {
        updateSettingsUseCase { it.copy(mascotMoodOverride = mood) }
    }

    fun onDisclaimerClicked(show: Boolean) = intent {
        reduce { state.copy(showDisclaimer = show) }
    }
}
