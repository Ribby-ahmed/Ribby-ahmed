package com.example.model

enum class ModifierType {
    NORMAL,
    ROBOT,
    CHIPMUNK,
    DEMON,
    ALIEN_RADIO,
    STADIUM_ECHO,
    AUTOTUNE,
    BITCRUSH
}

data class VoiceModifierConfig(
    val type: ModifierType,
    val name: String,
    val description: String,
    val badge: String,
    val pitchMultiplier: Float = 1.0f,
    val distortion: Float = 0.0f,
    val echoLevel: Float = 0.0f,
    val ringModFreq: Float = 0.0f
)

data class SquadMember(
    val id: String,
    val name: String,
    val role: String,
    val avatarColorHex: Long,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val isDeafened: Boolean = false,
    val volume: Float = 1.0f,
    val pingMs: Int = 24,
    val activeModifier: ModifierType = ModifierType.NORMAL,
    val lastReaction: String? = null,
    val micLevel: Float = 0.0f, // 0.0 to 1.0 for audio wave visualizer
    val animatedAvatarId: String? = null
)

data class SoundEffectItem(
    val id: String,
    val title: String,
    val category: SoundCategory,
    val iconEmoji: String,
    val description: String,
    val soundKey: String,
    val isCustom: Boolean = false
)

enum class SoundCategory(val title: String) {
    ALL("All Sounds"),
    MEME("Meme & Trolls"),
    HYPE("Crowd & Hype"),
    GAMING("Game Callouts"),
    CUSTOM("Custom FX")
}

data class ReactionEmojiItem(
    val id: String,
    val emoji: String,
    val punchline: String,
    val category: String,
    val isCustom: Boolean = false,
    val colorHex: Long = 0xFF00E5FF
)

data class FloatingReaction(
    val id: Long = System.currentTimeMillis() + (0..999).random(),
    val emoji: String,
    val punchline: String,
    val senderName: String,
    val startX: Float, // Normalized 0.1 to 0.9 across screen width
    val colorHex: Long = 0xFF00E5FF
)

data class ServerRegion(
    val id: String,
    val name: String,
    val flag: String,
    val location: String,
    val basePingMs: Int
)

data class SquadChannel(
    val id: String,
    val name: String,
    val gameCategory: String,
    val iconEmoji: String,
    val activeUsers: Int,
    val maxUsers: Int = 8,
    val bitrateKbps: Int = 32,
    val isLocked: Boolean = false
)

data class NetworkStats(
    val pingMs: Int = 22,
    val jitterMs: Int = 2,
    val packetLossPercent: Float = 0.0f,
    val currentBitrateKbps: Int = 32,
    val bufferLatencyMs: Int = 18,
    val lowLatencyModeEnabled: Boolean = true,
    val selectedRegion: ServerRegion = ServerRegion("us-east", "US East (N. Virginia)", "🇺🇸", "Virginia, USA", 22)
)

enum class FriendStatus(val label: String) {
    ONLINE("Online"),
    IN_GAME("In Game"),
    IN_VOICE("In Voice"),
    OFFLINE("Offline")
}

data class FriendItem(
    val id: String,
    val username: String,
    val tag: String, // e.g. "#4092"
    val status: FriendStatus,
    val currentActivity: String, // e.g. "Apex Legends - In Match", "Valorant Ranked"
    val avatarColorHex: Long,
    val isFavorite: Boolean = false,
    val currentVoiceChannelId: String? = null,
    val currentVoiceChannelName: String? = null,
    val pingMs: Int = 24,
    val animatedAvatarId: String? = null
)

