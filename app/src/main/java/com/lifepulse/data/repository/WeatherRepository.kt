package com.lifepulse.data.repository

import android.util.Log
import com.google.gson.Gson
import com.lifepulse.data.local.UserPreferencesRepository
import com.lifepulse.data.local.dao.CachedAirQualityDao
import com.lifepulse.data.local.dao.CachedWeatherDao
import com.lifepulse.data.local.dao.SavedCityDao
import com.lifepulse.data.local.entity.CachedAirQualityEntity
import com.lifepulse.data.local.entity.CachedWeatherEntity
import com.lifepulse.data.local.entity.SavedCityEntity
import com.lifepulse.data.remote.api.OpenMeteoAirQualityApi
import com.lifepulse.data.remote.api.OpenMeteoGeocodingApi
import com.lifepulse.data.remote.api.OpenMeteoWeatherApi
import com.lifepulse.data.remote.dto.AirQualityResponseDto
import com.lifepulse.data.remote.dto.WeatherForecastDto
import com.lifepulse.domain.model.AirQualityData
import com.lifepulse.domain.model.City
import com.lifepulse.domain.model.CurrentWeather
import com.lifepulse.domain.model.DailyForecastItem
import com.lifepulse.domain.model.HourlyForecastItem
import com.lifepulse.domain.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WeatherRepository(
    private val weatherApi: OpenMeteoWeatherApi,
    private val geocodingApi: OpenMeteoGeocodingApi,
    private val airQualityApi: OpenMeteoAirQualityApi,
    private val savedCityDao: SavedCityDao,
    private val cachedWeatherDao: CachedWeatherDao,
    private val cachedAirQualityDao: CachedAirQualityDao,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val gson: Gson = Gson()
) {

    val savedCitiesFlow: Flow<List<City>> = savedCityDao.getAllSavedCities().map { entities ->
        entities.map { entity ->
            City(
                id = entity.id,
                name = entity.name,
                latitude = entity.latitude,
                longitude = entity.longitude,
                country = entity.country,
                admin1 = entity.admin1,
                timezone = entity.timezone,
                isSaved = true
            )
        }
    }

    suspend fun getWeatherDataForCity(city: City): WeatherData = withContext(Dispatchers.IO) {
        try {
            val (weatherDto, airQualityDto) = coroutineScope {
                val weatherDeferred = async { weatherApi.getWeatherForecast(city.latitude, city.longitude) }
                val airQualityDeferred = async {
                    try {
                        airQualityApi.getAirQuality(city.latitude, city.longitude)
                    } catch (e: Exception) {
                        Log.w("WeatherRepository", "Air quality fetch failed", e)
                        null
                    }
                }
                Pair(weatherDeferred.await(), airQualityDeferred.await())
            }

            cacheWeatherData(city.id, weatherDto)
            if (airQualityDto != null) {
                cacheAirQualityData(city.id, airQualityDto)
            }

            mapToDomainWeatherData(city, weatherDto, airQualityDto, isCached = false)

        } catch (e: Exception) {
            Log.e("WeatherRepository", "Network fetch error, attempting Room fallback", e)
            val cachedWeather = cachedWeatherDao.getCachedWeather(city.id)
            if (cachedWeather != null) {
                val weatherDto = gson.fromJson(cachedWeather.jsonPayload, WeatherForecastDto::class.java)
                val cachedAq = cachedAirQualityDao.getCachedAirQuality(city.id)
                val airQualityDto = cachedAq?.let { gson.fromJson(it.jsonPayload, AirQualityResponseDto::class.java) }

                mapToDomainWeatherData(
                    city = city,
                    weatherDto = weatherDto,
                    airQualityDto = airQualityDto,
                    isCached = true,
                    timestamp = cachedWeather.cachedTimestamp
                )
            } else {
                throw e
            }
        }
    }

    suspend fun searchCities(query: String): List<City> = withContext(Dispatchers.IO) {
        if (query.trim().length < 2) return@withContext emptyList()
        val response = geocodingApi.searchCity(name = query.trim())
        val savedCities = savedCityDao.getAllSavedCities().first()
        val savedIds = savedCities.map { it.id }.toSet()

        response.results?.map { dto ->
            City(
                id = dto.id,
                name = dto.name,
                latitude = dto.latitude,
                longitude = dto.longitude,
                country = dto.country,
                admin1 = dto.admin1,
                timezone = dto.timezone,
                isSaved = savedIds.contains(dto.id)
            )
        } ?: emptyList()
    }

    suspend fun saveCity(city: City) = withContext(Dispatchers.IO) {
        savedCityDao.insertCity(
            SavedCityEntity(
                id = city.id,
                name = city.name,
                latitude = city.latitude,
                longitude = city.longitude,
                country = city.country,
                admin1 = city.admin1,
                timezone = city.timezone
            )
        )
    }

    suspend fun removeCity(cityId: Long) = withContext(Dispatchers.IO) {
        savedCityDao.deleteCityById(cityId)
    }

    fun isCitySavedFlow(cityId: Long): Flow<Boolean> = savedCityDao.isCitySavedFlow(cityId)

    suspend fun setActiveCity(city: City) {
        userPreferencesRepository.setActiveCity(city)
    }

    private suspend fun cacheWeatherData(cityId: Long, dto: WeatherForecastDto) {
        try {
            val json = gson.toJson(dto)
            cachedWeatherDao.insertCachedWeather(
                CachedWeatherEntity(cityId = cityId, jsonPayload = json)
            )
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Failed to cache weather data", e)
        }
    }

    private suspend fun cacheAirQualityData(cityId: Long, dto: AirQualityResponseDto) {
        try {
            val json = gson.toJson(dto)
            cachedAirQualityDao.insertCachedAirQuality(
                CachedAirQualityEntity(cityId = cityId, jsonPayload = json)
            )
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Failed to cache air quality data", e)
        }
    }

    private fun mapToDomainWeatherData(
        city: City,
        weatherDto: WeatherForecastDto,
        airQualityDto: AirQualityResponseDto?,
        isCached: Boolean,
        timestamp: Long = System.currentTimeMillis()
    ): WeatherData {
        val currentDto = weatherDto.current ?: throw IllegalStateException("Current weather data missing")

        val current = CurrentWeather(
            tempC = currentDto.temperature2m,
            apparentTempC = currentDto.apparentTemperature,
            humidityPercent = currentDto.relativeHumidity2m,
            windSpeedKmH = currentDto.windSpeed10m,
            windDirectionDeg = currentDto.windDirection10m,
            precipitationMm = currentDto.precipitation,
            weatherCode = currentDto.weatherCode,
            isDay = currentDto.isDay == 1,
            surfacePressureHpa = currentDto.surfacePressure,
            timeString = currentDto.time
        )

        val hourlyList = mutableListOf<HourlyForecastItem>()
        val h = weatherDto.hourly
        if (h?.time != null) {
            val count = h.time.size
            for (i in 0 until count) {
                hourlyList.add(
                    HourlyForecastItem(
                        timeString = h.time[i],
                        tempC = h.temperature2m?.getOrNull(i) ?: 0.0,
                        apparentTempC = h.apparentTemperature?.getOrNull(i) ?: 0.0,
                        weatherCode = h.weatherCode?.getOrNull(i) ?: 0,
                        precipitationProbPercent = h.precipitationProbability?.getOrNull(i) ?: 0,
                        precipitationMm = h.precipitation?.getOrNull(i) ?: 0.0,
                        uvIndex = h.uvIndex?.getOrNull(i) ?: 0.0,
                        humidityPercent = h.relativeHumidity2m?.getOrNull(i) ?: 0,
                        windSpeedKmH = h.windSpeed10m?.getOrNull(i) ?: 0.0
                    )
                )
            }
        }

        val dailyList = mutableListOf<DailyForecastItem>()
        val d = weatherDto.daily
        if (d?.time != null) {
            val count = d.time.size
            for (i in 0 until count) {
                dailyList.add(
                    DailyForecastItem(
                        dateString = d.time[i],
                        tempMaxC = d.temperature2mMax?.getOrNull(i) ?: 0.0,
                        tempMinC = d.temperature2mMin?.getOrNull(i) ?: 0.0,
                        apparentTempMaxC = d.apparentTemperatureMax?.getOrNull(i) ?: 0.0,
                        apparentTempMinC = d.apparentTemperatureMin?.getOrNull(i) ?: 0.0,
                        weatherCode = d.weatherCode?.getOrNull(i) ?: 0,
                        precipitationProbMaxPercent = d.precipitationProbabilityMax?.getOrNull(i) ?: 0,
                        precipitationSumMm = d.precipitationSum?.getOrNull(i) ?: 0.0,
                        uvIndexMax = d.uvIndexMax?.getOrNull(i) ?: 0.0,
                        maxWindSpeedKmH = d.windSpeed10mMax?.getOrNull(i) ?: 0.0,
                        sunriseTime = d.sunrise?.getOrNull(i),
                        sunsetTime = d.sunset?.getOrNull(i)
                    )
                )
            }
        }

        val airQualityData = airQualityDto?.current?.let { aq ->
            AirQualityData(
                usAqi = aq.usAqi,
                europeanAqi = aq.europeanAqi,
                pm25 = aq.pm25,
                pm10 = aq.pm10,
                no2 = aq.nitrogenDioxide,
                so2 = aq.sulphurDioxide,
                o3 = aq.ozone,
                co = aq.carbonMonoxide,
                dust = aq.dust,
                uvIndex = aq.uvIndex
            )
        }

        return WeatherData(
            city = city,
            current = current,
            hourly = hourlyList,
            daily = dailyList,
            airQuality = airQualityData,
            fetchedTimestamp = timestamp,
            isCachedData = isCached
        )
    }
}
