package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GamerEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted

@Composable
fun LiveWaveformVisualizer(
    bars: FloatArray,
    micLevel: Float,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedLevel = remember { Animatable(0f) }

    LaunchedEffect(micLevel) {
        animatedLevel.animateTo(micLevel, tween(80))
    }

    Box(
        modifier = modifier
            .background(SurfaceVariantDark, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (isSpeaking) GamerEmerald else TextMuted,
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSpeaking) "MIC ACTIVE · LIVE DSP" else "VOICE IDLE · GATE CLOSED",
                        color = if (isSpeaking) GamerEmerald else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                val dbApprox = if (isSpeaking) ((animatedLevel.value * 60) - 45).toInt() else -60
                Text(
                    text = "${dbApprox} dB",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Waveform equalizer bars
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                val totalBars = bars.size
                val barSpacing = 4.dp.toPx()
                val totalSpacing = (totalBars - 1) * barSpacing
                val barWidth = ((size.width - totalSpacing) / totalBars).coerceAtLeast(3f)
                val canvasHeight = size.height

                for (i in 0 until totalBars) {
                    val rawBarHeight = (bars[i] * canvasHeight).coerceIn(4f, canvasHeight)
                    val barHeight = if (isSpeaking) rawBarHeight else (canvasHeight * 0.08f)
                    val x = i * (barWidth + barSpacing)
                    val y = (canvasHeight - barHeight) / 2f

                    val barBrush = Brush.verticalGradient(
                        colors = listOf(
                            CyberCyan,
                            NeonPurple
                        ),
                        startY = y,
                        endY = y + barHeight
                    )

                    drawRoundRect(
                        brush = barBrush,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
        }
    }
}
