package com.lifepulse.domain.model

data class WeatherData(
    val city: City,
    val current: CurrentWeather,
    val hourly: List<HourlyForecastItem>,
    val daily: List<DailyForecastItem>,
    val airQuality: AirQualityData?,
    val fetchedTimestamp: Long = System.currentTimeMillis(),
    val isCachedData: Boolean = false
)
