package com.lifepulse.data.remote.dto

data class GeocodingResponseDto(
    val results: List<GeocodingItemDto>?
)

data class GeocodingItemDto(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?,
    val timezone: String?
)
