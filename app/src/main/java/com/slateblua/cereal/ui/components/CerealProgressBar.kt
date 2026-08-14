package com.slateblua.cereal.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.slateblua.cereal.ui.theme.SlateBluePrimary

@Composable
fun CerealProgressBar(
    modifier: Modifier = Modifier,
    progress: Float, // 0f to 1f
    height: Dp = 14.dp,
    trackColor: Color = Color(0xFFE5E9F5),
    fillGradient: List<Color> = listOf(Color(0xFF7A86F5), SlateBluePrimary)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "progress_anim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
    ) {
        if (animatedProgress > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(height / 2))
                    .background(Brush.horizontalGradient(fillGradient))
            ) {
                // Top gloss shine line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height * 0.35f)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                        .clip(CircleShape)
                        .background(Color(0x55FFFFFF))
                )
            }
        }
    }
}
