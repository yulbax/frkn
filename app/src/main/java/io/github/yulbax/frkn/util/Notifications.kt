package io.github.yulbax.frkn.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun Context.createNotificationChannel(id: String, name: String, importance: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(NotificationChannel(id, name, importance))
    }
}
