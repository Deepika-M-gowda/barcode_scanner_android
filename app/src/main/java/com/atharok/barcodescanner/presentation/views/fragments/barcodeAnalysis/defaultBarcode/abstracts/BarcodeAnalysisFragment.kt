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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.BARCODE_CONTENTS_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_FORMAT_KEY
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.common.utils.QR_CODE_ERROR_CORRECTION_LEVEL_KEY
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.presentation.intent.createStartActivityIntent
import com.atharok.barcodescanner.presentation.views.activities.BarcodeDetailsActivity
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment
import com.atharok.barcodescanner.presentation.views.fragments.templates.ExpandableViewFragment

abstract class BarcodeAnalysisFragment<T: BarcodeAnalysis>: BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureMenu()

        arguments?.takeIf {
            // Si les données ResultScanData sont bien stockées en mémoire
            it.containsKey(PRODUCT_KEY)// && it.getSerializable(PRODUCT_KEY) is BarcodeProduct
        }?.apply {

            serializable(PRODUCT_KEY, BarcodeAnalysis::class.java)?.let { barcodeAnalysis ->
                try {
                    @Suppress("UNCHECKED_CAST")
                    start(barcodeAnalysis as T)
                }catch (e: ClassCastException){
                    e.printStackTrace()
                }
            }
        }
    }

    protected open fun configureMenu() {}

    abstract fun start(product: T)

    protected fun configureExpandableViewFragment(frameLayout: FrameLayout, title: String, contents: CharSequence?, iconDrawableResource: Int? = null){

        if(!contents.isNullOrBlank()) {

            val fragment = ExpandableViewFragment.newInstance(
                title = title,
                contents = contents.trim(),
                drawableResource = iconDrawableResource
            )

            applyFragment(frameLayout.id, fragment)
        } else {
            frameLayout.visibility = View.GONE
        }
    }

    protected fun startBarcodeDetailsActivity(){

        arguments?.serializable(PRODUCT_KEY, BarcodeAnalysis::class.java)?.let { barcodeAnalysis ->
            val intent = createStartActivityIntent(requireContext(), BarcodeDetailsActivity::class).apply {
                putExtra(BARCODE_CONTENTS_KEY, barcodeAnalysis.barcode.contents)
                putExtra(BARCODE_FORMAT_KEY, barcodeAnalysis.barcode.formatName)
                putExtra(QR_CODE_ERROR_CORRECTION_LEVEL_KEY, barcodeAnalysis.barcode.errorCorrectionLevel)
            }

            startActivity(intent)
        }
    }
}