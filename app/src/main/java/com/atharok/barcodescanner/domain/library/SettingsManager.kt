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

class SettingsManager(private val context: Context) {

    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    // Appearance
    private val colorKey = context.getString(R.string.preferences_color_key)
    private val themeKey = context.getString(R.string.preferences_theme_key)
    private var color = prefs.getString(colorKey, getDefaultColorKey())
    private var theme = prefs.getString(themeKey, "system")

    // Scan
    private val searchOnApiKey = context.getString(R.string.preferences_switch_scan_search_on_api_key)
    private val vibrateScanKey = context.getString(R.string.preferences_switch_scan_vibrate_key)
    private val bipScanKey = context.getString(R.string.preferences_switch_scan_bip_key)
    var useSearchOnApiKey = prefs.getBoolean(searchOnApiKey, true)
        private set
    var useVibrateScan = prefs.getBoolean(vibrateScanKey, false)
        private set
    var useBipScan = prefs.getBoolean(bipScanKey, false)
        private set

    // Search
    private val searchEngineKey = context.getString(R.string.preferences_search_engine_key)
    private var defaultSearchEngine = prefs.getString(searchEngineKey, "google")

    fun reload() {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        color = prefs.getString(colorKey, getDefaultColorKey())
        theme = prefs.getString(themeKey, "system")
        useSearchOnApiKey = prefs.getBoolean(searchOnApiKey, true)
        useVibrateScan = prefs.getBoolean(vibrateScanKey, false)
        useBipScan = prefs.getBoolean(bipScanKey, false)
        defaultSearchEngine = prefs.getString(searchEngineKey, "google")
    }

    fun getTheme(): Int {
        return if(useDarkTheme()) {
            //R.style.DarkTheme
            when(color){
                "material_you" -> R.style.MaterialYouDarkTheme
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
                "material_you" -> R.style.MaterialYouLightTheme
                "blue" -> R.style.BlueLightTheme
                "orange" -> R.style.OrangeLightTheme
                "green" -> R.style.GreenLightTheme
                "red" -> R.style.RedLightTheme
                "purple" -> R.style.PurpleLightTheme
                else -> R.style.BlueLightTheme
            }
        }
    }

    fun getSearchEngineUrl(contents: String): String = when(defaultSearchEngine){
        "google" -> context.getString(R.string.search_engine_google_url, contents)
        "bing" -> context.getString(R.string.search_engine_bing_url, contents)
        "duckduckgo" -> context.getString(R.string.search_engine_duck_duck_go_url, contents)
        "startpage" -> context.getString(R.string.search_engine_startpage_url, contents)
        "qwant" -> context.getString(R.string.search_engine_qwant_url, contents)
        "ecosia" -> context.getString(R.string.search_engine_ecosia_url, contents)
        "lilo" -> context.getString(R.string.search_engine_lilo_url, contents)
        else -> context.getString(R.string.search_engine_google_url, contents)
    }

    private fun useDarkTheme(): Boolean = when(theme){
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
}