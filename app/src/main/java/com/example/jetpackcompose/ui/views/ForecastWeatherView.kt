package com.example.jetpackcompose.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcompose.data.ForecastItem
import com.example.jetpackcompose.storage.Keys
import com.example.jetpackcompose.ui.components.SearchBarSample
import com.example.jetpackcompose.ui.components.WeatherCard
import com.example.jetpackcompose.viewmodel.WeatherViewModel

/**
 * Displays a detailed weather forecast view.
 * This function includes a search bar for querying forecast data and a list of weather cards.
 * It retrieves the user's hometown and API key from the DataStore and fetches weather data accordingly.
 *
 * @param forecast A list of forecast items to be displayed.
 */
@Composable
fun ForecastWeatherView(forecast: List<ForecastItem>) {
    val context = LocalContext.current
    var hometown by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    val weatherViewModel: WeatherViewModel = viewModel()
    val errorMessage by weatherViewModel.errorMessage.collectAsState()
    val forecastData by weatherViewModel.forecast.collectAsState()

    // Load hometown and API key from the DataStore, then fetch forecast data
    LaunchedEffect(Unit) {
        context.dataStore.data.collect { preferences ->
            hometown = preferences[Keys.HOMETOWN_KEY] ?: ""
            apiKey = preferences[Keys.API_TOKEN_KEY] ?: ""

            if (hometown.isNotEmpty() && apiKey.isNotEmpty()) {
                // Attempt to fetch forecast data from API
                try {
                    weatherViewModel.fetchForecastData(hometown, apiKey)
                } catch (e: Exception) {
                    // Handle errors during data fetch
                    println("Error fetching forecast data: ${e.message}")
                }
            }
        }
    }

    val searchQuery = rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            SearchBarSample(
                selectedMenu = "Forecast",
                apiKey = apiKey,
                onQueryChanged = { query ->
                    searchQuery.value = query
                    if (query.isNotEmpty()) {
                        try {
                            weatherViewModel.fetchForecastData(query, apiKey)
                        } catch (e: Exception) {
                            // Handle errors during query-based API fetch
                            println("Error fetching forecast for query: ${e.message}")
                        }
                    } else {
                        if (hometown.isNotEmpty() && apiKey.isNotEmpty()) {
                            try {
                                weatherViewModel.fetchForecastData(hometown, apiKey)
                            } catch (e: Exception) {
                                println("Error fetching forecast for hometown: ${e.message}")
                            }
                        }
                    }
                }
            )
        }

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 25.sp),
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }

        if (searchQuery.value.isEmpty() && hometown.isEmpty()) {
            Text(
                text = "Set your hometown in settings",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 24.sp,
                    color = Color.Gray
                ),
                modifier = Modifier.padding(16.dp)
            )
        } else if (forecastData.isNotEmpty()) {
            Text(
                text = "Forecast for ${searchQuery.value.takeIf { it.isNotEmpty() } ?: hometown}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 28.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .align(Alignment.CenterHorizontally)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(forecastData) { forecastItem ->
                    WeatherCard(forecastItem = forecastItem)
                }
            }
        }
    }
}
