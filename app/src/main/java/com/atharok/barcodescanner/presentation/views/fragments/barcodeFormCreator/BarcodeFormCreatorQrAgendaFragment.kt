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
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.databinding.FragmentBarcodeFormCreatorQrAgendaBinding
import com.atharok.barcodescanner.domain.library.VEventBuilder
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.INPUT_MODE_CALENDAR
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * A simple [Fragment] subclass.
 */
class BarcodeFormCreatorQrAgendaFragment : AbstractBarcodeFormCreatorQrFragment() {

    private val date: Date = get()
    private val simpleDateTimeFormat: SimpleDateFormat = get { parametersOf("yyyyMMdd'T'HHmmss'Z'") }
    private val simpleDateFormat: SimpleDateFormat = get { parametersOf("yyyy-MM-dd") }
    private val simpleTimeFormat: SimpleDateFormat = get { parametersOf("HH:mm z") }

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

        viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginDatePicker.setContent(simpleDateFormat.format(date))
        viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.setContent(simpleTimeFormat.format(date))
        viewBinding.fragmentBarcodeFormCreatorQrAgendaEndDatePicker.setContent(viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginDatePicker.getContent())
        viewBinding.fragmentBarcodeFormCreatorQrAgendaEndTimePicker.setContent(viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.getContent())

        configureOnClickAllOfDayCheckBox(viewBinding.fragmentBarcodeFormCreatorQrAgendaAllOfDayCheckBox)
        configureOnClickDateTimePicker()
    }

    override fun getBarcodeTextFromForm(): String {
        val dtStart: String
        val dtEnd: String

        if(viewBinding.fragmentBarcodeFormCreatorQrAgendaAllOfDayCheckBox.isChecked) {
            dtStart = viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginDatePicker.getContent().replace("-", "")
            dtEnd = viewBinding.fragmentBarcodeFormCreatorQrAgendaEndDatePicker.getContent().replace("-", "")
        } else {
            val dateStartStr = viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginDatePicker.getContent()
            val dateEndStr = viewBinding.fragmentBarcodeFormCreatorQrAgendaEndDatePicker.getContent()
            val timeStartStr = viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.getContent()
            val timeEndStr = viewBinding.fragmentBarcodeFormCreatorQrAgendaEndTimePicker.getContent()

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
            if(checkBox.isChecked) {
                viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.visibility=View.GONE
                viewBinding.fragmentBarcodeFormCreatorQrAgendaEndTimePicker.visibility=View.GONE
            } else {
                viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.visibility=View.VISIBLE
                viewBinding.fragmentBarcodeFormCreatorQrAgendaEndTimePicker.visibility=View.VISIBLE
            }
        }
    }

    // ---- Date Time Picker ----

    private fun configureOnClickDateTimePicker(){
        // Begin Date
        viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginDatePicker.let { cardIconTextView ->
            cardIconTextView.getRoot().setOnClickListener {
                showDatePickerDialog({ cardIconTextView.setContent(it) }, "Begin Date")
            }
        }

        // BeginTime
        viewBinding.fragmentBarcodeFormCreatorQrAgendaBeginTimePicker.let { cardIconTextView ->
            cardIconTextView.getRoot().setOnClickListener {
                if(!viewBinding.fragmentBarcodeFormCreatorQrAgendaAllOfDayCheckBox.isChecked) {
                    showTimePickerDialog({ cardIconTextView.setContent(it) }, "Begin Time")
                }
            }
        }

        // End Date
        viewBinding.fragmentBarcodeFormCreatorQrAgendaEndDatePicker.let { cardIconTextView ->
            cardIconTextView.getRoot().setOnClickListener {
                showDatePickerDialog({ cardIconTextView.setContent(it) }, "End Date")
            }
        }

        // End Time
        viewBinding.fragmentBarcodeFormCreatorQrAgendaEndTimePicker.let { cardIconTextView ->
            cardIconTextView.getRoot().setOnClickListener {
                if(!viewBinding.fragmentBarcodeFormCreatorQrAgendaAllOfDayCheckBox.isChecked){
                    showTimePickerDialog({ cardIconTextView.setContent(it) }, "End Time")
                }
            }
        }
    }

    private val calendar: Calendar by lazy { Calendar.getInstance() }

    private fun showDatePickerDialog(onPositiveClick: (String) -> Unit, tag: String) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setInputMode(INPUT_MODE_CALENDAR)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            onPositiveClick(simpleDateFormat.format(it))
        }

        datePicker.show(requireActivity().supportFragmentManager, tag)
    }

    private fun showTimePickerDialog(onPositiveClick: (String) -> Unit, tag: String) {
        val timePicker = MaterialTimePicker.Builder()
            .setInputMode(INPUT_MODE_CLOCK)
            .setTimeFormat(
                if(DateFormat.is24HourFormat(requireContext())) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
            )
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .build()

        timePicker.addOnPositiveButtonClickListener {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
            onPositiveClick(simpleTimeFormat.format(calendar.time))
        }

        timePicker.show(requireActivity().supportFragmentManager, tag)
    }
}