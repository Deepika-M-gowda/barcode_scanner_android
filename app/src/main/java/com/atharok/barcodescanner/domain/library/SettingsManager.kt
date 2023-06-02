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
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.preference.PreferenceManager
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.domain.entity.barcode.QrCodeErrorCorrectionLevel

class SettingsManager(private val context: Context) {

    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    // Appearance
    private val colorKey = context.getString(R.string.preferences_color_key)
    private val themeKey = context.getString(R.string.preferences_theme_key)
    private var color = prefs.getString(colorKey, getDefaultColorKey())
    private var theme = prefs.getString(themeKey, "system")

    // Scan
    private val useCameraXApiKey = context.getString(R.string.preferences_switch_scan_use_camera_x_api_key)
    private val searchOnApiKey = context.getString(R.string.preferences_switch_scan_search_on_api_key)
    private val vibrateScanKey = context.getString(R.string.preferences_switch_scan_vibrate_key)
    private val bipScanKey = context.getString(R.string.preferences_switch_scan_bip_key)
    private val autoScreenRotationScanDisabledKey = context.getString(R.string.preferences_switch_scan_screen_rotation_key)
    private val copyBarcodeScanKey = context.getString(R.string.preferences_switch_scan_barcode_copied_key)
    private val addBarcodeToHistoryScanKey = context.getString(R.string.preferences_switch_scan_add_barcode_to_the_history_key)
    private val defaultZoomValueKey = context.getString(R.string.preferences_seek_bar_camera_default_zoom_value_key)

    var useCameraXApi = prefs.getBoolean(useCameraXApiKey, true)
        private set
    var useSearchOnApi = prefs.getBoolean(searchOnApiKey, true)
        private set
    var useVibrateScan = prefs.getBoolean(vibrateScanKey, false)
        private set
    var useBipScan = prefs.getBoolean(bipScanKey, false)
        private set
    var isAutoScreenRotationScanDisabled = prefs.getBoolean(autoScreenRotationScanDisabledKey, true)
        private set
    var shouldCopyBarcodeScan = prefs.getBoolean(copyBarcodeScanKey, false)
        private set
    var shouldAddBarcodeScanToHistory = prefs.getBoolean(addBarcodeToHistoryScanKey, true)
        private set

    // Barcode Generation
    private val errorCorrectionLevelKey = context.getString(R.string.preferences_barcode_generation_error_correction_level_key)
    private var errorCorrectionLevelEntry = prefs.getString(errorCorrectionLevelKey, "low")

    // Search
    private val searchEngineKey = context.getString(R.string.preferences_search_engine_key)
    private var defaultSearchEngine = prefs.getString(searchEngineKey, "google")

    fun reload() {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        color = prefs.getString(colorKey, getDefaultColorKey())
        theme = prefs.getString(themeKey, "system")
        useCameraXApi = prefs.getBoolean(useCameraXApiKey, true)
        useSearchOnApi = prefs.getBoolean(searchOnApiKey, true)
        useVibrateScan = prefs.getBoolean(vibrateScanKey, false)
        useBipScan = prefs.getBoolean(bipScanKey, false)
        isAutoScreenRotationScanDisabled = prefs.getBoolean(autoScreenRotationScanDisabledKey, true)
        shouldCopyBarcodeScan = prefs.getBoolean(copyBarcodeScanKey, false)
        shouldAddBarcodeScanToHistory = prefs.getBoolean(addBarcodeToHistoryScanKey, true)
        errorCorrectionLevelEntry = prefs.getString(errorCorrectionLevelKey, "low")
        defaultSearchEngine = prefs.getString(searchEngineKey, "google")
    }

    fun getTheme(): Int {
        return if(useDarkTheme()) {
            //R.style.DarkTheme
            when(color){
                "material_you" -> if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) R.style.MaterialYouDarkTheme else R.style.BlueDarkTheme
                "blue" -> R.style.BlueDarkTheme
                "orange" -> R.style.OrangeDarkTheme
                "green" -> R.style.GreenDarkTheme
                "red" -> R.style.RedDarkTheme
                "purple" -> R.style.PurpleDarkTheme
                else -> R.style.BlueDarkTheme
            }
        } else {
            //R.style.LightTheme
            when(color){
                "material_you" -> if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) R.style.MaterialYouLightTheme else R.style.BlueLightTheme
                "blue" -> R.style.BlueLightTheme
                "orange" -> R.style.OrangeLightTheme
                "green" -> R.style.GreenLightTheme
                "red" -> R.style.RedLightTheme
                "purple" -> R.style.PurpleLightTheme
                else -> R.style.BlueLightTheme
            }
        }
    }

    fun getQrCodeErrorCorrectionLevel(): QrCodeErrorCorrectionLevel = when(errorCorrectionLevelEntry){
        "low" -> QrCodeErrorCorrectionLevel.L
        "medium" -> QrCodeErrorCorrectionLevel.M
        "quartile" -> QrCodeErrorCorrectionLevel.Q
        "high" -> QrCodeErrorCorrectionLevel.H
        else -> QrCodeErrorCorrectionLevel.L
    }

    fun getSearchEngineUrl(contents: String): String = when(defaultSearchEngine){
        "google" -> context.getString(R.string.search_engine_google_url, contents)
        "bing" -> context.getString(R.string.search_engine_bing_url, contents)
        "duckduckgo" -> context.getString(R.string.search_engine_duck_duck_go_url, contents)
        "startpage" -> context.getString(R.string.search_engine_startpage_url, contents)
        "bravesearch" -> context.getString(R.string.search_engine_brave_search_url, contents)
        "qwant" -> context.getString(R.string.search_engine_qwant_url, contents)
        "ecosia" -> context.getString(R.string.search_engine_ecosia_url, contents)
        "lilo" -> context.getString(R.string.search_engine_lilo_url, contents)
        else -> context.getString(R.string.search_engine_google_url, contents)
    }

    fun useDarkTheme(): Boolean = when(theme){
        "system" -> isDarkThemeSystemOn()
        "light" -> false
        "dark" -> true
        else -> isDarkThemeSystemOn()
    }

    private fun isDarkThemeSystemOn(): Boolean =
        context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES

    private fun getDefaultColorKey(): String =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) "material_you" else "blue"

    fun getDefaultZoomValue(): Int = prefs.getInt(defaultZoomValueKey, 50)
}