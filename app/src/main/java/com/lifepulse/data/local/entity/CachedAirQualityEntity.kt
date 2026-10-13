package com.lifepulse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_air_quality")
data class CachedAirQualityEntity(
    @PrimaryKey val cityId: Long,
    val jsonPayload: String,
    val cachedTimestamp: Long = System.currentTimeMillis()
)
