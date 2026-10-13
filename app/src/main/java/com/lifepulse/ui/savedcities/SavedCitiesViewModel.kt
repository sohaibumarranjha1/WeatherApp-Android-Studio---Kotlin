package com.lifepulse.ui.savedcities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lifepulse.data.repository.WeatherRepository
import com.lifepulse.domain.model.City
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedCitiesViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val savedCities: StateFlow<List<City>> = weatherRepository.savedCitiesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteCity(cityId: Long) {
        viewModelScope.launch {
            weatherRepository.removeCity(cityId)
        }
    }

    fun selectActiveCity(city: City, onSelected: () -> Unit) {
        viewModelScope.launch {
            weatherRepository.setActiveCity(city)
            onSelected()
        }
    }

    class Factory(
        private val weatherRepository: WeatherRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SavedCitiesViewModel(weatherRepository) as T
        }
    }
}
