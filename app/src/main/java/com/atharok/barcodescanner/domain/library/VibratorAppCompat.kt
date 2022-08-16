/*
 * Barcode Scanner
 * Copyright (C) 2021  Atharok
 *
 * This file is part of Barcode Scanner.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.atharok.barcodescanner.domain.library

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi

class VibratorAppCompat(private val context: Context) {

    companion object {
        private const val VIBRATE_DURATION = 500L
    }

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            createVibratorManager().defaultVibrator
        } else {
            createVibrator()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun createVibratorManager(): VibratorManager {
        return context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    }

    @Suppress("DEPRECATION")
    private fun createVibrator(): Vibrator {
        return context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibrate() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            @Suppress("DEPRECATION")
            vibrator.vibrate(VIBRATE_DURATION)
        }
    }
}