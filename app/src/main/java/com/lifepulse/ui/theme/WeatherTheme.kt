package com.lifepulse.ui.theme

import androidx.compose.ui.graphics.Color

data class WeatherThemePalette(
    val upperGlow: Color,
    val midTransition: Color,
    val lowerBackground: Color,
    val accent: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color
)

object WeatherThemes {
    val sunny = WeatherThemePalette(
        upperGlow = Color(0xFF6F4A25),
        midTransition = Color(0xFF30251F),
        lowerBackground = Color(0xFF111315),
        accent = Color(0xFFD7B77D),
        surface = Color(0xFF1C2229),
        surfaceVariant = Color(0xFF222A32),
        border = Color(0xFF3B4651)
    )

    val rainy = WeatherThemePalette(
        upperGlow = Color(0xFF294A62),
        midTransition = Color(0xFF1B2934),
        lowerBackground = Color(0xFF101418),
        accent = Color(0xFF9BBED5),
        surface = Color(0xFF1A2229),
        surfaceVariant = Color(0xFF202A32),
        border = Color(0xFF354859)
    )

    val cloudy = WeatherThemePalette(
        upperGlow = Color(0xFF48545C),
        midTransition = Color(0xFF252D32),
        lowerBackground = Color(0xFF111416),
        accent = Color(0xFFB9C3C9),
        surface = Color(0xFF1C2226),
        surfaceVariant = Color(0xFF232B30),
        border = Color(0xFF3A454D)
    )

    val windy = WeatherThemePalette(
        upperGlow = Color(0xFF235B5C),
        midTransition = Color(0xFF192F33),
        lowerBackground = Color(0xFF101516),
        accent = Color(0xFFA0D1C7),
        surface = Color(0xFF192224),
        surfaceVariant = Color(0xFF1F2B2E),
        border = Color(0xFF30484A)
    )

    val snowy = WeatherThemePalette(
        upperGlow = Color(0xFF45647B),
        midTransition = Color(0xFF25313C),
        lowerBackground = Color(0xFF101419),
        accent = Color(0xFFC3D8E5),
        surface = Color(0xFF192229),
        surfaceVariant = Color(0xFF202B34),
        border = Color(0xFF364D5E)
    )

    val thunderstorm = WeatherThemePalette(
        upperGlow = Color(0xFF403354),
        midTransition = Color(0xFF211E2A),
        lowerBackground = Color(0xFF100F15),
        accent = Color(0xFFB6A6DB),
        surface = Color(0xFF1B1822),
        surfaceVariant = Color(0xFF23202C),
        border = Color(0xFF423B54)
    )

    val night = WeatherThemePalette(
        upperGlow = Color(0xFF292F59),
        midTransition = Color(0xFF181A31),
        lowerBackground = Color(0xFF0B0D13),
        accent = Color(0xFFB9C3E5),
        surface = Color(0xFF161824),
        surfaceVariant = Color(0xFF1E2133),
        border = Color(0xFF323759)
    )

    fun getPalette(code: Int, isDay: Boolean): WeatherThemePalette {
        if (!isDay && code !in listOf(95, 96, 99)) return night
        return when (code) {
            0, 1 -> sunny
            2, 3 -> cloudy
            45, 48 -> cloudy
            51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> rainy
            71, 73, 75, 77, 85, 86 -> snowy
            95, 96, 99 -> thunderstorm
            else -> if (isDay) sunny else night
        }
    }
}
