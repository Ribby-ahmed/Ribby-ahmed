package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_sounds")
data class CustomSoundEntity(
    @PrimaryKey val id: String,
    val title: String,
    val iconEmoji: String,
    val description: String,
    val synthType: String, // "SYNTH_AIRHORN", "SYNTH_LAUGH", "SYNTH_LASER", "SYNTH_CHIME", "SYNTH_BASS"
    val baseFreqHz: Float,
    val durationMs: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_reactions")
data class CustomReactionEntity(
    @PrimaryKey val id: String,
    val emoji: String,
    val punchline: String,
    val colorHex: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val username: String = "ApexLegend",
    val gamerTagCode: String = "#2048",
    val bio: String = "Clutching in ranked! Hit me with soundboard memes 🎺",
    val googleEmail: String? = null,
    val isGoogleConnected: Boolean = false,
    val googleDisplayName: String? = null,
    val googleConnectedDate: String? = null,
    val animatedAvatarId: String = "CYBER_NEON",
    val onlineStatus: String = "ONLINE",
    val currentGamePlaying: String = "Apex Legends",
    val activeModifier: String = "NORMAL",
    val lowLatencyMode: Boolean = true,
    val vadSensitivity: Int = 45, // 0 - 100
    val pushToTalkEnabled: Boolean = false,
    val selectedRegionId: String = "us-east",
    val micVolume: Float = 1.0f,
    val soundboardVolume: Float = 0.85f,
    val voiceHoursLogged: Float = 34.2f,
    val soundboardTriggersCount: Int = 520,
    val reactionsSentCount: Int = 340
)

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey val id: String,
    val username: String,
    val tag: String,
    val status: String, // "ONLINE", "IN_GAME", "IN_VOICE", "OFFLINE"
    val currentActivity: String,
    val avatarColorHex: Long,
    val isFavorite: Boolean = false,
    val currentVoiceChannelId: String? = null,
    val currentVoiceChannelName: String? = null,
    val pingMs: Int = 24,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "friend_requests")
data class FriendRequestEntity(
    @PrimaryKey val id: String,
    val username: String,
    val tag: String,
    val avatarColorHex: Long,
    val mutualFriendsCount: Int = 2,
    val timestamp: Long = System.currentTimeMillis()
)
