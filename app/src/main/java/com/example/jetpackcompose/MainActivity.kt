package com.example.jetpackcompose

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcompose.viewmodel.WeatherViewModel
import com.example.jetpackcompose.ui.WeatherApp
import com.example.jetpackcompose.viewmodel.PopupServiceManager

/**
 * The MainActivity serves as the entry point of the application.
 * It initializes the PopupService for managing notifications and sets up the main UI using Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    // Manager to handle popup notifications and associated permissions
    private val popupServiceManager = PopupServiceManager(this)

    /**
     * Called when the activity is first created.
     * Sets up the UI and manages service permissions.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handles permissions and starts the PopupService
        handlePopupService()

        // Sets the content of the activity to the WeatherApp composable
        setContent {
            val viewModel: WeatherViewModel = viewModel() // Obtain the ViewModel instance
            WeatherApp(viewModel) // Pass the ViewModel to the WeatherApp UI
        }
    }

    /**
     * Handles the initialization of the PopupService.
     * Requests notification permissions on devices running Android TIRAMISU (API 33) or higher.
     */
    private fun handlePopupService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            popupServiceManager.requestPermission()
        } else {
            popupServiceManager.startPopupService()
        }
    }
}
