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

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.databinding.FragmentBarcodeMatrixUpiParsedBinding
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment

class BarcodeMatrixUpiParsedFragment : BaseFragment() {

    private var uri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uri = it.getString(URI_BUNDLE_KEY)
        }
    }

    private var _binding: FragmentBarcodeMatrixUpiParsedBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeMatrixUpiParsedBinding.inflate(inflater, container, false)
        if (uri?.startsWith("upi") != true) {
            viewBinding.root.visibility = View.GONE
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uri?.let {

            /*val entitledTextView = viewBinding.fragmentBarcodeMatrixUpiTemplateEntitledView.templateTextViewTitleTextView
            entitledTextView.setText(R.string.matrix_uri_upi_entitled_label)*/

            val uriParsed: Uri = Uri.parse(uri)
            displayText(
                textView = viewBinding.fragmentBarcodeMatrixUpiParsedUpiIdTextView,
                layout = viewBinding.fragmentBarcodeMatrixUpiParsedUpiIdLayout,
                text = uriParsed.getQueryParameter("pa")
            )
            displayText(
                textView = viewBinding.fragmentBarcodeMatrixUpiParsedPayeeNameTextView,
                layout = viewBinding.fragmentBarcodeMatrixUpiParsedPayeeNameLayout,
                text = uriParsed.getQueryParameter("pn")
            )
            displayText(
                textView = viewBinding.fragmentBarcodeMatrixUpiParsedAmountTextView,
                layout = viewBinding.fragmentBarcodeMatrixUpiParsedAmountLayout,
                text = uriParsed.getQueryParameter("am")
            )
            displayText(
                textView = viewBinding.fragmentBarcodeMatrixUpiParsedCurrencyTextView,
                layout = viewBinding.fragmentBarcodeMatrixUpiParsedCurrencyLayout,
                text = uriParsed.getQueryParameter("cu")
            )
            displayText(
                textView = viewBinding.fragmentBarcodeMatrixUpiParsedDescriptionTextView,
                layout = viewBinding.fragmentBarcodeMatrixUpiParsedDescriptionLayout,
                text = uriParsed.getQueryParameter("tn")
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    companion object {
        private const val URI_BUNDLE_KEY = "uriBundleKey"

        @JvmStatic
        fun newInstance(uri: String) =
            BarcodeMatrixUpiParsedFragment().apply {
                arguments = Bundle().apply {
                    putString(URI_BUNDLE_KEY, uri)
                }
            }
    }
}