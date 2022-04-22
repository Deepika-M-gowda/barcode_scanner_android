package com.atharok.barcodescanner.data.sensor

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager

fun createVibrator(application: Application): Vibrator {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}