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
import com.atharok.barcodescanner.common.utils.EAN_13_LENGTH
import com.google.zxing.BarcodeFormat

class BarcodeFormCreatorEAN13Fragment: AbstractBarcodeFormCreatorBasicFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputEditText = viewBinding.fragmentBarcodeFormCreatorTextInputEditText
        inputEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(EAN_13_LENGTH))
        inputEditText.inputType = InputType.TYPE_CLASS_NUMBER
    }

    override val checkError: (contents: String) -> String? by lazy {
        { barcodeFormatChecker.checkEAN13Error(it) }
    }

    override fun getBarcodeFormat(): BarcodeFormat = BarcodeFormat.EAN_13
}