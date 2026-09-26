package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CustomSoundEntity::class,
        CustomReactionEntity::class,
        UserSettingsEntity::class,
        FriendEntity::class,
        FriendRequestEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class SquadDatabase : RoomDatabase() {
    abstract fun squadDao(): SquadDao

    companion object {
        @Volatile
        private var INSTANCE: SquadDatabase? = null

        fun getDatabase(context: Context): SquadDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SquadDatabase::class.java,
                    "squad_voice_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
