package com.lifepulse.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AirQualityResponseDto(
    val latitude: Double,
    val longitude: Double,
    val current: AirQualityCurrentDto?
)

data class AirQualityCurrentDto(
    @SerializedName("us_aqi") val usAqi: Int?,
    @SerializedName("european_aqi") val europeanAqi: Int?,
    @SerializedName("pm10") val pm10: Double?,
    @SerializedName("pm2_5") val pm25: Double?,
    @SerializedName("carbon_monoxide") val carbonMonoxide: Double?,
    @SerializedName("nitrogen_dioxide") val nitrogenDioxide: Double?,
    @SerializedName("sulphur_dioxide") val sulphurDioxide: Double?,
    val ozone: Double?,
    val dust: Double?,
    @SerializedName("uv_index") val uvIndex: Double?
)
