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
import org.koin.android.ext.android.get

class MainActivity: BaseActivity() {

    companion object {
        private const val ITEM_ID_KEY = "itemIdKey"
    }

    private val mainScannerFragment: MainScannerFragment = get()
    private val mainHistoryFragment: MainHistoryFragment = get()
    private val mainBarcodeCreatorListFragment: MainBarcodeCreatorListFragment = get()
    private val mainSettingsFragment: MainSettingsFragment = get()

    private val viewBinding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityMainToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)// On n'affiche pas l'icone "retour" dans la MainActivity

        configureBottomNavigationMenu()

        showInitialFragment()

        setContentView(viewBinding.root)
    }

    private fun showInitialFragment() {
        when (intent?.action) {
            "${BuildConfig.APPLICATION_ID}.SCAN" -> viewBinding.activityMainMenuBottomNavigation.selectedItemId = R.id.menu_navigation_bottom_view_scan
            "${BuildConfig.APPLICATION_ID}.HISTORY" -> viewBinding.activityMainMenuBottomNavigation.selectedItemId = R.id.menu_navigation_bottom_view_history
            "${BuildConfig.APPLICATION_ID}.CREATE" -> viewBinding.activityMainMenuBottomNavigation.selectedItemId = R.id.menu_navigation_bottom_view_create
        }
        intent?.action = null
        val itemIdSelected: Int = intent.getIntExtra(ITEM_ID_KEY, viewBinding.activityMainMenuBottomNavigation.selectedItemId)
        configureFragment(itemIdSelected)
    }

    // ---- Menu ----

    private fun configureBottomNavigationMenu(){

        viewBinding.activityMainMenuBottomNavigation.setOnItemSelectedListener {
            intent.putExtra(ITEM_ID_KEY, it.itemId)
            configureFragment(it.itemId)
        }
    }

    private fun configureFragment(id: Int): Boolean{
        /*return when(id){
            R.id.menu_navigation_bottom_view_scan -> changeFragment(MainScannerFragment::class, R.string.title_scan)
            R.id.menu_navigation_bottom_view_history -> changeFragment(MainHistoryFragment::class, R.string.title_history)
            R.id.menu_navigation_bottom_view_create -> changeFragment(MainBarcodeCreatorListFragment::class, R.string.title_bar_code_creator)
            R.id.menu_navigation_bottom_view_settings -> changeFragment(MainSettingsFragment::class, R.string.title_settings)
            else -> false
        }*/
        return when(id){
            R.id.menu_navigation_bottom_view_scan -> changeFragment(mainScannerFragment, R.string.title_scan)
            R.id.menu_navigation_bottom_view_history -> changeFragment(mainHistoryFragment, R.string.title_history)
            R.id.menu_navigation_bottom_view_create -> changeFragment(mainBarcodeCreatorListFragment, R.string.title_bar_code_creator)
            R.id.menu_navigation_bottom_view_settings -> changeFragment(mainSettingsFragment, R.string.title_settings)
            else -> false
        }
    }

    // ---- Theme ----
    fun updateTheme(){
        settingsManager.reload()
        setTheme(settingsManager.getTheme())
        intent.putExtra(ITEM_ID_KEY, viewBinding.activityMainMenuBottomNavigation.selectedItemId)
        recreate()
    }

    /*private fun changeFragment(fragmentClass: KClass<out Fragment>, titleResource: Int) : Boolean {

        supportActionBar?.setTitle(titleResource)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(viewBinding.activityMainFrameLayout.id, fragmentClass.java, null)
            //addToBackStack(null) // Permet de revenir aux fragments affichés précédement via le bouton back
        }

        return true
    }*/

    private fun changeFragment(fragment: Fragment, titleResource: Int) : Boolean {

        supportActionBar?.setTitle(titleResource)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(viewBinding.activityMainFrameLayout.id, fragment)
            //addToBackStack(null) // Permet de revenir aux fragments affichés précédement via le bouton back
        }

        return true
    }
}