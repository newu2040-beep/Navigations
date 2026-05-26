package com.example.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GestureDao {
    // Gestures
    @Query("SELECT * FROM custom_gestures ORDER BY name ASC")
    fun getAllGestures(): Flow<List<GestureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGesture(gesture: GestureEntity): Long

    @Delete
    suspend fun deleteGesture(gesture: GestureEntity)

    @Query("DELETE FROM custom_gestures WHERE id = :id")
    suspend fun deleteGestureById(id: Int)

    // Automation Rules
    @Query("SELECT * FROM automation_rules ORDER BY name ASC")
    fun getAllAutomationRules(): Flow<List<AutomationRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomationRule(rule: AutomationRuleEntity): Long

    @Delete
    suspend fun deleteAutomationRule(rule: AutomationRuleEntity)

    // Stats
    @Query("SELECT * FROM gesture_stats ORDER BY timestamp DESC")
    fun getAllStats(): Flow<List<GestureStatsEntity>>

    @Query("SELECT MIN(id) as id, identifier, category, SUM(count) as count, MAX(timestamp) as timestamp FROM gesture_stats GROUP BY identifier, category ORDER BY count DESC")
    fun getAggregatedStats(): Flow<List<GestureStatsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStat(stat: GestureStatsEntity): Long

    @Query("UPDATE gesture_stats SET count = count + 1, timestamp = :timestamp WHERE identifier = :identifier AND category = :category")
    suspend fun incrementStat(identifier: String, category: String, timestamp: Long): Int

    @Transaction
    suspend fun incrementOrInsertStat(identifier: String, category: String) {
        val now = System.currentTimeMillis()
        val rowsUpdated = incrementStat(identifier, category, now)
        if (rowsUpdated == 0) {
            insertStat(GestureStatsEntity(identifier = identifier, category = category, count = 1, timestamp = now))
        }
    }
}
