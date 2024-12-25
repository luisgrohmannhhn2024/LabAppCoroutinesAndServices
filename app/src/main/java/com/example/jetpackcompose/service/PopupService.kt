package com.example.jetpackcompose.service

import android.app.*
import android.content.*
import android.os.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.PendingIntent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.jetpackcompose.MainActivity
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.jetpackcompose.ui.views.dataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * PopupService handles periodic notifications based on user preferences.
 * It functions as a Foreground Service and dynamically updates its behavior.
 */
class PopupService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var delayMillis: Long = -1L
    private var i = 0 // Counter for notifications
    private val dataStore by lazy { applicationContext.dataStore }
    private var isNotificationEnabled: Boolean = false

    // BroadcastReceiver to dynamically update the timer option
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val newTimerOption = intent?.getStringExtra("timer_option") ?: "Deactivated"
            updateTimerOption(newTimerOption)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Start the service with an initial placeholder notification
        val initialNotification = getNotification("Initializing...")
        startForeground(1, initialNotification)

        // Register the BroadcastReceiver for dynamic updates
        registerUpdateReceiver()

        // Initialize the timer settings from persistent storage
        initializeTimerFromSettings()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove all callbacks and unregister the receiver
        handler.removeCallbacks(showNotificationRunnable)
        unregisterReceiver(updateReceiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Restart the periodic notification task if a delay is set
        if (delayMillis != -1L) {
            handler.removeCallbacks(showNotificationRunnable)
            handler.post(showNotificationRunnable)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Runnable to send periodic notifications
    private val showNotificationRunnable = object : Runnable {
        override fun run() {
            if (isNotificationEnabled) {
                sendNotification("Hello World $i") // Incremental notification message
                i++
            }
            handler.postDelayed(this, delayMillis)
        }
    }

    /**
     * Updates the timer settings based on the user's choice.
     * @param option The selected timer option as a string.
     */
    private fun updateTimerOption(option: String) {
        delayMillis = timerOptionToMillis(option)
        isNotificationEnabled = delayMillis != -1L
        handler.removeCallbacks(showNotificationRunnable)

        if (delayMillis == -1L) {
            stopSelf() // Stop service if "Deactivated" is selected
        } else {
            handler.postDelayed(showNotificationRunnable, delayMillis)
        }

    }

    /**
     * Fetches the user's saved timer option from persistent storage.
     * @return The timer option as a string.
     */
    private suspend fun fetchTimerOptionFromSettings(): String {
        val key = stringPreferencesKey("timer_option_key")
        return dataStore.data.map { preferences ->
            preferences[key] ?: "Deactivated"
        }.first()
    }

    /**
     * Registers the BroadcastReceiver for dynamic updates.
     */
    private fun registerUpdateReceiver() {
        try {
            ContextCompat.registerReceiver(
                this,
                updateReceiver,
                IntentFilter("com.example.jetpackcompose.UPDATE_TIMER"),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        } catch (e: Exception) {
            Log.e("PopupService", "Error registering receiver: ${e.message}", e)
        }
    }

    /**
     * Converts the selected timer option into milliseconds.
     * @param option The selected timer option as a string.
     * @return The delay in milliseconds, or -1 if the option is invalid.
     */
    private fun timerOptionToMillis(option: String): Long {
        return when (option) {
            "10s" -> 10_000L
            "30s" -> 30_000L
            "60s" -> 60_000L
            "30 min" -> 30 * 60 * 1000L
            "60 min" -> 60 * 60 * 1000L
            else -> -1L
        }
    }

    /**
     * Initializes the timer settings by loading them from persistent storage.
     */
    private fun initializeTimerFromSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val timerOption = fetchTimerOptionFromSettings()
                delayMillis = timerOptionToMillis(timerOption)

                if (delayMillis != -1L) {
                    isNotificationEnabled = true
                    handler.post(showNotificationRunnable)
                }
            } catch (e: Exception) {
                Log.e("PopupService", "Error initializing timer: ${e.message}", e)
            }
        }
    }

    /**
     * Sends a notification to the user.
     * @param message The content of the notification.
     */
    private fun sendNotification(message: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("PopupService", "Notification permission not granted")
            return
        }

        val notificationManager = NotificationManagerCompat.from(this)
        val notification = getNotification(message)
        notificationManager.notify(1, notification)
    }

    /**
     * Constructs a notification object with the specified content.
     * @param contentText The content text to display in the notification.
     * @return The constructed notification.
     */
    private fun getNotification(contentText: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "popup_service_channel")
            .setContentTitle("Popup Service")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    /**
     * Creates a notification channel for Android O+ devices.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "popup_service_channel",
                "Popup Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications from Popup Service"
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
