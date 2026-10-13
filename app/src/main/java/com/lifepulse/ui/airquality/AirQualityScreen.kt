package com.lifepulse.ui.airquality

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifepulse.ui.components.AqiSummaryCard
import com.lifepulse.ui.components.ErrorStateCard
import com.lifepulse.ui.components.LoadingStateCard
import com.lifepulse.ui.theme.LifePulseTheme
import com.lifepulse.ui.theme.WeatherThemes
import java.util.Locale

@Composable
fun AirQualityScreen(
    viewModel: AirQualityViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is AirQualityUiState.Loading -> {
            LifePulseTheme {
                LoadingStateCard("Evaluating atmospheric pollutants...")
            }
        }

        is AirQualityUiState.Error -> {
            LifePulseTheme {
                ErrorStateCard(
                    message = state.message,
                    onRetry = { viewModel.loadAirQualityData() }
                )
            }
        }

        is AirQualityUiState.Success -> {
            val aq = state.weatherData.airQuality
            val palette = WeatherThemes.getPalette(
                code = state.weatherData.current.weatherCode,
                isDay = state.weatherData.current.isDay
            )

            LifePulseTheme(palette = palette) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ENVIRONMENTAL QUALITY",
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

                    AqiSummaryCard(airQuality = aq)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (aq != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "POLLUTANT CONCENTRATIONS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                PollutantDetailRow("PM2.5 (Fine Particulate Matter)", aq.pm25, "µg/m³")
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))
                                PollutantDetailRow("PM10 (Coarse Particulate Matter)", aq.pm10, "µg/m³")
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))
                                PollutantDetailRow("NO₂ (Nitrogen Dioxide)", aq.no2, "µg/m³")
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))
                                PollutantDetailRow("SO₂ (Sulphur Dioxide)", aq.so2, "µg/m³")
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))
                                PollutantDetailRow("O₃ (Ozone)", aq.o3, "µg/m³")
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))
                                PollutantDetailRow("CO (Carbon Monoxide)", aq.co, "µg/m³")
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))
                                PollutantDetailRow("Dust", aq.dust, "µg/m³")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "AQI SCALE STANDARDS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "US AQI scale ranges from 0 to 500, categorized into Good (0 to 50), Moderate (51 to 100), Sensitive Warning (101 to 150), Unhealthy (151 to 200), Very Unhealthy (201 to 300), and Hazardous (301+).\n\nEuropean EAQI scale uses European Environment Agency criteria ranging from Very Good (0 to 20) to Extremely Poor (>100).\n\nNotice: Atmospheric information is provided for personal daily utility and wellness guidance.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Composable
fun PollutantDetailRow(
    name: String,
    value: Double?,
    unit: String
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value?.let { "${String.format(Locale.US, "%.1f", it)} $unit" } ?: "Unmeasured",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}
