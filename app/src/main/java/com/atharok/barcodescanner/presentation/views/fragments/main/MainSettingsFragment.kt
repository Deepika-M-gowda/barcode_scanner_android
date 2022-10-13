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

package com.atharok.barcodescanner.presentation.views.fragments.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.utils.INTENT_SEARCH_URL
import com.atharok.barcodescanner.common.utils.INTENT_START_ACTIVITY
import com.atharok.barcodescanner.presentation.views.activities.*
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import kotlin.reflect.KClass

class MainSettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPreferencesFromResource(R.xml.root_preferences, null)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            configureChangeLanguagePreference()
        }

        //configureThemePreference()
        configureAboutPreference(R.string.preferences_about_permissions_key, AboutPermissionsDescriptionActivity::class)
        configureAboutPreference(R.string.preferences_about_library_third_key, AboutLibraryThirdActivity::class)
        configureAboutPreference(R.string.preferences_about_bdd_key, AboutBddActivity::class)
        configureSourceCodePreference()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity: Activity = requireActivity()
        if(activity is BaseActivity){
            activity.lockDeviceRotation(false)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {}

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val mActivity = requireActivity()
        if(mActivity is MainActivity){
            when(key){
                getString(R.string.preferences_color_key) -> mActivity.updateTheme()
                getString(R.string.preferences_theme_key) -> mActivity.updateTheme()

                getString(R.string.preferences_switch_scan_vibrate_key),
                getString(R.string.preferences_switch_scan_bip_key),
                getString(R.string.preferences_switch_scan_screen_rotation_key),
                getString(R.string.preferences_switch_scan_search_on_api_key),
                getString(R.string.preferences_search_engine_key)
                -> mActivity.settingsManager.reload()
            }
        }
    }

    private fun configureChangeLanguagePreference(){
        val pref = findPreference(getString(R.string.preferences_languages_key)) as Preference?

        if(pref != null) {
            pref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                startActivity(createActionAppLocaleSettingsIntent())
                true
            }
        }
    }

    private fun createActionAppLocaleSettingsIntent(): Intent = Intent(
        Settings.ACTION_APP_LOCALE_SETTINGS,
        Uri.fromParts("package", requireActivity().packageName, null)
    )

    private fun configureAboutPreference(keyResource: Int, activityKClass: KClass<out Activity>){
        val pref = findPreference(getString(keyResource)) as Preference?

        if(pref != null) {
            pref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                startAboutActivity(activityKClass)
            }
        }
    }

    private fun configureSourceCodePreference(){
        val pref = findPreference(getString(R.string.preferences_source_code_key)) as Preference?

        if(pref != null) {
            pref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val url = requireActivity().getString(R.string.source_code_link)
                val intent: Intent = get(named(INTENT_SEARCH_URL)) { parametersOf(url) }
                requireActivity().startActivity(intent)
                true
            }
        }
    }

    /**
     * Configure la positon du Switch du Dark Theme en fonction de ce qui est inscrit dans les
     * Preferences. Cela sert surtout lors du premier démarrage de l'application, où l'application
     * se met automatiquement dans le même thème que celui du système. On doit donc configurer la
     * position du Switch en fonction de ça.
     */
    /*private fun configureThemePreference(){

        val mActivity = requireActivity()
        if(mActivity is MainActivity){
            val pref = findPreference(getString(R.string.preferences_switch_dark_theme_key)) as CustomSwitchPreferenceCompat?
            pref?.isChecked = mActivity.settingsManager.useDarkTheme
        }
    }*/

    private fun startAboutActivity(activityKClass: KClass<out Activity>): Boolean {
        val intent = get<Intent>(named(INTENT_START_ACTIVITY)) { parametersOf(activityKClass) }
        startActivity(intent)
        return true
    }
}