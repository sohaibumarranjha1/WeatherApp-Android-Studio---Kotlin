package com.lifepulse.domain.model

data class HourlyForecastItem(
    val timeString: String,
    val tempC: Double,
    val apparentTempC: Double,
    val weatherCode: Int,
    val precipitationProbPercent: Int,
    val precipitationMm: Double,
    val uvIndex: Double,
    val humidityPercent: Int,
    val windSpeedKmH: Double,
    val isDay: Boolean = true
)
