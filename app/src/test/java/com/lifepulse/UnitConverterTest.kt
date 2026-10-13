package com.lifepulse

import com.lifepulse.domain.model.PrecipitationUnit
import com.lifepulse.domain.model.TemperatureUnit
import com.lifepulse.domain.model.WindSpeedUnit
import com.lifepulse.util.UnitConverter
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConverterTest {

    @Test
    fun testTemperatureConversion() {
        val c = UnitConverter.formatTemperature(25.0, TemperatureUnit.CELSIUS)
        assertEquals("25°", c)

        val f = UnitConverter.formatTemperature(25.0, TemperatureUnit.FAHRENHEIT)
        assertEquals("77°", f)
    }

    @Test
    fun testWindSpeedConversion() {
        val kmh = UnitConverter.formatWindSpeed(36.0, WindSpeedUnit.KM_H)
        assertEquals("36 km/h", kmh)

        val ms = UnitConverter.formatWindSpeed(36.0, WindSpeedUnit.M_S)
        assertEquals("10.0 m/s", ms)

        val mph = UnitConverter.formatWindSpeed(36.0, WindSpeedUnit.MPH)
        assertEquals("22 mph", mph)
    }

    @Test
    fun testPrecipitationConversion() {
        val mm = UnitConverter.formatPrecipitation(10.0, PrecipitationUnit.MM)
        assertEquals("10.0 mm", mm)

        val inch = UnitConverter.formatPrecipitation(25.4, PrecipitationUnit.INCH)
        assertEquals("1.00 in", inch)
    }

    @Test
    fun testWeatherDescription() {
        assertEquals("Clear Sky", UnitConverter.getWeatherDescription(0))
        assertEquals("Thunderstorm", UnitConverter.getWeatherDescription(95))
        assertEquals("Slight Rain", UnitConverter.getWeatherDescription(61))
    }

    @Test
    fun testUsAqiCategory() {
        assertEquals("Good", UnitConverter.getUsAqiCategory(30).label)
        assertEquals("Moderate", UnitConverter.getUsAqiCategory(75).label)
        assertEquals("Sensitive Warning", UnitConverter.getUsAqiCategory(120).label)
        assertEquals("Unhealthy", UnitConverter.getUsAqiCategory(180).label)
    }
}
