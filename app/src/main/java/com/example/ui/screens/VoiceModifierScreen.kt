package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ModifierType
import com.example.model.VoiceModifierConfig
import com.example.ui.components.LiveWaveformVisualizer
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GamerEmerald
import com.example.ui.theme.LaserRed
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VoiceModifierScreen(
    modifiers: List<VoiceModifierConfig>,
    activeModifier: ModifierType,
    isLoopbackActive: Boolean,
    micLevel: Float,
    waveformBars: FloatArray,
    pitchFineTune: Float,
    distortionFineTune: Float,
    echoDelayMs: Int,
    vadThreshold: Int,
    onSelectModifier: (ModifierType) -> Unit,
    onToggleLoopback: () -> Unit,
    onPitchChange: (Float) -> Unit,
    onDistortionChange: (Float) -> Unit,
    onEchoChange: (Int) -> Unit,
    onVadThresholdChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("voice_modifier_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VOICE MODIFIERS",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Real-time DSP audio transforms for squad trolling & comms",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Live Mic Visualizer & Loopback Preview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = null,
                                tint = if (isLoopbackActive) GamerEmerald else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Hear Yourself (Mic Loopback)",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isLoopbackActive) "Loopback ON: test your voice effect live" else "Loopback OFF: only squad hears your mod",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = isLoopbackActive,
                            onCheckedChange = { onToggleLoopback() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GamerEmerald,
                                checkedTrackColor = GamerEmerald.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = SurfaceVariantDark
                            ),
                            modifier = Modifier.testTag("toggle_loopback_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LiveWaveformVisualizer(
                        bars = waveformBars,
                        micLevel = micLevel,
                        isSpeaking = micLevel > 0.08f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Modifier Presets Grid Header
        item {
            Text(
                text = "VOICE PRESETS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Voice Presets
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                modifiers.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { modConfig ->
                            val isSelected = modConfig.type == activeModifier
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) CyberCyan else SurfaceBorder,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { onSelectModifier(modConfig.type) }
                                    .testTag("modifier_card_${modConfig.type.name}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) CyberCyan.copy(alpha = 0.1f) else SurfaceDark
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = modConfig.badge, fontSize = 24.sp)
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Active",
                                                tint = CyberCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = modConfig.name,
                                        color = if (isSelected) CyberCyan else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    Text(
                                        text = modConfig.description,
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        maxLines = 2,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Fine-Tuning Sliders Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("fine_tune_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "EFFECT FINE-TUNING",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pitch Shift Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Pitch Shift Multiplier", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = String.format("%.2fx", pitchFineTune),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Slider(
                        value = pitchFineTune,
                        onValueChange = onPitchChange,
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = CyberCyan,
                            inactiveTrackColor = SurfaceBorder
                        ),
                        modifier = Modifier.testTag("pitch_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Distortion & Crunch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Distortion / Rage Drive", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = "${(distortionFineTune * 100).toInt()}%",
                            color = LaserRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Slider(
                        value = distortionFineTune,
                        onValueChange = onDistortionChange,
                        valueRange = 0.0f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = LaserRed,
                            activeTrackColor = LaserRed,
                            inactiveTrackColor = SurfaceBorder
                        ),
                        modifier = Modifier.testTag("distortion_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Echo Delay Ms
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Echo / Stadium Delay", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = "${echoDelayMs}ms",
                            color = NeonPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Slider(
                        value = echoDelayMs.toFloat(),
                        onValueChange = { onEchoChange(it.toInt()) },
                        valueRange = 50f..450f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonPurple,
                            activeTrackColor = NeonPurple,
                            inactiveTrackColor = SurfaceBorder
                        ),
                        modifier = Modifier.testTag("echo_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Noise Gate / VAD Threshold
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Voice Gate Sensitivity (VAD)", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = "${vadThreshold}%",
                            color = GamerEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Slider(
                        value = vadThreshold.toFloat(),
                        onValueChange = { onVadThresholdChange(it.toInt()) },
                        valueRange = 5f..95f,
                        colors = SliderDefaults.colors(
                            thumbColor = GamerEmerald,
                            activeTrackColor = GamerEmerald,
                            inactiveTrackColor = SurfaceBorder
                        ),
                        modifier = Modifier.testTag("vad_slider")
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
