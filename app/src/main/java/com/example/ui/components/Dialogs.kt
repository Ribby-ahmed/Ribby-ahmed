package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.model.SquadChannel
import com.example.model.SquadMember
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
fun MemberVolumeDialog(
    member: SquadMember,
    onDismiss: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit
) {
    var volume by remember { mutableFloatStateOf(member.volume) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().testTag("member_volume_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(member.avatarColorHex), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.name.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = member.name,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = member.role,
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "User Volume", color = TextSecondary, fontSize = 13.sp)
                    Text(
                        text = "${(volume * 100).toInt()}%",
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Slider(
                    value = volume,
                    onValueChange = {
                        volume = it
                        onVolumeChange(it)
                    },
                    valueRange = 0.0f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberCyan,
                        activeTrackColor = CyberCyan,
                        inactiveTrackColor = SurfaceBorder
                    ),
                    modifier = Modifier.testTag("member_volume_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onToggleMute()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("toggle_member_mute_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (member.isMuted) GamerEmerald else LaserRed.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (member.isMuted) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = null,
                        tint = if (member.isMuted) Color.Black else LaserRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (member.isMuted) "Unmute Member" else "Mute Member",
                        color = if (member.isMuted) Color.Black else LaserRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AddCustomSoundDialog(
    onDismiss: () -> Unit,
    onAddSound: (title: String, icon: String, description: String, synthType: String, freq: Float, durationMs: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("🎺") }
    var description by remember { mutableStateOf("Custom hype sound") }
    var synthType by remember { mutableStateOf("SYNTH_AIRHORN") }
    var baseFreq by remember { mutableFloatStateOf(440f) }

    val synthOptions = listOf(
        "SYNTH_AIRHORN" to "Airhorn MLG",
        "SYNTH_LAUGH" to "Crowd Laugh",
        "SYNTH_LASER" to "Laser Pew",
        "SYNTH_CHIME" to "Victory Chime",
        "SYNTH_BASS" to "808 Bass Boom"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().testTag("add_custom_sound_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Add Custom Soundboard Button",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Sound Title (e.g. GG EZ)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("custom_sound_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = icon,
                        onValueChange = { icon = it },
                        label = { Text("Emoji") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = SurfaceBorder
                        ),
                        modifier = Modifier.width(90.dp).testTag("custom_sound_icon_input")
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Short Description") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = SurfaceBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Synth Preset:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    synthOptions.take(3).forEach { (type, label) ->
                        val isSelected = synthType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) CyberCyan.copy(alpha = 0.2f) else SurfaceVariantDark,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) CyberCyan else SurfaceBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { synthType = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) CyberCyan else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAddSound(title, icon, description, synthType, baseFreq, 1200)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_custom_sound_button")
                    ) {
                        Text("Add Sound", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddCustomReactionDialog(
    onDismiss: () -> Unit,
    onAddReaction: (emoji: String, punchline: String, colorHex: Long) -> Unit
) {
    var emoji by remember { mutableStateOf("💀") }
    var punchline by remember { mutableStateOf("HE'S ONE SHOT!") }
    var selectedColor by remember { mutableStateOf(0xFF00E5FF) }

    val presetColors = listOf(
        0xFF00E5FF to "Cyan",
        0xFF9D4EDD to "Purple",
        0xFF10B981 to "Green",
        0xFFEF4444 to "Red",
        0xFFF59E0B to "Gold",
        0xFFEC4899 to "Pink"
    )

    val quickEmojis = listOf("💀", "🤡", "🔥", "🧂", "🐐", "🎯", "💣", "💩", "🐔", "🥶")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().testTag("add_custom_reaction_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Craft Custom Reaction Meme",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = punchline,
                    onValueChange = { punchline = it },
                    label = { Text("Reaction Punchline (e.g. REVIVE ME BRO)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("custom_reaction_punchline_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Choose Emoji:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    quickEmojis.take(6).forEach { em ->
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    if (emoji == em) CyberCyan.copy(alpha = 0.2f) else SurfaceVariantDark,
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    if (emoji == em) CyberCyan else SurfaceBorder,
                                    CircleShape
                                )
                                .clickable { emoji = em },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = em, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Glow Color:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetColors.forEach { (colorHex, _) ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(colorHex), CircleShape)
                                .border(
                                    width = if (selectedColor == colorHex) 2.5.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (punchline.isNotBlank() && emoji.isNotBlank()) {
                                onAddReaction(emoji, punchline, selectedColor)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_custom_reaction_button")
                    ) {
                        Text("Save Reaction", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SwitchChannelDialog(
    channels: List<SquadChannel>,
    currentChannelId: String,
    onDismiss: () -> Unit,
    onSelectChannel: (SquadChannel) -> Unit,
    onCreateChannel: (name: String, game: String, icon: String, bitrate: Int) -> Unit
) {
    var showCreateForm by remember { mutableStateOf(false) }
    var newChannelName by remember { mutableStateOf("") }
    var newChannelGame by remember { mutableStateOf("Overwatch 2") }
    var newChannelIcon by remember { mutableStateOf("⚡") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().testTag("switch_channel_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showCreateForm) "Create Squad Channel" else "Squad Voice Channels",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!showCreateForm) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(channels) { ch ->
                            val isSelected = ch.id == currentChannelId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(
                                        if (isSelected) CyberCyan.copy(alpha = 0.15f) else SurfaceVariantDark,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) CyberCyan else SurfaceBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        onSelectChannel(ch)
                                        onDismiss()
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = ch.iconEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "#${ch.name}",
                                            color = if (isSelected) CyberCyan else TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${ch.gameCategory} · ${ch.bitrateKbps}kbps",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "${ch.activeUsers}/${ch.maxUsers} 👤",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showCreateForm = true },
                        modifier = Modifier.fillMaxWidth().testTag("open_create_channel_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = CyberCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create New Squad Room", color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedTextField(
                        value = newChannelName,
                        onValueChange = { newChannelName = it },
                        label = { Text("Channel Name (e.g. Scrim-Lobby-A)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = SurfaceBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newChannelIcon,
                            onValueChange = { newChannelIcon = it },
                            label = { Text("Icon") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = SurfaceBorder
                            ),
                            modifier = Modifier.width(80.dp)
                        )

                        OutlinedTextField(
                            value = newChannelGame,
                            onValueChange = { newChannelGame = it },
                            label = { Text("Game Title") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = SurfaceBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showCreateForm = false }) {
                            Text("Back", color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newChannelName.isNotBlank()) {
                                    onCreateChannel(newChannelName, newChannelGame, newChannelIcon, 32)
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            enabled = newChannelName.isNotBlank(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Create & Join", color = BackgroundDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
