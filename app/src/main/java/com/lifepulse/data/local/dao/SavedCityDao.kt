package com.lifepulse.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lifepulse.data.local.entity.SavedCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCityDao {

    @Query("SELECT * FROM saved_cities ORDER BY savedTimestamp DESC")
    fun getAllSavedCities(): Flow<List<SavedCityEntity>>

    @Query("SELECT * FROM saved_cities WHERE id = :id LIMIT 1")
    suspend fun getCityById(id: Long): SavedCityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: SavedCityEntity): Long

    @Delete
    suspend fun deleteCity(city: SavedCityEntity): Int

    @Query("DELETE FROM saved_cities WHERE id = :id")
    suspend fun deleteCityById(id: Long): Int

    @Query("SELECT EXISTS(SELECT 1 FROM saved_cities WHERE id = :id)")
    fun isCitySavedFlow(id: Long): Flow<Boolean>
}
