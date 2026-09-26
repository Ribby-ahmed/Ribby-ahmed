package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SquadDao {
    @Query("SELECT * FROM custom_sounds ORDER BY timestamp DESC")
    fun getAllCustomSounds(): Flow<List<CustomSoundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomSound(sound: CustomSoundEntity)

    @Query("DELETE FROM custom_sounds WHERE id = :id")
    suspend fun deleteCustomSound(id: String)

    @Query("SELECT * FROM custom_reactions ORDER BY timestamp DESC")
    fun getAllCustomReactions(): Flow<List<CustomReactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomReaction(reaction: CustomReactionEntity)

    @Query("DELETE FROM custom_reactions WHERE id = :id")
    suspend fun deleteCustomReaction(id: String)

    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettings(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSettings(settings: UserSettingsEntity)

    @Query("SELECT * FROM friends ORDER BY isFavorite DESC, timestamp DESC")
    fun getAllFriends(): Flow<List<FriendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity)

    @Query("DELETE FROM friends WHERE id = :id")
    suspend fun deleteFriend(id: String)

    @Query("UPDATE friends SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("SELECT * FROM friend_requests ORDER BY timestamp DESC")
    fun getAllFriendRequests(): Flow<List<FriendRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRequest(request: FriendRequestEntity)

    @Query("DELETE FROM friend_requests WHERE id = :id")
    suspend fun deleteFriendRequest(id: String)
}
