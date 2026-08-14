package com.slateblua.cereal.ui.lesson

import androidx.lifecycle.ViewModel
import com.slateblua.cereal.domain.model.LessonStep
import com.slateblua.cereal.domain.model.MascotMood
import com.slateblua.cereal.domain.usecase.CompleteLessonUseCase
import com.slateblua.cereal.domain.usecase.GetLessonUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class LessonViewModel(
    private val lessonId: String,
    private val getLessonUseCase: GetLessonUseCase,
    private val completeLessonUseCase: CompleteLessonUseCase
) : ViewModel(), ContainerHost<LessonState, LessonSideEffect> {

    override val container: Container<LessonState, LessonSideEffect> = container(LessonState()) {
        loadLesson()
    }

    private fun loadLesson() = intent {
        reduce { state.copy(isLoading = true) }
        val result = getLessonUseCase(lessonId)
        result.fold(
            onSuccess = { lesson ->
                val firstStep = lesson.steps.firstOrNull()
                val initialVisible = if (firstStep != null) listOf(firstStep) else emptyList()
                reduce {
                    state.copy(
                        isLoading = false,
                        lesson = lesson,
                        currentStepIndex = 0,
                        currentStep = firstStep,
                        visibleSteps = initialVisible,
                        progress = if (lesson.steps.isNotEmpty()) 1f / lesson.steps.size.toFloat() else 0f
                    )
                }
            },
            onFailure = { error ->
                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Failed to load lesson"
                    )
                }
                postSideEffect(LessonSideEffect.ShowToast(error.localizedMessage ?: "Failed to load lesson"))
            }
        )
    }

    fun onOptionSelected(optionId: String) = intent {
        if (state.isAnswerChecked) return@intent
        val currentStep = state.currentStep as? LessonStep.MultipleChoice ?: return@intent
        val selectedOption = currentStep.options.firstOrNull { it.id == optionId } ?: return@intent
        val isCorrect = selectedOption.isCorrect

        val stepId = currentStep.id
        val updatedMap = state.selectedOptionByStepId + (stepId to optionId)

        if (isCorrect) {
            val explanation = selectedOption.explanation.ifBlank { "You nailed it! Keep going." }
            reduce {
                state.copy(
                    selectedOptionId = optionId,
                    selectedOptionByStepId = updatedMap,
                    isAnswerChecked = true,
                    isAnswerCorrect = true,
                    isWrongAnswerPromptVisible = false,
                    pipFeedbackMessage = "Spot on! $explanation",
                    pipFeedbackMood = MascotMood.CELEBRATING
                )
            }
        } else {
            val hint = currentStep.hint ?: "Review the lesson takeaways above to find the right answer."
            reduce {
                state.copy(
                    selectedOptionId = optionId,
                    selectedOptionByStepId = updatedMap,
                    isAnswerChecked = true,
                    isAnswerCorrect = false,
                    isWrongAnswerPromptVisible = true,
                    isHintVisible = false,
                    hintText = hint,
                    pipFeedbackMessage = "That's not quite right! Would you like to try again, or do you need a hint?",
                    pipFeedbackMood = MascotMood.HAPPY
                )
            }
        }
    }

    @Suppress("unused")
    fun onCheckAnswerClicked() = Unit // now handled automatically in onOptionSelected

    fun onGetHintClicked() = intent {
        reduce {
            state.copy(
                isHintVisible = true,
                pipFeedbackMood = MascotMood.HAPPY
            )
        }
    }

    fun onTryAgainClicked() = intent {
        val stepId = state.currentStep?.id
        val updatedMap = if (stepId != null) {
            state.selectedOptionByStepId - stepId
        } else {
            state.selectedOptionByStepId
        }
        reduce {
            state.copy(
                selectedOptionId = null,
                selectedOptionByStepId = updatedMap,
                isAnswerChecked = false,
                isAnswerCorrect = false,
                isWrongAnswerPromptVisible = false,
                pipFeedbackMessage = null
            )
        }
    }

    fun onContinueClicked() = intent {
        val lesson = state.lesson ?: return@intent
        val nextIndex = state.currentStepIndex + 1

        if (nextIndex < lesson.steps.size) {
            val nextStep = lesson.steps[nextIndex]
            reduce {
                state.copy(
                    currentStepIndex = nextIndex,
                    currentStep = nextStep,
                    visibleSteps = state.visibleSteps + nextStep,
                    selectedOptionId = null,
                    isAnswerChecked = false,
                    isAnswerCorrect = false,
                    isWrongAnswerPromptVisible = false,
                    isHintVisible = false,
                    hintText = null,
                    pipFeedbackMessage = null,
                    progress = (nextIndex + 1).toFloat() / lesson.steps.size.toFloat()
                )
            }
        } else {
            completeLessonUseCase(lesson.id, lesson.xpReward)
            reduce {
                state.copy(
                    isLessonFinished = true,
                    progress = 1.0f
                )
            }
        }
    }

    fun onCloseClicked() = intent {
        postSideEffect(LessonSideEffect.NavigateBack)
    }
}
