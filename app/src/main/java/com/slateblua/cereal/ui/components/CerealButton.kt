package com.slateblua.cereal.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slateblua.cereal.ui.theme.SlateBlueDark
import com.slateblua.cereal.ui.theme.SlateBluePrimary

enum class ButtonStyle {
    PRIMARY,
    SECONDARY,
    SUCCESS,
    WARNING,
    GHOST
}

@Composable
fun CerealButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    icon: (@Composable () -> Unit)? = null,
    height: Dp = 56.dp,
    shapeRadius: Dp = 16.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val view = LocalView.current

    LaunchedEffect(isPressed) {
        if (isPressed && enabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    val (faceColor, borderColor, textColor) = when (style) {
        ButtonStyle.PRIMARY -> if (enabled) {
            Triple(SlateBluePrimary, SlateBlueDark, Color.White)
        } else {
            Triple(Color(0xFFD4D8E6), Color(0xFFB0B6CB), Color(0xFF8C93AA))
        }
        ButtonStyle.SECONDARY -> if (enabled) {
            Triple(Color(0xFFFFB800), Color(0xFFD49700), Color(0xFF1E2238))
        } else {
            Triple(Color(0xFFE5E7EB), Color(0xFFCBD5E1), Color(0xFF94A3B8))
        }
        ButtonStyle.SUCCESS -> if (enabled) {
            Triple(Color(0xFF00C9A7), Color(0xFF009B80), Color.White)
        } else {
            Triple(Color(0xFFD4D8E6), Color(0xFFB0B6CB), Color(0xFF8C93AA))
        }
        ButtonStyle.WARNING -> if (enabled) {
            Triple(Color(0xFFFF5E7E), Color(0xFFD93A5B), Color.White)
        } else {
            Triple(Color(0xFFD4D8E6), Color(0xFFB0B6CB), Color(0xFF8C93AA))
        }
        ButtonStyle.GHOST -> {
            Triple(Color.Transparent, Color.Transparent, MaterialTheme.colorScheme.primary)
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "button_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (style != ButtonStyle.GHOST && enabled && !isPressed) 3.dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "button_shadow_elevation"
    )

    val animatedFaceColor by animateColorAsState(
        targetValue = if (isPressed && enabled && style != ButtonStyle.GHOST) {
            borderColor
        } else {
            faceColor
        },
        animationSpec = tween(durationMillis = 100),
        label = "button_face_color"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(shapeRadius),
                clip = false
            )
            .clip(RoundedCornerShape(shapeRadius))
            .background(animatedFaceColor)
            .then(
                if (style == ButtonStyle.GHOST) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(shapeRadius)
                    )
                } else if (enabled) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = borderColor.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(shapeRadius)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
