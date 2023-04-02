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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.databinding.FragmentBarcodeFormCreatorQrAgendaBinding
import com.atharok.barcodescanner.domain.library.VEventBuilder
import com.atharok.barcodescanner.presentation.views.fragments.android.DatePickerFragment
import com.atharok.barcodescanner.presentation.views.fragments.android.TimePickerFragment
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class BarcodeFormCreatorQrAgendaFragment : AbstractBarcodeFormCreatorQrFragment() {

    private val date: Date = get()
    private val simpleDateTimeFormat: SimpleDateFormat = get { parametersOf("yyyyMMdd'T'HHmmss'Z'") }
    private val simpleDateFormat: SimpleDateFormat = get { parametersOf("yyyy-MM-dd") }
    private val simpleTimeFormat: SimpleDateFormat = get { parametersOf("HH:mm") }

    private var _binding: FragmentBarcodeFormCreatorQrAgendaBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeFormCreatorQrAgendaBinding.inflate(inflater, container, false)
        configureMenu()
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginDatePicker.text = simpleDateFormat.format(date)
        viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.text = simpleTimeFormat.format(date)
        viewBinding.fragmentBarcodeFormCreatorQrAgendaEndDatePicker.text = viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginDatePicker.text
        viewBinding.fragmentBarcodeFormCreatorQrAgendaEndTimePicker.text = viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.text

        configureOnClickAllOfDayCheckBox(viewBinding.fragmentBarcodeFormCreatorQrAgendaAllOfDayCheckBox)
        configureOnClickDateTimePicker()
    }

    override fun getBarcodeTextFromForm(): String {

        val dtStart: String
        val dtEnd: String

        if(viewBinding.fragmentBarcodeFormCreatorQrAgendaAllOfDayCheckBox.isChecked){
            dtStart = viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginDatePicker.text.toString().replace("-", "")
            dtEnd = viewBinding.fragmentBarcodeFormCreatorQrAgendaEndDatePicker.text.toString().replace("-", "")

        }else{
            val dateStartStr = viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginDatePicker.text.toString()
            val dateEndStr = viewBinding.fragmentBarcodeFormCreatorQrAgendaEndDatePicker.text.toString()
            val timeStartStr = viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.text.toString()
            val timeEndStr = viewBinding.fragmentBarcodeFormCreatorQrAgendaEndTimePicker.text.toString()

            val dateStartTimestamp = simpleDateFormat.parse(dateStartStr)?.time ?: 0L
            val dateEndTimestamp = simpleDateFormat.parse(dateEndStr)?.time ?: 0L
            val timeStartTimestamp = simpleTimeFormat.parse(timeStartStr)?.time ?: 0L
            val timeEndTimestamp = simpleTimeFormat.parse(timeEndStr)?.time ?: 0L

            dtStart = simpleDateTimeFormat.format(dateStartTimestamp+timeStartTimestamp)
            dtEnd = simpleDateTimeFormat.format(dateEndTimestamp+timeEndTimestamp)
        }

        return VEventBuilder().apply {
            setSummary(viewBinding.fragmentBarcodeFormCreatorQrAgendaSummaryInputEditText.text.toString())
            setDtStart(dtStart)
            setDtEnd(dtEnd)
            setLocation(viewBinding.fragmentBarcodeFormCreatorQrAgendaPlaceInputEditText.text.toString())
            setDescription(viewBinding.fragmentBarcodeFormCreatorQrAgendaDescriptionInputEditText.text.toString())
        }.build()
    }

    private fun configureOnClickAllOfDayCheckBox(checkBox: CheckBox) {

        checkBox.setOnClickListener {
            if(checkBox.isChecked){
                viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.visibility=View.GONE
                viewBinding.fragmentBarcodeFormCreatorQrAgendaEndTimePicker.visibility=View.GONE
            }else{
                viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.visibility=View.VISIBLE
                viewBinding.fragmentBarcodeFormCreatorQrAgendaEndTimePicker.visibility=View.VISIBLE
            }
        }
    }

    private fun configureOnClickDateTimePicker(){
        // Begin Date
        viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginDatePicker.setOnClickListener {
            showDatePickerDialog(viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginDatePicker)
        }

        // BeginTime
        viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.setOnClickListener {
            if(!viewBinding.fragmentBarcodeFormCreatorQrAgendaAllOfDayCheckBox.isChecked)
                showTimePickerDialog(viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker)
        }

        // End Date
        viewBinding.fragmentBarcodeFormCreatorQrAgendaEndDatePicker.setOnClickListener {
            showDatePickerDialog(viewBinding.fragmentBarcodeFormCreatorQrAgendaEndDatePicker)
        }

        // End Time
        viewBinding.fragmentBarcodeFormCreatorQrAgendaEndTimePicker.setOnClickListener {
            if(!viewBinding.fragmentBarcodeFormCreatorQrAgendaAllOfDayCheckBox.isChecked)
                showTimePickerDialog(viewBinding.fragmentBarcodeFormCreatorQrAgendaEndTimePicker)
        }
    }

    private fun showDatePickerDialog(textView: TextView) {
        val fragment = DatePickerFragment.newInstance  {
            textView.text = it
        }
        fragment.show(requireActivity().supportFragmentManager, "dateTag")
    }

    private fun showTimePickerDialog(textView: TextView) {
        val fragment = TimePickerFragment.newInstance {
            textView.text = it
        }
        fragment.show(requireActivity().supportFragmentManager, "timeTag")
    }
}