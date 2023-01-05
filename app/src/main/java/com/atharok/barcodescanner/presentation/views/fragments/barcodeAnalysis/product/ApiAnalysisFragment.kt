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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.utils.INTENT_SEARCH_URL
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

abstract class ApiAnalysisFragment<T: BarcodeAnalysis>: BarcodeAnalysisFragment<T>() {

    private lateinit var sourceApiInfoAlertDialog: AlertDialog

    private fun configureSourceApiInfoAlertDialog(titleResource: Int, layout: Int, urlResource: Int, barcodeContents: String){
        sourceApiInfoAlertDialog = AlertDialog.Builder(requireActivity()).apply {
            setTitle(getString(titleResource))
            setView(layout)
            setNegativeButton(R.string.close_dialog_label) { dialogInterface, _ -> dialogInterface.cancel() }
            setPositiveButton(R.string.go_to_dialog_label) { _, _ ->
                val intent: Intent = get(named(INTENT_SEARCH_URL)) { parametersOf(getString(urlResource, barcodeContents)) }
                startActivity(intent)
            }
        }.create()
    }

    override fun configureMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_activity_barcode_analysis, menu)

                // On retire le menu pour rechercher dans les APIs, car ici la recherche à déjà été faite.
                menu.removeItem(R.id.menu_activity_barcode_analysis_download_from_apis)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when(menuItem.itemId) {
                R.id.menu_activity_barcode_analysis_product_source_api_info_item -> {
                    sourceApiInfoAlertDialog.show()
                    true
                }
                R.id.menu_activity_barcode_analysis_about_barcode_item -> {
                    startBarcodeDetailsActivity()
                    true
                }
                else -> false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun start(product: T) {
        configureSourceApiInfoAlertDialog(product.source.nameResource, product.source.layout, product.source.urlResource, product.barcode.contents)
    }
}