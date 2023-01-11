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

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commitNow
import com.atharok.barcodescanner.BuildConfig
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.ActivityMainBinding
import com.atharok.barcodescanner.presentation.views.fragments.main.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject

class MainActivity: BaseActivity() {

    companion object {
        private const val ITEM_ID_KEY = "itemIdKey"
    }

    private val mainCameraXScannerFragment: MainCameraXScannerFragment by inject()
    private val mainScannerFragment: MainScannerFragment by inject()
    private val mainHistoryFragment: MainHistoryFragment by inject()
    private val mainBarcodeCreatorListFragment: MainBarcodeCreatorListFragment by inject()
    private val mainSettingsFragment: MainSettingsFragment by inject()

    private val viewBinding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityMainToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)// On n'affiche pas l'icone "retour" dans la MainActivity

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            configureNavigationRailView()
        }else{
            configureBottomNavigationMenu()
        }

        setContentView(viewBinding.root)
    }

    // ---- Configuration ----

    /**
     * Permet de coloriser la bar de navigation (en bas) de la même couleur que la BottomNavigationView.
     */
    private fun configureNavigationBarColor(bottomNavigationView: BottomNavigationView) {
        if (Build.VERSION.SDK_INT >= 24 || settingsManager.useDarkTheme()) {
            val bottomNavigationViewBackground = bottomNavigationView.background
            if (bottomNavigationViewBackground is MaterialShapeDrawable) {
                this.window.navigationBarColor = bottomNavigationViewBackground.resolvedTintColor
            }
        }
    }

    private fun configureBottomNavigationMenu() {

        viewBinding.activityMainMenuBottomNavigation?.let { bottomNavigationView ->

            configureNavigationBarColor(bottomNavigationView)

            bottomNavigationView.setOnItemSelectedListener {
                intent.putExtra(ITEM_ID_KEY, it.itemId)
                changeView(it.itemId)
            }

            initializeFirstFragmentToShow {
                bottomNavigationView.selectedItemId = it
            }
        }
    }

    private fun configureNavigationRailView() {

        viewBinding.activityMainNavigationRail?.let { navigationRailView ->
            navigationRailView.setOnItemSelectedListener {
                intent.putExtra(ITEM_ID_KEY, it.itemId)
                changeView(it.itemId)
            }

            initializeFirstFragmentToShow {
                navigationRailView.selectedItemId = it
            }
        }
    }

    private fun initializeFirstFragmentToShow(onChangeItem: (selectedItemId: Int) -> Unit) {

        // Utile lorsqu'on ouvre l'application via un shortcut
        val selectedItemId = when (intent?.action) {
            "${BuildConfig.APPLICATION_ID}.SCAN" -> R.id.menu_navigation_bottom_view_scan
            "${BuildConfig.APPLICATION_ID}.HISTORY" -> R.id.menu_navigation_bottom_view_history
            "${BuildConfig.APPLICATION_ID}.CREATE" -> R.id.menu_navigation_bottom_view_create
            "android.intent.action.APPLICATION_PREFERENCES" -> R.id.menu_navigation_bottom_view_settings
            else -> intent.getIntExtra(ITEM_ID_KEY, R.id.menu_navigation_bottom_view_scan)
        }

        onChangeItem(selectedItemId)

        val itemIdSelected: Int = intent.getIntExtra(ITEM_ID_KEY, selectedItemId)
        changeView(itemIdSelected)
    }

    // ---- Change Fragment ----

    private fun changeView(id: Int): Boolean = when(id){
        R.id.menu_navigation_bottom_view_scan -> changeFragment(if(settingsManager.useCameraXApi) mainCameraXScannerFragment else mainScannerFragment, R.string.title_scan)
        R.id.menu_navigation_bottom_view_history -> changeFragment(mainHistoryFragment, R.string.title_history)
        R.id.menu_navigation_bottom_view_create -> changeFragment(mainBarcodeCreatorListFragment, R.string.title_bar_code_creator)
        R.id.menu_navigation_bottom_view_settings -> changeFragment(mainSettingsFragment, R.string.title_settings)
        else -> false
    }

    private fun changeFragment(fragment: Fragment, titleResource: Int): Boolean {

        supportActionBar?.setTitle(titleResource)

        supportFragmentManager.commitNow {
            setReorderingAllowed(false)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(viewBinding.activityMainFrameLayout.id, fragment)
            //addToBackStack(null) // Permet de revenir aux fragments affichés précédement via le bouton back
        }

        return true
    }

    // ---- UI ----

    fun showSnackbar(text: String) {
        val snackbar = Snackbar.make(viewBinding.root, text, Snackbar.LENGTH_SHORT)
        snackbar.anchorView = viewBinding.activityMainMenuBottomNavigation
        snackbar.show()
    }

    // ---- Theme ----

    fun updateTheme() {
        settingsManager.reload()
        //setTheme(settingsManager.getTheme())

        viewBinding.activityMainMenuBottomNavigation?.let { bottomNavigationView ->
            intent.putExtra(ITEM_ID_KEY, bottomNavigationView.selectedItemId)
        }

        viewBinding.activityMainNavigationRail?.let { navigationRailView ->
            intent.putExtra(ITEM_ID_KEY, navigationRailView.selectedItemId)
        }

        recreate()
    }
}