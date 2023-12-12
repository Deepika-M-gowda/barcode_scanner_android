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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeImageEditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.BARCODE_CONTENTS_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_FORMAT_KEY
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.common.utils.QR_CODE_ERROR_CORRECTION_LEVEL_KEY
import com.atharok.barcodescanner.databinding.FragmentBarcodeInfoBinding
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.QrCodeErrorCorrectionLevel
import com.atharok.barcodescanner.domain.entity.product.DefaultBarcodeAnalysis
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.BarcodeAnalysisAboutFragment
import com.atharok.barcodescanner.presentation.views.fragments.templates.ExpandableViewFragment
import com.google.zxing.BarcodeFormat
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

class BarcodeInfoFragment : BaseFragment() {

    private var _binding: FragmentBarcodeInfoBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeInfoBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.fragmentBarcodeInfoOuterView.fixAnimateLayoutChangesInNestedScroll()

        arguments?.let {
            val contents: String = it.getString(BARCODE_CONTENTS_KEY) ?: ""
            val format: BarcodeFormat = it.serializable(BARCODE_FORMAT_KEY, BarcodeFormat::class.java) ?: BarcodeFormat.QR_CODE
            val qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel = it.serializable(QR_CODE_ERROR_CORRECTION_LEVEL_KEY, QrCodeErrorCorrectionLevel::class.java) ?: QrCodeErrorCorrectionLevel.NONE

            configureContentsExpandableViewFragment(contents, format)
            configureAboutBarcodeFragment(contents, format, qrCodeErrorCorrectionLevel)
        }
    }

    private fun configureContentsExpandableViewFragment(contents: String, format: BarcodeFormat) {

        val iconResource: Int = when(format) {
            BarcodeFormat.QR_CODE -> R.drawable.baseline_qr_code_24
            BarcodeFormat.AZTEC -> R.drawable.ic_aztec_code_24
            BarcodeFormat.DATA_MATRIX -> R.drawable.ic_data_matrix_code_24
            BarcodeFormat.PDF_417 -> R.drawable.ic_pdf_417_code_24
            else -> R.drawable.ic_bar_code_24
        }

        val contentsFragment = ExpandableViewFragment.newInstance(
            title = getString(R.string.bar_code_content_label),
            contents = contents,
            drawableResource = iconResource
        )

        applyFragment(
            containerViewId = viewBinding.fragmentBarcodeInfoContentsFrameLayout.id,
            fragment = contentsFragment
        )
    }

    private fun configureAboutBarcodeFragment(contents: String, format: BarcodeFormat, qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel) {

        val barcode: Barcode = get { parametersOf(contents, format.name, qrCodeErrorCorrectionLevel) }
        val barcodeAnalysis = DefaultBarcodeAnalysis(barcode)

        val args: Bundle = get<Bundle>().apply {
            putSerializable(PRODUCT_KEY, barcodeAnalysis)
        }

        applyFragment(
            containerViewId = viewBinding.fragmentBarcodeInfoAboutBarcodeFrameLayout.id,
            fragmentClass = BarcodeAnalysisAboutFragment::class,
            args = args
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(
            contents: String,
            format: BarcodeFormat,
            errorCorrectionLevel: QrCodeErrorCorrectionLevel
        ) = BarcodeInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(BARCODE_CONTENTS_KEY, contents)
                    putSerializable(BARCODE_FORMAT_KEY, format)
                    putSerializable(QR_CODE_ERROR_CORRECTION_LEVEL_KEY, errorCorrectionLevel)
                }
            }
    }
}