package com.eltafouk.masareefi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.CallReceived
import androidx.compose.material.icons.rounded.CallMade
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eltafouk.masareefi.ui.theme.CardBorder
import com.eltafouk.masareefi.ui.theme.DarkSurface
import com.eltafouk.masareefi.ui.theme.ExpenseRed
import com.eltafouk.masareefi.ui.theme.ExpenseRedLight
import com.eltafouk.masareefi.ui.theme.IncomeGreen
import com.eltafouk.masareefi.ui.theme.IncomeGreenLight
import com.eltafouk.masareefi.ui.theme.TextPrimary
import com.eltafouk.masareefi.ui.theme.TextSecondary

@Composable
fun FinancialCardsSection(
    totalIncome: Double,
    totalExpense: Double,
    incomeCount: Int,
    expenseCount: Int,
    modifier: Modifier = Modifier
) {
    val remaining = totalIncome - totalExpense
    val isPositive = remaining >= 0

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Compact Hero Card: كارت المتبقي (الرصيد الصافي)
        RemainingBalanceCard(
            remaining = remaining,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            isPositive = isPositive
        )

        // 2. Row of 2 Small Compact Cards: كرتين متساويين للدخل والمنصرف
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IncomeSmallCard(
                amount = totalIncome,
                count = incomeCount,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            ExpenseSmallCard(
                amount = totalExpense,
                count = expenseCount,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun RemainingBalanceCard(
    remaining: Double,
    totalIncome: Double,
    totalExpense: Double,
    isPositive: Boolean
) {
    val gradientBrush = if (isPositive) {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF0F3E36),
                Color(0xFF134E4A),
                Color(0xFF1E293B)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF4C0519),
                Color(0xFF881337),
                Color(0xFF1E293B)
            )
        )
    }

    val statusPillColor = if (isPositive) IncomeGreen else ExpenseRed
    val statusPillText = when {
        totalIncome == 0.0 && totalExpense == 0.0 -> "لا توجد معاملات"
        remaining > 0 -> "فائض مالي ✨"
        remaining == 0.0 -> "متعادلة ⚖️"
        else -> "عجز مالي ⚠️"
    }

    val expenseRatio = if (totalIncome > 0) {
        (totalExpense / totalIncome).toFloat().coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Right Side (Start in RTL): عنوان الكارت + أسفله جملة صرفت كذا من الدخل + شريط التقدم
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Header of the card: Icon + "الرصيد المتبقي" + Status Pill
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Payments,
                                contentDescription = null,
                                tint = if (isPositive) IncomeGreenLight else ExpenseRedLight,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "الرصيد المتبقي",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.5.sp),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusPillColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = statusPillText,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = statusPillColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // أسفل اسم الكارت: جملة صرفت كذا من الدخل
                    if (totalIncome > 0) {
                        Text(
                            text = "صرفت ${(expenseRatio * 100).toInt()}% من الدخل",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { expenseRatio },
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (expenseRatio > 0.85f) ExpenseRed else IncomeGreen,
                            trackColor = Color.White.copy(alpha = 0.15f)
                        )
                    } else {
                        Text(
                            text = "لا توجد مصاريف مسجلة",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Left Side (End in RTL / شمال الكارت): الرقم يأخذ كامل الارتفاع
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ArabicCurrencyFormat.formatMoney(remaining),
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
                        color = if (isPositive) Color.White else ExpenseRedLight,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun IncomeSmallCard(
    amount: Double,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, IncomeGreen.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(IncomeGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CallReceived,
                            contentDescription = null,
                            tint = IncomeGreen,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الدخل",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(IncomeGreen.copy(alpha = 0.12f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = IncomeGreenLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ArabicCurrencyFormat.formatMoney(amount),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.5.sp),
                color = IncomeGreenLight,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ExpenseSmallCard(
    amount: Double,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, ExpenseRed.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ExpenseRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CallMade,
                            contentDescription = null,
                            tint = ExpenseRed,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "المنصرف",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ExpenseRed.copy(alpha = 0.12f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = ExpenseRedLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ArabicCurrencyFormat.formatMoney(amount),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.5.sp),
                color = ExpenseRedLight,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
