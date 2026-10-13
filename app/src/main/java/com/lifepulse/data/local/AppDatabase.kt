package com.lifepulse.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lifepulse.data.local.dao.CachedAirQualityDao
import com.lifepulse.data.local.dao.CachedWeatherDao
import com.lifepulse.data.local.dao.SavedCityDao
import com.lifepulse.data.local.entity.CachedAirQualityEntity
import com.lifepulse.data.local.entity.CachedWeatherEntity
import com.lifepulse.data.local.entity.SavedCityEntity

@Database(
    entities = [
        SavedCityEntity::class,
        CachedWeatherEntity::class,
        CachedAirQualityEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun savedCityDao(): SavedCityDao
    abstract fun cachedWeatherDao(): CachedWeatherDao
    abstract fun cachedAirQualityDao(): CachedAirQualityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifepulse_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
