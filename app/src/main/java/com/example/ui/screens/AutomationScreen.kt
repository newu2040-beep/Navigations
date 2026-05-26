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
import com.example.database.AutomationRuleEntity
import com.example.database.AutomationStep
import com.example.ui.components.*
import com.example.ui.viewmodel.GestureViewModel

@Composable
fun AutomationScreen(viewModel: GestureViewModel) {
    val rules by viewModel.listRules.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                FuturisticHeader(
                    title = "Automation",
                    subtitle = "Chain Multiple Systems Sequence Actions via Sensor Events"
                )
            }

            // Description card
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    GlassCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Chained Action Sequences", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Automate consecutive beeps, vibrations, HUD logs, delays, and flashlight cycles in series.", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Sub-header + add action
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CUSTOM MACROS RULES STATE",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp
                    )

                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp).testTag("add_rule_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("NEW ENGINE", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (rules.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.SettingsApplications, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(48.dp))
                        Text("No customized triggers created yet. Build macro rules.", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f))
                    }
                }
            } else {
                items(rules) { rule ->
                    RuleListItem(
                        rule = rule,
                        onDelete = { viewModel.deleteAutomationRuleEntity(rule) },
                        onExecuteSim = {
                            val listSteps = com.example.database.GestureConverters().stringToPoints(rule.stepsJson) // Wait steps string parsing
                            // Handle custom rule sim running
                            if (rule.triggerType == "BACK_TAP_DOUBLE") {
                                viewModel.triggerBackTapSimulated(true)
                            } else if (rule.triggerType == "BACK_TAP_TRIPLE") {
                                viewModel.triggerBackTapSimulated(false)
                            } else {
                                viewModel.triggerShakeSimulated()
                            }
                        }
                    )
                }
            }
        }

        if (showCreateDialog) {
            CreateChainDialog(
                onDismiss = { showCreateDialog = false },
                onSave = { name, triggerType, source, steps ->
                    viewModel.saveAutomationRule(name, triggerType, source, steps)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun RuleListItem(
    rule: AutomationRuleEntity,
    onDelete: () -> Unit,
    onExecuteSim: () -> Unit
) {
    val steps = com.example.database.GestureConverters().stringToSteps(rule.stepsJson) ?: emptyList()

    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = rule.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SensorWindow, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Triggered via ${rule.triggerSource}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onExecuteSim,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Simulate", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
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

                // Step dots summary
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    steps.sortedBy { it.order }.forEachIndexed { index, step ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${step.order}. ${step.actionType}",
                                fontSize = 8.sp,
                                color = Color.White
                            )
                        }

                        if (index < steps.size - 1) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateChainDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, triggerType: String, source: String, steps: List<AutomationStep>) -> Unit
) {
    var ruleName by remember { mutableStateOf("") }
    var triggerType by remember { mutableStateOf("BACK_TAP_DOUBLE") }

    // Dynamic steps builder list
    var stepsList = remember { mutableStateListOf<AutomationStep>() }

    // Active step form inputs
    var activeActionType by remember { mutableStateOf("VIBRATE") }
    var activeParameter by remember { mutableStateOf("") }

    val triggerOptions = listOf(
        "BACK_TAP_DOUBLE" to "Double Tap Back",
        "BACK_TAP_TRIPLE" to "Triple Tap Back",
        "SHAKE_PHONE" to "Phone Shake"
    )

    val stepActionOptions = listOf("VIBRATE", "PLAY_SOUND", "SHOW_NOTIFICATION", "TOGGLE_FLASHLIGHT", "DELAY")

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
                text = "MACRO SEQUENCE BUILDER",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = ruleName,
                    onValueChange = { ruleName = it },
                    label = { Text("Chain Core Name (e.g., Chill Toggle)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("rule_name_input")
                )

                Column {
                    Text("Sensor Trigger Event Binding", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                    Box(modifier = Modifier.height(76.dp).background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp)).padding(4.dp)) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            items(triggerOptions) { (type, label) ->
                                val isSelected = triggerType == type
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { triggerType = type }
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = isSelected, onClick = { triggerType = type })
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(label, style = MaterialTheme.typography.bodySmall, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Steps table panel
                Column {
                    Text("Automation Steps List (${stepsList.size})", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.15f))
                            .padding(8.dp)
                    ) {
                        if (stepsList.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No sequence steps set yet.", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.3f))
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(stepsList) { step ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${step.order}. Action [${step.actionType}] param: \"${step.parameter}\"", fontSize = 10.sp, color = Color.White)
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color.Red,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { stepsList.remove(step) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Add steps inputs mini board
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Add Mini Step to Sequence:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Options Box
                        Box(modifier = Modifier.weight(1.2f).height(65.dp).background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(2.dp)) {
                            LazyColumn {
                                items(stepActionOptions) { action ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (activeActionType == action) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                                            .clickable { activeActionType = action }
                                            .padding(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(action, fontSize = 8.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        // Parameter Box
                        OutlinedTextField(
                            value = activeParameter,
                            onValueChange = { activeParameter = it },
                            label = { Text("Param (e.g. 500, Hello)", fontSize = 8.sp) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 10.sp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(58.dp)
                        )

                        // Plus button
                        IconButton(
                            onClick = {
                                stepsList.add(
                                    AutomationStep(
                                        order = stepsList.size + 1,
                                        actionType = activeActionType,
                                        parameter = activeParameter
                                    )
                                )
                                activeParameter = ""
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Step", tint = Color.White)
                        }
                    }
                }

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }

                    Button(
                        onClick = {
                            if (ruleName.trim().isNotEmpty() && stepsList.isNotEmpty()) {
                                val sourceLabel = triggerOptions.find { it.first == triggerType }?.second ?: "Sensor"
                                onSave(ruleName, triggerType, sourceLabel, stepsList.toList())
                            }
                        },
                        enabled = ruleName.trim().isNotEmpty() && stepsList.isNotEmpty()
                    ) {
                        Text("SAVE CHAIN")
                    }
                }
            }
        }
    )
}
