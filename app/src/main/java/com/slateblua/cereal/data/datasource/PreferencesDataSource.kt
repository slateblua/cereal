package com.slateblua.cereal.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.slateblua.cereal.domain.model.AppSettings
import com.slateblua.cereal.domain.model.MascotMood
import com.slateblua.cereal.domain.model.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cereal_user_preferences")

class PreferencesDataSource(
    private val context: Context
) {
    private object Keys {
        val XP = intPreferencesKey("user_xp")
        val LAST_ACTIVE_MS = longPreferencesKey("last_active_epoch_ms")
        val COMPLETED_LESSONS = stringSetPreferencesKey("completed_lessons")
        // Settings
        val MASCOT_MOOD_OVERRIDE = stringPreferencesKey("mascot_mood_override")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    val userStatsFlow: Flow<UserStats> = context.dataStore.data.map { prefs ->
        val xp = prefs[Keys.XP] ?: 0
        val lastActive = prefs[Keys.LAST_ACTIVE_MS] ?: System.currentTimeMillis()
        val completed = prefs[Keys.COMPLETED_LESSONS] ?: emptySet()

        val (level, title) = UserStats.calculateLevel(completed.size)

        UserStats(
            xp = xp,
            lastActiveEpochMs = lastActive,
            completedLessonIds = completed,
            level = level,
            title = title
        )
    }

    val appSettingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val moodStr = prefs[Keys.MASCOT_MOOD_OVERRIDE]
        val mood = moodStr?.let {
            try { MascotMood.valueOf(it) } catch (_: Exception) { null }
        }

        AppSettings(
            mascotMoodOverride = mood,
            isDarkMode = prefs[Keys.DARK_MODE] ?: false,
            userName = prefs[Keys.USER_NAME] ?: "Cereal Explorer"
        )
    }

    suspend fun addXp(amount: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.XP] ?: 0
            prefs[Keys.XP] = current + amount
        }
    }

    suspend fun markLessonCompleted(lessonId: String, xpEarned: Int) {
        context.dataStore.edit { prefs ->
            val currentLessons = prefs[Keys.COMPLETED_LESSONS]?.toMutableSet() ?: mutableSetOf()
            currentLessons.add(lessonId)
            prefs[Keys.COMPLETED_LESSONS] = currentLessons

            val currentXp = prefs[Keys.XP] ?: 0
            prefs[Keys.XP] = currentXp + xpEarned
        }
    }

    suspend fun updateSettings(transform: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val moodStr = prefs[Keys.MASCOT_MOOD_OVERRIDE]
            val mood = moodStr?.let {
                try { MascotMood.valueOf(it) } catch (_: Exception) { null }
            }

            val current = AppSettings(
                mascotMoodOverride = mood,
                isDarkMode = prefs[Keys.DARK_MODE] ?: false,
                userName = prefs[Keys.USER_NAME] ?: "Cereal Explorer"
            )

            val updated = transform(current)

            if (updated.mascotMoodOverride != null) {
                prefs[Keys.MASCOT_MOOD_OVERRIDE] = updated.mascotMoodOverride.name
            } else {
                prefs.remove(Keys.MASCOT_MOOD_OVERRIDE)
            }
            prefs[Keys.DARK_MODE] = updated.isDarkMode
            prefs[Keys.USER_NAME] = updated.userName
        }
    }
}
