package com.lifepulse.ui.forecast

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifepulse.ui.components.DailyForecastCard
import com.lifepulse.ui.components.ErrorStateCard
import com.lifepulse.ui.components.HourlyForecastRow
import com.lifepulse.ui.components.LoadingStateCard
import com.lifepulse.ui.theme.LifePulseTheme
import com.lifepulse.ui.theme.WeatherThemes

@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ForecastUiState.Loading -> {
            LifePulseTheme {
                LoadingStateCard("Computing multi-day trajectory...")
            }
        }

        is ForecastUiState.Error -> {
            LifePulseTheme {
                ErrorStateCard(
                    message = state.message,
                    onRetry = { viewModel.loadForecastData() }
                )
            }
        }

        is ForecastUiState.Success -> {
            val palette = WeatherThemes.getPalette(
                code = state.weatherData.current.weatherCode,
                isDay = state.weatherData.current.isDay
            )

            LifePulseTheme(
                themeMode = state.userPreferences.themeMode,
                palette = palette
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ATMOSPHERIC FORECAST",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = state.weatherData.city.name,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DailyForecastCard(
                        dailyItems = state.weatherData.daily,
                        tempUnit = state.userPreferences.tempUnit
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    HourlyForecastRow(
                        hourlyItems = state.weatherData.hourly,
                        tempUnit = state.userPreferences.tempUnit
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}
