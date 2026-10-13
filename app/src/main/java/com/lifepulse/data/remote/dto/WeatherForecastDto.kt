package com.lifepulse.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherForecastDto(
    val latitude: Double,
    val longitude: Double,
    val timezone: String?,
    val current: CurrentWeatherDto?,
    val hourly: HourlyWeatherDto?,
    val daily: DailyWeatherDto?
)

data class CurrentWeatherDto(
    val time: String,
    @SerializedName("temperature_2m") val temperature2m: Double,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: Int,
    @SerializedName("apparent_temperature") val apparentTemperature: Double,
    @SerializedName("is_day") val isDay: Int,
    val precipitation: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("surface_pressure") val surfacePressure: Double,
    @SerializedName("wind_speed_10m") val windSpeed10m: Double,
    @SerializedName("wind_direction_10m") val windDirection10m: Int
)

data class HourlyWeatherDto(
    val time: List<String>?,
    @SerializedName("temperature_2m") val temperature2m: List<Double>?,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: List<Int>?,
    @SerializedName("apparent_temperature") val apparentTemperature: List<Double>?,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int>?,
    val precipitation: List<Double>?,
    @SerializedName("weather_code") val weatherCode: List<Int>?,
    @SerializedName("uv_index") val uvIndex: List<Double>?,
    @SerializedName("wind_speed_10m") val windSpeed10m: List<Double>?
)

data class DailyWeatherDto(
    val time: List<String>?,
    @SerializedName("weather_code") val weatherCode: List<Int>?,
    @SerializedName("temperature_2m_max") val temperature2mMax: List<Double>?,
    @SerializedName("temperature_2m_min") val temperature2mMin: List<Double>?,
    @SerializedName("apparent_temperature_max") val apparentTemperatureMax: List<Double>?,
    @SerializedName("apparent_temperature_min") val apparentTemperatureMin: List<Double>?,
    val sunrise: List<String>?,
    val sunset: List<String>?,
    @SerializedName("uv_index_max") val uvIndexMax: List<Double>?,
    @SerializedName("precipitation_sum") val precipitationSum: List<Double>?,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>?,
    @SerializedName("wind_speed_10m_max") val windSpeed10mMax: List<Double>?
)
