package com.eltafouk.masareefi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColors = darkColorScheme(
    primary = IncomeGreen,
    onPrimary = DarkBackground,
    primaryContainer = IncomeGreenContainer,
    onPrimaryContainer = IncomeGreenLight,

    secondary = BalanceBlue,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextPrimary,

    tertiary = ExpenseRed,
    onTertiary = DarkBackground,
    tertiaryContainer = ExpenseRedContainer,
    onTertiaryContainer = ExpenseRedLight,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary
)

@Composable
fun MasareefiTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = DarkColors,
            typography = MasareefiTypography,
            content = content
        )
    }
}
