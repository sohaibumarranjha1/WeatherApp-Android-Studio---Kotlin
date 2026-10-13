package com.lifepulse

import com.lifepulse.domain.engine.RecommendationEngine
import com.lifepulse.domain.model.AirQualityData
import com.lifepulse.domain.model.CurrentWeather
import com.lifepulse.domain.model.HourlyForecastItem
import com.lifepulse.domain.model.RecommendationCategory
import com.lifepulse.domain.model.RecommendationSeverity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationEngineTest {

    private fun createCurrentWeather(
        tempC: Double = 22.0,
        precipMm: Double = 0.0,
        weatherCode: Int = 0,
        windSpeedKmH: Double = 15.0
    ): CurrentWeather {
        return CurrentWeather(
            tempC = tempC,
            apparentTempC = tempC,
            humidityPercent = 50,
            windSpeedKmH = windSpeedKmH,
            windDirectionDeg = 180,
            precipitationMm = precipMm,
            weatherCode = weatherCode,
            isDay = true,
            surfacePressureHpa = 1013.0,
            timeString = "2026-03-30T12:00"
        )
    }

    @Test
    fun testNullCurrentWeather_ReturnsInfo() {
        val recs = RecommendationEngine.generateRecommendations(null, emptyList(), null)
        assertEquals(1, recs.size)
        assertEquals("no_data", recs[0].id)
    }

    @Test
    fun testRainRecommendation() {
        val current = createCurrentWeather(precipMm = 2.5, weatherCode = 61)
        val recs = RecommendationEngine.generateRecommendations(current, emptyList(), null)

        val rainRec = recs.find { it.category == RecommendationCategory.PRECIPITATION }
        assertTrue(rainRec != null)
        assertEquals(RecommendationSeverity.ALERT, rainRec?.severity)
    }

    @Test
    fun testExtremeHeatRecommendation() {
        val current = createCurrentWeather(tempC = 35.0)
        val recs = RecommendationEngine.generateRecommendations(current, emptyList(), null)

        val heatRec = recs.find { it.category == RecommendationCategory.TEMPERATURE }
        assertTrue(heatRec != null)
        assertEquals("extreme_heat", heatRec?.id)
        assertEquals(RecommendationSeverity.ALERT, heatRec?.severity)
    }

    @Test
    fun testHighWindRecommendation() {
        val current = createCurrentWeather(windSpeedKmH = 55.0)
        val recs = RecommendationEngine.generateRecommendations(current, emptyList(), null)

        val windRec = recs.find { it.category == RecommendationCategory.WIND }
        assertTrue(windRec != null)
        assertEquals("high_wind", windRec?.id)
    }

    @Test
    fun testHighUvRecommendation() {
        val current = createCurrentWeather()
        val hourly = listOf(
            HourlyForecastItem(
                timeString = "2026-03-30T13:00",
                tempC = 22.0,
                apparentTempC = 22.0,
                weatherCode = 0,
                precipitationProbPercent = 0,
                precipitationMm = 0.0,
                uvIndex = 9.5,
                humidityPercent = 50,
                windSpeedKmH = 10.0
            )
        )

        val recs = RecommendationEngine.generateRecommendations(current, hourly, null)
        val uvRec = recs.find { it.category == RecommendationCategory.UV }
        assertTrue(uvRec != null)
        assertEquals("extreme_uv", uvRec?.id)
    }

    @Test
    fun testUnhealthyAqiRecommendation() {
        val current = createCurrentWeather()
        val aq = AirQualityData(
            usAqi = 145,
            europeanAqi = 70,
            pm25 = 45.0,
            pm10 = 80.0,
            no2 = 30.0,
            so2 = 10.0,
            o3 = 50.0,
            co = 1.0,
            dust = 5.0,
            uvIndex = 3.0
        )

        val recs = RecommendationEngine.generateRecommendations(current, emptyList(), aq)
        val aqRec = recs.find { it.category == RecommendationCategory.AIR_QUALITY }
        assertTrue(aqRec != null)
        assertEquals("unhealthy_aqi", aqRec?.id)
    }

    @Test
    fun testPleasantWeather_ReturnsDefaultInfo() {
        val current = createCurrentWeather(tempC = 22.0, windSpeedKmH = 10.0)
        val recs = RecommendationEngine.generateRecommendations(current, emptyList(), null)

        assertEquals(1, recs.size)
        assertEquals("pleasant_weather", recs[0].id)
    }
}
