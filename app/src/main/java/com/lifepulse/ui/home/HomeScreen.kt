package com.lifepulse.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifepulse.ui.components.AqiSummaryCard
import com.lifepulse.ui.components.CachedDataBanner
import com.lifepulse.ui.components.ErrorStateCard
import com.lifepulse.ui.components.HourlyForecastRow
import com.lifepulse.ui.components.LoadingStateCard
import com.lifepulse.ui.components.MainWeatherHeroCard
import com.lifepulse.ui.components.RecommendationSection
import com.lifepulse.ui.theme.LifePulseTheme
import com.lifepulse.ui.theme.WeatherThemes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            LifePulseTheme {
                LoadingStateCard()
            }
        }

        is HomeUiState.Error -> {
            LifePulseTheme {
                ErrorStateCard(
                    message = state.message,
                    onRetry = { viewModel.refresh(isPullRefresh = false) }
                )
            }
        }

        is HomeUiState.Success -> {
            val palette = WeatherThemes.getPalette(
                code = state.weatherData.current.weatherCode,
                isDay = state.weatherData.current.isDay
            )

            LifePulseTheme(
                themeMode = state.userPreferences.themeMode,
                palette = palette
            ) {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.refresh(isPullRefresh = true) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        BrandHeader(
                            cityName = state.weatherData.city.name,
                            onSearchClick = onNavigateToSearch
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (state.weatherData.isCachedData) {
                            CachedDataBanner(
                                cachedTimestamp = state.weatherData.fetchedTimestamp,
                                onRefresh = { viewModel.refresh(isPullRefresh = false) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        MainWeatherHeroCard(
                            current = state.weatherData.current,
                            todayForecast = state.weatherData.daily.firstOrNull(),
                            cityName = state.weatherData.city.displayName,
                            tempUnit = state.userPreferences.tempUnit,
                            windUnit = state.userPreferences.windSpeedUnit,
                            precipUnit = state.userPreferences.precipitationUnit
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        if (state.recommendations.isNotEmpty()) {
                            RecommendationSection(recommendations = state.recommendations)
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        if (state.weatherData.hourly.isNotEmpty()) {
                            HourlyForecastRow(
                                hourlyItems = state.weatherData.hourly,
                                tempUnit = state.userPreferences.tempUnit
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        AqiSummaryCard(airQuality = state.weatherData.airQuality)

                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BrandHeader(
    cityName: String,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "LIFEPULSE",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 2.sp
            )
            Text(
                text = " · ",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = cityName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Locations",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
