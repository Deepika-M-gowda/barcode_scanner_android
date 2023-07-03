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

import android.view.View
import android.widget.TextView
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_SCOPE_SESSION
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_SCOPE_SESSION_ID
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import com.google.zxing.client.result.ParsedResult
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

abstract class AbstractBarcodeMatrixFragment : BarcodeAnalysisFragment<BarcodeAnalysis>() {

    private val barcodeAnalysisScope get() = getKoin().getOrCreateScope(
        BARCODE_ANALYSIS_SCOPE_SESSION_ID,
        named(BARCODE_ANALYSIS_SCOPE_SESSION)
    ) // close in BarcodeAnalysisActivity

    override fun start(product: BarcodeAnalysis) {
        val parsedResult = barcodeAnalysisScope.get<ParsedResult> {
            parametersOf(product.barcode.contents, product.barcode.getBarcodeFormat())
        }

        start(product, parsedResult)
    }

    abstract fun start(product: BarcodeAnalysis, parsedResult: ParsedResult)

    protected fun configureText(textView: TextView, layout: View, text: String?) {
        displayText(textView, layout, text)
    }

    protected fun configureTextArray(textView: TextView, layout: View, array: Array<String?>?) {
        displayArray(textView, layout, array)
    }
}