package com.slateblua.cereal.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slateblua.cereal.domain.model.StepOption
import com.slateblua.cereal.ui.theme.MintSuccess

val WrongRed = Color(0xFFE53935)

enum class ChoiceState {
    DEFAULT,
    SELECTED,
    CORRECT,
    WRONG
}

@Composable
fun InteractiveChoiceCard(
    modifier: Modifier = Modifier,
    option: StepOption,
    state: ChoiceState,
    onSelect: () -> Unit,
    enabled: Boolean = true
) {
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val primaryColor = MaterialTheme.colorScheme.primary

    val targetBorderColor = when (state) {
        ChoiceState.DEFAULT -> outlineColor
        ChoiceState.SELECTED -> primaryColor
        ChoiceState.CORRECT -> MintSuccess
        ChoiceState.WRONG -> WrongRed
    }

    val targetBgColor = when (state) {
        ChoiceState.DEFAULT -> MaterialTheme.colorScheme.surface
        ChoiceState.SELECTED -> primaryColor.copy(alpha = 0.10f)
        ChoiceState.CORRECT -> MintSuccess.copy(alpha = 0.12f)
        ChoiceState.WRONG -> WrongRed.copy(alpha = 0.08f)
    }

    val borderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "choice_border"
    )

    val backgroundColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "choice_bg"
    )

    val circleColor by animateColorAsState(
        targetValue = when (state) {
            ChoiceState.DEFAULT -> Color.Transparent
            ChoiceState.SELECTED -> primaryColor
            ChoiceState.CORRECT -> MintSuccess
            ChoiceState.WRONG -> WrongRed
        },
        animationSpec = tween(300),
        label = "choice_circle"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(enabled = enabled, onClick = onSelect)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Circle indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(circleColor)
                    .border(
                        2.dp,
                        when (state) {
                            ChoiceState.DEFAULT -> outlineColor
                            ChoiceState.SELECTED -> primaryColor
                            ChoiceState.CORRECT -> MintSuccess
                            ChoiceState.WRONG -> WrongRed
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    ChoiceState.SELECTED -> Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    ChoiceState.CORRECT -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    ChoiceState.WRONG -> Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    ChoiceState.DEFAULT -> Unit
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = option.text,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = if (state != ChoiceState.DEFAULT) FontWeight.Bold else FontWeight.Medium,
                color = when (state) {
                    ChoiceState.CORRECT -> MintSuccess
                    ChoiceState.WRONG -> WrongRed
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
