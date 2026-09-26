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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.FriendRequestEntity
import com.example.model.FriendItem
import com.example.model.FriendStatus
import com.example.model.UserProfile
import com.example.ui.components.AddFriendDialog
import com.example.ui.components.AnimatedProfileAvatar
import com.example.ui.components.GoogleBlue
import com.example.ui.theme.AmberHype
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

enum class FriendFilterTab(val title: String) {
    ONLINE("Online"),
    IN_GAME("In Game"),
    ALL("All Friends"),
    REQUESTS("Requests")
}

@Composable
fun FriendsScreen(
    friends: List<FriendItem>,
    friendRequests: List<FriendRequestEntity>,
    currentChannelMembers: List<com.example.model.SquadMember>,
    userProfile: UserProfile? = null,
    onAddFriend: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    onInviteToVoice: (FriendItem) -> Unit,
    onPokeWithSound: (FriendItem) -> Unit,
    onAcceptRequest: (FriendRequestEntity) -> Unit,
    onDeclineRequest: (String) -> Unit,
    onNavigateToProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(FriendFilterTab.ONLINE) }
    var showAddFriendDialog by remember { mutableStateOf(false) }

    val filteredFriends = friends.filter { friend ->
        val matchesSearch = searchQuery.isBlank() ||
                friend.username.contains(searchQuery, ignoreCase = true) ||
                friend.tag.contains(searchQuery, ignoreCase = true) ||
                friend.currentActivity.contains(searchQuery, ignoreCase = true)

        val matchesTab = when (selectedTab) {
            FriendFilterTab.ONLINE -> friend.status != FriendStatus.OFFLINE
            FriendFilterTab.IN_GAME -> friend.status == FriendStatus.IN_GAME
            FriendFilterTab.ALL -> true
            FriendFilterTab.REQUESTS -> true
        }

        matchesSearch && matchesTab
    }

    val onlineCount = friends.count { it.status != FriendStatus.OFFLINE }
    val inGameCount = friends.count { it.status == FriendStatus.IN_GAME }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("friends_screen")
    ) {
        // Header with Add Friend button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GAMING FRIENDS",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "$onlineCount Online · $inGameCount in Matches",
                    color = GamerEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { showAddFriendDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("open_add_friend_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = BackgroundDark,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add Friend",
                    color = BackgroundDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (userProfile != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        Color(userProfile.animatedAvatar.accentColorHex).copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(onClick = onNavigateToProfile)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("friends_my_profile_chip")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedProfileAvatar(
                            avatarType = userProfile.animatedAvatar,
                            size = 38.dp,
                            showSpeakingRing = false
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userProfile.username,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = userProfile.gamerTagCode,
                                    color = CyberCyan,
                                    fontSize = 11.sp
                                )
                                if (userProfile.isGoogleConnected) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "✓", color = GoogleBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = userProfile.bio,
                                color = TextMuted,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(SurfaceVariantDark, RoundedCornerShape(6.dp))
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "My Profile →",
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search friends by name, tag, or game...", fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = SurfaceBorder,
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("friends_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Filter Bar
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FriendFilterTab.values()) { tab ->
                val isSelected = tab == selectedTab
                val badgeCount = when (tab) {
                    FriendFilterTab.ONLINE -> onlineCount
                    FriendFilterTab.IN_GAME -> inGameCount
                    FriendFilterTab.ALL -> friends.size
                    FriendFilterTab.REQUESTS -> friendRequests.size
                }

                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) CyberCyan.copy(alpha = 0.2f) else SurfaceDark,
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) CyberCyan else SurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("friend_tab_${tab.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = tab.title,
                            color = if (isSelected) CyberCyan else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        if (badgeCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (tab == FriendFilterTab.REQUESTS) NeonPurple else if (isSelected) CyberCyan else SurfaceVariantDark,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "$badgeCount",
                                    color = if (tab == FriendFilterTab.REQUESTS) Color.White else if (isSelected) BackgroundDark else TextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main List Content
        if (selectedTab == FriendFilterTab.REQUESTS) {
            // Friend Requests List
            if (friendRequests.isEmpty()) {
                EmptyStateCard(
                    message = "No pending friend requests",
                    icon = "📭"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(friendRequests) { req ->
                        FriendRequestCard(
                            request = req,
                            onAccept = { onAcceptRequest(req) },
                            onDecline = { onDeclineRequest(req.id) }
                        )
                    }
                }
            }
        } else {
            // Friends List
            if (filteredFriends.isEmpty()) {
                EmptyStateCard(
                    message = if (searchQuery.isNotBlank()) "No friends match '$searchQuery'" else "No friends in this category",
                    icon = "🎮"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredFriends) { friend ->
                        val isAlreadyInVoice = currentChannelMembers.any { it.name.startsWith(friend.username) }

                        FriendItemCard(
                            friend = friend,
                            isInCurrentVoice = isAlreadyInVoice,
                            onInviteToVoice = { onInviteToVoice(friend) },
                            onPoke = { onPokeWithSound(friend) },
                            onToggleFavorite = { onToggleFavorite(friend.id, friend.isFavorite) },
                            onRemove = { onRemoveFriend(friend.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddFriendDialog) {
        AddFriendDialog(
            onDismiss = { showAddFriendDialog = false },
            onAddFriend = { tag -> onAddFriend(tag) }
        )
    }
}

@Composable
private fun FriendItemCard(
    friend: FriendItem,
    isInCurrentVoice: Boolean,
    onInviteToVoice: () -> Unit,
    onPoke: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRemove: () -> Unit
) {
    val statusColor = when (friend.status) {
        FriendStatus.ONLINE -> GamerEmerald
        FriendStatus.IN_GAME -> CyberCyan
        FriendStatus.IN_VOICE -> NeonPurple
        FriendStatus.OFFLINE -> TextMuted
    }

    val activityIcon = when (friend.status) {
        FriendStatus.IN_GAME -> "🎮"
        FriendStatus.IN_VOICE -> "🎙️"
        FriendStatus.ONLINE -> "🟢"
        FriendStatus.OFFLINE -> "⚪"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
            .testTag("friend_card_${friend.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left info (avatar, username, tag, activity)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(friend.avatarColorHex), CircleShape)
                            .border(1.5.dp, statusColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = friend.username.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    // Online status dot
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(statusColor, CircleShape)
                            .border(2.dp, SurfaceDark, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = friend.username,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = friend.tag,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        if (friend.isFavorite) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Favorite",
                                tint = AmberHype,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = activityIcon, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = friend.currentActivity,
                            color = if (friend.status == FriendStatus.IN_GAME) CyberCyan else TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Right Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Poke button with sound
                IconButton(
                    onClick = onPoke,
                    modifier = Modifier
                        .size(34.dp)
                        .background(SurfaceVariantDark, CircleShape)
                        .border(1.dp, SurfaceBorder, CircleShape)
                        .testTag("poke_friend_${friend.id}")
                ) {
                    Text(text = "💥", fontSize = 14.sp)
                }

                // Favorite star button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(34.dp)
                        .background(SurfaceVariantDark, CircleShape)
                        .border(1.dp, SurfaceBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (friend.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (friend.isFavorite) AmberHype else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Invite to Voice channel / In Voice indicator
                if (isInCurrentVoice) {
                    Box(
                        modifier = Modifier
                            .background(GamerEmerald.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .border(1.dp, GamerEmerald, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = null,
                                tint = GamerEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "In Voice",
                                color = GamerEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onInviteToVoice,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("invite_friend_${friend.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GroupAdd,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Invite",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Remove friend button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Friend",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendRequestCard(
    request: FriendRequestEntity,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
            .testTag("request_card_${request.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(request.avatarColorHex), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = request.username.take(2).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = request.username,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = request.tag,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = "${request.mutualFriendsCount} mutual squad friends",
                        color = CyberCyan,
                        fontSize = 11.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Accept Button
                IconButton(
                    onClick = onAccept,
                    modifier = Modifier
                        .size(36.dp)
                        .background(GamerEmerald, CircleShape)
                        .testTag("accept_request_${request.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept",
                        tint = BackgroundDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Decline Button
                IconButton(
                    onClick = onDecline,
                    modifier = Modifier
                        .size(36.dp)
                        .background(SurfaceVariantDark, CircleShape)
                        .border(1.dp, SurfaceBorder, CircleShape)
                        .testTag("decline_request_${request.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decline",
                        tint = LaserRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(message: String, icon: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 40.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
