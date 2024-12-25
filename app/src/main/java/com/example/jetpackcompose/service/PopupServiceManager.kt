package com.example.jetpackcompose.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import com.example.jetpackcompose.service.PopupService

/**
 * Manages the lifecycle and permissions for the PopupService.
 * Ensures that notification permissions are handled and the service is started correctly.
 */
class PopupServiceManager(private val context: Context) {

    /**
     * Requests permission for notifications. If granted, the PopupService is started.
     * Displays a toast message if the permission is denied.
     */
    fun requestPermission() {
        val requestPermissionLauncher =
            (context as ComponentActivity).registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startPopupService()
                } else {
                    Toast.makeText(
                        context,
                        "Permission denied, notifications won't work",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startPopupService() // Start the service directly for lower Android versions
        }
    }

    /**
     * Starts the PopupService as a foreground service.
     * Ensures that the service is started correctly based on the provided context.
     */
    fun startPopupService() {
        try {
            val serviceIntent = Intent(context, PopupService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Failed to start the service: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
