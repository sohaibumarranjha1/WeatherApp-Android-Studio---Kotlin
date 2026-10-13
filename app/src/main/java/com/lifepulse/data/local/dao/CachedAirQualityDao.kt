package com.lifepulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lifepulse.data.local.entity.CachedAirQualityEntity

@Dao
interface CachedAirQualityDao {

    @Query("SELECT * FROM cached_air_quality WHERE cityId = :cityId LIMIT 1")
    suspend fun getCachedAirQuality(cityId: Long): CachedAirQualityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedAirQuality(entity: CachedAirQualityEntity): Long
}
