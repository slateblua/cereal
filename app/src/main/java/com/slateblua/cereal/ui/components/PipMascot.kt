package com.slateblua.cereal.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.slateblua.cereal.domain.model.MascotMood
import com.slateblua.cereal.ui.theme.GoldXp
import com.slateblua.cereal.ui.theme.MintSuccess
import com.slateblua.cereal.ui.theme.SlateBlueDark
import com.slateblua.cereal.ui.theme.SlateBlueLight
import com.slateblua.cereal.ui.theme.SlateBluePrimary
import com.slateblua.cereal.ui.theme.TextPrimaryLight

@Composable
fun PipMascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 90.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pip_animations")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pip_bounce"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val width = this.size.width
            val height = this.size.height

            val centerY = height * 0.52f + bounceOffset

            // 1. Soft Shadow underneath Pip
            drawOval(
                color = Color(0x221E2238),
                topLeft = Offset(width * 0.2f, height * 0.88f),
                size = Size(width * 0.6f, height * 0.1f)
            )

            // 2. Bowl Body (Cute rounded cereal bowl shape)
            val bowlPath = Path().apply {
                moveTo(width * 0.18f, centerY - height * 0.18f)
                cubicTo(
                    width * 0.18f, centerY + height * 0.35f,
                    width * 0.82f, centerY + height * 0.35f,
                    width * 0.82f, centerY - height * 0.18f
                )
                close()
            }

            // Bowl gradient fill
            drawPath(
                path = bowlPath,
                brush = Brush.verticalGradient(
                    colors = listOf(SlateBlueLight, SlateBluePrimary, SlateBlueDark),
                    startY = centerY - height * 0.2f,
                    endY = centerY + height * 0.35f
                )
            )

            // Bowl Border
            drawPath(
                path = bowlPath,
                color = SlateBlueDark,
                style = Stroke(width = width * 0.04f, cap = StrokeCap.Round)
            )

            // 3. Bowl Rim (Ellipse on top)
            drawOval(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF9E6), Color(0xFFF0E5D0))
                ),
                topLeft = Offset(width * 0.16f, centerY - height * 0.26f),
                size = Size(width * 0.68f, height * 0.16f)
            )
            drawOval(
                color = SlateBlueDark,
                topLeft = Offset(width * 0.16f, centerY - height * 0.26f),
                size = Size(width * 0.68f, height * 0.16f),
                style = Stroke(width = width * 0.035f)
            )

            // 4. Cereal & Milk Inside
            drawOval(
                color = Color.White,
                topLeft = Offset(width * 0.2f, centerY - height * 0.23f),
                size = Size(width * 0.6f, height * 0.12f)
            )

            // Floating colorful cereal loops (Berry, Honey, Mint)
            drawCerealLoop(Offset(width * 0.32f, centerY - height * 0.18f), GoldXp, width * 0.045f)
            drawCerealLoop(Offset(width * 0.48f, centerY - height * 0.20f), MintSuccess, width * 0.042f)
            drawCerealLoop(Offset(width * 0.65f, centerY - height * 0.17f), Color(0xFFFF5E7E), width * 0.046f)

            // 5. Cute Face Features (Eyes, Cheeks, Mouth according to mood)
            drawMascotFace(mood, width, height, centerY)

            drawSpoonAntenna(width, height, centerY)
        }
    }
}

private fun DrawScope.drawCerealLoop(center: Offset, color: Color, radius: Float) {
    drawCircle(color = color, radius = radius, center = center)
    drawCircle(color = Color.White, radius = radius * 0.4f, center = center)
}

private fun DrawScope.drawSpoonAntenna(width: Float, height: Float, centerY: Float) {
    val startX = width * 0.72f
    val startY = centerY - height * 0.22f
    val endX = width * 0.82f
    val endY = centerY - height * 0.44f

    drawLine(
        color = Color(0xFFD6DBE8),
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = width * 0.04f,
        cap = StrokeCap.Round
    )

    // Spoon head (golden coin/star highlight)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFDF7A), GoldXp),
            center = Offset(endX, endY),
            radius = width * 0.08f
        ),
        radius = width * 0.075f,
        center = Offset(endX, endY)
    )
    drawCircle(
        color = Color(0xFFD49700),
        radius = width * 0.075f,
        center = Offset(endX, endY),
        style = Stroke(width = width * 0.02f)
    )
}

private fun DrawScope.drawMascotFace(
    mood: MascotMood,
    width: Float,
    height: Float,
    centerY: Float
) {
    val eyeY = centerY + height * 0.02f
    val leftEyeX = width * 0.38f
    val rightEyeX = width * 0.62f

    // Rosy Cheeks
    drawCircle(
        color = Color(0x66FF6B8B),
        radius = width * 0.055f,
        center = Offset(width * 0.28f, centerY + height * 0.09f)
    )
    drawCircle(
        color = Color(0x66FF6B8B),
        radius = width * 0.055f,
        center = Offset(width * 0.72f, centerY + height * 0.09f)
    )

    when (mood) {
        MascotMood.HAPPY, MascotMood.PROUD -> {
            // Big happy eyes with sparkles
            drawHappyEye(leftEyeX, eyeY, width)
            drawHappyEye(rightEyeX, eyeY, width)

            // Warm smile
            val smilePath = Path().apply {
                moveTo(width * 0.44f, centerY + height * 0.08f)
                quadraticTo(
                    width * 0.5f, centerY + height * 0.16f,
                    width * 0.56f, centerY + height * 0.08f
                )
            }
            drawPath(
                path = smilePath,
                color = TextPrimaryLight,
                style = Stroke(width = width * 0.035f, cap = StrokeCap.Round)
            )
        }
        MascotMood.CELEBRATING, MascotMood.EXCITED -> {
            // Star/Arc ecstatic eyes (^ ^)
            drawExcitedEye(leftEyeX, eyeY, width)
            drawExcitedEye(rightEyeX, eyeY, width)

            // Big open laughing mouth
            val mouthPath = Path().apply {
                moveTo(width * 0.42f, centerY + height * 0.07f)
                cubicTo(
                    width * 0.42f, centerY + height * 0.18f,
                    width * 0.58f, centerY + height * 0.18f,
                    width * 0.58f, centerY + height * 0.07f
                )
                close()
            }
            drawPath(mouthPath, color = Color(0xFFFF5E7E))
            drawPath(
                path = mouthPath,
                color = TextPrimaryLight,
                style = Stroke(width = width * 0.03f, cap = StrokeCap.Round)
            )
        }
    }
}

private fun DrawScope.drawHappyEye(x: Float, y: Float, width: Float) {
    drawCircle(color = TextPrimaryLight, radius = width * 0.045f, center = Offset(x, y))
    drawCircle(color = Color.White, radius = width * 0.018f, center = Offset(x + width * 0.015f, y - width * 0.015f))
    drawCircle(color = Color.White, radius = width * 0.008f, center = Offset(x - width * 0.012f, y + width * 0.015f))
}

private fun DrawScope.drawExcitedEye(x: Float, y: Float, width: Float) {
    val eyePath = Path().apply {
        moveTo(x - width * 0.04f, y + width * 0.02f)
        quadraticTo(x, y - width * 0.04f, x + width * 0.04f, y + width * 0.02f)
    }
    drawPath(
        path = eyePath,
        color = TextPrimaryLight,
        style = Stroke(width = width * 0.038f, cap = StrokeCap.Round)
    )
}
