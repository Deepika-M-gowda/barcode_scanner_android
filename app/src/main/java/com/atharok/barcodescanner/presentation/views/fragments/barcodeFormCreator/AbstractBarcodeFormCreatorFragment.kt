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
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.utils.BARCODE_CONTENTS_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_FORMAT_KEY
import com.atharok.barcodescanner.common.utils.QR_CODE_ERROR_CORRECTION_LEVEL_KEY
import com.atharok.barcodescanner.domain.entity.barcode.QrCodeErrorCorrectionLevel
import com.atharok.barcodescanner.domain.library.BarcodeFormatChecker
import com.atharok.barcodescanner.presentation.intent.createStartActivityIntent
import com.atharok.barcodescanner.presentation.views.activities.BarcodeDetailsActivity
import com.atharok.barcodescanner.presentation.views.activities.BarcodeFormCreatorActivity
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment
import com.google.zxing.BarcodeFormat
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

abstract class AbstractBarcodeFormCreatorFragment: BaseFragment() {

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

    fun closeVirtualKeyBoard(view: View) {
        val inputMethodManager: InputMethodManager = get()
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun startBarcodeDetailsActivity(
        content: String,
        barcodeFormat: BarcodeFormat,
        qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel = QrCodeErrorCorrectionLevel.NONE
    ) {
        val intent = createStartActivityIntent(requireContext(), BarcodeDetailsActivity::class).apply {
            putExtra(BARCODE_CONTENTS_KEY, content)
            putExtra(BARCODE_FORMAT_KEY, barcodeFormat.name)
            putExtra(QR_CODE_ERROR_CORRECTION_LEVEL_KEY, qrCodeErrorCorrectionLevel)
        }
        startActivity(intent)
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
}