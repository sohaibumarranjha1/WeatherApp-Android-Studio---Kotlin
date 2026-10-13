package com.lifepulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lifepulse.data.local.entity.CachedWeatherEntity

@Dao
interface CachedWeatherDao {

    @Query("SELECT * FROM cached_weather WHERE cityId = :cityId LIMIT 1")
    suspend fun getCachedWeather(cityId: Long): CachedWeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedWeather(entity: CachedWeatherEntity): Long
}
