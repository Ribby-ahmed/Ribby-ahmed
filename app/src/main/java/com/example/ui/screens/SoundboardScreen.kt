package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SoundCategory
import com.example.model.SoundEffectItem
import com.example.ui.components.AddCustomSoundDialog
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GamerEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SoundboardScreen(
    sounds: List<SoundEffectItem>,
    selectedCategory: SoundCategory,
    isBroadcastEnabled: Boolean,
    onPlaySound: (SoundEffectItem) -> Unit,
    onSelectCategory: (SoundCategory) -> Unit,
    onToggleBroadcast: () -> Unit,
    onAddCustomSound: (title: String, icon: String, description: String, synthType: String, freq: Float, durationMs: Int) -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddSoundDialog by remember { mutableStateOf(false) }
    var soundboardVolume by remember { mutableFloatStateOf(0.85f) }

    val filteredSounds = if (selectedCategory == SoundCategory.ALL) {
        sounds
    } else {
        sounds.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("soundboard_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SQUAD SOUNDBOARD",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Instant low-latency procedural sound effects & memes",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddSoundDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("open_add_sound_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Custom FX", color = BackgroundDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Broadcast to Squad Switch Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = if (isBroadcastEnabled) CyberCyan else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isBroadcastEnabled) "Broadcast to Squad Channel (Active)" else "Local Only (Preview Mode)",
                            color = if (isBroadcastEnabled) CyberCyan else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (isBroadcastEnabled) "All squad members hear your soundboard!" else "Only you hear the sound",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Switch(
                    checked = isBroadcastEnabled,
                    onCheckedChange = { onToggleBroadcast() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyberCyan,
                        checkedTrackColor = CyberCyan.copy(alpha = 0.3f),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceVariantDark
                    ),
                    modifier = Modifier.testTag("toggle_soundboard_broadcast_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SoundCategory.values()) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) CyberCyan.copy(alpha = 0.2f) else SurfaceVariantDark,
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) CyberCyan else SurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectCategory(cat) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("sound_category_${cat.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat.title,
                        color = if (isSelected) CyberCyan else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Soundboard Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredSounds) { sound ->
                SoundboardPadCard(
                    sound = sound,
                    onPlay = { onPlaySound(sound) }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Soundboard Master Volume
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Pad Vol: ${(soundboardVolume * 100).toInt()}%", color = TextSecondary, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = soundboardVolume,
                onValueChange = {
                    soundboardVolume = it
                    onVolumeChange(it)
                },
                valueRange = 0.1f..1.0f,
                colors = SliderDefaults.colors(
                    thumbColor = CyberCyan,
                    activeTrackColor = CyberCyan,
                    inactiveTrackColor = SurfaceBorder
                ),
                modifier = Modifier.weight(1f).testTag("soundboard_volume_slider")
            )
        }
    }

    if (showAddSoundDialog) {
        AddCustomSoundDialog(
            onDismiss = { showAddSoundDialog = false },
            onAddSound = { title, icon, desc, synth, freq, dur ->
                onAddCustomSound(title, icon, desc, synth, freq, dur)
            }
        )
    }
}

@Composable
private fun SoundboardPadCard(
    sound: SoundEffectItem,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onPlay)
            .testTag("soundboard_pad_${sound.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = sound.iconEmoji, fontSize = 28.sp)
                if (sound.isCustom) {
                    Box(
                        modifier = Modifier
                            .background(NeonPurple.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text("CUSTOM", color = NeonPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(SurfaceVariantDark, RoundedCornerShape(6.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(sound.category.name, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column {
                Text(
                    text = sound.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = sound.description,
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}
