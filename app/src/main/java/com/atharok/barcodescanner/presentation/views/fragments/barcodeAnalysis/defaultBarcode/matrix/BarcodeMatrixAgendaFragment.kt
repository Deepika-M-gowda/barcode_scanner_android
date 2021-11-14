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
import com.atharok.barcodescanner.databinding.FragmentBarcodeMatrixAgendaBinding
import com.atharok.barcodescanner.domain.entity.product.BarcodeProduct
import com.google.zxing.client.result.CalendarParsedResult
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class BarcodeMatrixAgendaFragment : AbstractBarcodeMatrixFragment() {

    private val simpleDateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("E dd MMM yyyy HH:mm", Locale.getDefault())
    }

    private var _binding: FragmentBarcodeMatrixAgendaBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeMatrixAgendaBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: BarcodeProduct, parsedResult: ParsedResult) {

        if(parsedResult is CalendarParsedResult && parsedResult.type == ParsedResultType.CALENDAR) {
            configureNameEvent(parsedResult.summary)
            configureStartEvent(parsedResult.startTimestamp)
            configureEndEvent(parsedResult.endTimestamp)
            configurePlace(parsedResult.location)
            configureDescription(parsedResult.description)
        }else{
            viewBinding.root.visibility = View.GONE
        }
    }

    private fun configureNameEvent(name: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixAgendaNameEventTextView,
        layout = viewBinding.fragmentBarcodeMatrixAgendaNameEventLayout,
        text = name
    )

    private fun configureStartEvent(startDate: Long?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixAgendaStartDateTextView,
        layout = viewBinding.fragmentBarcodeMatrixAgendaStartDateLayout,
        text = getDateFormat(startDate)
    )

    private fun configureEndEvent(endDate: Long?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixAgendaEndDateTextView,
        layout = viewBinding.fragmentBarcodeMatrixAgendaEndDateLayout,
        text = getDateFormat(endDate)
    )

    private fun configurePlace(place: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixAgendaPlaceTextView,
        layout = viewBinding.fragmentBarcodeMatrixAgendaPlaceLayout,
        text = place
    )

    private fun configureDescription(description: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixAgendaDescriptionTextView,
        layout = viewBinding.fragmentBarcodeMatrixAgendaDescriptionLayout,
        text = description
    )

    /*private fun getDateFormat(date: Long?): String? = if(date!=null){
        val d = date + TimeZone.getTimeZone("GMT").rawOffset - TimeZone.getDefault().rawOffset
        simpleDateFormat.format(d)
    } else null*/

    private fun getDateFormat(date: Long?): String? = if(date!=null){
        simpleDateFormat.format(date)
    } else null
}