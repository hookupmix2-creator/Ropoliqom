package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ui.components.ProvidePersianRtlLayout

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = NavyDarkest,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = PurpleCard,
    onSecondaryContainer = Color.White,
    tertiary = EmeraldProfit,
    onTertiary = Color.White,
    background = NavyDarkest,
    onBackground = TextPrimary,
    surface = NavyDark,
    onSurface = TextPrimary,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    error = CrimsonLoss,
    onError = Color.White
)

private val LuxuryLightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = GoldLight,
    onPrimaryContainer = NavyDarkest,
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = PurpleDark,
    tertiary = EmeraldProfit,
    onTertiary = Color.White,
    background = Color(0xFFF1F5F9),
    onBackground = NavyDarkest,
    surface = Color.White,
    onSurface = NavyDarkest,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = CrimsonLoss,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LuxuryDarkColorScheme else LuxuryLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        ProvidePersianRtlLayout {
            content()
        }
    }
}

