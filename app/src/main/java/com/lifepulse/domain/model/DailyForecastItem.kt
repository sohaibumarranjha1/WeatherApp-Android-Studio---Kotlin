package com.lifepulse.domain.model

data class DailyForecastItem(
    val dateString: String,
    val tempMaxC: Double,
    val tempMinC: Double,
    val apparentTempMaxC: Double,
    val apparentTempMinC: Double,
    val weatherCode: Int,
    val precipitationProbMaxPercent: Int,
    val precipitationSumMm: Double,
    val uvIndexMax: Double,
    val maxWindSpeedKmH: Double,
    val sunriseTime: String? = null,
    val sunsetTime: String? = null
)
