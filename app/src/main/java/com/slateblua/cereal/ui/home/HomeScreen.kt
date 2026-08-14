package com.slateblua.cereal.ui.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slateblua.cereal.domain.model.MascotMood
import com.slateblua.cereal.ui.components.ButtonStyle
import com.slateblua.cereal.ui.components.CerealButton
import com.slateblua.cereal.ui.components.PipMascot
import com.slateblua.cereal.ui.components.TopStatsHeader
import com.slateblua.cereal.ui.theme.SlateBlueLight
import com.slateblua.cereal.ui.theme.SlateBluePrimary
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onNavigateToLesson: (String) -> Unit,
    onNavigateToLessonsTab: () -> Unit = {},
) {
    val state by viewModel.collectAsState()

    val mascotMood = state.settings.mascotMoodOverride ?: if (state.completedLessonsCount > 0) MascotMood.EXCITED else MascotMood.HAPPY

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is HomeSideEffect.NavigateToLesson -> onNavigateToLesson(effect.lessonId)
            is HomeSideEffect.NavigateToLessonsTab -> onNavigateToLessonsTab()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopStatsHeader(
            stats = state.userStats,
            title = "cereal",
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = 24.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                // 1. Welcome Mascot Card with user-selected expression
                item {
                    HomeGreetingCard(
                        mood = mascotMood,
                        completedCount = state.completedLessonsCount,
                        totalLessons = state.totalLessonsCount
                    )
                }

                // 3. Next Lesson Action Card
                item {
                    NextLessonCard(
                        lessonTitle = state.nextAvailableLesson?.title ?: "Explore Lessons",
                        unitTitle = state.nextAvailableLesson?.unitTitle ?: "Curriculum",
                        onContinue = { viewModel.onContinueLessonClicked() }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeGreetingCard(
    mood: MascotMood,
    completedCount: Int,
    totalLessons: Int,
    modifier: Modifier = Modifier
) {
    val message = if (completedCount == 0) {
        "Ready to build your financial foundation? Start your first lesson today."
    } else if (completedCount >= totalLessons) {
        "You have completed all current lessons! Review any topic to stay sharp."
    } else {
        "You have completed $completedCount of $totalLessons lessons. Keep up the great pace!"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(SlateBluePrimary, SlateBlueLight)
                )
            )
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            PipMascot(
                mood = mood,
                size = 68.dp
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Welcome to Cereal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun NextLessonCard(
    lessonTitle: String,
    unitTitle: String,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.5.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = unitTitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = lessonTitle,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            CerealButton(
                text = "Go",
                onClick = onContinue,
                style = ButtonStyle.PRIMARY
            )
        }
    }
}