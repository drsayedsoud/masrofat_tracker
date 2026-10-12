package com.eltafouk.masareefi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eltafouk.masareefi.ui.theme.AiGold
import com.eltafouk.masareefi.ui.theme.AiSparkle
import com.eltafouk.masareefi.ui.theme.CardBorder
import com.eltafouk.masareefi.ui.theme.DarkBackground
import com.eltafouk.masareefi.ui.theme.DarkSurface
import com.eltafouk.masareefi.ui.theme.DarkSurfaceElevated
import com.eltafouk.masareefi.ui.theme.DarkSurfaceVariant
import com.eltafouk.masareefi.ui.theme.ExpenseRed
import com.eltafouk.masareefi.ui.theme.IncomeGreen
import com.eltafouk.masareefi.ui.theme.IncomeGreenLight
import com.eltafouk.masareefi.ui.theme.TextPrimary
import com.eltafouk.masareefi.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun GeminiKeyDialog(
    currentApiKey: String,
    currentModel: String,
    forceOffline: Boolean,
    isOnline: Boolean,
    onSaveApiKey: (String) -> Unit,
    onSaveModel: (String) -> Unit,
    onSaveForceOffline: (Boolean) -> Unit,
    onTestKey: suspend (String) -> Result<Boolean>,
    onDismiss: () -> Unit
) {
    var keyInput by remember { mutableStateOf(currentApiKey) }
    var selectedModel by remember { mutableStateOf(currentModel) }
    var isTesting by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var testSuccess by remember { mutableStateOf(false) }
    var offlineSetting by remember { mutableStateOf(forceOffline) }

    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AiSparkle.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = AiSparkle,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "إعدادات الذكاء الاصطناعي",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "إغلاق",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Online/Offline status badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isOnline) IncomeGreen.copy(alpha = 0.12f)
                            else ExpenseRed.copy(alpha = 0.12f)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isOnline) Icons.Rounded.Wifi else Icons.Rounded.WifiOff,
                            contentDescription = null,
                            tint = if (isOnline) IncomeGreenLight else ExpenseRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOnline) "الإنترنت متصل — جاهز للذكاء الاصطناعي" else "الإنترنت غير متوفر — يعمل التحليل المحلي الأوفلاين",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOnline) IncomeGreenLight else ExpenseRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Gemini API Key Input
                Text(
                    text = "مفتاح Google Gemini API:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = {
                        keyInput = it
                        testResult = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "الصق مفتاح API الخاص بك هنا...", color = TextSecondary)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Key,
                            contentDescription = null,
                            tint = AiGold,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = AiSparkle,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Test Key Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                isTesting = true
                                testResult = null
                                val res = onTestKey(keyInput)
                                isTesting = false
                                if (res.isSuccess) {
                                    testSuccess = true
                                    testResult = "تم التحقق من المفتاح بنجاح! جاهز للاستخدام"
                                } else {
                                    testSuccess = false
                                    testResult = res.exceptionOrNull()?.message ?: "فشل التحقق من المفتاح"
                                }
                            }
                        },
                        enabled = keyInput.isNotBlank() && !isTesting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceVariant,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = AiSparkle,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(text = "فحص المفتاح 🔍", style = MaterialTheme.typography.labelSmall)
                    }

                    if (keyInput.isNotBlank()) {
                        TextButton(
                            onClick = {
                                keyInput = ""
                                onSaveApiKey("")
                                testResult = null
                            }
                        ) {
                            Text(text = "مسح المفتاح", color = ExpenseRed, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                // Test result banner
                if (testResult != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (testSuccess) IncomeGreen.copy(alpha = 0.15f)
                                else ExpenseRed.copy(alpha = 0.15f)
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (testSuccess) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
                            contentDescription = null,
                            tint = if (testSuccess) IncomeGreen else ExpenseRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = testResult.orEmpty(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (testSuccess) IncomeGreenLight else ExpenseRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Offline Mode Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "وضع عدم الاتصال الدائم (أوفلاين)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "الاعتماد دائماً على القواعد المحلية بدون نت",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = offlineSetting,
                        onCheckedChange = {
                            offlineSetting = it
                            onSaveForceOffline(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = IncomeGreen,
                            checkedTrackColor = IncomeGreen.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "💡 إذا لم يتوفر مفتاح أو انقطع النت، سيعمل التطبيق بكفاءة ويفهم الكلمات المصرية (مثل: صرفت، دخل، قهوة، بنزين، مرتب) محلياً بدون إنترنت.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save & Close Button
                Button(
                    onClick = {
                        onSaveApiKey(keyInput)
                        onSaveModel(selectedModel)
                        onSaveForceOffline(offlineSetting)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IncomeGreen,
                        contentColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "حفظ وإغلاق",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
