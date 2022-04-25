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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeCreatorForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.presentation.views.fragments.android.DatePickerFragment
import com.atharok.barcodescanner.presentation.views.fragments.android.TimePickerFragment
import com.atharok.barcodescanner.databinding.FragmentFormCreateQrCodeAgendaBinding
import com.atharok.barcodescanner.domain.library.VEventBuilder
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FormCreateQrCodeAgendaFragment : AbstractFormCreateBarcodeFragment() {

    private val date: Date = get()
    private val simpleDateTimeFormat: SimpleDateFormat = get { parametersOf("yyyyMMdd'T'HHmmss'Z'") }
    private val simpleDateFormat: SimpleDateFormat = get { parametersOf("yyyy-MM-dd") }
    private val simpleTimeFormat: SimpleDateFormat = get { parametersOf("HH:mm") }

    private var _binding: FragmentFormCreateQrCodeAgendaBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormCreateQrCodeAgendaBinding.inflate(inflater, container, false)

        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.fragmentFormCreateQrCodeAgendaBeginDatePicker.text = simpleDateFormat.format(date)
        viewBinding.fragmentFormCreateQrCodeAgendaBeginTimePicker.text = simpleTimeFormat.format(date)
        viewBinding.fragmentFormCreateQrCodeAgendaEndDatePicker.text = viewBinding.fragmentFormCreateQrCodeAgendaBeginDatePicker.text
        viewBinding.fragmentFormCreateQrCodeAgendaEndTimePicker.text = viewBinding.fragmentFormCreateQrCodeAgendaBeginTimePicker.text

        configureOnClickAllOfDayCheckBox(viewBinding.fragmentFormCreateQrCodeAgendaAllOfDayCheckBox)
        configureOnClickDateTimePicker()
    }

    override fun generateBarcodeTextFromForm(): String {

        val dtStart: String
        val dtEnd: String

        if(viewBinding.fragmentFormCreateQrCodeAgendaAllOfDayCheckBox.isChecked){
            dtStart = viewBinding.fragmentFormCreateQrCodeAgendaBeginDatePicker.text.toString().replace("-", "")
            dtEnd = viewBinding.fragmentFormCreateQrCodeAgendaEndDatePicker.text.toString().replace("-", "")

        }else{
            val dateStartStr = viewBinding.fragmentFormCreateQrCodeAgendaBeginDatePicker.text.toString()
            val dateEndStr = viewBinding.fragmentFormCreateQrCodeAgendaEndDatePicker.text.toString()
            val timeStartStr = viewBinding.fragmentFormCreateQrCodeAgendaBeginTimePicker.text.toString()
            val timeEndStr = viewBinding.fragmentFormCreateQrCodeAgendaEndTimePicker.text.toString()

            val dateStartTimestamp = simpleDateFormat.parse(dateStartStr)?.time ?: 0L
            val dateEndTimestamp = simpleDateFormat.parse(dateEndStr)?.time ?: 0L
            val timeStartTimestamp = simpleTimeFormat.parse(timeStartStr)?.time ?: 0L
            val timeEndTimestamp = simpleTimeFormat.parse(timeEndStr)?.time ?: 0L

            dtStart = simpleDateTimeFormat.format(dateStartTimestamp+timeStartTimestamp)
            dtEnd = simpleDateTimeFormat.format(dateEndTimestamp+timeEndTimestamp)
        }

        return VEventBuilder().apply {
            setSummary(viewBinding.fragmentFormCreateQrCodeAgendaSummaryInputEditText.text.toString())
            setDtStart(dtStart)
            setDtEnd(dtEnd)
            setLocation(viewBinding.fragmentFormCreateQrCodeAgendaPlaceInputEditText.text.toString())
            setDescription(viewBinding.fragmentFormCreateQrCodeAgendaDescriptionInputEditText.text.toString())
        }.build()
    }

    private fun configureOnClickAllOfDayCheckBox(checkBox: CheckBox) {

        checkBox.setOnClickListener {
            if(checkBox.isChecked){
                viewBinding.fragmentFormCreateQrCodeAgendaBeginTimePicker.visibility=View.GONE
                viewBinding.fragmentFormCreateQrCodeAgendaEndTimePicker.visibility=View.GONE
            }else{
                viewBinding.fragmentFormCreateQrCodeAgendaBeginTimePicker.visibility=View.VISIBLE
                viewBinding.fragmentFormCreateQrCodeAgendaEndTimePicker.visibility=View.VISIBLE
            }
        }
    }

    private fun configureOnClickDateTimePicker(){
        // Begin Date
        viewBinding.fragmentFormCreateQrCodeAgendaBeginDatePicker.setOnClickListener {
            showDatePickerDialog(viewBinding.fragmentFormCreateQrCodeAgendaBeginDatePicker)
        }

        // BeginTime
        viewBinding.fragmentFormCreateQrCodeAgendaBeginTimePicker.setOnClickListener {
            if(!viewBinding.fragmentFormCreateQrCodeAgendaAllOfDayCheckBox.isChecked)
                showTimePickerDialog(viewBinding.fragmentFormCreateQrCodeAgendaBeginTimePicker)
        }

        // End Date
        viewBinding.fragmentFormCreateQrCodeAgendaEndDatePicker.setOnClickListener {
            showDatePickerDialog(viewBinding.fragmentFormCreateQrCodeAgendaEndDatePicker)
        }

        // End Time
        viewBinding.fragmentFormCreateQrCodeAgendaEndTimePicker.setOnClickListener {
            if(!viewBinding.fragmentFormCreateQrCodeAgendaAllOfDayCheckBox.isChecked)
                showTimePickerDialog(viewBinding.fragmentFormCreateQrCodeAgendaEndTimePicker)
        }
    }

    private fun showDatePickerDialog(textView: TextView) {
        val fragment = DatePickerFragment.newInstance(textView)
        fragment.show(requireActivity().supportFragmentManager, "dateTag")
    }

    private fun showTimePickerDialog(textView: TextView) {
        val fragment = TimePickerFragment.newInstance(textView)
        fragment.show(requireActivity().supportFragmentManager, "timeTad")
    }
}
