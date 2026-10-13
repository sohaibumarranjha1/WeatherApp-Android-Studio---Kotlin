package com.lifepulse.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Forecast : Screen("forecast", "Forecast", Icons.Default.DateRange)
    object AirQuality : Screen("air_quality", "Air Quality", Icons.Default.Air)
    object SavedCities : Screen("saved_cities", "Saved", Icons.Default.Bookmark)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object About : Screen("about", "About", Icons.Default.Info)
}

val bottomNavScreens = listOf(
    Screen.Home,
    Screen.Forecast,
    Screen.AirQuality,
    Screen.SavedCities,
    Screen.Settings
)
