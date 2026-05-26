package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.*
import com.example.gesture.GestureRecognizer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GestureViewModel(application: Application) : AndroidViewModel(application) {
    private val database = GestureDatabase.getDatabase(application)
    private val repository = GestureRepository(database.gestureDao())

    // UI Reactiveness
    val listGestures: StateFlow<List<GestureEntity>> = repository.allGestures
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listRules: StateFlow<List<AutomationRuleEntity>> = repository.allRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aggregatedStats: StateFlow<List<GestureStatsEntity>> = repository.aggregatedStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Settings States
    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled = _vibrationEnabled.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled = _soundEnabled.asStateFlow()

    private val _sensitivity = MutableStateFlow(0.75f)
    val sensitivity = _sensitivity.asStateFlow()

    private val _selectedTheme = MutableStateFlow("NEON_PURPLE")
    val selectedTheme = _selectedTheme.asStateFlow()

    // Persistent Onboarding Setup Status (Immersive User Intent)
    private val sharedPrefs = application.getSharedPreferences("navigations_prefs", Context.MODE_PRIVATE)
    private val _onboardingCompleted = MutableStateFlow(sharedPrefs.getBoolean("onboarding_completed", false))
    val onboardingCompleted = _onboardingCompleted.asStateFlow()

    fun completeOnboarding() {
        sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
        _onboardingCompleted.value = true
        triggerVibrate(150)
        triggerHUDNotification("Navigations Engine Fully Activated")
    }

    fun resetOnboarding() {
        sharedPrefs.edit().putBoolean("onboarding_completed", false).apply()
        _onboardingCompleted.value = false
        triggerHUDNotification("Cockpit setup reset successfully")
    }

    private val _isAmoled = MutableStateFlow(false)
    val isAmoled = _isAmoled.asStateFlow()

    private val _isBackgroundServiceRunning = MutableStateFlow(true)
    val isBackgroundServiceRunning = _isBackgroundServiceRunning.asStateFlow()

    private val _isOverlaySystemActive = MutableStateFlow(true)
    val isOverlaySystemActive = _isOverlaySystemActive.asStateFlow()

    private val _isGamingMode = MutableStateFlow(false)
    val isGamingMode = _isGamingMode.asStateFlow()

    private val _isSleepMode = MutableStateFlow(false)
    val isSleepMode = _isSleepMode.asStateFlow()

    // Simulator Interaction State Variables
    private val _flashlightActive = MutableStateFlow(false)
    val flashlightActive = _flashlightActive.asStateFlow()

    private val _musicPlaying = MutableStateFlow(false)
    val musicPlaying = _musicPlaying.asStateFlow()

    private val _musicTrack = MutableStateFlow("Cosmic Overdrive - SynthRider")
    val musicTrack = _musicTrack.asStateFlow()

    private val _screenshotFlashed = MutableStateFlow(false)
    val screenshotFlashed = _screenshotFlashed.asStateFlow()

    private val _activeCameraLensSimulator = MutableStateFlow(false)
    val activeCameraLensSimulator = _activeCameraLensSimulator.asStateFlow()

    private val _hudNotification = MutableStateFlow<String?>(null)
    val hudNotification = _hudNotification.asStateFlow()

    private val _activeSimulatingActionList = MutableStateFlow<List<String>>(emptyList())
    val activeSimulatingActionList = _activeSimulatingActionList.asStateFlow()

    init {
        // Seed database if empty
        viewModelScope.launch {
            repository.allGestures.first().let { current ->
                if (current.isEmpty()) {
                    seedDefaultDBData()
                }
            }
        }
    }

    private suspend fun seedDefaultDBData() {
        // Seed default drawing gestures
        val initialGestures = listOf(
            GestureEntity(
                name = "M",
                actionType = "SYSTEM_ACTION",
                actionTarget = "Open Music",
                pointsJson = pointsToString(GestureRecognizer.DEFAULT_TEMPLATES["M"] ?: emptyList()),
                category = "Media",
                isSystem = true
            ),
            GestureEntity(
                name = "C",
                actionType = "SYSTEM_ACTION",
                actionTarget = "Open Camera",
                pointsJson = pointsToString(GestureRecognizer.DEFAULT_TEMPLATES["C"] ?: emptyList()),
                category = "Navigation",
                isSystem = true
            ),
            GestureEntity(
                name = "W",
                actionType = "LAUNCH_APP",
                actionTarget = "Open WhatsApp",
                pointsJson = pointsToString(GestureRecognizer.DEFAULT_TEMPLATES["W"] ?: emptyList()),
                category = "Communication",
                isSystem = true
            ),
            GestureEntity(
                name = "S",
                actionType = "SYSTEM_ACTION",
                actionTarget = "Screenshot",
                pointsJson = pointsToString(GestureRecognizer.DEFAULT_TEMPLATES["S"] ?: emptyList()),
                category = "Utility",
                isSystem = true
            ),
            GestureEntity(
                name = "V",
                actionType = "SYSTEM_ACTION",
                actionTarget = "Toggle Flashlight",
                pointsJson = pointsToString(GestureRecognizer.DEFAULT_TEMPLATES["V"] ?: emptyList()),
                category = "Utility",
                isSystem = true
            ),
            GestureEntity(
                name = "Circle",
                actionType = "AUTOMATION",
                actionTarget = "Super Clean Boost",
                pointsJson = pointsToString(GestureRecognizer.DEFAULT_TEMPLATES["Circle"] ?: emptyList()),
                category = "Automation",
                isSystem = true
            )
        )

        initialGestures.forEach { repository.insertGesture(it) }

        // Seed default automation rules
        val initialRules = listOf(
            AutomationRuleEntity(
                name = "Super Clean Boost",
                triggerType = "GESTURE",
                triggerSource = "Circle",
                stepsJson = stepsToString(
                    listOf(
                        AutomationStep(1, "VIBRATE", "80"),
                        AutomationStep(2, "PLAY_SOUND", "1"),
                        AutomationStep(3, "SHOW_NOTIFICATION", "System memory cleaned (+4.2GB RAM optimized)"),
                        AutomationStep(4, "DELAY", "1000"),
                        AutomationStep(5, "SHOW_NOTIFICATION", "Gaming engines ready at 120 FPS")
                    )
                )
            ),
            AutomationRuleEntity(
                name = "Double Tap Back Settings",
                triggerType = "BACK_TAP_DOUBLE",
                triggerSource = "Double Tap",
                stepsJson = stepsToString(
                    listOf(
                        AutomationStep(1, "VIBRATE", "50"),
                        AutomationStep(2, "TOGGLE_FLASHLIGHT", ""),
                        AutomationStep(3, "SHOW_NOTIFICATION", "Flashlight Toggled via Back Tap")
                    )
                )
            ),
            AutomationRuleEntity(
                name = "Gravity Shake Assistant",
                triggerType = "SHAKE_PHONE",
                triggerSource = "Phone Shake",
                stepsJson = stepsToString(
                    listOf(
                        AutomationStep(1, "VIBRATE", "150"),
                        AutomationStep(2, "PLAY_SOUND", "3"),
                        AutomationStep(3, "SHOW_NOTIFICATION", "Assistant HUD Overlay Activated")
                    )
                )
            )
        )

        initialRules.forEach { repository.insertRule(it) }

        // Seed some historic stats to make analytics graph look spectacular
        val pastActions = listOf(
            Triple("M", "Drawing", 34),
            Triple("C", "Drawing", 19),
            Triple("S", "Drawing", 52),
            Triple("Double Tap", "Back Tap", 87),
            Triple("Triple Tap", "Back Tap", 21),
            Triple("Phone Shake", "Motion Tap", 45),
            Triple("Circle", "Drawing", 12)
        )

        pastActions.forEach {
            for (i in 1..it.third) {
                repository.incrementStat(it.first, it.second)
            }
        }
    }

    private fun pointsToString(points: List<GesturePoint>): String {
        return GestureConverters().pointsToString(points)
    }

    private fun stepsToString(steps: List<AutomationStep>): String {
        return GestureConverters().stepsToString(steps)
    }

    // Toggle Settings
    fun setVibrationEnabled(enabled: Boolean) { _vibrationEnabled.value = enabled }
    fun setSoundEnabled(enabled: Boolean) { _soundEnabled.value = enabled }
    fun setSensitivity(value: Float) { _sensitivity.value = value }
    fun setSelectedTheme(theme: String) { _selectedTheme.value = theme }
    fun setAmoledActive(active: Boolean) { _isAmoled.value = active }
    fun toggleBackgroundService() { _isBackgroundServiceRunning.value = !_isBackgroundServiceRunning.value }
    fun toggleOverlaySystem() { _isOverlaySystemActive.value = !_isOverlaySystemActive.value }
    fun toggleGamingMode() { _isGamingMode.value = !_isGamingMode.value }
    fun toggleSleepMode() { _isSleepMode.value = !_isSleepMode.value }

    // Floating Lens capture close
    fun clearCameraLens() { _activeCameraLensSimulator.value = false }
    fun clearScreenshotFlash() { _screenshotFlashed.value = false }

    // Database CRUD Operations
    fun saveNewGesture(name: String, actionType: String, actionTarget: String, points: List<GesturePoint>, category: String) {
        viewModelScope.launch {
            val systemConv = GestureConverters()
            val pointsStr = systemConv.pointsToString(points)
            repository.insertGesture(
                GestureEntity(
                    name = name,
                    actionType = actionType,
                    actionTarget = actionTarget,
                    pointsJson = pointsStr,
                    category = category
                )
            )
            triggerHUDNotification("New Gesture [$name] Saved successfully")
            playBeep(2)
        }
    }

    fun deleteGestureEntity(it: GestureEntity) {
        viewModelScope.launch {
            repository.deleteGesture(it)
            triggerHUDNotification("Gesture [${it.name}] Deleted")
        }
    }

    fun saveAutomationRule(name: String, triggerType: String, triggerSource: String, steps: List<AutomationStep>) {
        viewModelScope.launch {
            repository.insertRule(
                AutomationRuleEntity(
                    name = name,
                    triggerType = triggerType,
                    triggerSource = triggerSource,
                    stepsJson = stepsToString(steps)
                )
            )
            triggerHUDNotification("Automation Rule [$name] Live Now")
        }
    }

    fun deleteAutomationRuleEntity(it: AutomationRuleEntity) {
        viewModelScope.launch {
            repository.deleteRule(it)
            triggerHUDNotification("Automation [${it.name}] Deleted")
        }
    }

    // Interactive Trigger Pipeline (Central HUD Controller)
    fun triggerGestureRecognizedEvent(gestureName: String) {
        viewModelScope.launch {
            repository.incrementStat(gestureName, "Drawing")

            // Retrieve matching gesture definition
            val matchedGesture = listGestures.value.find { it.name.equals(gestureName, ignoreCase = true) }
            val actionTarget = matchedGesture?.actionTarget ?: when (gestureName) {
                "M" -> "Open Music"
                "C" -> "Open Camera"
                "W" -> "Open WhatsApp"
                "S" -> "Screenshot"
                "V" -> "Toggle Flashlight"
                else -> "Launch Shortcut"
            }

            executeSystemActionSim(actionTarget, gestureName, matchedGesture?.actionType ?: "SYSTEM_ACTION")
        }
    }

    fun triggerBackTapSimulated(isDouble: Boolean) {
        viewModelScope.launch {
            val tapLabel = if (isDouble) "Double Tap" else "Triple Tap"
            val category = "Back Tap"
            repository.incrementStat(tapLabel, category)

            // Look up bound action
            val targetRule = listRules.value.find {
                it.triggerType == (if (isDouble) "BACK_TAP_DOUBLE" else "BACK_TAP_TRIPLE")
            }

            if (targetRule != null) {
                val steps = GestureConverters().stringToSteps(targetRule.stepsJson) ?: emptyList()
                executeAutomationChain(steps, targetRule.name)
            } else {
                // Fallback direct simulator
                val fallbackAction = if (isDouble) "Toggle Flashlight" else "Play/Pause Music"
                executeSystemActionSim(fallbackAction, tapLabel, "SYSTEM_ACTION")
            }
        }
    }

    fun triggerShakeSimulated() {
        viewModelScope.launch {
            val targetLabel = "Phone Shake"
            repository.incrementStat(targetLabel, "Motion Tap")

            val targetRule = listRules.value.find { it.triggerType == "SHAKE_PHONE" }
            if (targetRule != null) {
                val steps = GestureConverters().stringToSteps(targetRule.stepsJson) ?: emptyList()
                executeAutomationChain(steps, targetRule.name)
            } else {
                executeSystemActionSim("Screenshot", targetLabel, "SYSTEM_ACTION")
            }
        }
    }

    private suspend fun executeAutomationChain(steps: List<AutomationStep>, ruleName: String) {
        val ongoingSteps = mutableListOf<String>()
        _activeSimulatingActionList.value = ongoingSteps

        triggerVibrate(150)
        triggerHUDNotification("Executing Rule: $ruleName")

        for (step in steps.sortedBy { it.order }) {
            ongoingSteps.add("Step ${step.order}: ${step.actionType} ${step.parameter}")
            _activeSimulatingActionList.value = ongoingSteps.toList()

            when (step.actionType) {
                "VIBRATE" -> {
                    val duration = step.parameter.toLongOrNull() ?: 100L
                    triggerVibrate(duration)
                }
                "PLAY_SOUND" -> {
                    val code = step.parameter.toIntOrNull() ?: 1
                    playBeep(code)
                }
                "SHOW_NOTIFICATION" -> {
                    triggerHUDNotification(step.parameter)
                }
                "TOGGLE_FLASHLIGHT" -> {
                    _flashlightActive.value = !_flashlightActive.value
                }
                "LAUNCH_APP" -> {
                    executeSystemActionSim("Open App: ${step.parameter}", "Automation", "LAUNCH_APP")
                }
                "DELAY" -> {
                    val time = step.parameter.toLongOrNull() ?: 500L
                    delay(time)
                }
            }
            delay(400)
        }

        delay(1500)
        _activeSimulatingActionList.value = emptyList()
    }

    private suspend fun executeSystemActionSim(actionName: String, triggerSource: String, actionType: String) {
        triggerVibrate(80)
        playBeep(1)

        when (actionName) {
            "Open Music" -> {
                _musicPlaying.value = !_musicPlaying.value
                triggerHUDNotification(if (_musicPlaying.value) "Cosmic Player: Playing Track" else "Cosmic Player: Paused")
            }
            "Open Camera" -> {
                _activeCameraLensSimulator.value = true
                triggerHUDNotification("Augmented Lens activated via $triggerSource")
            }
            "Screenshot" -> {
                _screenshotFlashed.value = true
                playBeep(3)
                triggerHUDNotification("NeoGlass Frame captured to Gallery successfully")
            }
            "Toggle Flashlight" -> {
                _flashlightActive.value = !_flashlightActive.value
                triggerHUDNotification(if (_flashlightActive.value) "Tactical Beaming: ACTIVATED" else "Tactical Beaming: STANDBY")
            }
            "Super Clean Boost" -> {
                triggerHUDNotification("Cyber Memory Purger executing...")
                delay(800)
                triggerHUDNotification("RAM fully swept. Optimized gaming threads ready.")
            }
            else -> {
                triggerHUDNotification("Navigating: Launching [$actionName] via $triggerSource")
            }
        }
    }

    // Vibration Feedback Engine
    fun triggerVibrate(milliseconds: Long) {
        if (!_vibrationEnabled.value) return
        val context = getApplication<Application>().applicationContext
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(milliseconds)
            }
        } catch (e: Exception) {
            // Silenced on non-supporting devices Or emulator
        }
    }

    // Audio Feedback Beeps
    private fun playBeep(toneCode: Int) {
        if (!_soundEnabled.value) return
        try {
            val type = when (toneCode) {
                1 -> ToneGenerator.TONE_PROP_BEEP
                2 -> ToneGenerator.TONE_CDMA_PIP
                3 -> ToneGenerator.TONE_PROP_PROMPT
                else -> ToneGenerator.TONE_SUP_PIP
            }
            val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60)
            tg.startTone(type, 180)
            tg.release()
        } catch (e: Exception) {
            // Silenced if sound cannot be initialized
        }
    }

    fun triggerHUDNotification(text: String?) {
        _hudNotification.value = text
        if (text != null) {
            viewModelScope.launch {
                delay(3000)
                if (_hudNotification.value == text) {
                    _hudNotification.value = null
                }
            }
        }
    }
}
