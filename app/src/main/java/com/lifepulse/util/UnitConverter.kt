package com.lifepulse.util

import com.lifepulse.domain.model.PrecipitationUnit
import com.lifepulse.domain.model.TemperatureUnit
import com.lifepulse.domain.model.WindSpeedUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object UnitConverter {

    fun formatTemperature(tempC: Double, unit: TemperatureUnit): String {
        return when (unit) {
            TemperatureUnit.CELSIUS -> "${tempC.roundToInt()}°"
            TemperatureUnit.FAHRENHEIT -> "${((tempC * 9 / 5) + 32).roundToInt()}°"
        }
    }

    fun formatWindSpeed(speedKmH: Double, unit: WindSpeedUnit): String {
        return when (unit) {
            WindSpeedUnit.KM_H -> "${speedKmH.roundToInt()} km/h"
            WindSpeedUnit.M_S -> String.format(Locale.US, "%.1f m/s", speedKmH / 3.6)
            WindSpeedUnit.MPH -> "${(speedKmH * 0.621371).roundToInt()} mph"
        }
    }

    fun formatPrecipitation(precipMm: Double, unit: PrecipitationUnit): String {
        return when (unit) {
            PrecipitationUnit.MM -> String.format(Locale.US, "%.1f mm", precipMm)
            PrecipitationUnit.INCH -> String.format(Locale.US, "%.2f in", precipMm / 25.4)
        }
    }

    fun getWeatherDescription(code: Int): String {
        return when (code) {
            0 -> "Clear Sky"
            1 -> "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45 -> "Fog"
            48 -> "Rime Fog"
            51 -> "Light Drizzle"
            53 -> "Moderate Drizzle"
            55 -> "Dense Drizzle"
            56 -> "Light Freezing Drizzle"
            57 -> "Dense Freezing Drizzle"
            61 -> "Slight Rain"
            63 -> "Moderate Rain"
            65 -> "Heavy Rain"
            66 -> "Light Freezing Rain"
            67 -> "Heavy Freezing Rain"
            71 -> "Slight Snow"
            73 -> "Moderate Snow"
            75 -> "Heavy Snow"
            77 -> "Snow Grains"
            80 -> "Slight Rain Showers"
            81 -> "Moderate Rain Showers"
            82 -> "Violent Rain Showers"
            85 -> "Slight Snow Showers"
            86 -> "Heavy Snow Showers"
            95 -> "Thunderstorm"
            96 -> "Thunderstorm with Hail"
            99 -> "Severe Thunderstorm"
            else -> "Conditions ($code)"
        }
    }

    fun getUsAqiCategory(aqi: Int?): AqiCategory {
        if (aqi == null) return AqiCategory("Unmeasured", "Air quality data is currently unavailable.", 0xFF888888)
        return when {
            aqi <= 50 -> AqiCategory("Good", "Air quality is satisfactory and poses negligible health risk.", 0xFF83A583)
            aqi <= 100 -> AqiCategory("Moderate", "Air quality is acceptable. Unusually sensitive individuals should monitor exertion.", 0xFFD4A359)
            aqi <= 150 -> AqiCategory("Sensitive Warning", "Members of sensitive groups may experience minor effects.", 0xFFD48B59)
            aqi <= 200 -> AqiCategory("Unhealthy", "The general public may begin to experience health effects.", 0xFFC25B56)
            aqi <= 300 -> AqiCategory("Very Unhealthy", "Health alert indicating increased health risk for all populations.", 0xFF9E4B56)
            else -> AqiCategory("Hazardous", "Health warning of emergency atmospheric conditions.", 0xFF803838)
        }
    }

    fun getEuropeanAqiCategory(aqi: Int?): AqiCategory {
        if (aqi == null) return AqiCategory("Unmeasured", "European EAQI data unavailable.", 0xFF888888)
        return when {
            aqi <= 20 -> AqiCategory("Very Good", "Atmospheric air quality is exceptionally clean.", 0xFF83A583)
            aqi <= 40 -> AqiCategory("Good", "Air quality meets high standards.", 0xFF94B594)
            aqi <= 60 -> AqiCategory("Fair", "Air quality is fair with minor pollutant concentrations.", 0xFFD4A359)
            aqi <= 80 -> AqiCategory("Poor", "Air quality is poor. Limit prolonged strenuous activity.", 0xFFD48B59)
            aqi <= 100 -> AqiCategory("Very Poor", "Air quality is very poor with elevated pollutants.", 0xFFC25B56)
            else -> AqiCategory("Extremely Poor", "Air quality is extremely poor.", 0xFF803838)
        }
    }

    fun formatTimeString(timeIso: String): String {
        return try {
            if (timeIso.contains("T")) {
                timeIso.substringAfter("T").take(5)
            } else {
                timeIso
            }
        } catch (e: Exception) {
            timeIso
        }
    }

    fun formatDateString(dateIso: String): String {
        return try {
            val dateOnly = if (dateIso.contains("T")) dateIso.substringBefore("T") else dateIso
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date: Date? = inputFormat.parse(dateOnly)
            if (date != null) {
                val outputFormat = SimpleDateFormat("EEE, MMM d", Locale.US)
                outputFormat.format(date)
            } else {
                dateIso
            }
        } catch (e: Exception) {
            dateIso
        }
    }

    data class AqiCategory(
        val label: String,
        val description: String,
        val colorHex: Long
    )
}
