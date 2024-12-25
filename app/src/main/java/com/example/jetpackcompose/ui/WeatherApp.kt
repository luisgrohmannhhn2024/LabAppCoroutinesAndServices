package com.example.jetpackcompose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.jetpackcompose.viewmodel.WeatherViewModel
import com.example.jetpackcompose.ui.components.BottomNavBar
import com.example.jetpackcompose.ui.views.CurrentWeatherView
import com.example.jetpackcompose.ui.views.ForecastWeatherView
import com.example.jetpackcompose.ui.views.SettingsView

/**
 * Main composable function for the WeatherApp.
 * This function provides navigation and displays different views like CurrentWeatherView,
 * ForecastWeatherView, and SettingsView based on user selection.
 *
 * @param viewModel The [WeatherViewModel] instance for managing weather-related data.
 */
@Composable
fun WeatherApp(viewModel: WeatherViewModel) {
    // Collect current weather, forecast, and icon URL states from the ViewModel
    val currentWeather by viewModel.currentWeather.collectAsState()
    val forecast by viewModel.forecast.collectAsState()
    val iconUrl by viewModel.iconUrl.collectAsState()

    // State for tracking the currently selected navigation item
    var selectedItem by remember { mutableStateOf(0) }

    // Define colors for the UI layout
    val upperHalfColor = Color.White
    val lowerHalfColor = Color(0xFF1E88E5)

    // Main container for the app's UI
    Box(modifier = Modifier.fillMaxSize()) {
        // Background layout divided into two colored halves
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(upperHalfColor)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(lowerHalfColor)
            )
        }

        // Foreground layout with system padding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .background(upperHalfColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp), // Reserve space for the BottomNavBar
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Display content based on the selected navigation item
                when (selectedItem) {
                    0 -> CurrentWeatherView(currentWeather = currentWeather, iconUrl = iconUrl)
                    1 -> ForecastWeatherView(forecast = forecast)
                    2 -> SettingsView(onSave = { selectedItem = 0 })
                }
            }

            // Bottom navigation bar for switching between views
            BottomNavBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it },
                modifier = Modifier.align(Alignment.BottomCenter),
                backgroundColor = lowerHalfColor
            )
        }
    }
}
