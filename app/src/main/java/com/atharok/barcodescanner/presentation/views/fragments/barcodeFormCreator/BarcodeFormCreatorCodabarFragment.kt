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

import android.os.Bundle
import android.text.InputType
import android.view.View
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.google.zxing.BarcodeFormat

class BarcodeFormCreatorCodabarFragment: AbstractBarcodeFormCreatorBasicFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputEditText = viewBinding.fragmentBarcodeFormCreatorTextInputEditText
        inputEditText.inputType = InputType.TYPE_CLASS_TEXT
    }

    override fun generateBarcode() {
        val barcodeContents: String = getBarcodeTextFromForm()

        if(barcodeContents.isBlank()){
            configureErrorMessage(getString(R.string.error_barcode_none_character_message))
            return
        }

        if(!barcodeFormatChecker.checkCodabarRegex(barcodeContents)) {
            configureErrorMessage(getString(R.string.error_barcode_codabar_regex_error_message))
            return
        }

        hideErrorMessage()
        startBarcodeDetailsActivity(barcodeContents, BarcodeFormat.CODABAR)
    }

    override fun getBarcodeType(): BarcodeType = BarcodeType.INDUSTRIAL
}