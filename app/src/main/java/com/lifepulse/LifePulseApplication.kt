package com.lifepulse

import android.app.Application
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.lifepulse.data.local.AppDatabase
import com.lifepulse.data.local.UserPreferencesRepository
import com.lifepulse.data.remote.NetworkModule
import com.lifepulse.data.repository.WeatherRepository
import com.lifepulse.notifications.NotificationHelper
import com.lifepulse.worker.WeatherNotificationWorker
import java.util.concurrent.TimeUnit

class LifePulseApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    lateinit var weatherRepository: WeatherRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        userPreferencesRepository = UserPreferencesRepository(this)
        weatherRepository = WeatherRepository(
            weatherApi = NetworkModule.weatherApi,
            geocodingApi = NetworkModule.geocodingApi,
            airQualityApi = NetworkModule.airQualityApi,
            savedCityDao = database.savedCityDao(),
            cachedWeatherDao = database.cachedWeatherDao(),
            cachedAirQualityDao = database.cachedAirQualityDao(),
            userPreferencesRepository = userPreferencesRepository
        )

        NotificationHelper.createNotificationChannel(this)
        scheduleBackgroundWork()
    }

    private fun scheduleBackgroundWork() {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<WeatherNotificationWorker>(
                3, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                WeatherNotificationWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        } catch (e: Exception) {
            Log.e("LifePulseApp", "Failed to schedule background worker", e)
        }
    }

    companion object {
        lateinit var instance: LifePulseApplication
            private set
    }
}
