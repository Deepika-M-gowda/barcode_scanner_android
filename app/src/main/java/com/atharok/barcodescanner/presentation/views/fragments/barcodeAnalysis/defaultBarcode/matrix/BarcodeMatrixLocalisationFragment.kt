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

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.databinding.FragmentBarcodeMatrixLocalisationBinding
import com.atharok.barcodescanner.domain.entity.product.BarcodeProduct
import com.google.zxing.client.result.GeoParsedResult
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType

/**
 * A simple [Fragment] subclass.
 */
class BarcodeMatrixLocalisationFragment : AbstractBarcodeMatrixFragment() {

    private var _binding: FragmentBarcodeMatrixLocalisationBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeMatrixLocalisationBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: BarcodeProduct, parsedResult: ParsedResult) {

        if(parsedResult is GeoParsedResult && parsedResult.type == ParsedResultType.GEO) {
            configureLatitude(parsedResult.latitude)
            configureLongitude(parsedResult.longitude)
            configureAltitude(parsedResult.altitude)
            configureQuery(parsedResult.query)

        }else{
            viewBinding.root.visibility = View.GONE
        }
    }

    private fun configureLatitude(latitude: Double?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixLocalisationLatitudeTextView,
        layout = viewBinding.fragmentBarcodeMatrixLocalisationLatitudeLayout,
        text = latitude?.toString()
    )

    private fun configureLongitude(longitude: Double?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixLocalisationLongitudeTextView,
        layout = viewBinding.fragmentBarcodeMatrixLocalisationLongitudeLayout,
        text = longitude?.toString()
    )

    private fun configureAltitude(altitude: Double?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixLocalisationAltitudeTextView,
        layout = viewBinding.fragmentBarcodeMatrixLocalisationAltitudeLayout,
        text = altitude?.toString()
    )

    private fun configureQuery(query: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixLocalisationQueryTextView,
        layout = viewBinding.fragmentBarcodeMatrixLocalisationQueryLayout,
        text = query
    )
}