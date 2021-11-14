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

package com.atharok.barcodescanner.presentation.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.domain.library.SettingsManager
import org.koin.android.ext.android.inject
import kotlin.reflect.KClass

abstract class BaseActivity: AppCompatActivity() {

    val settingsManager: SettingsManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        this.setTheme(settingsManager.getTheme())
        super.onCreate(savedInstanceState)
    }

    protected fun applyFragment(containerViewId: Int, fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(containerViewId, fragment)
            .commit()
    }

    protected fun applyFragment(containerViewId: Int, fragmentClass: KClass<out Fragment>, args: Bundle? = null, tag: String? = null){
        supportFragmentManager
            .beginTransaction()
            .replace(containerViewId, fragmentClass.java, args, tag)
            .commit()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
    }

    // -------------------------------
    // ------ Activity Override ------
    // -------------------------------
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    // -------------------------------
}