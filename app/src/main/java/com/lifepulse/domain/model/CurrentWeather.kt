package com.lifepulse.domain.model

data class CurrentWeather(
    val tempC: Double,
    val apparentTempC: Double,
    val humidityPercent: Int,
    val windSpeedKmH: Double,
    val windDirectionDeg: Int,
    val precipitationMm: Double,
    val weatherCode: Int,
    val isDay: Boolean,
    val surfacePressureHpa: Double,
    val timeString: String
)
