package com.slateblua.cereal.ui.lessons

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slateblua.cereal.domain.model.MascotMood
import com.slateblua.cereal.domain.model.NodeStatus
import com.slateblua.cereal.domain.model.RoadmapNode
import com.slateblua.cereal.domain.model.RoadmapUnit
import com.slateblua.cereal.ui.components.CerealButton
import com.slateblua.cereal.ui.components.PipMascot
import com.slateblua.cereal.ui.components.TopStatsHeader
import com.slateblua.cereal.ui.theme.MintSuccess
import com.slateblua.cereal.ui.theme.MintSuccessDark
import com.slateblua.cereal.ui.theme.SlateBlueDark
import com.slateblua.cereal.ui.theme.SlateBluePrimary
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kotlin.math.sin
import androidx.core.graphics.toColorInt

@Composable
fun LessonsScreen(
    viewModel: LessonsViewModel,
    onNavigateToLesson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    var showStreakDialog by remember { mutableStateOf(false) }
    var streakDaysForDialog by remember { mutableIntStateOf(1) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is LessonsSideEffect.NavigateToLesson -> onNavigateToLesson(effect.lessonId)
            is LessonsSideEffect.ShowStreakDialog -> {
                streakDaysForDialog = effect.streakDays
                showStreakDialog = true
            }
            is LessonsSideEffect.ShowLockedNodeMessage -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showStreakDialog) {
        LessonsStreakDialog(
            streakDays = streakDaysForDialog,
            onDismiss = { showStreakDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopStatsHeader(
            stats = state.userStats,
            title = "lessons",
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
                    bottom = 24.dp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                state.units.forEach { unit ->
                    item(key = "unit_header_${unit.id}") {
                        UnitHeaderCard(
                            unit = unit,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }

                    itemsIndexed(
                        items = unit.nodes,
                        key = { _, node -> "node_${node.id}" }
                    ) { index, node ->
                        RoadmapNodeItem(
                            node = node,
                            index = index,
                            onNodeClick = { viewModel.onNodeClicked(node) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UnitHeaderCard(
    unit: RoadmapUnit,
    modifier: Modifier = Modifier
) {
    val bannerColor = try {
        Color(unit.bannerColorHex.toColorInt())
    } catch (_: Exception) {
        SlateBluePrimary
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bannerColor)
            .padding(18.dp)
    ) {
        Column {
            Text(
                text = "Unit ${unit.unitNumber}",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = unit.title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = unit.description,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun RoadmapNodeItem(
    node: RoadmapNode,
    index: Int,
    onNodeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val xOffsetFraction = sin(index * 1.3).toFloat()
    val xOffsetDp = (xOffsetFraction * 75).dp

    val transition = rememberInfiniteTransition(label = "pulse_node")
    val pulseScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "active_pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .offset(x = xOffsetDp)
    ) {
        val (faceColor, shadowColor, iconTint) = when (node.status) {
            NodeStatus.COMPLETED -> Triple(MintSuccess, MintSuccessDark, Color.White)
            NodeStatus.AVAILABLE -> Triple(SlateBluePrimary, SlateBlueDark, Color.White)
            NodeStatus.LOCKED -> Triple(Color(0xFFE2E6F2), Color(0xFFC5CBE0), Color(0xFF98A1B8))
        }

        Box(
            modifier = Modifier
                .then(
                    if (node.status == NodeStatus.AVAILABLE) Modifier.scale(pulseScale) else Modifier
                )
                .size(72.dp)
                .clickable(onClick = onNodeClick),
            contentAlignment = Alignment.Center
        ) {
            // Shadow
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .offset(y = 4.dp)
                    .clip(CircleShape)
                    .background(shadowColor)
            )

            // Face
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(faceColor)
                    .border(
                        3.dp,
                        if (node.status == NodeStatus.AVAILABLE) Color.White else shadowColor,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (node.status) {
                    NodeStatus.COMPLETED -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    NodeStatus.AVAILABLE -> {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start lesson",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    NodeStatus.LOCKED -> {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = iconTint,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (node.status == NodeStatus.AVAILABLE) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
                .border(
                    1.5.dp,
                    if (node.status == NodeStatus.AVAILABLE) SlateBlueDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = node.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = if (node.status == NodeStatus.AVAILABLE) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LessonsStreakDialog(
    streakDays: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "$streakDays-day streak",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PipMascot(mood = MascotMood.CELEBRATING, size = 80.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "You've practiced financial habits for $streakDays days in a row. Consistency is the true key to financial freedom.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        },
        confirmButton = {
            CerealButton(
                text = "Keep it up",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}
