package com.eltafouk.masareefi.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eltafouk.masareefi.ui.theme.CardBorder
import com.eltafouk.masareefi.ui.theme.DarkSurface
import com.eltafouk.masareefi.ui.theme.DarkSurfaceVariant
import com.eltafouk.masareefi.ui.theme.ExpenseRed
import com.eltafouk.masareefi.ui.theme.IncomeGreen
import com.eltafouk.masareefi.ui.theme.TextPrimary
import com.eltafouk.masareefi.ui.theme.TextSecondary

@Composable
fun DonutChartCard(
    totalIncome: Double,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    val total = totalIncome + totalExpense
    val incomePercent = if (total > 0) (totalIncome / total).toFloat() else 0.5f
    val expensePercent = if (total > 0) (totalExpense / total).toFloat() else 0.5f

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(totalIncome, totalExpense) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Circular Donut Chart (Expanded 100dp)
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (total <= 0.0) {
                        Canvas(modifier = Modifier.size(88.dp)) {
                            drawCircle(
                                color = DarkSurfaceVariant,
                                style = Stroke(width = 13.dp.toPx())
                            )
                        }
                        Text(
                            text = "فارغ",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextSecondary
                        )
                    } else {
                        Canvas(modifier = Modifier.size(88.dp)) {
                            val strokeWidth = 14.dp.toPx()
                            val incomeSweep = 360f * incomePercent * animProgress.value
                            val expenseSweep = 360f * expensePercent * animProgress.value

                            drawCircle(
                                color = DarkSurfaceVariant.copy(alpha = 0.5f),
                                style = Stroke(width = strokeWidth)
                            )

                            if (totalIncome > 0) {
                                drawArc(
                                    color = IncomeGreen,
                                    startAngle = -90f,
                                    sweepAngle = incomeSweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }

                            if (totalExpense > 0) {
                                val startAngle = -90f + incomeSweep
                                drawArc(
                                    color = ExpenseRed,
                                    startAngle = startAngle,
                                    sweepAngle = expenseSweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                        }

                        val net = totalIncome - totalExpense
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (net >= 0) "فائض" else "عجز",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                fontWeight = FontWeight.Bold,
                                color = if (net >= 0) IncomeGreen else ExpenseRed
                            )
                            Text(
                                text = ArabicCurrencyFormat.formatMoney(kotlin.math.abs(net)),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = TextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Legend indicators on the side
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Income Legend Row
                    CompactLegendRow(
                        color = IncomeGreen,
                        label = "الدخل",
                        amount = totalIncome,
                        percent = if (total > 0) (incomePercent * 100).toInt() else 0
                    )

                    // Expense Legend Row
                    CompactLegendRow(
                        color = ExpenseRed,
                        label = "المنصرف",
                        amount = totalExpense,
                        percent = if (total > 0) (expensePercent * 100).toInt() else 0
                    )

                    // Ratio Row for downward completeness
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "توازن الصرف",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            color = TextSecondary
                        )
                        val ratioText = if (total > 0) {
                            "المصروف ${(expensePercent * 100).toInt()}%"
                        } else "متوازن"
                        Text(
                            text = ratioText,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = if (expensePercent > 0.85f && total > 0) ExpenseRed else IncomeGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactLegendRow(
    color: Color,
    label: String,
    amount: Double,
    percent: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$label ($percent%)",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.5.sp),
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = ArabicCurrencyFormat.formatMoney(amount),
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.5.sp),
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
