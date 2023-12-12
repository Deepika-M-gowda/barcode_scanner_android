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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeFormCreator

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.utils.BARCODE_CONTENTS_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_FORMAT_KEY
import com.atharok.barcodescanner.common.utils.QR_CODE_ERROR_CORRECTION_LEVEL_KEY
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.entity.barcode.QrCodeErrorCorrectionLevel
import com.atharok.barcodescanner.domain.library.BarcodeFormatChecker
import com.atharok.barcodescanner.domain.library.SettingsManager
import com.atharok.barcodescanner.presentation.intent.createStartActivityIntent
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseBarcodeViewModel
import com.atharok.barcodescanner.presentation.views.activities.BarcodeDetailsActivity
import com.atharok.barcodescanner.presentation.views.activities.BarcodeFormCreatorActivity
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment
import com.google.zxing.BarcodeFormat
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.parameter.parametersOf

abstract class AbstractBarcodeFormCreatorFragment: BaseFragment() {

    private val databaseBarcodeViewModel: DatabaseBarcodeViewModel by activityViewModel()
    protected val barcodeFormatChecker: BarcodeFormatChecker by inject()

    protected fun configureMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_activity_confirm, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when(menuItem.itemId){
                R.id.menu_activity_confirm_item -> {generateBarcode(); true}
                else -> false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    protected fun startBarcodeDetailsActivity(
        contents: String,
        barcodeFormat: BarcodeFormat,
        qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel = QrCodeErrorCorrectionLevel.NONE
    ) {
        insertBarcodeIntoDatabase(contents, barcodeFormat, qrCodeErrorCorrectionLevel)
        val intent = createStartActivityIntent(requireContext(), BarcodeDetailsActivity::class).apply {
            putExtra(BARCODE_CONTENTS_KEY, contents)
            putExtra(BARCODE_FORMAT_KEY, barcodeFormat.name)
            putExtra(QR_CODE_ERROR_CORRECTION_LEVEL_KEY, qrCodeErrorCorrectionLevel.name)
        }
        startActivity(intent)
    }

    private fun insertBarcodeIntoDatabase(
        contents: String,
        barcodeFormat: BarcodeFormat,
        qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel
    ) {
        if(get<SettingsManager>().shouldAddBarcodeGenerateToHistory) {
            val barcode: Barcode = get {
                parametersOf(contents, barcodeFormat.name, qrCodeErrorCorrectionLevel)
            }
            barcode.type = getBarcodeType().name

            // Insert les informations du code-barres dans la base de données (de manière asynchrone)
            databaseBarcodeViewModel.insertBarcode(barcode)
        }
    }

    protected fun configureErrorMessage(message: String) {
        val activity = requireActivity()
        if(activity is BarcodeFormCreatorActivity){
            activity.configureErrorMessage(message)
        }
    }

    protected fun hideErrorMessage() {
        val activity = requireActivity()
        if(activity is BarcodeFormCreatorActivity){
            activity.hideErrorMessage()
        }
    }

    abstract fun getBarcodeTextFromForm(): String
    abstract fun generateBarcode()
    abstract fun getBarcodeType(): BarcodeType
}