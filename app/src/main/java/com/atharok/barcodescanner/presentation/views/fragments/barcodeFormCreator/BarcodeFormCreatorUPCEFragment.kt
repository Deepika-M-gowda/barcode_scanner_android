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
import android.text.InputFilter
import android.text.InputType
import android.view.View
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.canBeConvertibleToLong
import com.google.zxing.BarcodeFormat

class BarcodeFormCreatorUPCEFragment: AbstractBarcodeFormCreatorBasicFragment() {
    companion object {
        private const val MAX_LENGTH = 8
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputEditText = viewBinding.fragmentBarcodeFormCreatorTextInputEditText
        inputEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_LENGTH))
        inputEditText.inputType = InputType.TYPE_CLASS_NUMBER
    }

    override fun generateBarcode() {
        val barcodeContents: String = getBarcodeTextFromForm()

        if(barcodeContents.isBlank()){
            configureErrorMessage(getString(R.string.error_barcode_none_character_message))
            return
        }

        if(barcodeContents.length != MAX_LENGTH) {
            configureErrorMessage(getString(R.string.error_barcode_wrong_length_message, MAX_LENGTH.toString()))
            return
        }

        if(!barcodeContents.canBeConvertibleToLong()) {
            configureErrorMessage(getString(R.string.error_barcode_not_a_number_message))
            return
        }

        if(!barcodeContents.startsWith("0")){
            configureErrorMessage(getString(R.string.error_barcode_upc_e_not_start_with_0_error_message))
            return
        }

        val checkDigit: Int = barcodeFormatChecker.calculateUPCECheckDigit(barcodeContents)
        if(checkDigit != Character.getNumericValue(barcodeContents.last())){
            configureErrorMessage(getString(R.string.error_barcode_wrong_key_message, MAX_LENGTH.toString(), checkDigit.toString()))
            return
        }

        hideErrorMessage()
        startBarcodeDetailsActivity(barcodeContents, BarcodeFormat.UPC_E)
    }
}