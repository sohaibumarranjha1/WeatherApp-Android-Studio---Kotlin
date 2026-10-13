package com.lifepulse.ui.airquality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lifepulse.data.local.UserPreferencesRepository
import com.lifepulse.data.repository.WeatherRepository
import com.lifepulse.domain.model.City
import com.lifepulse.domain.model.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AirQualityUiState {
    object Loading : AirQualityUiState
    data class Success(val weatherData: WeatherData) : AirQualityUiState
    data class Error(val message: String) : AirQualityUiState
}

class AirQualityViewModel(
    private val weatherRepository: WeatherRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AirQualityUiState>(AirQualityUiState.Loading)
    val uiState: StateFlow<AirQualityUiState> = _uiState.asStateFlow()

    init {
        loadAirQualityData()
    }

    fun loadAirQualityData() {
        viewModelScope.launch {
            _uiState.value = AirQualityUiState.Loading
            userPreferencesRepository.userPreferencesFlow.collect { prefs ->
                try {
                    val activeCity = City(
                        id = prefs.activeCityId,
                        name = prefs.activeCityName,
                        latitude = prefs.activeCityLat,
                        longitude = prefs.activeCityLon,
                        country = prefs.activeCityCountry
                    )
                    val weatherData = weatherRepository.getWeatherDataForCity(activeCity)
                    _uiState.value = AirQualityUiState.Success(weatherData)
                } catch (e: Exception) {
                    _uiState.value = AirQualityUiState.Error(
                        e.localizedMessage ?: "Failed to load air quality data."
                    )
                }
            }
        }
    }

    class Factory(
        private val weatherRepository: WeatherRepository,
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AirQualityViewModel(weatherRepository, userPreferencesRepository) as T
        }
    }
}
