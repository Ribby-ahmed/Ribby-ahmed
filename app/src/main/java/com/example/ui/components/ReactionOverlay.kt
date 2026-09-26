package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FloatingReaction
import com.example.ui.theme.BackgroundDark
import kotlin.math.roundToInt

@Composable
fun ReactionOverlay(
    reactions: List<FloatingReaction>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("reaction_overlay")) {
        val totalHeight = maxHeight
        val totalWidth = maxWidth

        reactions.forEach { reaction ->
            SingleFloatingReaction(
                reaction = reaction,
                totalHeightPx = totalHeight.value * 2.8f,
                totalWidthPx = totalWidth.value * 2.5f
            )
        }
    }
}

@Composable
private fun SingleFloatingReaction(
    reaction: FloatingReaction,
    totalHeightPx: Float,
    totalWidthPx: Float
) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(0.4f) }
    val wobble = remember { Animatable(0f) }

    LaunchedEffect(reaction.id) {
        // Pop in with scale spring
        scale.animateTo(
            targetValue = 1.15f,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
        )
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 150)
        )

        // Float upwards
        offsetY.animateTo(
            targetValue = -totalHeightPx * 0.75f,
            animationSpec = tween(durationMillis = 3200, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(reaction.id) {
        // Fade out towards top
        kotlinx.coroutines.delay(2200)
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val xPos = (reaction.startX * totalWidthPx * 0.7f).roundToInt()
    val yPos = (totalHeightPx * 0.65f + offsetY.value).roundToInt()

    Box(
        modifier = Modifier
            .offset { IntOffset(x = xPos, y = yPos) }
            .scale(scale.value)
            .alpha(alpha.value)
            .padding(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = reaction.emoji,
                fontSize = 44.sp
            )
            if (reaction.punchline.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .offset(y = (-6).dp)
                        .background(
                            color = BackgroundDark.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.5.dp,
                            color = Color(reaction.colorHex),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = reaction.punchline,
                            color = Color(reaction.colorHex),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Text(
                text = reaction.senderName,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
