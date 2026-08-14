package com.slateblua.cereal.ui.lesson

import com.slateblua.cereal.domain.model.Lesson
import com.slateblua.cereal.domain.model.LessonStep
import com.slateblua.cereal.domain.model.MascotMood

data class LessonState(
    val isLoading: Boolean = true,
    val lesson: Lesson? = null,
    val currentStepIndex: Int = 0,
    val currentStep: LessonStep? = null,
    val visibleSteps: List<LessonStep> = emptyList(),
    val selectedOptionId: String? = null,
    val selectedOptionByStepId: Map<String, String> = emptyMap(),
    val isAnswerChecked: Boolean = false,
    val isAnswerCorrect: Boolean = false,
    val isWrongAnswerPromptVisible: Boolean = false,
    val isHintVisible: Boolean = false,
    val hintText: String? = null,
    val pipFeedbackMessage: String? = null,
    val pipFeedbackMood: MascotMood = MascotMood.HAPPY,
    val isTyping: Boolean = false,
    val isLessonFinished: Boolean = false,
    val progress: Float = 0f,
    val errorMessage: String? = null
)

sealed interface LessonSideEffect {
    data object NavigateBack : LessonSideEffect
    data class ShowToast(val message: String) : LessonSideEffect
}
