package com.example.jetpackcompose.data

/**
 * Represents the complete weather forecast response from the API.
 * @property cod The status code returned by the API.
 * @property message Any message or additional information from the API.
 * @property cnt The number of forecast items returned.
 * @property list The list of [ForecastItem] objects representing each forecast entry.
 */
data class ForecastData(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>
)

/**
 * Represents an individual forecast entry in the weather forecast.
 * @property dt The timestamp of the forecast in Unix format.
 * @property main The main weather details, such as temperature and pressure.
 * @property weather A list of [Weather] objects describing the weather conditions.
 * @property clouds Cloud data, such as cloud coverage percentage.
 * @property wind Wind details, such as speed and direction.
 * @property visibility Visibility in meters.
 * @property pop Probability of precipitation (percentage as a double).
 * @property sys Additional system data related to the forecast.
 * @property dt_txt A human-readable date-time string.
 * @property rain Optional rain data, showing the amount of rain in millimeters.
 */
data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: ForecastSys,
    val dt_txt: String,
    val rain: Rain? = null
)

/**
 * Represents additional system data for the forecast.
 * @property pod The part of the day (e.g., "d" for day or "n" for night).
 */
data class ForecastSys(val pod: String)

/**
 * Represents rain data, if available.
 * @property `3h` The amount of rain in millimeters in the last 3 hours (nullable).
 */
data class Rain(val `3h`: Double? = null)
