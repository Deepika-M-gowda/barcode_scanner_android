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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix

import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_SCOPE_SESSION
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_SCOPE_SESSION_ID
import com.atharok.barcodescanner.domain.entity.product.BarcodeProduct
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.ProductBarcodeFragment
import com.google.zxing.client.result.ParsedResult
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

abstract class AbstractBarcodeMatrixFragment : ProductBarcodeFragment<BarcodeProduct>() {

    private val barcodeAnalysisScope get() = getKoin().getOrCreateScope(
        BARCODE_ANALYSIS_SCOPE_SESSION_ID,
        named(BARCODE_ANALYSIS_SCOPE_SESSION)
    )

    override fun start(product: BarcodeProduct) {
        val parsedResult = barcodeAnalysisScope.get<ParsedResult> {
            parametersOf(product.barcode.contents, product.barcode.getBarcodeFormat())
        }

        start(product, parsedResult)
    }

    abstract fun start(product: BarcodeProduct, parsedResult: ParsedResult)
}