package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.AnimatedAvatarType
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-performance, fully procedurally rendered animated gaming avatar.
 * Renders distinct animated effects for each AnimatedAvatarType.
 */
@Composable
fun AnimatedProfileAvatar(
    avatarType: AnimatedAvatarType,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    isSpeaking: Boolean = false,
    micLevel: Float = 0f,
    showSpeakingRing: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")

    // Continuous animations
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val reverseRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rev_rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -0.08f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    val speakingGlowColor = Color(avatarType.accentColorHex)
    val speakingBorderWidth = if (isSpeaking) (3.5.dp + (micLevel * 3).dp) else 0.dp

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (showSpeakingRing && isSpeaking) {
                    Modifier.border(
                        width = speakingBorderWidth,
                        color = speakingGlowColor.copy(alpha = 0.85f),
                        shape = CircleShape
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
        ) {
            val width = this.size.width
            val height = this.size.height
            val center = Offset(width / 2f, height / 2f)
            val radius = width / 2f

            when (avatarType) {
                AnimatedAvatarType.CYBER_NEON -> {
                    // Deep void cyber background
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF0F1E36), Color(0xFF060913)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Rotating Cyber HUD outer segmented ring
                    rotate(rotation, center) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFF00E5FF),
                                    Color(0x2200E5FF),
                                    Color(0xFFA855F7),
                                    Color(0xFF00E5FF)
                                )
                            ),
                            radius = radius * 0.90f,
                            center = center,
                            style = Stroke(
                                width = width * 0.04f,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(width * 0.12f, width * 0.06f),
                                    phase = 0f
                                )
                            )
                        )
                    }

                    // Rotating inner scanner ticks
                    rotate(reverseRotation, center) {
                        drawCircle(
                            color = Color(0xFF00E5FF).copy(alpha = 0.4f),
                            radius = radius * 0.72f,
                            center = center,
                            style = Stroke(
                                width = width * 0.02f,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(width * 0.05f, width * 0.05f),
                                    phase = 0f
                                )
                            )
                        )
                    }

                    // Cyber Android Visor in center
                    val visorHeight = height * 0.18f
                    val visorWidth = width * 0.52f
                    val visorTop = (height - visorHeight) / 2f + (floatOffset * height * 0.3f)
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00E5FF).copy(alpha = shimmer),
                                Color(0xFF67E8F9),
                                Color(0xFF00E5FF).copy(alpha = shimmer)
                            ),
                            start = Offset(center.x - visorWidth / 2, visorTop),
                            end = Offset(center.x + visorWidth / 2, visorTop + visorHeight)
                        ),
                        topLeft = Offset(center.x - visorWidth / 2, visorTop),
                        size = Size(visorWidth, visorHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(visorHeight / 2)
                    )

                    // Cyber Eye Focus dots
                    val eyeSpacing = width * 0.14f
                    drawCircle(
                        color = Color.White,
                        radius = width * 0.04f * pulse,
                        center = Offset(center.x - eyeSpacing, visorTop + visorHeight / 2)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = width * 0.04f * pulse,
                        center = Offset(center.x + eyeSpacing, visorTop + visorHeight / 2)
                    )
                }

                AnimatedAvatarType.PIXEL_RETRO -> {
                    // Retro Arcade CRT background
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF261830), Color(0xFF0F0818)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Pulsing golden neon ring
                    drawCircle(
                        color = Color(0xFFFFD600).copy(alpha = 0.6f * shimmer),
                        radius = radius * 0.88f * pulse.coerceIn(0.95f, 1.05f),
                        center = center,
                        style = Stroke(width = width * 0.035f)
                    )

                    // Animated Equalizer Frequency Bars behind the 8-bit head
                    val barWidth = width * 0.06f
                    val barSpacing = width * 0.09f
                    val barCount = 5
                    val startX = center.x - ((barCount - 1) * barSpacing) / 2f
                    for (i in 0 until barCount) {
                        val barHeightFactor = when (i) {
                            0 -> 0.18f + 0.12f * sin(rotation * 0.08f + i)
                            1 -> 0.28f + 0.18f * cos(rotation * 0.06f + i)
                            2 -> 0.40f + 0.15f * sin(rotation * 0.05f)
                            3 -> 0.28f + 0.18f * sin(rotation * 0.06f + i)
                            else -> 0.18f + 0.12f * cos(rotation * 0.08f + i)
                        }
                        val bHeight = height * barHeightFactor.coerceIn(0.12f, 0.45f)
                        val bX = startX + i * barSpacing
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFD600), Color(0xFFFF6D00))
                            ),
                            topLeft = Offset(bX - barWidth / 2, height * 0.68f - bHeight),
                            size = Size(barWidth, bHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
                        )
                    }

                    // 8-Bit Pixel Character Head
                    val headSize = width * 0.38f
                    val headTop = center.y - headSize * 0.45f + (floatOffset * height * 0.4f)
                    drawRect(
                        color = Color(0xFFFFFBEB),
                        topLeft = Offset(center.x - headSize / 2, headTop),
                        size = Size(headSize, headSize * 0.85f)
                    )

                    // Pixel Gaming Headphones (Left & Right Ear Cups)
                    val earCupW = width * 0.08f
                    val earCupH = headSize * 0.70f
                    drawRect(
                        color = Color(0xFFFF007A),
                        topLeft = Offset(center.x - headSize / 2 - earCupW, headTop + headSize * 0.08f),
                        size = Size(earCupW, earCupH)
                    )
                    drawRect(
                        color = Color(0xFFFF007A),
                        topLeft = Offset(center.x + headSize / 2, headTop + headSize * 0.08f),
                        size = Size(earCupW, earCupH)
                    )
                    // Headphone band
                    drawArc(
                        color = Color(0xFFFF007A),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(center.x - headSize / 2 - earCupW / 2, headTop - headSize * 0.22f),
                        size = Size(headSize + earCupW, headSize * 0.6f),
                        style = Stroke(width = width * 0.045f)
                    )

                    // Pixel Blinking Eyes
                    val eyeW = headSize * 0.18f
                    val eyeH = if (shimmer < 0.4f) headSize * 0.04f else headSize * 0.22f
                    drawRect(
                        color = Color(0xFF1E1B4B),
                        topLeft = Offset(center.x - headSize * 0.32f, headTop + headSize * 0.32f),
                        size = Size(eyeW, eyeH)
                    )
                    drawRect(
                        color = Color(0xFF1E1B4B),
                        topLeft = Offset(center.x + headSize * 0.14f, headTop + headSize * 0.32f),
                        size = Size(eyeW, eyeH)
                    )
                }

                AnimatedAvatarType.FLAME_PHOENIX -> {
                    // Deep lava void background
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF3B0B02), Color(0xFF140200)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Ascending ember sparks
                    for (i in 0..5) {
                        val sparkAngle = (i * 60f + rotation * 0.5f) * (Math.PI / 180f).toFloat()
                        val sparkDist = radius * (0.35f + (pulse - 0.88f) * 1.5f + (i * 0.08f))
                        val sparkX = center.x + cos(sparkAngle) * sparkDist
                        val sparkY = center.y + sin(sparkAngle) * sparkDist - (floatOffset * height * 0.5f)
                        drawCircle(
                            color = Color(0xFFFFD600).copy(alpha = (0.4f + 0.5f * sin(rotation * 0.1f + i)).coerceIn(0.1f, 1f)),
                            radius = width * 0.035f,
                            center = Offset(sparkX, sparkY)
                        )
                    }

                    // Radiating flame ring
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(Color(0xFFFF3D00), Color(0xFFFFD600), Color(0xFFFF0055), Color(0xFFFF3D00))
                        ),
                        radius = radius * 0.85f * pulse.coerceIn(0.96f, 1.04f),
                        center = center,
                        style = Stroke(width = width * 0.04f)
                    )

                    // Central Phoenix Fire Core
                    val coreRadius = radius * 0.45f * pulse
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFFFFFBEB), Color(0xFFFFD600), Color(0xFFFF3D00), Color.Transparent),
                            center = center,
                            radius = coreRadius
                        ),
                        radius = coreRadius,
                        center = center
                    )

                    // Phoenix Flame Crest (Crown)
                    val path = Path().apply {
                        moveTo(center.x, center.y - radius * 0.65f)
                        lineTo(center.x + radius * 0.32f, center.y + radius * 0.10f)
                        lineTo(center.x, center.y + radius * 0.35f)
                        lineTo(center.x - radius * 0.32f, center.y + radius * 0.10f)
                        close()
                    }
                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFD600), Color(0xFFFF3D00), Color(0x88FF0055))
                        )
                    )
                }

                AnimatedAvatarType.ELECTRIC_STORM -> {
                    // High-voltage plasma void
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF1E0A3C), Color(0xFF090214)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Crackling lightning discharge arcs
                    val arcSegments = 6
                    val arcPath = Path()
                    for (i in 0..arcSegments) {
                        val ang = (i * (360f / arcSegments) + rotation * 0.8f) * (Math.PI / 180f).toFloat()
                        val rJitter = radius * (0.75f + 0.14f * sin(rotation * 0.3f + i * 2f))
                        val x = center.x + cos(ang) * rJitter
                        val y = center.y + sin(ang) * rJitter
                        if (i == 0) arcPath.moveTo(x, y) else arcPath.lineTo(x, y)
                    }
                    arcPath.close()

                    drawPath(
                        path = arcPath,
                        color = Color(0xFFA78BFA).copy(alpha = shimmer),
                        style = Stroke(width = width * 0.03f, cap = StrokeCap.Round)
                    )

                    // Outer electric ring
                    drawCircle(
                        color = Color(0xFF7C4DFF).copy(alpha = 0.5f),
                        radius = radius * 0.88f,
                        center = center,
                        style = Stroke(width = width * 0.025f)
                    )

                    // Central Lightning Bolt Glyph
                    val boltW = width * 0.34f
                    val boltH = height * 0.52f
                    val boltPath = Path().apply {
                        moveTo(center.x + boltW * 0.12f, center.y - boltH / 2)
                        lineTo(center.x - boltW * 0.45f, center.y + boltH * 0.05f)
                        lineTo(center.x - boltW * 0.05f, center.y + boltH * 0.05f)
                        lineTo(center.x - boltW * 0.18f, center.y + boltH / 2)
                        lineTo(center.x + boltW * 0.45f, center.y - boltH * 0.05f)
                        lineTo(center.x + boltW * 0.05f, center.y - boltH * 0.05f)
                        close()
                    }

                    drawPath(
                        path = boltPath,
                        brush = Brush.verticalGradient(
                            listOf(Color.White, Color(0xFFC084FC), Color(0xFF7C4DFF))
                        )
                    )
                }

                AnimatedAvatarType.GALAXY_COSMIC -> {
                    // Deep space nebula background
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF280738), Color(0xFF0A0314)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Orbiting planetary rings
                    rotate(rotation, center) {
                        drawOval(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFE040FB),
                                    Color.Transparent,
                                    Color(0xFF00E5FF),
                                    Color(0xFFE040FB)
                                )
                            ),
                            topLeft = Offset(center.x - radius * 0.90f, center.y - radius * 0.32f),
                            size = Size(radius * 1.80f, radius * 0.64f),
                            style = Stroke(width = width * 0.04f)
                        )
                    }

                    // Rotating mini satellites / stars
                    rotate(reverseRotation, center) {
                        val starX = center.x + radius * 0.68f
                        val starY = center.y
                        drawCircle(
                            color = Color(0xFF00E5FF),
                            radius = width * 0.045f,
                            center = Offset(starX, starY)
                        )
                    }

                    // Astronaut / Void Helmet Core
                    val helmetRadius = radius * 0.44f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
                            center = center,
                            radius = helmetRadius
                        ),
                        radius = helmetRadius,
                        center = center
                    )
                    // Cosmic Visor
                    drawOval(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFE040FB).copy(alpha = shimmer), Color(0xFF00E5FF)),
                            start = Offset(center.x - helmetRadius * 0.7f, center.y - helmetRadius * 0.3f),
                            end = Offset(center.x + helmetRadius * 0.7f, center.y + helmetRadius * 0.3f)
                        ),
                        topLeft = Offset(center.x - helmetRadius * 0.65f, center.y - helmetRadius * 0.35f),
                        size = Size(helmetRadius * 1.30f, helmetRadius * 0.70f)
                    )
                }

                AnimatedAvatarType.TOXIC_HAZARD -> {
                    // Radioactive acid sludge background
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF0A2E12), Color(0xFF021206)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Radioactive Hazard segmented warning ring
                    rotate(rotation * 0.5f, center) {
                        drawCircle(
                            color = Color(0xFF00E676).copy(alpha = 0.5f),
                            radius = radius * 0.88f,
                            center = center,
                            style = Stroke(
                                width = width * 0.035f,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(width * 0.15f, width * 0.08f),
                                    phase = 0f
                                )
                            )
                        )
                    }

                    // Rising Slime Bubbles
                    for (i in 0..3) {
                        val bOffset = ((rotation * 0.4f + i * 90f) % 360f) / 360f
                        val bY = height * (0.85f - bOffset * 0.7f)
                        val bX = center.x + sin(bOffset * 10f + i) * (width * 0.25f)
                        drawCircle(
                            color = Color(0xFF69F0AE).copy(alpha = 0.7f - bOffset * 0.5f),
                            radius = width * (0.035f + (i % 2) * 0.015f),
                            center = Offset(bX, bY)
                        )
                    }

                    // Toxic Slime Face Blob
                    val blobRadius = radius * 0.45f * pulse
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF00E676), Color(0xFF007A3D)),
                            center = center,
                            radius = blobRadius
                        ),
                        radius = blobRadius,
                        center = center
                    )

                    // Biohazard Center Trefoil / Eyes
                    val eyeDist = blobRadius * 0.35f
                    drawCircle(
                        color = Color.Black,
                        radius = blobRadius * 0.22f,
                        center = Offset(center.x - eyeDist, center.y - eyeDist * 0.4f)
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = blobRadius * 0.22f,
                        center = Offset(center.x + eyeDist, center.y - eyeDist * 0.4f)
                    )
                    // Toxic dripping smile
                    drawArc(
                        color = Color.Black,
                        startAngle = 20f,
                        sweepAngle = 140f,
                        useCenter = false,
                        topLeft = Offset(center.x - blobRadius * 0.4f, center.y),
                        size = Size(blobRadius * 0.8f, blobRadius * 0.45f),
                        style = Stroke(width = width * 0.04f, cap = StrokeCap.Round)
                    )
                }

                AnimatedAvatarType.SOUNDWAVE_DJ -> {
                    // Deep club bass void
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF330822), Color(0xFF10020B)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Bouncing Soundwave perimeter bars
                    val wavePoints = 12
                    for (i in 0 until wavePoints) {
                        val ang = (i * (360f / wavePoints) + rotation * 0.2f) * (Math.PI / 180f).toFloat()
                        val hFactor = 0.12f + 0.14f * sin(rotation * 0.1f + i * 1.5f)
                        val innerR = radius * 0.72f
                        val outerR = innerR + radius * hFactor
                        val start = Offset(center.x + cos(ang) * innerR, center.y + sin(ang) * innerR)
                        val end = Offset(center.x + cos(ang) * outerR, center.y + sin(ang) * outerR)
                        drawLine(
                            brush = Brush.linearGradient(listOf(Color(0xFFFF007F), Color(0xFF00F5FF))),
                            start = start,
                            end = end,
                            strokeWidth = width * 0.035f,
                            cap = StrokeCap.Round
                        )
                    }

                    // DJ Headset & Mask
                    val djRadius = radius * 0.42f
                    drawCircle(
                        color = Color(0xFF1E1E24),
                        radius = djRadius,
                        center = center
                    )

                    // Neon Audio Visor
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFFF007F), Color(0xFF00F5FF), Color(0xFFFF007F))
                        ),
                        topLeft = Offset(center.x - djRadius * 0.7f, center.y - djRadius * 0.25f),
                        size = Size(djRadius * 1.4f, djRadius * 0.5f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
                    )
                }

                AnimatedAvatarType.KAWAII_NEKO -> {
                    // Pastel anime night sky
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF38152E), Color(0xFF160613)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Floating sparkle heart/star particles
                    val sparkleAngle = (rotation * 0.4f) * (Math.PI / 180f).toFloat()
                    val sX = center.x + cos(sparkleAngle) * (radius * 0.72f)
                    val sY = center.y + sin(sparkleAngle) * (radius * 0.72f)
                    drawCircle(
                        color = Color(0xFFFF80AB).copy(alpha = shimmer),
                        radius = width * 0.04f,
                        center = Offset(sX, sY)
                    )

                    // Outer pink neon halo
                    drawCircle(
                        color = Color(0xFFFF4081).copy(alpha = 0.55f),
                        radius = radius * 0.88f * pulse.coerceIn(0.97f, 1.03f),
                        center = center,
                        style = Stroke(width = width * 0.03f)
                    )

                    // Twitching Neon Neko Ears
                    val earTwitch = sin(rotation * 0.12f) * 4f
                    val leftEar = Path().apply {
                        moveTo(center.x - radius * 0.55f, center.y - radius * 0.15f)
                        lineTo(center.x - radius * 0.42f + earTwitch, center.y - radius * 0.75f)
                        lineTo(center.x - radius * 0.12f, center.y - radius * 0.35f)
                        close()
                    }
                    val rightEar = Path().apply {
                        moveTo(center.x + radius * 0.12f, center.y - radius * 0.35f)
                        lineTo(center.x + radius * 0.42f - earTwitch, center.y - radius * 0.75f)
                        lineTo(center.x + radius * 0.55f, center.y - radius * 0.15f)
                        close()
                    }
                    drawPath(leftEar, brush = Brush.verticalGradient(listOf(Color(0xFFFF4081), Color(0xFFFF80AB))))
                    drawPath(rightEar, brush = Brush.verticalGradient(listOf(Color(0xFFFF4081), Color(0xFFFF80AB))))

                    // Neko Head
                    val nekoRadius = radius * 0.42f
                    drawCircle(
                        color = Color(0xFFFFF0F5),
                        radius = nekoRadius,
                        center = Offset(center.x, center.y + radius * 0.1f)
                    )

                    // Cute Blinking Anime Eyes
                    val eyeW = nekoRadius * 0.22f
                    val eyeY = center.y + radius * 0.08f
                    drawCircle(
                        color = Color(0xFF2E0854),
                        radius = eyeW,
                        center = Offset(center.x - nekoRadius * 0.4f, eyeY)
                    )
                    drawCircle(
                        color = Color(0xFF2E0854),
                        radius = eyeW,
                        center = Offset(center.x + nekoRadius * 0.4f, eyeY)
                    )
                    // Eye glint
                    drawCircle(
                        color = Color.White,
                        radius = eyeW * 0.4f,
                        center = Offset(center.x - nekoRadius * 0.36f, eyeY - eyeW * 0.2f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = eyeW * 0.4f,
                        center = Offset(center.x + nekoRadius * 0.44f, eyeY - eyeW * 0.2f)
                    )
                }

                AnimatedAvatarType.MECHA_TITAN -> {
                    // Military steel command void
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF2D1515), Color(0xFF110707)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Spinning Target Lock Crosshairs
                    rotate(rotation, center) {
                        drawCircle(
                            color = Color(0xFFFF5252).copy(alpha = 0.55f),
                            radius = radius * 0.86f,
                            center = center,
                            style = Stroke(
                                width = width * 0.035f,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(width * 0.15f, width * 0.1f),
                                    phase = 0f
                                )
                            )
                        )
                        // Reticle lines
                        drawLine(
                            color = Color(0xFFFF5252).copy(alpha = 0.7f),
                            start = Offset(center.x - radius * 0.86f, center.y),
                            end = Offset(center.x - radius * 0.60f, center.y),
                            strokeWidth = width * 0.03f
                        )
                        drawLine(
                            color = Color(0xFFFF5252).copy(alpha = 0.7f),
                            start = Offset(center.x + radius * 0.60f, center.y),
                            end = Offset(center.x + radius * 0.86f, center.y),
                            strokeWidth = width * 0.03f
                        )
                    }

                    // Pulsing Red Arc Reactor Core
                    val coreR = radius * 0.38f * pulse
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, Color(0xFFFF5252), Color(0xFFB71C1C), Color.Transparent),
                            center = center,
                            radius = coreR
                        ),
                        radius = coreR,
                        center = center
                    )

                    // Armored Heavy Faceplate Silhouette
                    val plateW = width * 0.46f
                    val plateH = height * 0.40f
                    val platePath = Path().apply {
                        moveTo(center.x - plateW / 2, center.y - plateH * 0.2f)
                        lineTo(center.x - plateW * 0.3f, center.y - plateH / 2)
                        lineTo(center.x + plateW * 0.3f, center.y - plateH / 2)
                        lineTo(center.x + plateW / 2, center.y - plateH * 0.2f)
                        lineTo(center.x + plateW * 0.2f, center.y + plateH / 2)
                        lineTo(center.x - plateW * 0.2f, center.y + plateH / 2)
                        close()
                    }
                    drawPath(
                        path = platePath,
                        color = Color(0xFF1E212B).copy(alpha = 0.85f),
                        style = Stroke(width = width * 0.04f)
                    )
                }
            }
        }
    }
}
