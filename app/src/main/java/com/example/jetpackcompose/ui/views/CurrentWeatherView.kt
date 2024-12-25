package com.example.jetpackcompose.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jetpackcompose.data.WeatherData
import com.example.jetpackcompose.storage.Keys
import androidx.compose.ui.platform.LocalContext
import com.example.jetpackcompose.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcompose.ui.components.SearchBarSample

/**
 * Composable function to display the current weather information for a specified location.
 * Includes a search bar for entering city names and shows detailed weather metrics.
 *
 * @param currentWeather The current weather data to display.
 * @param iconUrl The URL for the weather icon.
 */
@Composable
fun CurrentWeatherView(currentWeather: WeatherData?, iconUrl: String?) {

    // ViewModel instance for managing weather-related data
    val weatherViewModel: WeatherViewModel = viewModel()
    val currentWeather by weatherViewModel.currentWeather.collectAsState()
    val iconUrl by weatherViewModel.iconUrl.collectAsState()

    // Mutable state variables to store hometown and API key
    var hometown by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    val errorMessage by weatherViewModel.errorMessage.collectAsState()

    // Context for accessing DataStore
    val context = LocalContext.current

    /**
     * LaunchedEffect block to load hometown and API key from DataStore
     * and fetch current weather data based on the saved hometown.
     */
    LaunchedEffect(Unit) {
        context.dataStore.data.collect { preferences ->
            hometown = preferences[Keys.HOMETOWN_KEY] ?: ""
            apiKey = preferences[Keys.API_TOKEN_KEY] ?: ""
            if (hometown.isNotEmpty()) {
                weatherViewModel.fetchWeatherData(hometown, apiKey)
            }
        }
    }

    // Mutable state to manage the search query
    val searchQuery = rememberSaveable { mutableStateOf("") }

    // UI layout for the search bar and weather details
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Search bar for entering city names
        SearchBarSample(
            selectedMenu = "Home",
            apiKey = apiKey,
            onQueryChanged = { query ->
                searchQuery.value = query
                if (query.isNotEmpty()) {
                    weatherViewModel.fetchWeatherData(query, apiKey)
                }
            }
        )
    }

    // Display error message if any
    errorMessage?.let {
        Text(
            text = it,
            color = Color.Red,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp),
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }

    // Show weather details if search query or hometown is available
    if (searchQuery.value.isNotEmpty() || hometown.isNotEmpty()) {
        currentWeather?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0xFFBBDEFB), RoundedCornerShape(32.dp))
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "${it.name}, ${it.sys.country}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp),
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    iconUrl?.let {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = "Weather icon",
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }

                /**
                 * Helper function to create a row displaying weather information.
                 *
                 * @param label The label for the weather metric.
                 * @param value The value of the weather metric.
                 */
                @Composable
                fun createWeatherInfoRow(label: String, value: String) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                                .padding(start = 32.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 22.sp),
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(start = 45.dp)
                        ) {
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 22.sp),
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Display various weather metrics
                createWeatherInfoRow("Description:", it.weather[0].description)
                Spacer(modifier = Modifier.height(8.dp))
                createWeatherInfoRow("Temp.:", "${it.main.temp}°C")
                Spacer(modifier = Modifier.height(8.dp))
                createWeatherInfoRow("Feels Like:", "${it.main.feels_like}°C")
                Spacer(modifier = Modifier.height(8.dp))
                createWeatherInfoRow("Humidity:", "${it.main.humidity}%")
                Spacer(modifier = Modifier.height(8.dp))
                createWeatherInfoRow("Wind:", "${it.wind.speed} m/s")
                Spacer(modifier = Modifier.height(8.dp))

                val sunriseTime = convertUnixToTime(it.sys.sunrise)
                val sunsetTime = convertUnixToTime(it.sys.sunset)

                createWeatherInfoRow("Sunrise:", sunriseTime)
                Spacer(modifier = Modifier.height(4.dp))
                createWeatherInfoRow("Sunset:", sunsetTime)
            }
        } ?: Text(
            text = "No current weather data available.",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
            color = Color.Gray,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Set your hometown in settings",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp),
            color = Color.Gray,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * Converts a Unix timestamp to a formatted time string.
 *
 * @param timestamp The Unix timestamp to convert.
 * @return A formatted string representing the time.
 */
fun convertUnixToTime(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}
