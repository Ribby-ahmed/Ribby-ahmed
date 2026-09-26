package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ModifierType
import com.example.model.ReactionEmojiItem
import com.example.model.SquadMember
import com.example.ui.components.AnimatedProfileAvatar
import com.example.ui.components.ChannelBanner
import com.example.ui.components.MemberVolumeDialog
import com.example.ui.components.SwitchChannelDialog
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
import com.example.viewmodel.SquadVoiceUiState

@Composable
fun SquadRoomScreen(
    uiState: SquadVoiceUiState,
    myMicLevel: Float,
    reactionEmojis: List<ReactionEmojiItem>,
    userProfile: com.example.model.UserProfile = com.example.model.UserProfile(),
    onToggleMic: () -> Unit,
    onToggleDeafen: () -> Unit,
    onPttPressChange: (Boolean) -> Unit,
    onNavigateToModifiers: () -> Unit,
    onNavigateToFriends: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onOpenNetwork: () -> Unit = {},
    onSelectChannel: (com.example.model.SquadChannel) -> Unit,
    onCreateChannel: (String, String, String, Int) -> Unit,
    onMemberVolumeChange: (String, Float) -> Unit,
    onMemberToggleMute: (String) -> Unit,
    onSendReaction: (String, String, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMemberForVolume by remember { mutableStateOf<SquadMember?>(null) }
    var showChannelSwitcher by remember { mutableStateOf(false) }

    val isSelfSpeaking = !uiState.isMicMuted && (
            (uiState.isPttModeEnabled && uiState.isPttActive) ||
                    (!uiState.isPttModeEnabled && myMicLevel > (uiState.vadThresholdPercent / 100f))
            )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("squad_room_screen")
    ) {
        // Channel Banner & Ping
        ChannelBanner(
            channel = uiState.currentChannel,
            networkStats = uiState.networkStats,
            onSwitchChannelClick = { showChannelSwitcher = true },
            onNetworkClick = onOpenNetwork
        )

        // Last broadcast sound alert pill
        if (uiState.lastBroadcastSoundTitle != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔊", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.lastBroadcastSoundTitle,
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Squad Members Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SQUAD IN VOICE (${uiState.members.size + 1}/8)",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar Button
                Box(
                    modifier = Modifier
                        .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                        .clickable(onClick = onNavigateToProfile)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("squad_my_profile_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedProfileAvatar(
                            avatarType = userProfile.animatedAvatar,
                            size = 18.dp,
                            showSpeakingRing = false
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Profile",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable(onClick = onNavigateToFriends)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("squad_invite_friends_button")
                ) {
                    Text(
                        text = "+ Friends",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Self Card
            item {
                SelfSquadCard(
                    userProfile = userProfile,
                    isSpeaking = isSelfSpeaking,
                    isMuted = uiState.isMicMuted,
                    activeModifier = uiState.activeModifier,
                    micLevel = myMicLevel,
                    onClick = onNavigateToProfile
                )
            }

            // Teammates Cards
            items(uiState.members) { member ->
                SquadMemberCard(
                    member = member,
                    onClick = { selectedMemberForVolume = member }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Reaction Dock
        QuickReactionDock(
            reactions = reactionEmojis,
            onSendReaction = { em, punch, color -> onSendReaction(em, punch, color) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Push To Talk or Mic Control Bar
        SelfControlsBar(
            isMicMuted = uiState.isMicMuted,
            isDeafened = uiState.isDeafened,
            isPttModeEnabled = uiState.isPttModeEnabled,
            isPttActive = uiState.isPttActive,
            activeModifier = uiState.activeModifier,
            onToggleMic = onToggleMic,
            onToggleDeafen = onToggleDeafen,
            onPttPressChange = onPttPressChange,
            onOpenModifiers = onNavigateToModifiers
        )

        Spacer(modifier = Modifier.height(4.dp))
    }

    // Member Volume Dialog
    if (selectedMemberForVolume != null) {
        val member = selectedMemberForVolume!!
        MemberVolumeDialog(
            member = member,
            onDismiss = { selectedMemberForVolume = null },
            onVolumeChange = { vol -> onMemberVolumeChange(member.id, vol) },
            onToggleMute = { onMemberToggleMute(member.id) }
        )
    }

    // Switch Channel Dialog
    if (showChannelSwitcher) {
        SwitchChannelDialog(
            channels = uiState.channels,
            currentChannelId = uiState.currentChannel.id,
            onDismiss = { showChannelSwitcher = false },
            onSelectChannel = onSelectChannel,
            onCreateChannel = onCreateChannel
        )
    }
}

@Composable
private fun SelfSquadCard(
    userProfile: com.example.model.UserProfile,
    isSpeaking: Boolean,
    isMuted: Boolean,
    activeModifier: ModifierType,
    micLevel: Float,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isSpeaking -> CyberCyan
            isMuted -> LaserRed.copy(alpha = 0.5f)
            else -> SurfaceBorder
        },
        label = "selfBorder"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = if (isSpeaking) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("self_squad_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                AnimatedProfileAvatar(
                    avatarType = userProfile.animatedAvatar,
                    size = 52.dp,
                    isSpeaking = isSpeaking,
                    micLevel = micLevel,
                    showSpeakingRing = true
                )

                if (isMuted) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .background(LaserRed, CircleShape)
                            .border(1.5.dp, BackgroundDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MicOff,
                            contentDescription = "Muted",
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You (${userProfile.username})",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1
            )

            // Modifier tag
            val modLabel = when (activeModifier) {
                ModifierType.NORMAL -> "Clear Voice"
                ModifierType.ROBOT -> "🤖 Robot"
                ModifierType.CHIPMUNK -> "🐿️ Helium"
                ModifierType.DEMON -> "👹 Demon"
                ModifierType.ALIEN_RADIO -> "📻 Radio"
                ModifierType.STADIUM_ECHO -> "🏟️ Echo"
                ModifierType.AUTOTUNE -> "🎵 Autotune"
                ModifierType.BITCRUSH -> "👾 8-Bit"
            }

            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .background(NeonPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = modLabel,
                    color = NeonPurple,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Speaking audio bar
            LinearProgressIndicator(
                progress = { if (isMuted) 0f else micLevel.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = if (isSpeaking) CyberCyan else SurfaceBorder,
                trackColor = SurfaceVariantDark
            )
        }
    }
}

@Composable
private fun SquadMemberCard(
    member: SquadMember,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            member.isSpeaking -> GamerEmerald
            member.isMuted -> LaserRed.copy(alpha = 0.5f)
            else -> SurfaceBorder
        },
        label = "memberBorder"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = if (member.isSpeaking) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("squad_member_card_${member.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (member.isSpeaking) {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1.0f,
                        targetValue = 1.25f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(550, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .scale(pulseScale)
                            .background(GamerEmerald.copy(alpha = 0.25f), CircleShape)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(member.avatarColorHex), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.name.take(2).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = member.name,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1
            )

            Text(
                text = "${member.pingMs}ms · ${(member.volume * 100).toInt()}% vol",
                color = TextMuted,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Speaking audio bar
            LinearProgressIndicator(
                progress = { if (member.isMuted) 0f else member.micLevel.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = if (member.isSpeaking) GamerEmerald else SurfaceBorder,
                trackColor = SurfaceVariantDark
            )
        }
    }
}

@Composable
private fun QuickReactionDock(
    reactions: List<ReactionEmojiItem>,
    onSendReaction: (String, String, Long) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUICK SQUAD REACTIONS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Pop to squad",
                    color = CyberCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(reactions.take(8)) { r ->
                    Box(
                        modifier = Modifier
                            .background(SurfaceVariantDark, RoundedCornerShape(12.dp))
                            .border(1.dp, Color(r.colorHex).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { onSendReaction(r.emoji, r.punchline, r.colorHex) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("quick_reaction_${r.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = r.emoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = r.punchline,
                                color = Color(r.colorHex),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelfControlsBar(
    isMicMuted: Boolean,
    isDeafened: Boolean,
    isPttModeEnabled: Boolean,
    isPttActive: Boolean,
    activeModifier: ModifierType,
    onToggleMic: () -> Unit,
    onToggleDeafen: () -> Unit,
    onPttPressChange: (Boolean) -> Unit,
    onOpenModifiers: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(20.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Mic Toggle
        IconButton(
            onClick = onToggleMic,
            modifier = Modifier
                .size(46.dp)
                .background(
                    if (isMicMuted) LaserRed.copy(alpha = 0.2f) else CyberCyan.copy(alpha = 0.2f),
                    CircleShape
                )
                .border(
                    1.dp,
                    if (isMicMuted) LaserRed else CyberCyan,
                    CircleShape
                )
                .testTag("toggle_mic_button")
        ) {
            Icon(
                imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = "Toggle Mic",
                tint = if (isMicMuted) LaserRed else CyberCyan
            )
        }

        // Deafen Toggle
        IconButton(
            onClick = onToggleDeafen,
            modifier = Modifier
                .size(46.dp)
                .background(
                    if (isDeafened) LaserRed.copy(alpha = 0.2f) else SurfaceVariantDark,
                    CircleShape
                )
                .border(
                    1.dp,
                    if (isDeafened) LaserRed else SurfaceBorder,
                    CircleShape
                )
                .testTag("toggle_deafen_button")
        ) {
            Icon(
                imageVector = if (isDeafened) Icons.Default.VolumeOff else Icons.Default.Headphones,
                contentDescription = "Toggle Deafen",
                tint = if (isDeafened) LaserRed else TextPrimary
            )
        }

        // Center Action: Push-To-Talk Button OR Voice Gate indicator
        if (isPttModeEnabled) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .padding(horizontal = 8.dp)
                    .background(
                        if (isPttActive) GamerEmerald else SurfaceVariantDark,
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.5.dp,
                        if (isPttActive) GamerEmerald else CyberCyan,
                        RoundedCornerShape(12.dp)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                onPttPressChange(true)
                                tryAwaitRelease()
                                onPttPressChange(false)
                            }
                        )
                    }
                    .testTag("ptt_hold_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPttActive) "TRANSMITTING... 🎙️" else "HOLD TO TALK (PTT)",
                    color = if (isPttActive) BackgroundDark else CyberCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        } else {
            // Voice Modifiers Quick Launcher
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .padding(horizontal = 8.dp)
                    .background(NeonPurple.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenModifiers)
                    .padding(horizontal = 10.dp)
                    .testTag("open_modifiers_bar"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VOICE MOD: ${activeModifier.name}",
                        color = NeonPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Settings / Fine-tune button
        IconButton(
            onClick = onOpenModifiers,
            modifier = Modifier
                .size(46.dp)
                .background(SurfaceVariantDark, CircleShape)
                .border(1.dp, SurfaceBorder, CircleShape)
                .testTag("settings_button")
        ) {
            Icon(Icons.Default.Tune, contentDescription = "Tune Voice", tint = TextPrimary)
        }
    }
}
