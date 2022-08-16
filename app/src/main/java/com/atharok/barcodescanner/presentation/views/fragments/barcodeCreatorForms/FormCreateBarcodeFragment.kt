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
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.common.extentions.getSerializableAppCompat
import com.atharok.barcodescanner.databinding.FragmentFormCreateBarcodeBinding
import com.atharok.barcodescanner.common.utils.BARCODE_FORMAT_KEY
import com.google.zxing.BarcodeFormat

/**
 * A simple [Fragment] subclass.
 */
class FormCreateBarcodeFragment: AbstractFormCreateBarcodeFragment() {

    private var _binding: FragmentFormCreateBarcodeBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormCreateBarcodeBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf {
            // Si les données OpenFoodFactsData sont bien stockées en mémoire
            it.containsKey(BARCODE_FORMAT_KEY)
        }?.apply {

            getSerializableAppCompat(BARCODE_FORMAT_KEY, BarcodeFormat::class.java)?.let {
                start(it)
            }
        }
    }

    private fun start(format: BarcodeFormat){

        val length: Int? = when(format){
            BarcodeFormat.EAN_13 -> 13
            BarcodeFormat.EAN_8 -> 8
            BarcodeFormat.UPC_A -> 12
            BarcodeFormat.UPC_E -> 8
            BarcodeFormat.PDF_417 -> 2710
            BarcodeFormat.CODE_128, BarcodeFormat.CODE_93, BarcodeFormat.CODE_39, BarcodeFormat.ITF -> 80
            else -> null
        }

        if(length!=null){
            viewBinding.fragmentFormCreateBarcodeTextInputEditText.filters = arrayOf<InputFilter>(LengthFilter(length))
        }

        val inputType = when(format){
            BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.UPC_A, BarcodeFormat.UPC_E, BarcodeFormat.ITF -> InputType.TYPE_CLASS_NUMBER
            else -> InputType.TYPE_CLASS_TEXT
        }

        viewBinding.fragmentFormCreateBarcodeTextInputEditText.inputType = inputType

    }

    override fun generateBarcodeTextFromForm(): String {
        closeVirtualKeyBoard(viewBinding.fragmentFormCreateBarcodeTextInputEditText)
        return viewBinding.fragmentFormCreateBarcodeTextInputEditText.text.toString()
    }
}