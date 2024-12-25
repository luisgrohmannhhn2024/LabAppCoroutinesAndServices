package com.example.jetpackcompose.storage

import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Defines keys used for storing preferences in the DataStore.
 */
object Keys {
    /**
     * Key for storing the user's hometown in the DataStore.
     */
    val HOMETOWN_KEY = stringPreferencesKey("hometown_key")

    /**
     * Key for storing the API token required for authentication.
     */
    val API_TOKEN_KEY = stringPreferencesKey("api_token_key")

    /**
     * Key for storing the timer option used for notifications.
     */
    val TIMER_OPTION_KEY = stringPreferencesKey("timer_option_key")
}
