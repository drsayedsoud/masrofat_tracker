package com.eltafouk.masareefi.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.eltafouk.masareefi.ui.components.DonutChartCard
import com.eltafouk.masareefi.ui.components.FinancialCardsSection
import com.eltafouk.masareefi.ui.components.GeminiKeyDialog
import com.eltafouk.masareefi.ui.components.ManualAddDialog
import com.eltafouk.masareefi.ui.components.ResetDataDialog
import com.eltafouk.masareefi.ui.components.TransactionTabsSection
import com.eltafouk.masareefi.ui.components.VoiceInputDialog
import com.eltafouk.masareefi.ui.theme.AiGold
import com.eltafouk.masareefi.ui.theme.AiSparkle
import com.eltafouk.masareefi.ui.theme.CardBorder
import com.eltafouk.masareefi.ui.theme.DarkBackground
import com.eltafouk.masareefi.ui.theme.DarkSurface
import com.eltafouk.masareefi.ui.theme.DarkSurfaceVariant
import com.eltafouk.masareefi.ui.theme.ExpenseRed
import com.eltafouk.masareefi.ui.theme.ExpenseRedLight
import com.eltafouk.masareefi.ui.theme.IncomeGreen
import com.eltafouk.masareefi.ui.theme.TextPrimary
import com.eltafouk.masareefi.ui.theme.TextSecondary
import com.eltafouk.masareefi.voice.SpeechState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: MasareefiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val incomeList by viewModel.incomeTransactions.collectAsState()
    val expenseList by viewModel.expenseTransactions.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val forceOffline by viewModel.forceOffline.collectAsState()
    val speechState by viewModel.speechState.collectAsState()
    val parsedSpeechResult by viewModel.parsedSpeechResult.collectAsState()
    val isProcessingAI by viewModel.isProcessingAI.collectAsState()
    val snackBarMessage by viewModel.snackBarMessage.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var quickText by remember { mutableStateOf("") }
    var showGeminiDialog by remember { mutableStateOf(false) }
    var showManualAddDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Permission launcher for microphone
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showVoiceDialog = true
            viewModel.startListening()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("يلزم إذن الميكروفون لاستخدام الإدخال الصوتي")
            }
        }
    }

    LaunchedEffect(snackBarMessage) {
        snackBarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackBar()
        }
    }

    LaunchedEffect(speechState, isProcessingAI) {
        if (speechState is SpeechState.Idle && showVoiceDialog && !isProcessingAI) {
            showVoiceDialog = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarSection(
                hasApiKey = apiKey.isNotBlank() && !forceOffline,
                isOnline = viewModel.isOnline(),
                onOpenSettings = { showGeminiDialog = true },
                onOpenResetDialog = { showResetDialog = true },
                onSettingsTapHint = {
                    scope.launch {
                        snackbarHostState.showSnackbar("اضغط مطولاً لفتح الإعدادات ⚙️")
                    }
                }
            )
        },
        bottomBar = {
            BottomInputBar(
                text = quickText,
                onTextChange = { quickText = it },
                onSend = {
                    if (quickText.isNotBlank()) {
                        viewModel.processQuickTextInput(quickText.trim())
                        quickText = ""
                        focusManager.clearFocus()
                    }
                },
                onStartVoice = {
                    val hasPerm = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPerm) {
                        showVoiceDialog = true
                        viewModel.startListening()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onManualAdd = { showManualAddDialog = true },
                isProcessing = isProcessingAI
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Three Financial Cards: المتبقي + دخل + منصرف
            item {
                FinancialCardsSection(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    incomeCount = incomeList.size,
                    expenseCount = expenseList.size
                )
            }

            // 2. Donut / Pie Chart: رسم بياني دائري للدخل والمنصرف
            item {
                DonutChartCard(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense
                )
            }

            // 3. Two Tabs with history lists: تبويبين دخل ومصاريف بسجل لكل منهم
            item {
                TransactionTabsSection(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it },
                    incomeList = incomeList,
                    expenseList = expenseList,
                    onDeleteTransaction = { viewModel.deleteTransaction(it) }
                )
            }

            // Extra padding at the bottom for smooth scroll
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    // Gemini Settings Dialog
    if (showGeminiDialog) {
        GeminiKeyDialog(
            currentApiKey = apiKey,
            currentModel = selectedModel,
            forceOffline = forceOffline,
            isOnline = viewModel.isOnline(),
            onSaveApiKey = { viewModel.saveApiKey(it) },
            onSaveModel = { viewModel.saveModel(it) },
            onSaveForceOffline = { viewModel.saveForceOffline(it) },
            onTestKey = { viewModel.testGeminiKey(it) },
            onDismiss = { showGeminiDialog = false }
        )
    }

    // Manual Add Dialog
    if (showManualAddDialog) {
        ManualAddDialog(
            onAdd = { type, amount, category, note ->
                viewModel.addTransaction(type, amount, category, note)
            },
            onDismiss = { showManualAddDialog = false }
        )
    }

    // Voice Input Dialog
    if (showVoiceDialog) {
        VoiceInputDialog(
            speechState = speechState,
            parsedResult = parsedSpeechResult,
            isProcessingAI = isProcessingAI,
            onStopListening = { viewModel.stopListening() },
            onConfirmAdd = { res ->
                viewModel.addTransaction(res.type, res.amount, res.category, res.note)
                showVoiceDialog = false
            },
            onDismiss = {
                viewModel.clearSpeechDialog()
                showVoiceDialog = false
            }
        )
    }

    // Reset Data Dialog (مسح كل بيانات الدخل والمصروفات بباسوورد 1153)
    if (showResetDialog) {
        ResetDataDialog(
            onConfirmReset = { viewModel.resetAllData() },
            onDismiss = { showResetDialog = false }
        )
    }
}

@Composable
private fun TopAppBarSection(
    hasApiKey: Boolean,
    isOnline: Boolean,
    onOpenSettings: () -> Unit,
    onOpenResetDialog: () -> Unit,
    onSettingsTapHint: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color(0xFF131D31),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Logo & Title (رزمة أموال Payments)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(IncomeGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Payments,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "مصاريفي",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 15.sp),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "متتبع المصاريف الذكي",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = TextSecondary
                    )
                }
            }

            // Action Buttons: Reset Inputs Button (Icon only) + Settings Button (Gear icon only with Long Press)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Reset Data Button (أيقونة تصفير مدخلات الدخل والمصروفات فقط)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .border(1.dp, ExpenseRed.copy(alpha = 0.35f), CircleShape)
                        .clickable(onClick = onOpenResetDialog),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.RestartAlt,
                        contentDescription = "إعادة ضبط ومسح المدخلات",
                        tint = ExpenseRedLight,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Settings Button (أيقونة ترس فقط — تفتح بالضغط المطول)
                val gearBg = if (hasApiKey && isOnline) AiSparkle.copy(alpha = 0.18f) else DarkSurfaceVariant
                val gearColor = if (hasApiKey && isOnline) AiSparkle else AiGold

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(gearBg)
                        .border(1.dp, gearColor.copy(alpha = 0.4f), CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onSettingsTapHint() },
                                onLongPress = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onOpenSettings()
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "الإعدادات (اضغط مطولاً)",
                        tint = gearColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onStartVoice: () -> Unit,
    onManualAdd: () -> Unit,
    isProcessing: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = DarkSurface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Manual Add (+) Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                        .clickable(onClick = onManualAdd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "إضافة يدوية",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text Field Input
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "اكتب: ضيفلي دخل 3000 أو صرفت 50 قهوة...",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = IncomeGreen,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send or Mic Button
                if (text.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(IncomeGreen)
                            .clickable(onClick = onSend),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = DarkBackground,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Send,
                                contentDescription = "إرسال",
                                tint = DarkBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    // Voice Mic Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AiSparkle)
                            .clickable(onClick = onStartVoice),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Mic,
                            contentDescription = "تحدث بالصوت",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
