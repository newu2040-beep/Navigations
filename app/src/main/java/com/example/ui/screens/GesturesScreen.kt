package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.database.GestureEntity
import com.example.database.GesturePoint
import com.example.ui.components.*
import com.example.ui.viewmodel.GestureViewModel

@Composable
fun GesturesScreen(viewModel: GestureViewModel) {
    val gestures by viewModel.listGestures.collectAsStateWithLifecycle()
    val sensitivity by viewModel.sensitivity.collectAsStateWithLifecycle()
    val vibeOn by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val soundOn by viewModel.soundEnabled.collectAsStateWithLifecycle()

    var showTrainingDialog by remember { mutableStateOf(false) }
    var activeTestingAreaExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
            item {
                FuturisticHeader(
                    title = "Gestures",
                    subtitle = "Manage Traces, Train New Codes & Calibrate Sensors"
                )
            }

            // Practice Area Toggle Panel
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .clickable { activeTestingAreaExpanded = !activeTestingAreaExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gesture, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Interactive Draw Overlay", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Toggle testing drawing board directly here", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                        }
                        Icon(
                            imageVector = if (activeTestingAreaExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    }

                    AnimatedVisibility(
                        visible = activeTestingAreaExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                            ) {
                                // Extract templates for custom matching
                                val templatesMap = gestures.associate { it.name to com.example.database.GestureConverters().stringToPoints(it.pointsJson).orEmpty() }
                                GestureDrawingCanvas(
                                    customTemplates = templatesMap,
                                    onGestureCompleted = { name, score, _ ->
                                        if (score >= 0.75f) {
                                            viewModel.triggerGestureRecognizedEvent(name)
                                        } else {
                                            viewModel.triggerVibrate(40)
                                            viewModel.triggerHUDNotification("Uncertain trace ($name: ${(score * 100).toInt()}% match)")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Calibrators
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "DETECTOR SENSITIVITY CALIBRATION",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    GlassCard {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Trace Sensitivity Tolerance", style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("${(sensitivity * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = sensitivity,
                            onValueChange = { viewModel.setSensitivity(it) },
                            modifier = Modifier.testTag("sensitivity_slider")
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Strict Match", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                            Text("Fluid Guesswork", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                        }

                        Divider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Gesture Vibration Sparks", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                Text("Haptic vibration ripples on match", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                            }
                            Switch(checked = vibeOn, onCheckedChange = { viewModel.setVibrationEnabled(it) })
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Synthesizer Sound Effects", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                Text("Trigger melodic prompt chirps on trigger", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                            }
                            Switch(checked = soundOn, onCheckedChange = { viewModel.setSoundEnabled(it) })
                        }
                    }
                }
            }

            // Saved Gestures Grid List
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REGISTERED TRACE MAPPINGS",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp
                    )

                    Button(
                        onClick = { showTrainingDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp).testTag("add_gesture_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("TRAIN", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (gestures.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Inbox, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(48.dp))
                        Text("No traces trained. Seed data or click train.", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f))
                    }
                }
            } else {
                items(gestures) { gesture ->
                    GestureListItem(
                        gesture = gesture,
                        onDelete = { viewModel.deleteGestureEntity(gesture) },
                        onRunSim = { viewModel.triggerGestureRecognizedEvent(gesture.name) }
                    )
                }
            }
        }

        // Training Wizard Dialog Overlay (Fills screen nicely with full visual polish)
        if (showTrainingDialog) {
            TrainingWizardDialog(
                onDismiss = { showTrainingDialog = false },
                onSave = { name, actionType, target, points, category ->
                    viewModel.saveNewGesture(name, actionType, target, points, category)
                    showTrainingDialog = false
                }
            )
        }
    }
}

@Composable
fun GestureListItem(
    gesture: GestureEntity,
    onDelete: () -> Unit,
    onRunSim: () -> Unit
) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Traced preview circular thumbnail
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = gesture.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Gesture \"${gesture.name}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${gesture.actionType}: ${gesture.actionTarget}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onRunSim,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .background(Color.Red.copy(alpha = 0.1f), CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TrainingWizardDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, actionType: String, target: String, points: List<GesturePoint>, category: String) -> Unit
) {
    var symbolName by remember { mutableStateOf("") }
    var actionType by remember { mutableStateOf("SYSTEM_ACTION") }
    var actionTarget by remember { mutableStateOf("Screenshot") }
    var category by remember { mutableStateOf("Utility") }
    var recordedPoints by remember { mutableStateOf<List<GesturePoint>>(emptyList()) }

    var stepNum by remember { mutableStateOf(1) } // 1: Info & Targets, 2: Drawing

    val typeOptions = listOf("SYSTEM_ACTION", "LAUNCH_APP")
    val actionOptions = if (actionType == "SYSTEM_ACTION") {
        listOf("Screenshot", "Toggle Flashlight", "Open Music", "Open Camera", "Super Clean Boost")
    } else {
        listOf("Open WhatsApp", "Open Spotify", "Open Settings", "Open Gallery")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp)),
        title = {
            Text(
                text = "TRAINING WIZARD (STEP $stepNum/2)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (stepNum == 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = symbolName,
                            onValueChange = { symbolName = it },
                            label = { Text("Unique Name/Letter (e.g. O, G)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("training_name_input")
                        )

                        Text("Select Action Binding", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        // Trigger action types
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            typeOptions.forEach { type ->
                                val isSelected = actionType == type
                                Button(
                                    onClick = { actionType = type; actionTarget = actionOptions[0] },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.08f)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = type.replace("_", " "), fontSize = 10.sp, maxLines = 1)
                                }
                            }
                        }

                        // Targets
                        Column {
                            Text("Binding Target:", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                            Box(modifier = Modifier.height(110.dp).background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp)).padding(4.dp)) {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    items(actionOptions) { target ->
                                        val isSelected = actionTarget == target
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                                                .clickable { actionTarget = target }
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(selected = isSelected, onClick = { actionTarget = target })
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(target, style = MaterialTheme.typography.bodySmall, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Drawing trace
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Draw \"$symbolName\" cleanly inside coordinates below:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        ) {
                            GestureDrawingCanvas(
                                onGestureCompleted = { _, _, rawPoints ->
                                    recordedPoints = rawPoints
                                }
                            )
                        }

                        Text(
                            text = if (recordedPoints.isEmpty()) "Brush head waiting..." else "${recordedPoints.size} anchors recorded",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (recordedPoints.isNotEmpty()) Color(0xFF00FFCC) else Color.Red.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Wizard actions
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (stepNum == 2) {
                            Button(
                                onClick = { stepNum = 1 },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))
                            ) {
                                Text("BACK")
                            }
                        }

                        Button(
                            onClick = {
                                if (stepNum == 1) {
                                    if (symbolName.trim().isNotEmpty()) {
                                        stepNum = 2
                                    }
                                } else {
                                    if (recordedPoints.isNotEmpty()) {
                                        onSave(symbolName, actionType, actionTarget, recordedPoints, category)
                                    }
                                }
                            },
                            enabled = if (stepNum == 1) symbolName.trim().isNotEmpty() else recordedPoints.isNotEmpty()
                        ) {
                            Text(if (stepNum == 1) "DRAW" else "SAVE")
                        }
                    }
                }
            }
        }
    )
}
