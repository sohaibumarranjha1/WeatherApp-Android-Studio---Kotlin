package com.lifepulse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lifepulse.data.local.UserPreferencesRepository
import com.lifepulse.data.repository.WeatherRepository
import com.lifepulse.domain.engine.RecommendationEngine
import com.lifepulse.domain.model.City
import com.lifepulse.domain.model.Recommendation
import com.lifepulse.domain.model.UserPreferences
import com.lifepulse.domain.model.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val weatherData: WeatherData,
        val recommendations: List<Recommendation>,
        val userPreferences: UserPreferences,
        val isRefreshing: Boolean = false
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeActiveCityAndPreferences()
    }

    fun observeActiveCityAndPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { prefs ->
                val activeCity = City(
                    id = prefs.activeCityId,
                    name = prefs.activeCityName,
                    latitude = prefs.activeCityLat,
                    longitude = prefs.activeCityLon,
                    country = prefs.activeCityCountry
                )
                refreshWeatherData(activeCity, prefs, isPullRefresh = false)
            }
        }
    }

    fun refresh(isPullRefresh: Boolean = true) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                if (isPullRefresh) {
                    _uiState.value = currentState.copy(isRefreshing = true)
                }
                val activeCity = currentState.weatherData.city
                val prefs = currentState.userPreferences
                refreshWeatherData(activeCity, prefs, isPullRefresh)
            }
        }
    }

    private suspend fun refreshWeatherData(
        city: City,
        prefs: UserPreferences,
        isPullRefresh: Boolean
    ) {
        try {
            val weatherData = weatherRepository.getWeatherDataForCity(city)
            val recommendations = RecommendationEngine.generateRecommendations(
                current = weatherData.current,
                hourly = weatherData.hourly,
                airQuality = weatherData.airQuality
            )

            _uiState.value = HomeUiState.Success(
                weatherData = weatherData,
                recommendations = recommendations,
                userPreferences = prefs,
                isRefreshing = false
            )
        } catch (e: Exception) {
            _uiState.value = HomeUiState.Error(
                e.localizedMessage ?: "Failed to load weather data. Check your connection."
            )
        }
    }

    class Factory(
        private val weatherRepository: WeatherRepository,
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(weatherRepository, userPreferencesRepository) as T
        }
    }
}
