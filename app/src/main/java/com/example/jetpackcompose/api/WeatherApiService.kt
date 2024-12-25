package com.example.jetpackcompose.api

import android.util.Log
import com.example.jetpackcompose.data.ForecastData
import com.example.jetpackcompose.data.ForecastItem
import com.example.jetpackcompose.data.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Service object for handling API calls to OpenWeatherMap.
 * Provides methods to fetch current weather and forecast data.
 */

object WeatherApiService {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    // HTTP Client for API requests
    private val client = OkHttpClient.Builder().build()

    // Retrofit instance for API communication
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // API interface for weather endpoints
    private val api = retrofit.create(WeatherApi::class.java)

    /**
     * Interface defining API endpoints for weather and forecast data.
     */

    interface WeatherApi {
        /**
         * Fetches the current weather data for a specific city.
         * @param city The name of the city.
         * @param apiKey The API key for authentication.
         * @param units Units for temperature (default: metric).
         * @return Response containing [WeatherData].
         */

        @GET("weather")
        suspend fun fetchWeather(
            @Query("q") city: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric"
        ): retrofit2.Response<WeatherData>

        /**
         * Fetches the weather forecast for a specific city.
         * @param city The name of the city.
         * @param apiKey The API key for authentication.
         * @param units Units for temperature (default: metric).
         * @return Response containing [ForecastData].
         */

        @GET("forecast")
        suspend fun fetchForecast(
            @Query("q") city: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric"
        ): retrofit2.Response<ForecastData>
    }

    /**
     * Fetches current weather data for the specified city.
     * Includes error handling and logging for better debugging.
     * @param city The name of the city.
     * @param apiKey The API key for authentication.
     * @return A [WeatherData] object or null if an error occurs.
     */
    suspend fun fetchWeather(city: String, apiKey: String): WeatherData? {
        return try {
            withContext(Dispatchers.IO) { // Use IO dispatcher for network operations
                val response = api.fetchWeather(city, apiKey)
                if (response.isSuccessful) {
                    response.body() // Return the parsed WeatherData
                } else {
                    Log.e("WeatherApiService", "Failed to fetch weather: ${response.code()} - ${response.message()}")
                    null // Log error and return null
                }
            }
        } catch (e: Exception) {
            Log.e("WeatherApiService", "Error fetching weather: ${e.message}", e)
            null // Log exception and return null
        }
    }

    /**
     * Fetches weather forecast data for the specified city.
     * Includes error handling and logging for better debugging.
     * @param city The name of the city.
     * @param apiKey The API key for authentication.
     * @return A list of [ForecastItem] objects or null if an error occurs.
     */
    suspend fun fetchForecast(city: String, apiKey: String): List<ForecastItem>? {
        return try {
            withContext(Dispatchers.IO) { // Use IO dispatcher for network operations
                val response = api.fetchForecast(city, apiKey)
                if (response.isSuccessful) {
                    response.body()?.list // Return the list of forecast items
                } else {
                    Log.e("WeatherApiService", "Failed to fetch forecast: ${response.code()} - ${response.message()}")
                    null // Log error and return null
                }
            }
        } catch (e: Exception) {
            Log.e("WeatherApiService", "Error fetching forecast: ${e.message}", e)
            null // Log exception and return null
        }
    }
}
