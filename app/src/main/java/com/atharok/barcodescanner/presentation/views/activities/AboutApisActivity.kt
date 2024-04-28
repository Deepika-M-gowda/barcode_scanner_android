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
import android.view.View
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.ActivityAboutApisBinding

class AboutApisActivity : BaseActivity() {

    private val viewBinding: ActivityAboutApisBinding by lazy {
        ActivityAboutApisBinding.inflate(layoutInflater)
    }
    override val rootView: View get() = viewBinding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityAboutApisActivityLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)// On affiche l'icone "retour"
        configureViews()

        setContentView(rootView)
    }

    private fun configureViews() {
        viewBinding.activityAboutApisAutomaticSearchDescriptionTextView.text = getString(
            R.string.preferences_information_about_remote_api_automatic_search_description,
            getString(R.string.preferences_switch_scan_search_on_api_label)
        )
        viewBinding.activityAboutApisManualSearchDescriptionTextView.text = getString(
            R.string.preferences_information_about_remote_api_manual_search_description,
            getString(R.string.preferences_switch_scan_search_on_api_label)
        )
    }
}