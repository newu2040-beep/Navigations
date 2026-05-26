package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "custom_gestures")
data class GestureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val actionType: String, // "LAUNCH_APP", "SYSTEM_ACTION", "SHORTCUT", "AUTOMATION"
    val actionTarget: String, // e.g., "Music", "Camera", "Screenshot", "Flashlight"
    val pointsJson: String, // JSON representation of List<GesturePoint>
    val category: String, // "Navigation", "Media", "Utility", "Gaming", "Accessibility"
    val isSystem: Boolean = false,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true
)

@Entity(tableName = "automation_rules")
data class AutomationRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val triggerType: String, // "GESTURE", "BACK_TAP_DOUBLE", "BACK_TAP_TRIPLE", "SHAKE_PHONE"
    val triggerSource: String, // e.g. "M", "Double Tap"
    val stepsJson: String, // JSON array of AutomationStep items
    val isEnabled: Boolean = true,
    val isGamingModeOnly: Boolean = false,
    val isSleepModeOnly: Boolean = false
)

@Entity(tableName = "gesture_stats")
data class GestureStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val identifier: String, // Name of gesture or tap action
    val category: String, // e.g. "Back Tap", "Drawing", "Shake"
    val count: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class GesturePoint(val x: Float, val y: Float)

data class AutomationStep(
    val order: Int,
    val actionType: String, // "VIBRATE", "SHOW_NOTIFICATION", "TOGGLE_FLASHLIGHT", "PLAY_SOUND", "LAUNCH_APP", "DELAY"
    val parameter: String = "" // parameter value (e.g., duration or app name)
)

class GestureConverters {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun stringToPoints(value: String): List<GesturePoint>? {
        val listType = Types.newParameterizedType(List::class.java, GesturePoint::class.java)
        val adapter = moshi.adapter<List<GesturePoint>>(listType)
        return try {
            adapter.fromJson(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun pointsToString(list: List<GesturePoint>): String {
        val listType = Types.newParameterizedType(List::class.java, GesturePoint::class.java)
        val adapter = moshi.adapter<List<GesturePoint>>(listType)
        return adapter.toJson(list)
    }

    @TypeConverter
    fun stringToSteps(value: String): List<AutomationStep>? {
        val listType = Types.newParameterizedType(List::class.java, AutomationStep::class.java)
        val adapter = moshi.adapter<List<AutomationStep>>(listType)
        return try {
            adapter.fromJson(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun stepsToString(list: List<AutomationStep>): String {
        val listType = Types.newParameterizedType(List::class.java, AutomationStep::class.java)
        val adapter = moshi.adapter<List<AutomationStep>>(listType)
        return adapter.toJson(list)
    }
}
