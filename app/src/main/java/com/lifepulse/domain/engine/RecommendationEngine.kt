package com.lifepulse.domain.engine

import com.lifepulse.domain.model.AirQualityData
import com.lifepulse.domain.model.CurrentWeather
import com.lifepulse.domain.model.HourlyForecastItem
import com.lifepulse.domain.model.Recommendation
import com.lifepulse.domain.model.RecommendationCategory
import com.lifepulse.domain.model.RecommendationSeverity

object RecommendationEngine {

    const val RAIN_PROBABILITY_THRESHOLD = 50
    const val HIGH_WIND_THRESHOLD_KMH = 40.0
    const val EXTREME_HEAT_THRESHOLD_C = 32.0
    const val FREEZING_TEMP_THRESHOLD_C = 0.0
    const val HIGH_UV_THRESHOLD = 6.0
    const val EXTREME_UV_THRESHOLD = 8.0
    const val UNHEALTHY_US_AQI_THRESHOLD = 100

    fun generateRecommendations(
        current: CurrentWeather?,
        hourly: List<HourlyForecastItem>,
        airQuality: AirQualityData?
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        if (current == null) {
            recommendations.add(
                Recommendation(
                    id = "no_data",
                    title = "Offline Intelligence Mode",
                    description = "Establish a network connection to sync real-time atmospheric measurements and localized alerts.",
                    category = RecommendationCategory.GENERAL,
                    severity = RecommendationSeverity.INFO,
                    actionText = "Refresh Data"
                )
            )
            return recommendations
        }

        val upcomingRainHour = hourly.take(12).firstOrNull { it.precipitationProbPercent >= RAIN_PROBABILITY_THRESHOLD }
        val isCurrentlyRaining = current.precipitationMm > 0.1 || current.weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82, 95)

        if (isCurrentlyRaining) {
            recommendations.add(
                Recommendation(
                    id = "current_rain",
                    title = "Precipitation Active",
                    description = "Active rainfall observed in your sector. Carry rain gear and exercise extra care while commuting.",
                    category = RecommendationCategory.PRECIPITATION,
                    severity = RecommendationSeverity.ALERT,
                    actionText = "Equip Umbrella"
                )
            )
        } else if (upcomingRainHour != null) {
            recommendations.add(
                Recommendation(
                    id = "upcoming_rain",
                    title = "Precipitation Expected",
                    description = "${upcomingRainHour.precipitationProbPercent}% probability of rain anticipated around ${upcomingRainHour.timeString.substringAfter("T").take(5)}.",
                    category = RecommendationCategory.PRECIPITATION,
                    severity = RecommendationSeverity.WARNING,
                    actionText = "Prepare Umbrella"
                )
            )
        }

        if (current.tempC >= EXTREME_HEAT_THRESHOLD_C) {
            recommendations.add(
                Recommendation(
                    id = "extreme_heat",
                    title = "Thermal Elevated Notice",
                    description = "Ambient temperature is elevated at ${current.tempC.toInt()}°C. Maintain hydration and limit direct exposure during zenith hours.",
                    category = RecommendationCategory.TEMPERATURE,
                    severity = RecommendationSeverity.ALERT,
                    actionText = "Prioritize Hydration"
                )
            )
        } else if (current.tempC <= FREEZING_TEMP_THRESHOLD_C) {
            recommendations.add(
                Recommendation(
                    id = "freezing_temp",
                    title = "Sub-Zero Temperature",
                    description = "Freezing threshold detected at ${current.tempC.toInt()}°C. Ensure thermal layering and watch for icy conditions.",
                    category = RecommendationCategory.TEMPERATURE,
                    severity = RecommendationSeverity.WARNING,
                    actionText = "Wear Insulated Layers"
                )
            )
        }

        if (current.windSpeedKmH >= HIGH_WIND_THRESHOLD_KMH) {
            recommendations.add(
                Recommendation(
                    id = "high_wind",
                    title = "Gale Force Wind Alert",
                    description = "Wind velocity reaching ${current.windSpeedKmH.toInt()} km/h. Secure loose outdoor fixtures and remain cautious while driving.",
                    category = RecommendationCategory.WIND,
                    severity = RecommendationSeverity.WARNING,
                    actionText = "Secure Fixtures"
                )
            )
        }

        val maxUvToday = hourly.take(24).maxOfOrNull { it.uvIndex } ?: 0.0
        if (maxUvToday >= EXTREME_UV_THRESHOLD) {
            recommendations.add(
                Recommendation(
                    id = "extreme_uv",
                    title = "Peak Solar Radiation",
                    description = "UV Index reaches ${maxUvToday.toInt()} today. Broad-spectrum SPF protection and protective eyewear are recommended.",
                    category = RecommendationCategory.UV,
                    severity = RecommendationSeverity.ALERT,
                    actionText = "Apply Sunscreen"
                )
            )
        } else if (maxUvToday >= HIGH_UV_THRESHOLD) {
            recommendations.add(
                Recommendation(
                    id = "high_uv",
                    title = "Elevated UV Index",
                    description = "UV Index peaking at ${maxUvToday.toInt()} during midday. Moderate sun protection advised.",
                    category = RecommendationCategory.UV,
                    severity = RecommendationSeverity.WARNING,
                    actionText = "Sun Protection"
                )
            )
        }

        if (airQuality != null) {
            val usAqi = airQuality.usAqi
            val pm25 = airQuality.pm25
            if (usAqi != null && usAqi >= UNHEALTHY_US_AQI_THRESHOLD) {
                recommendations.add(
                    Recommendation(
                        id = "unhealthy_aqi",
                        title = "Elevated Air Pollution",
                        description = "US AQI index is $usAqi. Sensitive individuals should consider reducing intense outdoor physical exertion.",
                        category = RecommendationCategory.AIR_QUALITY,
                        severity = RecommendationSeverity.WARNING,
                        actionText = "Limit Outdoor Exertion"
                    )
                )
            } else if (pm25 != null && pm25 > 35.0) {
                recommendations.add(
                    Recommendation(
                        id = "high_pm25",
                        title = "Elevated Fine Particles (PM2.5)",
                        description = "PM2.5 concentration is ${pm25.toInt()} µg/m³. Consider keeping windows sealed during peak traffic.",
                        category = RecommendationCategory.AIR_QUALITY,
                        severity = RecommendationSeverity.INFO,
                        actionText = "Seal Windows"
                    )
                )
            }
        }

        if (recommendations.isEmpty()) {
            recommendations.add(
                Recommendation(
                    id = "pleasant_weather",
                    title = "Optimal Atmospheric Conditions",
                    description = "Ambient conditions are exceptionally comfortable and balanced. An ideal time for outdoor activities.",
                    category = RecommendationCategory.GENERAL,
                    severity = RecommendationSeverity.INFO,
                    actionText = "Enjoy Outdoors"
                )
            )
        }

        return recommendations
    }
}
