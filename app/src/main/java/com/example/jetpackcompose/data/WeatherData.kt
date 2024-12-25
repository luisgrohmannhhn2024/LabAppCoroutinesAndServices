package com.example.jetpackcompose.data

/**
 * Represents the current weather data returned by the API.
 * @property coord Geographical coordinates of the location.
 * @property weather A list of weather conditions (e.g., description, icon).
 * @property base Internal parameter for the API.
 * @property main Main weather information (e.g., temperature, humidity).
 * @property visibility Visibility in meters.
 * @property wind Wind data such as speed and direction.
 * @property clouds Cloud coverage data.
 * @property dt Timestamp of the data in Unix format.
 * @property sys System data (e.g., country, sunrise, sunset).
 * @property timezone The timezone offset from UTC in seconds.
 * @property id Unique identifier for the city.
 * @property name The name of the city.
 * @property cod Status code from the API.
 */
data class WeatherData(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

/**
 * Represents geographical coordinates.
 * @property lon Longitude of the location.
 * @property lat Latitude of the location.
 */
data class Coord(val lon: Double, val lat: Double)

/**
 * Represents detailed weather information.
 * @property id Unique identifier for the weather condition.
 * @property main The main weather group (e.g., Rain, Snow, Clear).
 * @property description A more detailed description of the weather.
 * @property icon The ID of the weather icon to display.
 */
data class Weather(val id: Int, val main: String, val description: String, val icon: String)

/**
 * Represents main weather details such as temperature and pressure.
 * @property temp Current temperature in the specified units.
 * @property feels_like Perceived temperature considering humidity and wind.
 * @property temp_min Minimum temperature for the day.
 * @property temp_max Maximum temperature for the day.
 * @property pressure Atmospheric pressure in hPa.
 * @property humidity Humidity percentage.
 */
data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)

/**
 * Represents wind details.
 * @property speed Wind speed in meters per second.
 * @property deg Wind direction in degrees.
 * @property gust Wind gust speed in meters per second (optional).
 */
data class Wind(val speed: Double, val deg: Int, val gust: Double)

/**
 * Represents cloud coverage data.
 * @property all Cloud coverage percentage.
 */
data class Clouds(val all: Int)

/**
 * Represents system-level data related to the weather.
 * @property type Internal parameter for the API.
 * @property id Unique identifier for the weather system.
 * @property country Country code (e.g., DE for Germany).
 * @property sunrise Sunrise time in Unix format.
 * @property sunset Sunset time in Unix format.
 */
data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
