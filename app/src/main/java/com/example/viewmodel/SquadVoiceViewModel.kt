package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundboardAudioEngine
import com.example.audio.VoiceModifierAudioEngine
import com.example.data.CustomReactionEntity
import com.example.data.CustomSoundEntity
import com.example.data.SquadDatabase
import com.example.data.SquadRepository
import com.example.data.UserSettingsEntity
import com.example.model.FloatingReaction
import com.example.model.ModifierType
import com.example.model.NetworkStats
import com.example.model.ReactionEmojiItem
import com.example.model.ServerRegion
import com.example.model.SoundCategory
import com.example.model.SoundEffectItem
import com.example.model.SquadChannel
import com.example.model.SquadMember
import com.example.model.VoiceModifierConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SquadVoiceUiState(
    val currentChannel: SquadChannel,
    val channels: List<SquadChannel> = emptyList(),
    val members: List<SquadMember> = emptyList(),
    val isMicMuted: Boolean = false,
    val isDeafened: Boolean = false,
    val isPttActive: Boolean = false,
    val isPttModeEnabled: Boolean = false,
    val vadThresholdPercent: Int = 35,
    val activeModifier: ModifierType = ModifierType.NORMAL,
    val selectedCategory: SoundCategory = SoundCategory.ALL,
    val isSoundboardBroadcastEnabled: Boolean = true,
    val networkStats: NetworkStats = NetworkStats(),
    val isLoopbackTesting: Boolean = false,
    val floatingReactions: List<FloatingReaction> = emptyList(),
    val lastBroadcastSoundTitle: String? = null,
    val pitchFineTune: Float = 1.0f,
    val distortionFineTune: Float = 0.0f,
    val echoDelayMs: Int = 180,
    val myVolume: Float = 1.0f
)

class SquadVoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SquadRepository
    val audioEngine: VoiceModifierAudioEngine

    init {
        val db = SquadDatabase.getDatabase(application)
        repository = SquadRepository(db.squadDao())
        audioEngine = VoiceModifierAudioEngine(application)
    }

    private val _uiState = MutableStateFlow(
        SquadVoiceUiState(
            currentChannel = repository.defaultChannels.first(),
            channels = repository.defaultChannels,
            members = repository.defaultSquadMembers
        )
    )
    val uiState: StateFlow<SquadVoiceUiState> = _uiState.asStateFlow()

    val voiceModifiers: List<VoiceModifierConfig> = repository.defaultVoiceModifiers

    // Soundboard combined list (Defaults + Room Custom)
    val soundEffects: StateFlow<List<SoundEffectItem>> = repository.customSoundsFlow
        .combine(MutableStateFlow(repository.defaultSounds)) { custom, defaults ->
            defaults + custom
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.defaultSounds)

    // Reactions combined list (Defaults + Room Custom)
    val reactionEmojis: StateFlow<List<ReactionEmojiItem>> = repository.customReactionsFlow
        .combine(MutableStateFlow(repository.defaultReactions)) { custom, defaults ->
            defaults + custom
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.defaultReactions)

    // Friends list from repository
    val friends: StateFlow<List<com.example.model.FriendItem>> = repository.friendsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.defaultFriends)

    // Friend requests list
    val friendRequests: StateFlow<List<com.example.data.FriendRequestEntity>> = repository.friendRequestsFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf(
                com.example.data.FriendRequestEntity(
                    id = "req_1",
                    username = "GlitchKing",
                    tag = "#1024",
                    avatarColorHex = 0xFFEC4899,
                    mutualFriendsCount = 3
                ),
                com.example.data.FriendRequestEntity(
                    id = "req_2",
                    username = "ViperStrike",
                    tag = "#9911",
                    avatarColorHex = 0xFF00E5FF,
                    mutualFriendsCount = 1
                )
            )
        )

    val availableRegions: List<ServerRegion> = repository.availableRegions

    // User Profile state flow (Room persisted)
    val userProfile: StateFlow<com.example.model.UserProfile> = repository.userProfileFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            com.example.model.UserProfile()
        )

    // Engine flows
    val micLevel: StateFlow<Float> = audioEngine.currentMicLevel
    val waveformBars: StateFlow<FloatArray> = audioEngine.waveformBars

    private var simulationJob: Job? = null
    private var pingTickerJob: Job? = null

    init {
        // Start engine capture in background
        audioEngine.startCapture(enableLoopback = false)
        startSimulation()
        startNetworkLatencyTicker()
    }

    private fun startSimulation() {
        simulationJob = viewModelScope.launch {
            while (isActive) {
                delay(Random.nextLong(3000, 6500))

                // Random squad buddy speaks
                val currentMembers = _uiState.value.members
                if (currentMembers.isNotEmpty()) {
                    val speakerIndex = Random.nextInt(currentMembers.size)
                    val talkingDurationMs = Random.nextLong(1500, 3200)

                    _uiState.value = _uiState.value.copy(
                        members = currentMembers.mapIndexed { idx, member ->
                            if (idx == speakerIndex) member.copy(isSpeaking = true, micLevel = Random.nextFloat() * 0.7f + 0.3f)
                            else member.copy(isSpeaking = false, micLevel = 0f)
                        }
                    )

                    // Sometimes squad buddy triggers a quick reaction emoji or funny sound!
                    if (Random.nextInt(10) < 4) {
                        val randomReaction = repository.defaultReactions.random()
                        triggerReaction(
                            emoji = randomReaction.emoji,
                            punchline = randomReaction.punchline,
                            senderName = currentMembers[speakerIndex].name,
                            colorHex = randomReaction.colorHex
                        )
                    } else if (Random.nextInt(10) < 2) {
                        val randomSound = repository.defaultSounds.random()
                        SoundboardAudioEngine.playSound(randomSound.soundKey)
                        _uiState.value = _uiState.value.copy(
                            lastBroadcastSoundTitle = "${currentMembers[speakerIndex].name} played ${randomSound.title}!"
                        )
                    }

                    delay(talkingDurationMs)

                    // Stop speaking
                    _uiState.value = _uiState.value.copy(
                        members = _uiState.value.members.map { it.copy(isSpeaking = false, micLevel = 0f) }
                    )
                }
            }
        }
    }

    private fun startNetworkLatencyTicker() {
        pingTickerJob = viewModelScope.launch {
            while (isActive) {
                delay(1200)
                val base = _uiState.value.networkStats.selectedRegion.basePingMs
                val lowLat = _uiState.value.networkStats.lowLatencyModeEnabled
                val jitterRange = if (lowLat) 2 else 6
                val pingJitter = Random.nextInt(-jitterRange, jitterRange + 1)
                val currentPing = (base + pingJitter).coerceAtLeast(8)
                val packetLoss = if (lowLat) 0.0f else (Random.nextFloat() * 0.4f)

                _uiState.value = _uiState.value.copy(
                    networkStats = _uiState.value.networkStats.copy(
                        pingMs = currentPing,
                        jitterMs = (abs(pingJitter) + 1),
                        packetLossPercent = packetLoss,
                        bufferLatencyMs = if (lowLat) 14 else 36
                    )
                )
            }
        }
    }

    // --- Channel Operations ---
    fun selectChannel(channel: SquadChannel) {
        _uiState.value = _uiState.value.copy(currentChannel = channel)
    }

    fun createCustomChannel(name: String, gameCategory: String, icon: String, bitrate: Int) {
        val newChannel = SquadChannel(
            id = "c_${System.currentTimeMillis()}",
            name = name,
            gameCategory = gameCategory,
            iconEmoji = icon.ifBlank { "🎮" },
            activeUsers = 1,
            maxUsers = 8,
            bitrateKbps = bitrate
        )
        val updated = _uiState.value.channels + newChannel
        _uiState.value = _uiState.value.copy(
            channels = updated,
            currentChannel = newChannel
        )
    }

    // --- Voice Modifier Operations ---
    fun setVoiceModifier(type: ModifierType) {
        _uiState.value = _uiState.value.copy(activeModifier = type)
        audioEngine.activeModifier = type
        val config = repository.defaultVoiceModifiers.firstOrNull { it.type == type }
        if (config != null) {
            audioEngine.pitchMultiplier = config.pitchMultiplier
            audioEngine.distortionDrive = config.distortion
            _uiState.value = _uiState.value.copy(
                pitchFineTune = config.pitchMultiplier,
                distortionFineTune = config.distortion
            )
        }
    }

    fun updateFineTunePitch(multiplier: Float) {
        _uiState.value = _uiState.value.copy(pitchFineTune = multiplier)
        audioEngine.pitchMultiplier = multiplier
    }

    fun updateFineTuneDistortion(drive: Float) {
        _uiState.value = _uiState.value.copy(distortionFineTune = drive)
        audioEngine.distortionDrive = drive
    }

    fun updateFineTuneEcho(delayMs: Int) {
        _uiState.value = _uiState.value.copy(echoDelayMs = delayMs)
        audioEngine.echoDelayMs = delayMs
    }

    fun toggleLoopbackTesting() {
        val newState = !_uiState.value.isLoopbackTesting
        _uiState.value = _uiState.value.copy(isLoopbackTesting = newState)
        audioEngine.startCapture(enableLoopback = newState)
    }

    // --- Soundboard Operations ---
    fun playSoundEffect(sound: SoundEffectItem) {
        SoundboardAudioEngine.playSound(sound.soundKey)
        if (_uiState.value.isSoundboardBroadcastEnabled) {
            _uiState.value = _uiState.value.copy(
                lastBroadcastSoundTitle = "You broadcast ${sound.iconEmoji} ${sound.title} to squad!"
            )
            // Trigger haptic or reaction
            viewModelScope.launch {
                delay(2400)
                _uiState.value = _uiState.value.copy(lastBroadcastSoundTitle = null)
            }
        }
    }

    fun selectSoundCategory(category: SoundCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun toggleSoundboardBroadcast() {
        _uiState.value = _uiState.value.copy(
            isSoundboardBroadcastEnabled = !_uiState.value.isSoundboardBroadcastEnabled
        )
    }

    fun addCustomSound(title: String, icon: String, description: String, synthType: String, freq: Float, durationMs: Int) {
        viewModelScope.launch {
            val entity = CustomSoundEntity(
                id = "cs_${System.currentTimeMillis()}",
                title = title,
                iconEmoji = icon.ifBlank { "🎵" },
                description = description,
                synthType = synthType,
                baseFreqHz = freq,
                durationMs = durationMs
            )
            repository.addCustomSound(entity)
        }
    }

    // --- Reaction Operations ---
    fun triggerReaction(emoji: String, punchline: String, senderName: String = "You", colorHex: Long = 0xFF00E5FF) {
        SoundboardAudioEngine.playSound("POP")
        val newReaction = FloatingReaction(
            emoji = emoji,
            punchline = punchline,
            senderName = senderName,
            startX = Random.nextFloat() * 0.7f + 0.15f,
            colorHex = colorHex
        )

        val updatedList = (_uiState.value.floatingReactions + newReaction).takeLast(12)
        _uiState.value = _uiState.value.copy(floatingReactions = updatedList)

        // Clear after 3.8 seconds
        viewModelScope.launch {
            delay(3800)
            _uiState.value = _uiState.value.copy(
                floatingReactions = _uiState.value.floatingReactions.filter { it.id != newReaction.id }
            )
        }
    }

    fun addCustomReaction(emoji: String, punchline: String, colorHex: Long) {
        viewModelScope.launch {
            val entity = CustomReactionEntity(
                id = "cr_${System.currentTimeMillis()}",
                emoji = emoji,
                punchline = punchline,
                colorHex = colorHex
            )
            repository.addCustomReaction(entity)
        }
    }

    // --- Member Volume & Mute Operations ---
    fun toggleMemberMute(memberId: String) {
        _uiState.value = _uiState.value.copy(
            members = _uiState.value.members.map {
                if (it.id == memberId) it.copy(isMuted = !it.isMuted) else it
            }
        )
    }

    fun setMemberVolume(memberId: String, volume: Float) {
        _uiState.value = _uiState.value.copy(
            members = _uiState.value.members.map {
                if (it.id == memberId) it.copy(volume = volume) else it
            }
        )
    }

    fun toggleMyMic() {
        val newMute = !_uiState.value.isMicMuted
        _uiState.value = _uiState.value.copy(isMicMuted = newMute)
        if (newMute) {
            audioEngine.stopCapture()
        } else {
            audioEngine.startCapture(_uiState.value.isLoopbackTesting)
        }
    }

    fun toggleDeafen() {
        val newDeafen = !_uiState.value.isDeafened
        _uiState.value = _uiState.value.copy(
            isDeafened = newDeafen,
            isMicMuted = if (newDeafen) true else _uiState.value.isMicMuted
        )
    }

    fun setPttActive(isPressed: Boolean) {
        _uiState.value = _uiState.value.copy(isPttActive = isPressed)
    }

    fun togglePttMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isPttModeEnabled = enabled)
    }

    fun setVadThreshold(threshold: Int) {
        _uiState.value = _uiState.value.copy(vadThresholdPercent = threshold)
        audioEngine.noiseGateThreshold = threshold / 100f
    }

    // --- Network & Region Operations ---
    fun selectRegion(region: ServerRegion) {
        _uiState.value = _uiState.value.copy(
            networkStats = _uiState.value.networkStats.copy(
                selectedRegion = region,
                pingMs = region.basePingMs
            )
        )
    }

    fun toggleLowLatencyMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            networkStats = _uiState.value.networkStats.copy(
                lowLatencyModeEnabled = enabled,
                bufferLatencyMs = if (enabled) 14 else 36
            )
        )
    }

    fun setCodecBitrate(bitrateKbps: Int) {
        _uiState.value = _uiState.value.copy(
            networkStats = _uiState.value.networkStats.copy(currentBitrateKbps = bitrateKbps)
        )
    }

    fun setSoundboardMasterVolume(volume: Float) {
        SoundboardAudioEngine.masterVolume = volume
    }

    // --- Friends System Operations ---
    fun addFriend(usernameInput: String) {
        val trimmed = usernameInput.trim()
        if (trimmed.isBlank()) return

        val parts = trimmed.split("#")
        val username = parts[0].ifBlank { "Gamer" }
        val tag = if (parts.size > 1 && parts[1].isNotBlank()) "#${parts[1]}" else "#${(1000..9999).random()}"

        val avatarColors = listOf(0xFF00E5FF, 0xFF9D4EDD, 0xFF10B981, 0xFFF59E0B, 0xFFEF4444, 0xFFEC4899, 0xFF3B82F6)
        val selectedColor = avatarColors.random()

        viewModelScope.launch {
            repository.addFriend(
                username = username,
                tag = tag,
                status = com.example.model.FriendStatus.ONLINE,
                activity = "In SquadVoice Lobby",
                colorHex = selectedColor
            )
            SoundboardAudioEngine.playSound("POP")
            _uiState.value = _uiState.value.copy(
                lastBroadcastSoundTitle = "Added friend $username$tag! 🎉"
            )
            delay(2500)
            _uiState.value = _uiState.value.copy(lastBroadcastSoundTitle = null)
        }
    }

    fun removeFriend(id: String) {
        viewModelScope.launch {
            repository.removeFriend(id)
        }
    }

    fun toggleFriendFavorite(id: String, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, !isFav)
        }
    }

    fun acceptFriendRequest(request: com.example.data.FriendRequestEntity) {
        viewModelScope.launch {
            repository.deleteFriendRequest(request.id)
            repository.addFriend(
                username = request.username,
                tag = request.tag,
                status = com.example.model.FriendStatus.ONLINE,
                activity = "Just accepted friend invite",
                colorHex = request.avatarColorHex
            )
            SoundboardAudioEngine.playSound("VICTORY")
            _uiState.value = _uiState.value.copy(
                lastBroadcastSoundTitle = "You are now friends with ${request.username}${request.tag}!"
            )
            delay(2800)
            _uiState.value = _uiState.value.copy(lastBroadcastSoundTitle = null)
        }
    }

    fun declineFriendRequest(requestId: String) {
        viewModelScope.launch {
            repository.deleteFriendRequest(requestId)
        }
    }

    fun inviteFriendToVoice(friend: com.example.model.FriendItem) {
        val currentMembers = _uiState.value.members
        val alreadyInVoice = currentMembers.any { it.name.startsWith(friend.username) }

        if (!alreadyInVoice) {
            val newMember = SquadMember(
                id = "u_${friend.id}",
                name = friend.username,
                role = "Squad Friend",
                avatarColorHex = friend.avatarColorHex,
                pingMs = friend.pingMs,
                activeModifier = ModifierType.NORMAL,
                volume = 1.0f
            )

            _uiState.value = _uiState.value.copy(
                members = currentMembers + newMember,
                lastBroadcastSoundTitle = "🎮 ${friend.username} joined #${_uiState.value.currentChannel.name}!"
            )

            SoundboardAudioEngine.playSound("WHISTLE")
            triggerReaction("🎉", "JOINED SQUAD", friend.username, friend.avatarColorHex)

            viewModelScope.launch {
                delay(3000)
                _uiState.value = _uiState.value.copy(lastBroadcastSoundTitle = null)
            }
        } else {
            _uiState.value = _uiState.value.copy(
                lastBroadcastSoundTitle = "${friend.username} is already in voice channel!"
            )
        }
    }

    fun pokeFriendWithSound(friend: com.example.model.FriendItem, soundKey: String = "AIRHORN") {
        SoundboardAudioEngine.playSound(soundKey)
        triggerReaction("💥", "POKED YOU!", "You", 0xFF00E5FF)
        _uiState.value = _uiState.value.copy(
            lastBroadcastSoundTitle = "You poked ${friend.username} with $soundKey! 💥"
        )
        viewModelScope.launch {
            delay(2500)
            _uiState.value = _uiState.value.copy(lastBroadcastSoundTitle = null)
        }
    }

    fun connectGoogleAccount(email: String, displayName: String) {
        val current = userProfile.value
        val updated = current.copy(
            googleEmail = email,
            isGoogleConnected = true,
            googleDisplayName = displayName,
            googleConnectedDate = "Sep 26, 2026"
        )
        viewModelScope.launch {
            repository.updateUserProfile(updated)
            SoundboardAudioEngine.playSound("CHIME")
            triggerReaction("✨", "GOOGLE CONNECTED", "You", 0xFF4285F4)
            _uiState.value = _uiState.value.copy(
                lastBroadcastSoundTitle = "✓ Connected Google account: $email"
            )
            delay(3000)
            _uiState.value = _uiState.value.copy(lastBroadcastSoundTitle = null)
        }
    }

    fun disconnectGoogleAccount() {
        val current = userProfile.value
        val updated = current.copy(
            googleEmail = null,
            isGoogleConnected = false,
            googleDisplayName = null,
            googleConnectedDate = null
        )
        viewModelScope.launch {
            repository.updateUserProfile(updated)
            _uiState.value = _uiState.value.copy(
                lastBroadcastSoundTitle = "Google account disconnected"
            )
            delay(2500)
            _uiState.value = _uiState.value.copy(lastBroadcastSoundTitle = null)
        }
    }

    fun equipAnimatedAvatar(avatar: com.example.model.AnimatedAvatarType) {
        val current = userProfile.value
        val updated = current.copy(animatedAvatar = avatar)
        viewModelScope.launch {
            repository.updateUserProfile(updated)
            SoundboardAudioEngine.playSound("CHIME")
            triggerReaction("✨", "AVATAR EQUIPPED", "You", avatar.accentColorHex)
            _uiState.value = _uiState.value.copy(
                lastBroadcastSoundTitle = "Equipped ${avatar.title} animated avatar!"
            )
            delay(2500)
            _uiState.value = _uiState.value.copy(lastBroadcastSoundTitle = null)
        }
    }

    fun updateUserProfile(profile: com.example.model.UserProfile) {
        viewModelScope.launch {
            repository.updateUserProfile(profile)
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
        pingTickerJob?.cancel()
        audioEngine.stopCapture()
    }

    private fun abs(value: Int): Int = if (value < 0) -value else value
}
