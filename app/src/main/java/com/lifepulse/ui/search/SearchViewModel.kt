package com.lifepulse.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lifepulse.data.repository.WeatherRepository
import com.lifepulse.domain.model.City
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val cities: List<City>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchResults = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchResults: StateFlow<SearchUiState> = _searchResults.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(350L)
                .distinctUntilChanged()
                .collect { searchString ->
                    if (searchString.trim().length < 2) {
                        _searchResults.value = SearchUiState.Idle
                    } else {
                        performSearch(searchString)
                    }
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    private suspend fun performSearch(query: String) {
        _searchResults.value = SearchUiState.Loading
        try {
            val cities = weatherRepository.searchCities(query)
            _searchResults.value = SearchUiState.Success(cities)
        } catch (e: Exception) {
            _searchResults.value = SearchUiState.Error(
                e.localizedMessage ?: "Failed to search cities. Check your network connection."
            )
        }
    }

    fun toggleSaveCity(city: City) {
        viewModelScope.launch {
            if (city.isSaved) {
                weatherRepository.removeCity(city.id)
            } else {
                weatherRepository.saveCity(city)
            }
            if (_query.value.isNotBlank()) {
                performSearch(_query.value)
            }
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
            return SearchViewModel(weatherRepository) as T
        }
    }
}
