package com.lifepulse.domain.model

data class AirQualityData(
    val usAqi: Int?,
    val europeanAqi: Int?,
    val pm25: Double?,
    val pm10: Double?,
    val no2: Double?,
    val so2: Double?,
    val o3: Double?,
    val co: Double?,
    val dust: Double?,
    val uvIndex: Double?,
    val timestamp: Long = System.currentTimeMillis()
)
