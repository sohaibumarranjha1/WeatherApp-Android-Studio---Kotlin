package com.lifepulse.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lifepulse.LifePulseApplication
import com.lifepulse.ui.about.AboutScreen
import com.lifepulse.ui.airquality.AirQualityScreen
import com.lifepulse.ui.airquality.AirQualityViewModel
import com.lifepulse.ui.forecast.ForecastScreen
import com.lifepulse.ui.forecast.ForecastViewModel
import com.lifepulse.ui.home.HomeScreen
import com.lifepulse.ui.home.HomeViewModel
import com.lifepulse.ui.savedcities.SavedCitiesScreen
import com.lifepulse.ui.savedcities.SavedCitiesViewModel
import com.lifepulse.ui.search.SearchScreen
import com.lifepulse.ui.search.SearchViewModel
import com.lifepulse.ui.settings.SettingsScreen
import com.lifepulse.ui.settings.SettingsViewModel
import com.lifepulse.ui.theme.LifePulseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifePulseMainApp() {
    val app = LifePulseApplication.instance
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(app.userPreferencesRepository)
    )
    val userPrefs by settingsViewModel.userPreferences.collectAsState()

    LifePulseTheme(themeMode = userPrefs.themeMode) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

        Scaffold(
            topBar = {
                if (currentRoute != Screen.Home.route && currentRoute != Screen.Search.route) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "LIFEPULSE",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Locations"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            },
            bottomBar = {
                if (currentRoute != Screen.Search.route && currentRoute != Screen.About.route) {
                    val outlineColor = MaterialTheme.colorScheme.outline
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        modifier = Modifier.drawWithContent {
                            drawContent()
                            drawLine(
                                color = outlineColor,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    ) {
                        bottomNavScreens.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                                label = {
                                    Text(
                                        text = screen.title,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Home.route) {
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModel.Factory(app.weatherRepository, app.userPreferencesRepository)
                    )
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToSearch = { navController.navigate(Screen.Search.route) }
                    )
                }

                composable(Screen.Search.route) {
                    val searchViewModel: SearchViewModel = viewModel(
                        factory = SearchViewModel.Factory(app.weatherRepository)
                    )
                    SearchScreen(
                        viewModel = searchViewModel,
                        onCitySelected = {
                            navController.popBackStack(Screen.Home.route, inclusive = false)
                        }
                    )
                }

                composable(Screen.Forecast.route) {
                    val forecastViewModel: ForecastViewModel = viewModel(
                        factory = ForecastViewModel.Factory(app.weatherRepository, app.userPreferencesRepository)
                    )
                    ForecastScreen(viewModel = forecastViewModel)
                }

                composable(Screen.AirQuality.route) {
                    val airQualityViewModel: AirQualityViewModel = viewModel(
                        factory = AirQualityViewModel.Factory(app.weatherRepository, app.userPreferencesRepository)
                    )
                    AirQualityScreen(viewModel = airQualityViewModel)
                }

                composable(Screen.SavedCities.route) {
                    val savedCitiesViewModel: SavedCitiesViewModel = viewModel(
                        factory = SavedCitiesViewModel.Factory(app.weatherRepository)
                    )
                    SavedCitiesScreen(
                        viewModel = savedCitiesViewModel,
                        onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                        onCitySelected = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onNavigateToAbout = { navController.navigate(Screen.About.route) }
                    )
                }

                composable(Screen.About.route) {
                    AboutScreen()
                }
            }
        }
    }
}
