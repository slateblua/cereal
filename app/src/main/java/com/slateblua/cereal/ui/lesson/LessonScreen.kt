package com.slateblua.cereal.ui.lesson

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slateblua.cereal.domain.model.LessonStep
import com.slateblua.cereal.domain.model.MascotMood
import com.slateblua.cereal.ui.components.ButtonStyle
import com.slateblua.cereal.ui.components.CerealButton
import com.slateblua.cereal.ui.components.CerealProgressBar
import com.slateblua.cereal.ui.components.ChoiceState
import com.slateblua.cereal.ui.components.InteractiveChoiceCard
import com.slateblua.cereal.ui.components.PipMascot
import com.slateblua.cereal.ui.components.SpeechBubble
import com.slateblua.cereal.ui.theme.MintSuccess
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

// Shared enter/exit specs used throughout the lesson
private val stepEnter = fadeIn(tween(320)) + expandVertically(
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    expandFrom = Alignment.Top
)
private val stepExit = fadeOut(tween(200)) + shrinkVertically(tween(200))

@Composable
fun LessonScreen(
    viewModel: LessonViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is LessonSideEffect.NavigateBack -> onNavigateBack()
            is LessonSideEffect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (!state.isLessonFinished) {
            LessonTopBar(
                progress = state.progress,
                onCloseClick = { viewModel.onCloseClicked() }
            )
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (state.isLessonFinished) {
            LessonCelebrationScreen(
                lessonTitle = state.lesson?.title ?: "Lesson",
                onFinish = onNavigateBack,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
        } else {
            val listState = rememberLazyListState()

            LaunchedEffect(state.visibleSteps.size) {
                if (state.visibleSteps.isNotEmpty()) {
                    listState.animateScrollToItem(state.visibleSteps.size - 1)
                }
            }

            // Scroll to bottom whenever visible content grows (feedback panel expanding)
            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.sumOf { it.size } }
                    .collectLatest { listState.scrollToItem(listState.layoutInfo.totalItemsCount.coerceAtLeast(1) - 1, scrollOffset = Int.MAX_VALUE) }
            }

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = 32.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(
                    items = state.visibleSteps,
                    key = { it.id }
                ) { step ->
                    val isCurrent = step.id == state.currentStep?.id
                    val stepSelectedOptionId = if (isCurrent) state.selectedOptionId else state.selectedOptionByStepId[step.id]

                    AnimatedVisibility(
                        visible = true,
                        enter = stepEnter,
                        exit = stepExit,
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(320),
                            placementSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            fadeOutSpec = tween(200)
                        )
                    ) {
                        LessonStepItem(
                            step = step,
                            isCurrentStep = isCurrent,
                            selectedOptionId = stepSelectedOptionId,
                            isAnswerChecked = if (isCurrent) state.isAnswerChecked else true,
                            isAnswerCorrect = if (isCurrent) state.isAnswerCorrect else true,
                            isWrongAnswerPromptVisible = if (isCurrent) state.isWrongAnswerPromptVisible else false,
                            isHintVisible = if (isCurrent) state.isHintVisible else false,
                            hintText = if (isCurrent) state.hintText else null,
                            pipFeedbackMessage = if (isCurrent) state.pipFeedbackMessage else null,
                            pipFeedbackMood = state.pipFeedbackMood,
                            onSelectOption = { optionId -> if (isCurrent) viewModel.onOptionSelected(optionId) },
                            onGetHint = { if (isCurrent) viewModel.onGetHintClicked() },
                            onTryAgain = { if (isCurrent) viewModel.onTryAgainClicked() },
                            onContinue = { if (isCurrent) viewModel.onContinueClicked() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessonTopBar(
    progress: Float,
    onCloseClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        IconButton(onClick = onCloseClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close lesson",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        CerealProgressBar(
            progress = progress,
            modifier = Modifier.weight(1f),
            height = 10.dp
        )
    }
}

@Composable
fun LessonStepItem(
    step: LessonStep,
    isCurrentStep: Boolean,
    selectedOptionId: String?,
    isAnswerChecked: Boolean,
    isAnswerCorrect: Boolean,
    isWrongAnswerPromptVisible: Boolean,
    isHintVisible: Boolean,
    hintText: String?,
    pipFeedbackMessage: String?,
    pipFeedbackMood: MascotMood,
    onSelectOption: (String) -> Unit,
    onGetHint: () -> Unit,
    onTryAgain: () -> Unit,
    onContinue: () -> Unit
) {
    when (step) {
        is LessonStep.CompanionMessage -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PipMascot(
                        mood = step.mascotMood,
                        size = 52.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    SpeechBubble(
                        sender = step.sender,
                        content = step.content,
                        tip = step.tip,
                        modifier = Modifier.weight(1f)
                    )
                }
                AnimatedVisibility(
                    visible = isCurrentStep,
                    enter = fadeIn(tween(350)) + expandVertically(
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        expandFrom = Alignment.Top
                    ),
                    exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        CerealButton(
                            text = "Continue",
                            style = ButtonStyle.PRIMARY,
                            onClick = onContinue,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        is LessonStep.ExplanationCard -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            1.5.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = step.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = step.content,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!step.highlight.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Key takeaway: ${step.highlight}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    visible = isCurrentStep,
                    enter = fadeIn(tween(350)) + expandVertically(
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        expandFrom = Alignment.Top
                    ),
                    exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        CerealButton(
                            text = "Continue",
                            style = ButtonStyle.PRIMARY,
                            onClick = onContinue,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        is LessonStep.MultipleChoice -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = step.question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                step.options.forEach { option ->
                    val choiceState = when (selectedOptionId) {
                        option.id if isAnswerChecked && isAnswerCorrect -> ChoiceState.CORRECT
                        option.id if isAnswerChecked -> ChoiceState.WRONG
                        option.id -> ChoiceState.SELECTED
                        else -> ChoiceState.DEFAULT
                    }

                    InteractiveChoiceCard(
                        option = option,
                        state = choiceState,
                        onSelect = { onSelectOption(option.id) },
                        enabled = isCurrentStep && !isAnswerChecked,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Pip feedback after wrong answer
                AnimatedVisibility(
                    visible = isCurrentStep && isWrongAnswerPromptVisible,
                    enter = fadeIn(tween(350)) + expandVertically(
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        expandFrom = Alignment.Top
                    ),
                    exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PipMascot(
                                mood = pipFeedbackMood,
                                size = 52.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            SpeechBubble(
                                sender = "Pip",
                                content = pipFeedbackMessage ?: "That's not quite right! Would you like to try again, or do you need a hint?",
                                tip = if (isHintVisible) hintText else null,
                                modifier = Modifier.weight(1f),
                                actions = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (!isHintVisible) {
                                            CerealButton(
                                                text = "Get a hint",
                                                style = ButtonStyle.GHOST,
                                                height = 44.dp,
                                                shapeRadius = 12.dp,
                                                onClick = onGetHint,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        CerealButton(
                                            text = "Try again",
                                            style = ButtonStyle.PRIMARY,
                                            height = 44.dp,
                                            shapeRadius = 12.dp,
                                            onClick = onTryAgain,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                // Pip feedback after correct answer
                AnimatedVisibility(
                    visible = isCurrentStep && isAnswerChecked && isAnswerCorrect && !pipFeedbackMessage.isNullOrBlank(),
                    enter = fadeIn(tween(350)) + expandVertically(
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        expandFrom = Alignment.Top
                    ),
                    exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PipMascot(
                                mood = pipFeedbackMood,
                                size = 52.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            SpeechBubble(
                                sender = "Pip",
                                content = pipFeedbackMessage ?: "",
                                modifier = Modifier.weight(1f),
                                actions = {
                                    CerealButton(
                                        text = "Continue",
                                        style = ButtonStyle.SUCCESS,
                                        height = 44.dp,
                                        shapeRadius = 12.dp,
                                        onClick = onContinue,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
        is LessonStep.Summary -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(MintSuccess.copy(alpha = 0.12f))
                        .border(1.5.dp, MintSuccess.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PipMascot(mood = step.mascotMood, size = 76.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = step.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MintSuccess
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = step.content,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                // Continue button lives inside the summary step, no floating bar needed
                AnimatedVisibility(
                    visible = isCurrentStep,
                    enter = fadeIn(tween(400)) + expandVertically(
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        expandFrom = Alignment.Top
                    ),
                    exit = fadeOut(tween(200))
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        CerealButton(
                            text = "Complete lesson",
                            style = ButtonStyle.PRIMARY,
                            onClick = onContinue,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessonCelebrationScreen(
    lessonTitle: String,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            PipMascot(mood = MascotMood.CELEBRATING, size = 110.dp)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Lesson completed",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Great work finishing \"$lessonTitle\". You're building solid financial understanding.",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MintSuccess,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Curriculum progress updated",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        CerealButton(
            text = "Return to lessons",
            style = ButtonStyle.PRIMARY,
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
