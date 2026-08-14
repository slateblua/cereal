package com.slateblua.cereal.domain.model

data class UserStats(
    val xp: Int = 0,
    val lastActiveEpochMs: Long = System.currentTimeMillis(),
    val completedLessonIds: Set<String> = emptySet(),
    val hearts: Int = 5,
    val level: Int = 1,
    val title: String = "Financial Seedling"
) {
    companion object {
        fun calculateLevel(completedLessonsCount: Int): Pair<Int, String> {
            return when {
                completedLessonsCount >= 5 -> 5 to "Wealth Architect"
                completedLessonsCount >= 4 -> 4 to "Investment Vanguard"
                completedLessonsCount >= 3 -> 3 to "Budget Strategist"
                completedLessonsCount >= 1 -> 2 to "Smart Saver"
                else -> 1 to "Financial Seedling"
            }
        }
    }
}

data class AppSettings(
    val dailyReminderEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val mascotMoodOverride: MascotMood? = null,
    val isDarkMode: Boolean = false,
    val userName: String = "Cereal Explorer"
)
