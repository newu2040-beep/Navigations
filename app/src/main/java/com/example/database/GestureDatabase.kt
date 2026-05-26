package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [GestureEntity::class, AutomationRuleEntity::class, GestureStatsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(GestureConverters::class)
abstract class GestureDatabase : RoomDatabase() {
    abstract fun gestureDao(): GestureDao

    companion object {
        @Volatile
        private var INSTANCE: GestureDatabase? = null

        fun getDatabase(context: Context): GestureDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GestureDatabase::class.java,
                    "navigations_gesture_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
