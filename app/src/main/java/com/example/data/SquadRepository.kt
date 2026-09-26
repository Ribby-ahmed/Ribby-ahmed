package com.example.data

import com.example.model.ModifierType
import com.example.model.ReactionEmojiItem
import com.example.model.ServerRegion
import com.example.model.SoundCategory
import com.example.model.SoundEffectItem
import com.example.model.SquadChannel
import com.example.model.SquadMember
import com.example.model.VoiceModifierConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SquadRepository(private val dao: SquadDao) {

    val defaultVoiceModifiers = listOf(
        VoiceModifierConfig(
            type = ModifierType.NORMAL,
            name = "Clear Comms",
            description = "Studio grade gaming voice with gentle noise gate",
            badge = "🎙️"
        ),
        VoiceModifierConfig(
            type = ModifierType.ROBOT,
            name = "Cyber Borg",
            description = "Metallic ring-modulated android robot voice",
            badge = "🤖",
            ringModFreq = 85f
        ),
        VoiceModifierConfig(
            type = ModifierType.CHIPMUNK,
            name = "Helium Squeak",
            description = "Hilarious high-pitch helium balloon voice",
            badge = "🐿️",
            pitchMultiplier = 1.65f
        ),
        VoiceModifierConfig(
            type = ModifierType.DEMON,
            name = "Titan Lord",
            description = "Deep demonic underworld bass voice",
            badge = "👹",
            pitchMultiplier = 0.65f,
            distortion = 0.4f
        ),
        VoiceModifierConfig(
            type = ModifierType.ALIEN_RADIO,
            name = "Tactical Radio",
            description = "Military walkie-talkie bandpass with radio static",
            badge = "📻"
        ),
        VoiceModifierConfig(
            type = ModifierType.STADIUM_ECHO,
            name = "Arena Callout",
            description = "Massive stadium reverberation with multi-tap echo",
            badge = "🏟️",
            echoLevel = 0.5f
        ),
        VoiceModifierConfig(
            type = ModifierType.AUTOTUNE,
            name = "Trap Star",
            description = "Pitch-snapped quantized harmonic vocal",
            badge = "🎵"
        ),
        VoiceModifierConfig(
            type = ModifierType.BITCRUSH,
            name = "Rage 8-Bit",
            description = "Crunchy downsampled arcade bit-depth rage",
            badge = "👾",
            distortion = 0.8f
        )
    )

    val defaultSounds = listOf(
        SoundEffectItem("airhorn", "MLG Airhorn", SoundCategory.MEME, "💥", "Iconic triple horn fanfare", "AIRHORN"),
        SoundEffectItem("laughter", "Crowd Laughter", SoundCategory.HYPE, "😂", "Hilarious sitcom laugh track", "LAUGHTER"),
        SoundEffectItem("applause", "Stadium Claps", SoundCategory.HYPE, "👏", "Roaring ovation and cheering", "APPLAUSE"),
        SoundEffectItem("victory", "Victory Chime", SoundCategory.GAMING, "🏆", "8-bit level up / win fanfare", "VICTORY"),
        SoundEffectItem("sad_trombone", "Sad Trombone", SoundCategory.MEME, "🎺", "Wah-wah-wah fail sound", "SAD_TROMBONE"),
        SoundEffectItem("headshot", "Laser Headshot", SoundCategory.GAMING, "🎯", "High-frequency sniper ping", "HEADSHOT"),
        SoundEffectItem("bruh", "Bruh Sound #2", SoundCategory.MEME, "🗿", "Classic low vocoder bruh", "BRUH"),
        SoundEffectItem("cricket", "Awkward Silence", SoundCategory.MEME, "🦗", "Night crickets for missed jokes", "CRICKET"),
        SoundEffectItem("bass_drop", "808 Bass Boom", SoundCategory.HYPE, "🔊", "Earthquake sub-bass drop", "BASS_DROP"),
        SoundEffectItem("game_over", "Wasted / KO", SoundCategory.GAMING, "💀", "Descending bit-crushed loss", "GAME_OVER"),
        SoundEffectItem("whistle", "Squad Ping", SoundCategory.GAMING, "🚨", "Alert tactical attention call", "WHISTLE"),
        SoundEffectItem("gigachad", "Phonk Brass", SoundCategory.MEME, "🗿", "Heavy bass synth chad stab", "GIGACHAD")
    )

    val defaultReactions = listOf(
        ReactionEmojiItem("dead", "💀", "DEAD", "humor", colorHex = 0xFFEF4444),
        ReactionEmojiItem("clown", "🤡", "CLOWN SQUAD", "troll", colorHex = 0xFFF59E0B),
        ReactionEmojiItem("clutch", "🔥", "CLUTCH GOD", "hype", colorHex = 0xFFFF5722),
        ReactionEmojiItem("salty", "🧂", "SO SALTY", "troll", colorHex = 0xFF00E5FF),
        ReactionEmojiItem("goat", "🐐", "THE G.O.A.T", "hype", colorHex = 0xFF10B981),
        ReactionEmojiItem("sight", "🎯", "DIFF / SIGHT", "gaming", colorHex = 0xFF3B82F6),
        ReactionEmojiItem("winner", "🍗", "WINNER", "gaming", colorHex = 0xFFEAB308),
        ReactionEmojiItem("potato", "🥔", "POTATO AIM", "humor", colorHex = 0xFF8B5CF6),
        ReactionEmojiItem("moon", "🚀", "TO THE MOON", "hype", colorHex = 0xFF06B6D4),
        ReactionEmojiItem("brain", "🧠", "BIG BRAIN", "gaming", colorHex = 0xFFEC4899),
        ReactionEmojiItem("defuse", "💣", "DEFUSE NOW!", "gaming", colorHex = 0xFFF43F5E),
        ReactionEmojiItem("ice", "🥶", "COLD BLOODED", "hype", colorHex = 0xFF38BDF8)
    )

    val availableRegions = listOf(
        ServerRegion("us-east", "US-East (N. Virginia)", "🇺🇸", "Low Jitter 18ms", 18),
        ServerRegion("us-west", "US-West (Oregon)", "🇺🇸", "Edge Tier 32ms", 32),
        ServerRegion("eu-central", "EU-Central (Frankfurt)", "🇩🇪", "Direct Route 41ms", 41),
        ServerRegion("asia-east", "Asia-East (Tokyo)", "🇯🇵", "Fiber Ultra 64ms", 64),
        ServerRegion("sa-east", "SA-East (São Paulo)", "🇧🇷", "Direct Peering 88ms", 88)
    )

    val defaultChannels = listOf(
        SquadChannel("c1", "Ranked-Apex-Arena", "Battle Royale", "🎮", activeUsers = 4, maxUsers = 8, bitrateKbps = 32),
        SquadChannel("c2", "Valorant-Tactics", "Tactical FPS", "🎯", activeUsers = 5, maxUsers = 5, bitrateKbps = 48),
        SquadChannel("c3", "Rocket-Chaos", "Casual Fun", "🏎️", activeUsers = 3, maxUsers = 6, bitrateKbps = 24),
        SquadChannel("c4", "Midnight-Chill", "Lofi Lobby", "☕", activeUsers = 2, maxUsers = 12, bitrateKbps = 32)
    )

    val defaultSquadMembers = listOf(
        SquadMember("u1", "Viper_Queen", "Squad Leader", 0xFF9D4EDD, pingMs = 21, activeModifier = ModifierType.ROBOT),
        SquadMember("u2", "GhostSniper99", "Entry Fragger", 0xFF00E5FF, pingMs = 26, activeModifier = ModifierType.NORMAL),
        SquadMember("u3", "NeonPixel", "Support / Healer", 0xFF10B981, pingMs = 19, activeModifier = ModifierType.CHIPMUNK),
        SquadMember("u4", "NoobMaster77", "Clutch Anchor", 0xFFEF4444, pingMs = 34, activeModifier = ModifierType.DEMON)
    )

    // Flow for custom sounds stored in Room
    val customSoundsFlow: Flow<List<SoundEffectItem>> = dao.getAllCustomSounds().map { list ->
        list.map { entity ->
            SoundEffectItem(
                id = entity.id,
                title = entity.title,
                category = SoundCategory.CUSTOM,
                iconEmoji = entity.iconEmoji,
                description = entity.description,
                soundKey = entity.synthType,
                isCustom = true
            )
        }
    }

    // Flow for custom reactions stored in Room
    val customReactionsFlow: Flow<List<ReactionEmojiItem>> = dao.getAllCustomReactions().map { list ->
        list.map { entity ->
            ReactionEmojiItem(
                id = entity.id,
                emoji = entity.emoji,
                punchline = entity.punchline,
                category = "custom",
                isCustom = true,
                colorHex = entity.colorHex
            )
        }
    }

    val defaultFriends = listOf(
        com.example.model.FriendItem(
            id = "f1",
            username = "CyberNinja",
            tag = "#7701",
            status = com.example.model.FriendStatus.IN_GAME,
            currentActivity = "Cyberpunk - Phantom Liberty",
            avatarColorHex = 0xFF00E5FF,
            isFavorite = true,
            pingMs = 18
        ),
        com.example.model.FriendItem(
            id = "f2",
            username = "ValkyrieSky",
            tag = "#2048",
            status = com.example.model.FriendStatus.IN_GAME,
            currentActivity = "Apex Legends - Ranked Diamond",
            avatarColorHex = 0xFF9D4EDD,
            isFavorite = true,
            pingMs = 24
        ),
        com.example.model.FriendItem(
            id = "f3",
            username = "PixelSamurai",
            tag = "#3390",
            status = com.example.model.FriendStatus.IN_VOICE,
            currentActivity = "Voice in #Midnight-Chill",
            avatarColorHex = 0xFF10B981,
            currentVoiceChannelId = "c4",
            currentVoiceChannelName = "Midnight-Chill",
            pingMs = 21
        ),
        com.example.model.FriendItem(
            id = "f4",
            username = "ShadowPhantom",
            tag = "#0007",
            status = com.example.model.FriendStatus.ONLINE,
            currentActivity = "In Main Lobby · Ready for Scrims",
            avatarColorHex = 0xFFF59E0B,
            pingMs = 28
        ),
        com.example.model.FriendItem(
            id = "f5",
            username = "SniperWolf",
            tag = "#8821",
            status = com.example.model.FriendStatus.OFFLINE,
            currentActivity = "Last seen 2 hours ago",
            avatarColorHex = 0xFF64748B,
            pingMs = 45
        )
    )

    val friendsFlow: Flow<List<com.example.model.FriendItem>> = dao.getAllFriends().map { list ->
        if (list.isEmpty()) {
            defaultFriends
        } else {
            list.map { entity ->
                com.example.model.FriendItem(
                    id = entity.id,
                    username = entity.username,
                    tag = entity.tag,
                    status = try {
                        com.example.model.FriendStatus.valueOf(entity.status)
                    } catch (e: Exception) {
                        com.example.model.FriendStatus.ONLINE
                    },
                    currentActivity = entity.currentActivity,
                    avatarColorHex = entity.avatarColorHex,
                    isFavorite = entity.isFavorite,
                    currentVoiceChannelId = entity.currentVoiceChannelId,
                    currentVoiceChannelName = entity.currentVoiceChannelName,
                    pingMs = entity.pingMs
                )
            }
        }
    }

    val friendRequestsFlow: Flow<List<FriendRequestEntity>> = dao.getAllFriendRequests()

    suspend fun addFriend(
        username: String,
        tag: String,
        status: com.example.model.FriendStatus = com.example.model.FriendStatus.ONLINE,
        activity: String = "Online in SquadVoice",
        colorHex: Long = 0xFF00E5FF
    ) {
        val entity = FriendEntity(
            id = "f_${System.currentTimeMillis()}",
            username = username,
            tag = if (tag.startsWith("#")) tag else "#$tag",
            status = status.name,
            currentActivity = activity,
            avatarColorHex = colorHex,
            pingMs = (16..35).random()
        )
        dao.insertFriend(entity)
    }

    suspend fun removeFriend(id: String) = dao.deleteFriend(id)

    suspend fun toggleFavorite(id: String, isFav: Boolean) = dao.updateFavorite(id, isFav)

    suspend fun addFriendRequest(request: FriendRequestEntity) = dao.insertFriendRequest(request)

    suspend fun deleteFriendRequest(id: String) = dao.deleteFriendRequest(id)

    val userSettingsFlow: Flow<UserSettingsEntity?> = dao.getUserSettings()

    val userProfileFlow: Flow<com.example.model.UserProfile> = dao.getUserSettings().map { entity ->
        if (entity == null) {
            com.example.model.UserProfile()
        } else {
            val avatar = try {
                com.example.model.AnimatedAvatarType.valueOf(entity.animatedAvatarId)
            } catch (e: Exception) {
                com.example.model.AnimatedAvatarType.CYBER_NEON
            }
            val status = try {
                com.example.model.FriendStatus.valueOf(entity.onlineStatus)
            } catch (e: Exception) {
                com.example.model.FriendStatus.ONLINE
            }
            com.example.model.UserProfile(
                username = entity.username,
                gamerTagCode = entity.gamerTagCode,
                bio = entity.bio,
                googleEmail = entity.googleEmail,
                isGoogleConnected = entity.isGoogleConnected,
                googleDisplayName = entity.googleDisplayName,
                googleConnectedDate = entity.googleConnectedDate,
                animatedAvatar = avatar,
                onlineStatus = status,
                currentGamePlaying = entity.currentGamePlaying,
                voiceHoursLogged = entity.voiceHoursLogged,
                soundboardTriggersCount = entity.soundboardTriggersCount,
                reactionsSentCount = entity.reactionsSentCount
            )
        }
    }

    suspend fun updateUserProfile(profile: com.example.model.UserProfile) {
        val entity = UserSettingsEntity(
            id = 1,
            username = profile.username,
            gamerTagCode = profile.gamerTagCode,
            bio = profile.bio,
            googleEmail = profile.googleEmail,
            isGoogleConnected = profile.isGoogleConnected,
            googleDisplayName = profile.googleDisplayName,
            googleConnectedDate = profile.googleConnectedDate,
            animatedAvatarId = profile.animatedAvatar.id,
            onlineStatus = profile.onlineStatus.name,
            currentGamePlaying = profile.currentGamePlaying,
            voiceHoursLogged = profile.voiceHoursLogged,
            soundboardTriggersCount = profile.soundboardTriggersCount,
            reactionsSentCount = profile.reactionsSentCount
        )
        dao.saveUserSettings(entity)
    }

    suspend fun addCustomSound(sound: CustomSoundEntity) = dao.insertCustomSound(sound)
    suspend fun deleteCustomSound(id: String) = dao.deleteCustomSound(id)

    suspend fun addCustomReaction(reaction: CustomReactionEntity) = dao.insertCustomReaction(reaction)
    suspend fun deleteCustomReaction(id: String) = dao.deleteCustomReaction(id)

    suspend fun saveSettings(settings: UserSettingsEntity) = dao.saveUserSettings(settings)
}

