package com.lifepulse.data.remote.api

import com.lifepulse.data.remote.dto.AirQualityResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoAirQualityApi {

    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "us_aqi,european_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone,dust,uv_index",
        @Query("timezone") timezone: String = "auto"
    ): AirQualityResponseDto
}
