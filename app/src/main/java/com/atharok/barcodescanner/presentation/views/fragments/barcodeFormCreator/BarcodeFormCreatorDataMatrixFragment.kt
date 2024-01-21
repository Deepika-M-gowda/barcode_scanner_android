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

import com.google.zxing.BarcodeFormat

class BarcodeFormCreatorDataMatrixFragment: AbstractBarcodeFormCreatorBasicFragment() {
    override val checkError: (contents: String) -> String? by lazy {
        { barcodeFormatChecker.checkDataMatrixError(it) }
    }

    override fun getBarcodeFormat(): BarcodeFormat = BarcodeFormat.DATA_MATRIX
}