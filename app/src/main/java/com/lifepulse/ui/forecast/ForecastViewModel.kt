package com.lifepulse.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lifepulse.data.local.UserPreferencesRepository
import com.lifepulse.data.repository.WeatherRepository
import com.lifepulse.domain.model.City
import com.lifepulse.domain.model.UserPreferences
import com.lifepulse.domain.model.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ForecastUiState {
    object Loading : ForecastUiState
    data class Success(
        val weatherData: WeatherData,
        val userPreferences: UserPreferences
    ) : ForecastUiState
    data class Error(val message: String) : ForecastUiState
}

class ForecastViewModel(
    private val weatherRepository: WeatherRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForecastUiState>(ForecastUiState.Loading)
    val uiState: StateFlow<ForecastUiState> = _uiState.asStateFlow()

    init {
        loadForecastData()
    }

    fun loadForecastData() {
        viewModelScope.launch {
            _uiState.value = ForecastUiState.Loading
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
                    _uiState.value = ForecastUiState.Success(weatherData, prefs)
                } catch (e: Exception) {
                    _uiState.value = ForecastUiState.Error(
                        e.localizedMessage ?: "Failed to load forecast data."
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
            return ForecastViewModel(weatherRepository, userPreferencesRepository) as T
        }
    }
}
