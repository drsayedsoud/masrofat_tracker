package com.eltafouk.masareefi.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eltafouk.masareefi.ai.local.ParsedTransactionResult
import com.eltafouk.masareefi.data.db.TransactionType
import com.eltafouk.masareefi.ui.theme.AiSparkle
import com.eltafouk.masareefi.ui.theme.CardBorder
import com.eltafouk.masareefi.ui.theme.DarkBackground
import com.eltafouk.masareefi.ui.theme.DarkSurface
import com.eltafouk.masareefi.ui.theme.DarkSurfaceVariant
import com.eltafouk.masareefi.ui.theme.ExpenseRed
import com.eltafouk.masareefi.ui.theme.ExpenseRedLight
import com.eltafouk.masareefi.ui.theme.IncomeGreen
import com.eltafouk.masareefi.ui.theme.IncomeGreenLight
import com.eltafouk.masareefi.ui.theme.TextPrimary
import com.eltafouk.masareefi.ui.theme.TextSecondary
import com.eltafouk.masareefi.voice.SpeechState

@Composable
fun VoiceInputDialog(
    speechState: SpeechState,
    parsedResult: ParsedTransactionResult?,
    isProcessingAI: Boolean,
    onStopListening: () -> Unit,
    onConfirmAdd: (ParsedTransactionResult) -> Unit,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "التسجيل الصوتي الذكي",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )

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

                Spacer(modifier = Modifier.height(24.dp))

                // Microphone animated circle
                val isListening = speechState is SpeechState.Listening
                val micColor = if (isListening) IncomeGreen else AiSparkle

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(if (isListening) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(micColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(micColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Rounded.Mic else Icons.Rounded.Mic,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Status text / recognized text
                when (speechState) {
                    is SpeechState.Listening -> {
                        Text(
                            text = if (speechState.partial.isNotBlank()) speechState.partial else "تحدث الآن... (مثال: صرفت 50 قهوة)",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (speechState.partial.isNotBlank()) TextPrimary else TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onStopListening,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "تم الكلام", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    is SpeechState.Final -> {
                        Text(
                            text = "« ${speechState.text} »",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    is SpeechState.Error -> {
                        Text(
                            text = speechState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ExpenseRedLight
                        )
                    }
                    SpeechState.Idle -> {
                        Text(
                            text = "جاهز للاستماع...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                if (isProcessingAI) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = AiSparkle,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "جاري التحليل واستخراج البيانات...",
                            style = MaterialTheme.typography.labelMedium,
                            color = AiSparkle
                        )
                    }
                }

                // Parsed Preview Card
                if (parsedResult != null && parsedResult.success) {
                    Spacer(modifier = Modifier.height(18.dp))

                    val isIncome = parsedResult.type == TransactionType.INCOME
                    val accent = if (isIncome) IncomeGreen else ExpenseRed

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(accent.copy(alpha = 0.2f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isIncome) "دخل (+)" else "مصروف (-)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = accent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = parsedResult.source,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = ArabicCurrencyFormat.formatMoney(parsedResult.amount),
                                style = MaterialTheme.typography.headlineMedium,
                                color = if (isIncome) IncomeGreenLight else ExpenseRedLight,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "التصنيف: ${parsedResult.category} • ${parsedResult.note}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "إلغاء", color = TextSecondary)
                        }

                        Button(
                            onClick = { onConfirmAdd(parsedResult) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accent,
                                contentColor = DarkBackground
                            )
                        ) {
                            Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "إضافة للسجل",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
