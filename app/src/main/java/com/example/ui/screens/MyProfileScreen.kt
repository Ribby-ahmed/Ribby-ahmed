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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AnimatedAvatarType
import com.example.model.FriendStatus
import com.example.model.UserProfile
import com.example.ui.components.AnimatedProfileAvatar
import com.example.ui.components.ConnectGoogleAccountDialog
import com.example.ui.components.GoogleBlue
import com.example.ui.components.GoogleGreen
import com.example.ui.components.GoogleRed
import com.example.ui.components.GoogleYellow
import com.example.ui.components.SelectAnimatedAvatarDialog
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
fun MyProfileScreen(
    userProfile: UserProfile,
    activeModifierName: String,
    onUpdateProfile: (UserProfile) -> Unit,
    onConnectGoogle: (String, String) -> Unit,
    onDisconnectGoogle: () -> Unit,
    onEquipAvatar: (AnimatedAvatarType) -> Unit,
    modifier: Modifier = Modifier
) {
    var showGoogleDialog by remember { mutableStateOf(false) }
    var showAvatarWardrobeDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    var syncSoundboards by remember { mutableStateOf(true) }
    var syncVoicePresets by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("my_profile_screen")
    ) {
        // Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MY PROFILE",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Custom animated identity & connected accounts",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { showEditProfileDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("edit_profile_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = CyberCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Bio", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Hero Profile Card with LIVE Animated Avatar
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("profile_hero_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(userProfile.animatedAvatar.accentColorHex).copy(alpha = 0.5f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(userProfile.animatedAvatar.accentColorHex).copy(alpha = 0.18f),
                                    SurfaceDark
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Live Animated Avatar with change badge
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AnimatedProfileAvatar(
                                avatarType = userProfile.animatedAvatar,
                                size = 96.dp,
                                isSpeaking = true,
                                micLevel = 0.7f,
                                showSpeakingRing = true
                            )

                            // Quick change avatar floating pill
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(BackgroundDark)
                                    .border(1.dp, CyberCyan, CircleShape)
                                    .clickable { showAvatarWardrobeDialog = true }
                                    .padding(6.dp)
                                    .testTag("change_avatar_pill")
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = "Change Animated Avatar",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Avatar Title Pill
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(userProfile.animatedAvatar.accentColorHex).copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    Color(userProfile.animatedAvatar.accentColorHex).copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { showAvatarWardrobeDialog = true }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = userProfile.animatedAvatar.emojiBadge, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = userProfile.animatedAvatar.title,
                                    color = Color(userProfile.animatedAvatar.accentColorHex),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "• Animated", color = TextMuted, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // User display name & Gamer Tag
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userProfile.username,
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = userProfile.gamerTagCode,
                                color = CyberCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (userProfile.isGoogleConnected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Verified Google Account",
                                    tint = GoogleBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Bio
                        Text(
                            text = userProfile.bio,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Online Status selector chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val statuses = listOf(FriendStatus.ONLINE, FriendStatus.IN_GAME, FriendStatus.OFFLINE)
                            statuses.forEach { status ->
                                val isSelected = userProfile.onlineStatus == status
                                val statusColor = when (status) {
                                    FriendStatus.ONLINE -> GamerEmerald
                                    FriendStatus.IN_GAME -> NeonPurple
                                    FriendStatus.IN_VOICE -> CyberCyan
                                    FriendStatus.OFFLINE -> TextMuted
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) statusColor.copy(alpha = 0.2f) else SurfaceVariantDark,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) statusColor else SurfaceBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            onUpdateProfile(userProfile.copy(onlineStatus = status))
                                        }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .background(statusColor, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = status.label,
                                            color = if (isSelected) TextPrimary else TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // CONNECTED GOOGLE ACCOUNT SECTION
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("google_account_section_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (userProfile.isGoogleConnected) GoogleBlue.copy(alpha = 0.5f) else SurfaceBorder
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Google 4-color decorative bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).height(4.dp).background(GoogleBlue))
                        Box(modifier = Modifier.weight(1f).height(4.dp).background(GoogleRed))
                        Box(modifier = Modifier.weight(1f).height(4.dp).background(GoogleYellow))
                        Box(modifier = Modifier.weight(1f).height(4.dp).background(GoogleGreen))
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(GoogleBlue.copy(alpha = 0.15f), CircleShape)
                                        .border(1.dp, GoogleBlue.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "G",
                                        color = GoogleBlue,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Google Account Connection",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (userProfile.isGoogleConnected) "Connected & Cloud Synced" else "Not Connected",
                                        color = if (userProfile.isGoogleConnected) GamerEmerald else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            if (userProfile.isGoogleConnected) {
                                Box(
                                    modifier = Modifier
                                        .background(GamerEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .border(1.dp, GamerEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = GamerEmerald, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("SYNCED", color = GamerEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (userProfile.isGoogleConnected) {
                            // Connected info display
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceVariantDark, RoundedCornerShape(12.dp))
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Linked Google Email",
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = userProfile.googleEmail ?: "",
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (userProfile.googleDisplayName != null) {
                                            Text(
                                                text = "Google User: ${userProfile.googleDisplayName}",
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { showGoogleDialog = true },
                                        modifier = Modifier.size(32.dp).testTag("manage_google_account_button")
                                    ) {
                                        Icon(Icons.Default.SwitchAccount, contentDescription = "Manage", tint = CyberCyan)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Sync Options
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Sync Custom Soundboards & FX",
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Keep created audio memes safe on Google Drive",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                                Switch(
                                    checked = syncSoundboards,
                                    onCheckedChange = { syncSoundboards = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = BackgroundDark,
                                        checkedTrackColor = GoogleBlue,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = SurfaceVariantDark
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Auto-detect Google Play Games",
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Show friends what game you're playing",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                                Switch(
                                    checked = syncVoicePresets,
                                    onCheckedChange = { syncVoicePresets = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = BackgroundDark,
                                        checkedTrackColor = GoogleBlue,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = SurfaceVariantDark
                                    )
                                )
                            }
                        } else {
                            // Not connected CTA
                            Text(
                                text = "Connect your Google email to synchronize your custom soundboards, active voice modifier presets, and squad contacts across devices.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { showGoogleDialog = true },
                                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("connect_google_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "G",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Connect Google Account",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // ANIMATED AVATARS WARDROBE PREVIEW ROW
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ANIMATED AVATARS",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(GamerEmerald.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("ALL FREE", color = GamerEmerald, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }

                Text(
                    text = "View All (9) →",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showAvatarWardrobeDialog = true }.testTag("view_all_avatars_button")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AnimatedAvatarType.values()) { avatar ->
                    val isEquipped = avatar == userProfile.animatedAvatar

                    Card(
                        modifier = Modifier
                            .width(100.dp)
                            .clickable {
                                onEquipAvatar(avatar)
                            }
                            .testTag("quick_avatar_${avatar.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isEquipped) 1.5.dp else 1.dp,
                            color = if (isEquipped) GamerEmerald else SurfaceBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AnimatedProfileAvatar(
                                avatarType = avatar,
                                size = 52.dp,
                                isSpeaking = isEquipped,
                                micLevel = 0.5f,
                                showSpeakingRing = false
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = avatar.title,
                                color = TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )

                            Text(
                                text = if (isEquipped) "EQUIPPED" else "TAP TO USE",
                                color = if (isEquipped) GamerEmerald else Color(avatar.accentColorHex),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // GAMING & VOICE TELEMETRY STATS
        item {
            Text(
                text = "GAMING & VOICE STATS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Voice Time",
                    value = "${userProfile.voiceHoursLogged}h",
                    subtitle = "In Squad Rooms",
                    icon = Icons.Default.Schedule,
                    tint = CyberCyan,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Soundboard",
                    value = "${userProfile.soundboardTriggersCount}",
                    subtitle = "Memes Broadcasted",
                    icon = Icons.Default.VolumeUp,
                    tint = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Reactions",
                    value = "${userProfile.reactionsSentCount}",
                    subtitle = "Emojis Dropped",
                    icon = Icons.Default.Mood,
                    tint = Color(0xFFFF4081),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Active FX",
                    value = activeModifierName,
                    subtitle = "Voice Modifier",
                    icon = Icons.Default.GraphicEq,
                    tint = GamerEmerald,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // GAMER BADGES
        item {
            Text(
                text = "EARNED SQUAD BADGES",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    val badges = mutableListOf(
                        BadgeItem("🎙️", "Voice Mod Pioneer", "Used real-time DSP voice effects in squad"),
                        BadgeItem("📢", "Soundboard Maestro", "Triggered 500+ procedural memes"),
                        BadgeItem("⚡", "Sub-25ms Clutch", "Played with ultra low-latency packet mode")
                    )
                    if (userProfile.isGoogleConnected) {
                        badges.add(0, BadgeItem("✨", "Google Verified Player", "Connected authentic Google account"))
                    }

                    badges.forEachIndexed { index, badge ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(text = badge.emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = badge.title,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = badge.desc,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        if (index < badges.size - 1) {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Google Account Dialog
    if (showGoogleDialog) {
        ConnectGoogleAccountDialog(
            currentProfile = userProfile,
            onConnectGoogle = { email, displayName ->
                onConnectGoogle(email, displayName)
            },
            onDisconnectGoogle = {
                onDisconnectGoogle()
            },
            onDismiss = { showGoogleDialog = false }
        )
    }

    // Animated Avatar Wardrobe Dialog
    if (showAvatarWardrobeDialog) {
        SelectAnimatedAvatarDialog(
            currentAvatar = userProfile.animatedAvatar,
            onSelectAvatar = { avatar ->
                onEquipAvatar(avatar)
            },
            onDismiss = { showAvatarWardrobeDialog = false }
        )
    }

    // Edit Profile Details Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = userProfile,
            onSave = { updated ->
                onUpdateProfile(updated)
                showEditProfileDialog = false
            },
            onDismiss = { showEditProfileDialog = false }
        )
    }
}

private data class BadgeItem(val emoji: String, val title: String, val desc: String)

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextMuted, fontSize = 11.sp)
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun EditProfileDialog(
    currentProfile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf(currentProfile.username) }
    var tagCode by remember { mutableStateOf(currentProfile.gamerTagCode) }
    var bio by remember { mutableStateOf(currentProfile.bio) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().testTag("edit_profile_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Edit Gamer Profile",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_username_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tagCode,
                    onValueChange = { tagCode = it },
                    label = { Text("Gamer Tag #") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_tag_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Gamer Bio") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_bio_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            val cleanTag = if (tagCode.startsWith("#")) tagCode else "#$tagCode"
                            onSave(
                                currentProfile.copy(
                                    username = username.trim().ifEmpty { currentProfile.username },
                                    gamerTagCode = cleanTag,
                                    bio = bio.trim()
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.2f).testTag("save_profile_button")
                    ) {
                        Text("Save", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
