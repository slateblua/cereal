package com.slateblua.cereal.di

import com.slateblua.cereal.data.datasource.LessonLocalDataSource
import com.slateblua.cereal.data.datasource.PreferencesDataSource
import com.slateblua.cereal.data.repository.LessonRepositoryImpl
import com.slateblua.cereal.data.repository.UserPreferencesRepositoryImpl
import com.slateblua.cereal.domain.repository.LessonRepository
import com.slateblua.cereal.domain.repository.UserPreferencesRepository
import com.slateblua.cereal.domain.usecase.CompleteLessonUseCase
import com.slateblua.cereal.domain.usecase.GetAppSettingsUseCase
import com.slateblua.cereal.domain.usecase.GetLessonUseCase
import com.slateblua.cereal.domain.usecase.GetRoadmapUseCase
import com.slateblua.cereal.domain.usecase.GetUserStatsUseCase
import com.slateblua.cereal.domain.usecase.UpdateSettingsUseCase
import com.slateblua.cereal.ui.home.HomeViewModel
import com.slateblua.cereal.ui.lesson.LessonViewModel
import com.slateblua.cereal.ui.lessons.LessonsViewModel
import com.slateblua.cereal.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Data Sources
    single { LessonLocalDataSource(androidContext()) }
    single { PreferencesDataSource(androidContext()) }

    // Repositories
    single<LessonRepository> { LessonRepositoryImpl(get(), get()) }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }

    // Use Cases
    factory { GetRoadmapUseCase(get()) }
    factory { GetLessonUseCase(get()) }
    factory { CompleteLessonUseCase(get(), get()) }
    factory { GetUserStatsUseCase(get()) }
    factory { GetAppSettingsUseCase(get()) }
    factory { UpdateSettingsUseCase(get()) }

    // ViewModels
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { LessonsViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { (lessonId: String) -> LessonViewModel(lessonId, get(), get()) }
}
