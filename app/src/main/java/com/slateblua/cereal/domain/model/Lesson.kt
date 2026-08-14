package com.slateblua.cereal.domain.model

data class Lesson(
    val id: String,
    val unitId: String,
    val unitTitle: String,
    val title: String,
    val subtitle: String,
    val xpReward: Int,
    val iconName: String,
    val steps: List<LessonStep>
)

sealed interface LessonStep {
    val id: String

    data class CompanionMessage(
        override val id: String,
        val sender: String = "Pip",
        val mascotMood: MascotMood = MascotMood.HAPPY,
        val content: String,
        val tip: String? = null
    ) : LessonStep

    data class ExplanationCard(
        override val id: String,
        val title: String,
        val content: String,
        val highlight: String? = null
    ) : LessonStep

    data class MultipleChoice(
        override val id: String,
        val question: String,
        val options: List<LessonOption>,
        val hint: String? = null
    ) : LessonStep

    data class Summary(
        override val id: String,
        val title: String,
        val content: String,
        val mascotMood: MascotMood = MascotMood.CELEBRATING
    ) : LessonStep
}

data class LessonOption(
    val id: String,
    val text: String,
    val isCorrect: Boolean,
    val explanation: String = ""
)

typealias StepOption = LessonOption