enum class AnimatedAvatarType(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val accentColorHex: Long,
    val emojiBadge: String,
    val description: String
) {
    CYBER_NEON(
        id = "CYBER_NEON",
        title = "Neon Glitch Bot",
        subtitle = "Pulsing visor & cyan scanlines",
        category = "Cyber & Sci-Fi",
        accentColorHex = 0xFF00E5FF,
        emojiBadge = "🤖",
        description = "Advanced holographic android with rotating cyber HUD rings and neon eye flares."
    ),
    PIXEL_RETRO(
        id = "PIXEL_RETRO",
        title = "8-Bit Arcade Hero",
        subtitle = "Blinking gamer & audio visualizer",
        category = "Gaming & Anime",
        accentColorHex = 0xFFFFD600,
        emojiBadge = "👾",
        description = "Classic retro chiptune champion with dynamic audio frequency headphones."
    ),
    FLAME_PHOENIX(
        id = "FLAME_PHOENIX",
        title = "Inferno Phoenix",
        subtitle = "Ascending embers & flame aura",
        category = "Elemental & Mystic",
        accentColorHex = 0xFFFF6D00,
        emojiBadge = "🔥",
        description = "Mythic firebird radiating warm magma waves and ascending golden sparks."
    ),
    ELECTRIC_STORM(
        id = "ELECTRIC_STORM",
        title = "Plasma Lightning",
        subtitle = "Crackling violet arcs & bursts",
        category = "Elemental & Mystic",
        accentColorHex = 0xFF7C4DFF,
        emojiBadge = "⚡",
        description = "High-voltage plasma conduit with crackling random electric discharge arcs."
    ),
    GALAXY_COSMIC(
        id = "GALAXY_COSMIC",
        title = "Cosmic Voyager",
        subtitle = "Orbiting planets & nebula glow",
        category = "Cyber & Sci-Fi",
        accentColorHex = 0xFFE040FB,
        emojiBadge = "🪐",
        description = "Deep space explorer enclosed in a rotating galaxy spiral with twinkling stars."
    ),
    TOXIC_HAZARD(
        id = "TOXIC_HAZARD",
        title = "Toxic Bio-Slime",
        subtitle = "Bubbling radioactive slime & aura",
        category = "Cyber & Sci-Fi",
        accentColorHex = 0xFF00E676,
        emojiBadge = "☣️",
        description = "Radioactive gaming beast with bubbling acid goo and pulsing hazard symbols."
    ),
    SOUNDWAVE_DJ(
        id = "SOUNDWAVE_DJ",
        title = "Bass Drop Phantom",
        subtitle = "Bouncing frequency waves & beat pulse",
        category = "Gaming & Anime",
        accentColorHex = 0xFFFF4081,
        emojiBadge = "🎧",
        description = "Masked audio maestro with pulsating bass waves reacting to game audio."
    ),
    KAWAII_NEKO(
        id = "KAWAII_NEKO",
        title = "Cyber Neko Kitty",
        subtitle = "Twitching ears & floating hearts",
        category = "Gaming & Anime",
        accentColorHex = 0xFFFF80AB,
        emojiBadge = "🐱",
        description = "Charming cybernetic anime companion with twitching neon ears and star sparkles."
    ),
    MECHA_TITAN(
        id = "MECHA_TITAN",
        title = "Iron Mecha Overlord",
        subtitle = "Target lock crosshair & plasma core",
        category = "Cyber & Sci-Fi",
        accentColorHex = 0xFFFF5252,
        emojiBadge = "⚔️",
        description = "Heavy battle mech with spinning acquisition reticles and a glowing core reactor."
    )
}

data class UserProfile(
    val username: String = "ApexLegend",
    val gamerTagCode: String = "#2048",
    val bio: String = "Clutching in ranked! Hit me with soundboard memes 🎺",
    val googleEmail: String? = null,
    val isGoogleConnected: Boolean = false,
    val googleDisplayName: String? = null,
    val googleConnectedDate: String? = null,
    val animatedAvatar: AnimatedAvatarType = AnimatedAvatarType.CYBER_NEON,
    val onlineStatus: FriendStatus = FriendStatus.ONLINE,
    val currentGamePlaying: String = "Apex Legends",
    val voiceHoursLogged: Float = 34.2f,
    val soundboardTriggersCount: Int = 520,
    val reactionsSentCount: Int = 340,
    val badges: List<String> = listOf("Voice Mod Pioneer", "Soundboard Maestro", "Early Squad Adopter")
)

