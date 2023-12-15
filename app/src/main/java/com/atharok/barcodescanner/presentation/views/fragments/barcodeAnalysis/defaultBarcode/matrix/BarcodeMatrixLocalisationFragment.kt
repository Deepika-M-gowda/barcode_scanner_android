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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.databinding.FragmentBarcodeMatrixLocalisationBinding
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
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

    override fun start(product: BarcodeAnalysis, parsedResult: ParsedResult) {
        if(parsedResult is GeoParsedResult && parsedResult.type == ParsedResultType.GEO) {
            val latitudeView = viewBinding.fragmentBarcodeMatrixLocalisationLatitudeLayout
            val longitudeView = viewBinding.fragmentBarcodeMatrixLocalisationLongitudeLayout
            val altitudeView = viewBinding.fragmentBarcodeMatrixLocalisationAltitudeLayout
            val queryView = viewBinding.fragmentBarcodeMatrixLocalisationQueryLayout

            latitudeView.setContentsText(parsedResult.latitude.toString())
            longitudeView.setContentsText(parsedResult.longitude.toString())
            altitudeView.setContentsText(parsedResult.altitude.toString())
            queryView.setContentsText(parsedResult.query)
        } else {
            viewBinding.root.visibility = View.GONE
        }
    }
}