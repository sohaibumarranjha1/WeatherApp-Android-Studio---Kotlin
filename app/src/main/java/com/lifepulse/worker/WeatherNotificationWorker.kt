package com.lifepulse.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lifepulse.LifePulseApplication
import com.lifepulse.domain.engine.RecommendationEngine
import com.lifepulse.domain.model.City
import com.lifepulse.domain.model.RecommendationSeverity
import com.lifepulse.notifications.NotificationHelper
import kotlinx.coroutines.flow.first

class WeatherNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as LifePulseApplication
        val userPrefs = app.userPreferencesRepository.userPreferencesFlow.first()

        if (!userPrefs.notificationsEnabled) {
            return Result.success()
        }

        return try {
            val city = City(
                id = userPrefs.activeCityId,
                name = userPrefs.activeCityName,
                latitude = userPrefs.activeCityLat,
                longitude = userPrefs.activeCityLon,
                country = userPrefs.activeCityCountry
            )

            val weatherData = app.weatherRepository.getWeatherDataForCity(city)
            val recommendations = RecommendationEngine.generateRecommendations(
                current = weatherData.current,
                hourly = weatherData.hourly,
                airQuality = weatherData.airQuality
            )

            val highAlert = recommendations.firstOrNull {
                it.severity == RecommendationSeverity.ALERT || it.severity == RecommendationSeverity.WARNING
            }

            if (highAlert != null) {
                NotificationHelper.showWeatherNotification(
                    context = applicationContext,
                    title = "LifePulse (${city.name}): ${highAlert.title}",
                    message = highAlert.description
                )
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("WeatherWorker", "Error executing periodic weather background check", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "LifePulseWeatherCheckWorker"
    }
}
