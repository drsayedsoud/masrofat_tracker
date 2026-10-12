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
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.CallMade
import androidx.compose.material.icons.rounded.CallReceived
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eltafouk.masareefi.data.db.TransactionEntity
import com.eltafouk.masareefi.data.db.TransactionType
import com.eltafouk.masareefi.ui.theme.CardBorder
import com.eltafouk.masareefi.ui.theme.DarkSurface
import com.eltafouk.masareefi.ui.theme.DarkSurfaceVariant
import com.eltafouk.masareefi.ui.theme.ExpenseRed
import com.eltafouk.masareefi.ui.theme.ExpenseRedLight
import com.eltafouk.masareefi.ui.theme.IncomeGreen
import com.eltafouk.masareefi.ui.theme.IncomeGreenLight
import com.eltafouk.masareefi.ui.theme.TextPrimary
import com.eltafouk.masareefi.ui.theme.TextSecondary

@Composable
fun TransactionTabsSection(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    incomeList: List<TransactionEntity>,
    expenseList: List<TransactionEntity>,
    onDeleteTransaction: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Two Distinctly Colored Tabs: سجل المصاريف (يمين) وسجل الدخل (يسار)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = DarkSurface,
            contentColor = TextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = if (selectedTabIndex == 0) ExpenseRed else IncomeGreen,
                    height = 2.5.dp
                )
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CardBorder)
                )
            }
        ) {
            // Tab 0: المصاريف (على اليمين في واجهة العربي RTL)
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedTabIndex == 0) ExpenseRed.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CallMade,
                            contentDescription = null,
                            tint = if (selectedTabIndex == 0) ExpenseRed else TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "سجل المصاريف",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 9.sp),
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 0) ExpenseRedLight else TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (selectedTabIndex == 0) ExpenseRed.copy(alpha = 0.25f)
                                    else DarkSurfaceVariant
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${expenseList.size}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                color = if (selectedTabIndex == 0) ExpenseRedLight else TextSecondary
                            )
                        }
                    }
                }
            )

            // Tab 1: الدخل (على اليسار)
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedTabIndex == 1) IncomeGreen.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CallReceived,
                            contentDescription = null,
                            tint = if (selectedTabIndex == 1) IncomeGreen else TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "سجل الدخل",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 9.sp),
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 1) IncomeGreenLight else TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (selectedTabIndex == 1) IncomeGreen.copy(alpha = 0.25f)
                                    else DarkSurfaceVariant
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${incomeList.size}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                color = if (selectedTabIndex == 1) IncomeGreenLight else TextSecondary
                            )
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tab Content: Tab 0 = Expenses, Tab 1 = Income
        val currentList = if (selectedTabIndex == 0) expenseList else incomeList
        val isIncomeTab = selectedTabIndex == 1

        if (currentList.isEmpty()) {
            EmptyListPlaceholder(isIncome = isIncomeTab)
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                currentList.forEach { item ->
                    TransactionItemCard(
                        transaction = item,
                        onDeleteClick = { transactionToDelete = item }
                    )
                }
            }
        }
    }

    // Confirmation Dialog upon delete
    if (transactionToDelete != null) {
        val target = transactionToDelete!!
        val isIncome = target.type == TransactionType.INCOME

        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = ExpenseRed,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "تأكيد حذف المعاملة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "هل أنت متأكد من حذف ${if (isIncome) "دخل" else "مصروف"} «${target.note.ifBlank { target.category }}» بمبلغ ${ArabicCurrencyFormat.formatMoney(target.amount)}؟",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTransaction(target.id)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ExpenseRedLight)
                ) {
                    Text(text = "حذف نهائي", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { transactionToDelete = null }
                ) {
                    Text(text = "إلغاء", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun TransactionItemCard(
    transaction: TransactionEntity,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val accentColor = if (isIncome) IncomeGreen else ExpenseRed
    val icon = categoryIcon(transaction.category, isIncome)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Compact Icon + Description
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = transaction.note.ifBlank { transaction.category },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.5.sp),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = transaction.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = accentColor
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = TextSecondary
                        )
                        Text(
                            text = ArabicCurrencyFormat.formatTime(transaction.timestamp),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = TextSecondary
                        )
                    }
                }
            }

            // Right: Amount + Delete Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (isIncome) "+" else "-"} ${ArabicCurrencyFormat.formatMoney(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 11.5.sp),
                    color = if (isIncome) IncomeGreenLight else ExpenseRedLight,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(2.dp))

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "حذف",
                        tint = ExpenseRedLight.copy(alpha = 0.85f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyListPlaceholder(isIncome: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface.copy(alpha = 0.6f))
            .border(1.dp, CardBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncome) Icons.Rounded.Payments else Icons.AutoMirrored.Rounded.ReceiptLong,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isIncome) "لا توجد حركات دخل مسجلة بعد" else "لا توجد مصاريف مسجلة بعد",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isIncome) "جرب: ضيفلي دخل 5000 مرتب" else "جرب: صرفت 50 قهوة",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = TextSecondary
            )
        }
    }
}

private fun categoryIcon(category: String, isIncome: Boolean): ImageVector {
    if (isIncome) {
        return when {
            category.contains("راتب") || category.contains("مرتب") || category.contains("عمل") -> Icons.Rounded.Payments
            category.contains("جمعيات") || category.contains("التزام") -> Icons.Rounded.Savings
            else -> Icons.Rounded.Payments
        }
    }

    return when {
        category.contains("طعام") || category.contains("شراب") || category.contains("قهوة") -> Icons.Rounded.LocalCafe
        category.contains("مواصلات") || category.contains("بنزين") || category.contains("اوبر") -> Icons.Rounded.DirectionsCar
        category.contains("فواتير") || category.contains("سكن") || category.contains("كهربا") -> Icons.AutoMirrored.Rounded.ReceiptLong
        category.contains("صحة") || category.contains("علاج") || category.contains("دكتور") -> Icons.Rounded.MedicalServices
        category.contains("تسوق") || category.contains("ملابس") || category.contains("شراء") -> Icons.Rounded.ShoppingBag
        category.contains("ترفيه") || category.contains("رياضة") -> Icons.Rounded.SportsEsports
        else -> Icons.Rounded.CallMade
    }
}
