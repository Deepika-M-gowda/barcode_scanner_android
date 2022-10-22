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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.atharok.barcodescanner.BuildConfig
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.ActivityMainBinding
import com.atharok.barcodescanner.presentation.views.fragments.main.MainBarcodeCreatorListFragment
import com.atharok.barcodescanner.presentation.views.fragments.main.MainHistoryFragment
import com.atharok.barcodescanner.presentation.views.fragments.main.MainScannerFragment
import com.atharok.barcodescanner.presentation.views.fragments.main.MainSettingsFragment
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject

class MainActivity: BaseActivity() {

    companion object {
        private const val ITEM_ID_KEY = "itemIdKey"
    }

    private val mainScannerFragment: MainScannerFragment by inject()
    private val mainHistoryFragment: MainHistoryFragment by inject()
    private val mainBarcodeCreatorListFragment: MainBarcodeCreatorListFragment by inject()
    private val mainSettingsFragment: MainSettingsFragment by inject()

    private val viewBinding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityMainToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)// On n'affiche pas l'icone "retour" dans la MainActivity

        configureBottomNavigationMenu()

        if(savedInstanceState == null)
            showInitialFragment()

        setContentView(viewBinding.root)

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            manageSplashScreen()
        }*/
    }

    /*@RequiresApi(Build.VERSION_CODES.S)
    private fun manageSplashScreen() {

        splashScreen.setOnExitAnimationListener { splashScreenView ->

            // Create your custom animation.
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView.iconView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.height.toFloat()
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 500L

            // Call SplashScreenView.remove at the end of your custom animation.
            slideUp.doOnEnd { splashScreenView.remove() }

            // Run your animation.
            slideUp.start()
        }
    }*/

    private fun showInitialFragment() {
        val bottomNavigation = viewBinding.activityMainMenuBottomNavigation

        // Utile lorsqu'on ouvre l'application via un shortcut
        when (intent?.action) {
            "${BuildConfig.APPLICATION_ID}.SCAN" -> bottomNavigation.selectedItemId = R.id.menu_navigation_bottom_view_scan
            "${BuildConfig.APPLICATION_ID}.HISTORY" -> bottomNavigation.selectedItemId = R.id.menu_navigation_bottom_view_history
            "${BuildConfig.APPLICATION_ID}.CREATE" -> bottomNavigation.selectedItemId = R.id.menu_navigation_bottom_view_create
        }

        val itemIdSelected: Int = intent.getIntExtra(ITEM_ID_KEY, bottomNavigation.selectedItemId)
        configureFragment(itemIdSelected)
    }

    // ---- Menu ----

    private fun configureBottomNavigationMenu() {

        viewBinding.activityMainMenuBottomNavigation.setOnItemSelectedListener {
            intent.putExtra(ITEM_ID_KEY, it.itemId)
            configureFragment(it.itemId)
        }
    }

    private fun configureFragment(id: Int): Boolean = when(id){
        R.id.menu_navigation_bottom_view_scan -> changeFragment(mainScannerFragment, R.string.title_scan)
        R.id.menu_navigation_bottom_view_history -> changeFragment(mainHistoryFragment, R.string.title_history)
        R.id.menu_navigation_bottom_view_create -> changeFragment(mainBarcodeCreatorListFragment, R.string.title_bar_code_creator)
        R.id.menu_navigation_bottom_view_settings -> changeFragment(mainSettingsFragment, R.string.title_settings)
        else -> false
    }

    private fun changeFragment(fragment: Fragment, titleResource: Int): Boolean {

        supportActionBar?.setTitle(titleResource)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(viewBinding.activityMainFrameLayout.id, fragment)
            //addToBackStack(null) // Permet de revenir aux fragments affichés précédement via le bouton back
        }

        return true
    }

    fun showSnackbar(text: String) {
        val snackbar = Snackbar.make(viewBinding.root, text, Snackbar.LENGTH_SHORT)
        snackbar.anchorView = viewBinding.activityMainMenuBottomNavigation
        snackbar.show()
    }

    // ---- Theme ----
    fun updateTheme() {
        settingsManager.reload()
        setTheme(settingsManager.getTheme())
        intent.putExtra(ITEM_ID_KEY, viewBinding.activityMainMenuBottomNavigation.selectedItemId)
        recreate()
    }
}