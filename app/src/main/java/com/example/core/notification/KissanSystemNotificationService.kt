package com.example.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

/**
 * Dispatches real Android OS System Notifications to the Android system tray/status bar.
 * Supports:
 * - High priority channels with sound and vibration
 * - Deep-link pending intent back into MainActivity with route target
 * - Safe runtime permission check for Android 13+ (POST_NOTIFICATIONS)
 */
class KissanSystemNotificationService(private val context: Context) {

  companion object {
    const val CHANNEL_CRITICAL_ALERTS = "kissan_critical_alerts"
    const val CHANNEL_MARKET_UPDATES = "kissan_market_updates"
    const val CHANNEL_FARM_ADVISORY = "kissan_farm_advisory"

    private const val TAG = "KissanNotification"
    private var notificationIdCounter = 1000
  }

  init {
    createNotificationChannels()
  }

  private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        ?: return

      // 1. Critical Farm Alerts (High Priority, Sound, Vibration, Heads-up)
      val criticalChannel = NotificationChannel(
        CHANNEL_CRITICAL_ALERTS,
        "Critical Farm & Irrigation Alerts",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Urgent alerts regarding water stress, frost, disease outbreak, and sensor anomalies"
        enableLights(true)
        lightColor = Color.RED
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 350, 200, 350)
        setShowBadge(true)
      }

      // 2. Mandi & Market Updates
      val marketChannel = NotificationChannel(
        CHANNEL_MARKET_UPDATES,
        "Mandi Prices & Market Trends",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Favorable mandi selling windows and commodity price surges"
        enableLights(true)
        lightColor = Color.GREEN
        setShowBadge(true)
      }

      // 3. Farm Advisory
      val advisoryChannel = NotificationChannel(
        CHANNEL_FARM_ADVISORY,
        "Daily Farm & Weather Advisory",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Daily 7-day action items and weather spraying window recommendations"
        setShowBadge(true)
      }

      notificationManager.createNotificationChannels(listOf(criticalChannel, marketChannel, advisoryChannel))
    }
  }

  /**
   * Posts a real Android system notification in the system drawer/status bar.
   * Returns true if posted successfully, false otherwise.
   */
  fun postSystemNotification(
    title: String,
    message: String,
    channelId: String = CHANNEL_CRITICAL_ALERTS,
    destinationRoute: String? = null,
    notificationId: Int = ++notificationIdCounter
  ): Boolean {
    // Check permission for Android 13+ (API 33+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
      if (!hasPermission) {
        Log.w(TAG, "Cannot post system notification: POST_NOTIFICATIONS permission not granted yet.")
        return false
      }
    }

    try {
      createNotificationChannels()

      // Create PendingIntent to launch MainActivity when tapped
      val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        if (destinationRoute != null) {
          putExtra("TARGET_DESTINATION", destinationRoute)
        }
      }

      val pendingIntent = PendingIntent.getActivity(
        context,
        notificationId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(
          if (channelId == CHANNEL_CRITICAL_ALERTS) NotificationCompat.PRIORITY_HIGH
          else NotificationCompat.PRIORITY_DEFAULT
        )
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .setColor(Color.rgb(31, 122, 90)) // KisanEmerald

      NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
      Log.i(TAG, "System notification dispatched to OS tray: $title (id: $notificationId)")
      return true
    } catch (e: SecurityException) {
      Log.e(TAG, "SecurityException while posting notification: ${e.message}")
      return false
    } catch (e: Exception) {
      Log.e(TAG, "Error posting notification: ${e.message}")
      return false
    }
  }
}
