package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ReactionOverlay
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.MyProfileScreen
import com.example.ui.screens.NetworkSettingsScreen
import com.example.ui.screens.ReactionsScreen
import com.example.ui.screens.SoundboardScreen
import com.example.ui.screens.SquadRoomScreen
import com.example.ui.screens.VoiceModifierScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.SquadVoiceViewModel

enum class NavigationTab(val label: String, val icon: ImageVector) {
    SQUAD("Squad", Icons.Default.Group),
    FRIENDS("Friends", Icons.Default.People),
    VOICE_FX("Voice FX", Icons.Default.GraphicEq),
    SOUNDS("Sounds", Icons.Default.VolumeUp),
    REACTIONS("React", Icons.Default.Mood),
    PROFILE("Profile", Icons.Default.AccountCircle)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SquadVoiceApp()
            }
        }
    }
}

@Composable
fun SquadVoiceApp(
    viewModel: SquadVoiceViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val micLevel by viewModel.micLevel.collectAsStateWithLifecycle()
    val waveformBars by viewModel.waveformBars.collectAsStateWithLifecycle()
    val soundEffects by viewModel.soundEffects.collectAsStateWithLifecycle()
    val reactionEmojis by viewModel.reactionEmojis.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val friendRequests by viewModel.friendRequests.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(NavigationTab.SQUAD) }
    var showNetworkDialog by remember { mutableStateOf(false) }

    // Audio record permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.audioEngine.startCapture(enableLoopback = uiState.isLoopbackTesting)
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BackgroundDark,
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .border(1.dp, SurfaceBorder)
                        .testTag("main_bottom_nav"),
                    containerColor = SurfaceDark
                ) {
                    NavigationTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BackgroundDark,
                                selectedTextColor = CyberCyan,
                                indicatorColor = CyberCyan,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    NavigationTab.SQUAD -> {
                        SquadRoomScreen(
                            uiState = uiState,
                            myMicLevel = micLevel,
                            reactionEmojis = reactionEmojis,
                            userProfile = userProfile,
                            onToggleMic = { viewModel.toggleMyMic() },
                            onToggleDeafen = { viewModel.toggleDeafen() },
                            onPttPressChange = { isPressed -> viewModel.setPttActive(isPressed) },
                            onNavigateToModifiers = { currentTab = NavigationTab.VOICE_FX },
                            onNavigateToFriends = { currentTab = NavigationTab.FRIENDS },
                            onNavigateToProfile = { currentTab = NavigationTab.PROFILE },
                            onOpenNetwork = { showNetworkDialog = true },
                            onSelectChannel = { ch -> viewModel.selectChannel(ch) },
                            onCreateChannel = { name, game, icon, rate ->
                                viewModel.createCustomChannel(name, game, icon, rate)
                            },
                            onMemberVolumeChange = { id, vol -> viewModel.setMemberVolume(id, vol) },
                            onMemberToggleMute = { id -> viewModel.toggleMemberMute(id) },
                            onSendReaction = { em, punch, color ->
                                viewModel.triggerReaction(em, punch, "You", color)
                            }
                        )
                    }

                    NavigationTab.FRIENDS -> {
                        FriendsScreen(
                            friends = friends,
                            friendRequests = friendRequests,
                            currentChannelMembers = uiState.members,
                            userProfile = userProfile,
                            onAddFriend = { tag -> viewModel.addFriend(tag) },
                            onRemoveFriend = { id -> viewModel.removeFriend(id) },
                            onToggleFavorite = { id, isFav -> viewModel.toggleFriendFavorite(id, isFav) },
                            onInviteToVoice = { friend -> viewModel.inviteFriendToVoice(friend) },
                            onPokeWithSound = { friend -> viewModel.pokeFriendWithSound(friend, "AIRHORN") },
                            onAcceptRequest = { req -> viewModel.acceptFriendRequest(req) },
                            onDeclineRequest = { id -> viewModel.declineFriendRequest(id) },
                            onNavigateToProfile = { currentTab = NavigationTab.PROFILE }
                        )
                    }

                    NavigationTab.VOICE_FX -> {
                        VoiceModifierScreen(
                            modifiers = viewModel.voiceModifiers,
                            activeModifier = uiState.activeModifier,
                            isLoopbackActive = uiState.isLoopbackTesting,
                            micLevel = micLevel,
                            waveformBars = waveformBars,
                            pitchFineTune = uiState.pitchFineTune,
                            distortionFineTune = uiState.distortionFineTune,
                            echoDelayMs = uiState.echoDelayMs,
                            vadThreshold = uiState.vadThresholdPercent,
                            onSelectModifier = { mod -> viewModel.setVoiceModifier(mod) },
                            onToggleLoopback = { viewModel.toggleLoopbackTesting() },
                            onPitchChange = { mult -> viewModel.updateFineTunePitch(mult) },
                            onDistortionChange = { drive -> viewModel.updateFineTuneDistortion(drive) },
                            onEchoChange = { ms -> viewModel.updateFineTuneEcho(ms) },
                            onVadThresholdChange = { vad -> viewModel.setVadThreshold(vad) }
                        )
                    }

                    NavigationTab.SOUNDS -> {
                        SoundboardScreen(
                            sounds = soundEffects,
                            selectedCategory = uiState.selectedCategory,
                            isBroadcastEnabled = uiState.isSoundboardBroadcastEnabled,
                            onPlaySound = { sound -> viewModel.playSoundEffect(sound) },
                            onSelectCategory = { cat -> viewModel.selectSoundCategory(cat) },
                            onToggleBroadcast = { viewModel.toggleSoundboardBroadcast() },
                            onAddCustomSound = { title, icon, desc, synth, freq, dur ->
                                viewModel.addCustomSound(title, icon, desc, synth, freq, dur)
                            },
                            onVolumeChange = { vol -> viewModel.setSoundboardMasterVolume(vol) }
                        )
                    }

                    NavigationTab.REACTIONS -> {
                        ReactionsScreen(
                            reactions = reactionEmojis,
                            onSendReaction = { em, punch, color ->
                                viewModel.triggerReaction(em, punch, "You", color)
                            },
                            onAddCustomReaction = { em, punch, color ->
                                viewModel.addCustomReaction(em, punch, color)
                            }
                        )
                    }

                    NavigationTab.PROFILE -> {
                        MyProfileScreen(
                            userProfile = userProfile,
                            activeModifierName = viewModel.voiceModifiers.firstOrNull { it.type == uiState.activeModifier }?.name ?: "Clear Comms",
                            onUpdateProfile = { updated -> viewModel.updateUserProfile(updated) },
                            onConnectGoogle = { email, name -> viewModel.connectGoogleAccount(email, name) },
                            onDisconnectGoogle = { viewModel.disconnectGoogleAccount() },
                            onEquipAvatar = { avatar -> viewModel.equipAnimatedAvatar(avatar) }
                        )
                    }
                }
            }
        }

        // Network Settings & Telemetry Dialog
        if (showNetworkDialog) {
            Dialog(onDismissRequest = { showNetworkDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    NetworkSettingsScreen(
                        networkStats = uiState.networkStats,
                        availableRegions = viewModel.availableRegions,
                        isPttModeEnabled = uiState.isPttModeEnabled,
                        onToggleLowLatency = { en -> viewModel.toggleLowLatencyMode(en) },
                        onSelectRegion = { reg -> viewModel.selectRegion(reg) },
                        onSelectBitrate = { rate -> viewModel.setCodecBitrate(rate) },
                        onTogglePttMode = { en -> viewModel.togglePttMode(en) }
                    )
                }
            }
        }

        // Global Floating Reactions Overlay (rendered on top of all screens)
        ReactionOverlay(reactions = uiState.floatingReactions)
    }
}
