package com.lifepulse.ui.settings

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifepulse.domain.model.PrecipitationUnit
import com.lifepulse.domain.model.TemperatureUnit
import com.lifepulse.domain.model.ThemeMode
import com.lifepulse.domain.model.WindSpeedUnit
import com.lifepulse.ui.theme.LifePulseTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToAbout: () -> Unit
) {
    val prefs by viewModel.userPreferences.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateNotificationsEnabled(isGranted)
    }

    LifePulseTheme(themeMode = prefs.themeMode) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "PREFERENCES & UTILITY",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsCard(title = "Temperature Unit") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = prefs.tempUnit == TemperatureUnit.CELSIUS,
                        onClick = { viewModel.updateTempUnit(TemperatureUnit.CELSIUS) },
                        label = { Text("Celsius (°C)") }
                    )
                    FilterChip(
                        selected = prefs.tempUnit == TemperatureUnit.FAHRENHEIT,
                        onClick = { viewModel.updateTempUnit(TemperatureUnit.FAHRENHEIT) },
                        label = { Text("Fahrenheit (°F)") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsCard(title = "Wind Velocity Unit") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = prefs.windSpeedUnit == WindSpeedUnit.KM_H,
                        onClick = { viewModel.updateWindSpeedUnit(WindSpeedUnit.KM_H) },
                        label = { Text("km/h") }
                    )
                    FilterChip(
                        selected = prefs.windSpeedUnit == WindSpeedUnit.M_S,
                        onClick = { viewModel.updateWindSpeedUnit(WindSpeedUnit.M_S) },
                        label = { Text("m/s") }
                    )
                    FilterChip(
                        selected = prefs.windSpeedUnit == WindSpeedUnit.MPH,
                        onClick = { viewModel.updateWindSpeedUnit(WindSpeedUnit.MPH) },
                        label = { Text("mph") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsCard(title = "Precipitation Unit") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = prefs.precipitationUnit == PrecipitationUnit.MM,
                        onClick = { viewModel.updatePrecipitationUnit(PrecipitationUnit.MM) },
                        label = { Text("Millimeters (mm)") }
                    )
                    FilterChip(
                        selected = prefs.precipitationUnit == PrecipitationUnit.INCH,
                        onClick = { viewModel.updatePrecipitationUnit(PrecipitationUnit.INCH) },
                        label = { Text("Inches (in)") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsCard(title = "Appearance Mode") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = prefs.themeMode == ThemeMode.SYSTEM,
                        onClick = { viewModel.updateThemeMode(ThemeMode.SYSTEM) },
                        label = { Text("System") }
                    )
                    FilterChip(
                        selected = prefs.themeMode == ThemeMode.DARK,
                        onClick = { viewModel.updateThemeMode(ThemeMode.DARK) },
                        label = { Text("Midnight Dark") }
                    )
                    FilterChip(
                        selected = prefs.themeMode == ThemeMode.LIGHT,
                        onClick = { viewModel.updateThemeMode(ThemeMode.LIGHT) },
                        label = { Text("Ivory Light") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsCard(title = "Severe Atmospheric Alerts") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Periodic Severe Notifications",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Receive background updates for critical precipitation, heat, wind, and air quality advisories.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = prefs.notificationsEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.updateNotificationsEnabled(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                onClick = onNavigateToAbout
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "About LifePulse & Open-Meteo Attribution",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
