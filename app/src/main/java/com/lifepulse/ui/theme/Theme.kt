package com.lifepulse.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.lifepulse.domain.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = BrushedBrassAccent,
    secondary = MutedSage,
    tertiary = RestrainedAmber,
    background = InkBackgroundDark,
    surface = StoneSurfaceDark,
    surfaceVariant = StoneSurfaceVariantDark,
    onPrimary = InkBackgroundDark,
    onSecondary = InkBackgroundDark,
    onBackground = WarmIvoryPrimary,
    onSurface = WarmIvoryPrimary,
    onSurfaceVariant = SubtleStoneSecondary,
    outline = SubtleBorderDark,
    error = MutedCrimson
)

private val LightColorScheme = lightColorScheme(
    primary = WarmBrassAccentLight,
    secondary = MutedSageLight,
    tertiary = RestrainedAmberLight,
    background = WarmIvoryBackgroundLight,
    surface = StoneSurfaceLight,
    surfaceVariant = StoneSurfaceVariantLight,
    onPrimary = WarmIvoryBackgroundLight,
    onSecondary = WarmIvoryBackgroundLight,
    onBackground = DeepInkPrimary,
    onSurface = DeepInkPrimary,
    onSurfaceVariant = MutedCharcoalSecondary,
    outline = SubtleBorderLight,
    error = MutedCrimsonLight
)

@Composable
fun LifePulseTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    palette: WeatherThemePalette? = null,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        if (palette != null && darkTheme) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                palette.upperGlow,
                                palette.midTransition,
                                palette.lowerBackground
                            )
                        )
                    )
            ) {
                content()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background)
            ) {
                content()
            }
        }
    }
}
