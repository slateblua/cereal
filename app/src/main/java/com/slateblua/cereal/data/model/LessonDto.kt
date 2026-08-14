package com.slateblua.cereal.data.model

import com.slateblua.cereal.domain.model.Lesson
import com.slateblua.cereal.domain.model.LessonOption
import com.slateblua.cereal.domain.model.LessonStep
import com.slateblua.cereal.domain.model.MascotMood
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonDto(
    @SerialName("id") val id: String,
    @SerialName("unitId") val unitId: String,
    @SerialName("unitTitle") val unitTitle: String,
    @SerialName("title") val title: String,
    @SerialName("subtitle") val subtitle: String,
    @SerialName("xpReward") val xpReward: Int = 50,
    @SerialName("iconName") val iconName: String = "menu_book",
    @SerialName("steps") val steps: List<LessonStepDto>
) {
    fun toDomain(): Lesson {
        return Lesson(
            id = id,
            unitId = unitId,
            unitTitle = unitTitle,
            title = title,
            subtitle = subtitle,
            xpReward = xpReward,
            iconName = iconName,
            steps = steps.mapNotNull { it.toDomain() }
        )
    }
}

@Serializable
data class LessonStepDto(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("sender") val sender: String = "Pip",
    @SerialName("mascotMood") val mascotMood: String = "HAPPY",
    @SerialName("content") val content: String = "",
    @SerialName("tip") val tip: String? = null,
    @SerialName("title") val title: String = "",
    @SerialName("highlight") val highlight: String? = null,
    @SerialName("question") val question: String = "",
    @SerialName("options") val options: List<LessonOptionDto> = emptyList(),
    @SerialName("hint") val hint: String? = null
) {
    fun toDomain(): LessonStep? {
        val mood = try {
            MascotMood.valueOf(mascotMood.uppercase())
        } catch (_: Exception) {
            MascotMood.HAPPY
        }

        return when (type) {
            "COMPANION_MESSAGE" -> LessonStep.CompanionMessage(
                id = id,
                sender = sender,
                mascotMood = mood,
                content = content,
                tip = tip
            )
            "EXPLANATION_CARD" -> LessonStep.ExplanationCard(
                id = id,
                title = title,
                content = content,
                highlight = highlight
            )
            "MULTIPLE_CHOICE" -> LessonStep.MultipleChoice(
                id = id,
                question = question,
                options = options.map { it.toDomain() },
                hint = hint
            )
            "SUMMARY" -> LessonStep.Summary(
                id = id,
                title = title,
                content = content,
                mascotMood = mood
            )
            else -> null
        }
    }
}

@Serializable
data class LessonOptionDto(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String,
    @SerialName("isCorrect") val isCorrect: Boolean,
    @SerialName("explanation") val explanation: String = ""
) {
    fun toDomain(): LessonOption {
        return LessonOption(
            id = id,
            text = text,
            isCorrect = isCorrect,
            explanation = explanation
        )
    }
}
