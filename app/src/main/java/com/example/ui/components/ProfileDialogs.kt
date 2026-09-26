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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import com.example.model.UserProfile
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GamerEmerald
import com.example.ui.theme.LaserRed
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

// Google brand colors
val GoogleBlue = Color(0xFF4285F4)
val GoogleRed = Color(0xFFEA4335)
val GoogleYellow = Color(0xFFFBBC05)
val GoogleGreen = Color(0xFF34A853)

/**
 * Dialog to connect or manage Google Account email.
 */
@Composable
fun ConnectGoogleAccountDialog(
    currentProfile: UserProfile,
    onConnectGoogle: (email: String, displayName: String) -> Unit,
    onDisconnectGoogle: () -> Unit,
    onDismiss: () -> Unit
) {
    var emailInput by remember {
        mutableStateOf(currentProfile.googleEmail ?: "ribbyahmed41@gmail.com")
    }
    var displayNameInput by remember {
        mutableStateOf(currentProfile.googleDisplayName ?: currentProfile.username)
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("google_account_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Top Google decorative color bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                ) {
                    Box(modifier = Modifier.weight(1f).height(4.dp).background(GoogleBlue))
                    Box(modifier = Modifier.weight(1f).height(4.dp).background(GoogleRed))
                    Box(modifier = Modifier.weight(1f).height(4.dp).background(GoogleYellow))
                    Box(modifier = Modifier.weight(1f).height(4.dp).background(GoogleGreen))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(GoogleBlue.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, GoogleBlue.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = GoogleBlue,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (currentProfile.isGoogleConnected) "Google Account" else "Connect Google Account",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (currentProfile.isGoogleConnected) "Linked & Synchronized" else "Link email for cloud voice & squad sync",
                                color = if (currentProfile.isGoogleConnected) GamerEmerald else TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_google_dialog_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (currentProfile.isGoogleConnected) {
                    // Already connected card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceVariantDark, RoundedCornerShape(16.dp))
                            .border(1.dp, GamerEmerald.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = GamerEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Connected & Verified",
                                    color = GamerEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentProfile.googleEmail ?: "",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (currentProfile.googleConnectedDate != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Linked since ${currentProfile.googleConnectedDate}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Your Google account is actively backing up your custom soundboards, voice modifier presets, and squad room contacts.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onDisconnectGoogle()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LaserRed.copy(alpha = 0.15f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LaserRed.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("disconnect_google_button")
                        ) {
                            Icon(Icons.Default.LinkOff, contentDescription = null, tint = LaserRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Disconnect", color = LaserRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("done_google_button")
                        ) {
                            Text("Done", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Not connected: Input flow
                    Text(
                        text = "Connect your Google email to unlock cloud voice presets, automatic squad sync, and verified player status.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Suggested quick email pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GoogleBlue.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .border(1.dp, GoogleBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clickable { emailInput = "ribbyahmed41@gmail.com" }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = GoogleBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Quick Suggestion",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "ribbyahmed41@gmail.com",
                                    color = GoogleBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = {
                            emailInput = it
                            errorMessage = null
                        },
                        label = { Text("Google Email Address", fontSize = 12.sp) },
                        placeholder = { Text("username@gmail.com", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = GoogleBlue)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoogleBlue,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = GoogleBlue,
                            unfocusedLabelColor = TextSecondary,
                            focusedContainerColor = SurfaceVariantDark,
                            unfocusedContainerColor = SurfaceVariantDark
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("google_email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = displayNameInput,
                        onValueChange = { displayNameInput = it },
                        label = { Text("Google Display Name (Optional)", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Alex", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = CyberCyan,
                            unfocusedLabelColor = TextSecondary,
                            focusedContainerColor = SurfaceVariantDark,
                            unfocusedContainerColor = SurfaceVariantDark
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("google_display_name_input")
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = LaserRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                val trimmed = emailInput.trim()
                                if (!trimmed.contains("@") || !trimmed.contains(".")) {
                                    errorMessage = "Please enter a valid Google email address"
                                } else {
                                    onConnectGoogle(trimmed, displayNameInput.trim().ifEmpty { currentProfile.username })
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f).testTag("confirm_connect_google_button")
                        ) {
                            Text(
                                "Connect Account",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog to select and equip from the range of free, animated profile avatars.
 */
@Composable
fun SelectAnimatedAvatarDialog(
    currentAvatar: AnimatedAvatarType,
    onSelectAvatar: (AnimatedAvatarType) -> Unit,
    onDismiss: () -> Unit
) {
    val categories = listOf("All", "Cyber & Sci-Fi", "Elemental & Mystic", "Gaming & Anime")
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var previewingAvatar by remember { mutableStateOf(currentAvatar) }

    val filteredAvatars = remember(selectedCategoryIndex) {
        val cat = categories[selectedCategoryIndex]
        if (cat == "All") AnimatedAvatarType.values().toList()
        else AnimatedAvatarType.values().filter { it.category == cat }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(580.dp)
                .testTag("avatar_wardrobe_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✨ Animated Avatars",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(GamerEmerald.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .border(1.dp, GamerEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "100% FREE",
                                    color = GamerEmerald,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        Text(
                            text = "Live animated profile visuals for voice chat & squads",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_avatar_dialog_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Large Preview Banner of currently chosen avatar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(previewingAvatar.accentColorHex).copy(alpha = 0.15f),
                                    SurfaceVariantDark
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            Color(previewingAvatar.accentColorHex).copy(alpha = 0.45f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AnimatedProfileAvatar(
                            avatarType = previewingAvatar,
                            size = 68.dp,
                            isSpeaking = true,
                            micLevel = 0.6f
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = previewingAvatar.title,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = previewingAvatar.emojiBadge, fontSize = 14.sp)
                            }
                            Text(
                                text = previewingAvatar.subtitle,
                                color = Color(previewingAvatar.accentColorHex),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = previewingAvatar.description,
                                color = TextMuted,
                                fontSize = 10.sp,
                                maxLines = 2
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Categories Row
                ScrollableTabRow(
                    selectedTabIndex = selectedCategoryIndex,
                    containerColor = Color.Transparent,
                    contentColor = CyberCyan,
                    edgePadding = 0.dp,
                    indicator = {},
                    divider = {}
                ) {
                    categories.forEachIndexed { index, cat ->
                        val isSelected = selectedCategoryIndex == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedCategoryIndex = index },
                            text = {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) CyberCyan.copy(alpha = 0.18f) else SurfaceVariantDark,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) CyberCyan else SurfaceBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) CyberCyan else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Avatar Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f).testTag("animated_avatars_grid")
                ) {
                    items(filteredAvatars) { avatar ->
                        val isEquipped = avatar == currentAvatar
                        val isPreviewing = avatar == previewingAvatar

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { previewingAvatar = avatar }
                                .testTag("avatar_card_${avatar.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPreviewing) SurfaceVariantDark else BackgroundDark
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isPreviewing || isEquipped) 1.5.dp else 1.dp,
                                color = if (isEquipped) GamerEmerald
                                else if (isPreviewing) Color(avatar.accentColorHex)
                                else SurfaceBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    AnimatedProfileAvatar(
                                        avatarType = avatar,
                                        size = 54.dp,
                                        isSpeaking = isPreviewing,
                                        micLevel = 0.5f,
                                        showSpeakingRing = false
                                    )
                                    if (isEquipped) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .size(18.dp)
                                                .background(GamerEmerald, CircleShape)
                                                .border(1.5.dp, BackgroundDark, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Equipped",
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = avatar.title,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (isEquipped) "EQUIPPED" else "FREE",
                                    color = if (isEquipped) GamerEmerald else Color(avatar.accentColorHex),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Button
                Button(
                    onClick = {
                        onSelectAvatar(previewingAvatar)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("equip_avatar_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(previewingAvatar.accentColorHex)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (previewingAvatar == currentAvatar) "Keep Equipped" else "Equip ${previewingAvatar.title}",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
