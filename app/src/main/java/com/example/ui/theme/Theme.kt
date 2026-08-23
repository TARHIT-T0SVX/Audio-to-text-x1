package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.preferences.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SoftBluePill,
    tertiary = ProgressBlue,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = TextPrimaryDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SoftBluePill,
    tertiary = ProgressBlue,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = PrimaryBlue,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    outline = LightBorder
)

@Composable
fun TranscribeTheme(
    themeMode: AppThemeMode = AppThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
