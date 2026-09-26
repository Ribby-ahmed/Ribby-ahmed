package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ModifierType
import com.example.model.SoundCategory
import com.example.model.SoundEffectItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SquadVoice", appName)
  }

  @Test
  fun `soundboard model item verification`() {
    val item = SoundEffectItem(
      id = "airhorn",
      title = "MLG Airhorn",
      category = SoundCategory.MEME,
      iconEmoji = "💥",
      description = "Triple horn",
      soundKey = "AIRHORN"
    )
    assertEquals("AIRHORN", item.soundKey)
    assertEquals(SoundCategory.MEME, item.category)
  }

  @Test
  fun `modifier types contain all core gaming effects`() {
    val modifiers = ModifierType.values()
    assertTrue(modifiers.contains(ModifierType.ROBOT))
    assertTrue(modifiers.contains(ModifierType.CHIPMUNK))
    assertTrue(modifiers.contains(ModifierType.DEMON))
    assertTrue(modifiers.contains(ModifierType.ALIEN_RADIO))
    assertTrue(modifiers.contains(ModifierType.STADIUM_ECHO))
  }

  @Test
  fun `friend item model verification`() {
    val friend = com.example.model.FriendItem(
      id = "f_test",
      username = "ShadowSniper",
      tag = "#1337",
      status = com.example.model.FriendStatus.IN_GAME,
      currentActivity = "Apex Legends Ranked",
      avatarColorHex = 0xFF00E5FF,
      isFavorite = true,
      pingMs = 22
    )
    assertEquals("ShadowSniper", friend.username)
    assertEquals("#1337", friend.tag)
    assertEquals(com.example.model.FriendStatus.IN_GAME, friend.status)
    assertTrue(friend.isFavorite)
  }

  @Test
  fun `animated avatar types contain free gaming animated options`() {
    val avatars = com.example.model.AnimatedAvatarType.values()
    assertTrue(avatars.size >= 8)
    assertTrue(avatars.contains(com.example.model.AnimatedAvatarType.CYBER_NEON))
    assertTrue(avatars.contains(com.example.model.AnimatedAvatarType.PIXEL_RETRO))
    assertTrue(avatars.contains(com.example.model.AnimatedAvatarType.FLAME_PHOENIX))
    assertTrue(avatars.contains(com.example.model.AnimatedAvatarType.ELECTRIC_STORM))
    assertTrue(avatars.contains(com.example.model.AnimatedAvatarType.GALAXY_COSMIC))
    assertTrue(avatars.contains(com.example.model.AnimatedAvatarType.SOUNDWAVE_DJ))
    assertTrue(avatars.contains(com.example.model.AnimatedAvatarType.KAWAII_NEKO))
  }

  @Test
  fun `user profile google connection verification`() {
    val profile = com.example.model.UserProfile(
      username = "ApexLegend",
      googleEmail = "ribbyahmed41@gmail.com",
      isGoogleConnected = true,
      googleDisplayName = "Ahmed R.",
      animatedAvatar = com.example.model.AnimatedAvatarType.CYBER_NEON
    )
    assertTrue(profile.isGoogleConnected)
    assertEquals("ribbyahmed41@gmail.com", profile.googleEmail)
    assertEquals(com.example.model.AnimatedAvatarType.CYBER_NEON, profile.animatedAvatar)
  }
}

