package com.lifepulse.domain.model

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

enum class WindSpeedUnit {
    KM_H,
    M_S,
    MPH
}

enum class PrecipitationUnit {
    MM,
    INCH
}

enum class ThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

data class UserPreferences(
    val tempUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KM_H,
    val precipitationUnit: PrecipitationUnit = PrecipitationUnit.MM,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val activeCityId: Long = 2950159L,
    val activeCityName: String = "Berlin",
    val activeCityLat: Double = 52.52,
    val activeCityLon: Double = 13.41,
    val activeCityCountry: String = "Germany"
)
