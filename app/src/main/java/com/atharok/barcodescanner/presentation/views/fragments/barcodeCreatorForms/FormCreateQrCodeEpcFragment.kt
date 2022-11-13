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
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.databinding.FragmentFormCreateQrCodeEpcBinding
import com.atharok.barcodescanner.domain.library.Iban
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class FormCreateQrCodeEpcFragment: AbstractFormCreateBarcodeFragment() {

    companion object {
        private const val SERVICE_TAG = "BCD"
        private const val VERSION = "002"
        private const val CHARACTER_SET = "2"
        private const val IDENTIFICATION = "SCT"
    }

    private val iban: Iban by inject()
    private val inputMethodManager: InputMethodManager by inject()
    private val stringBuilder = StringBuilder()

    private var _binding: FragmentFormCreateQrCodeEpcBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormCreateQrCodeEpcBinding.inflate(inflater, container, false)
        configureInputEditTexts()
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun generateBarcodeTextFromForm(): String {
        val nameInputEditText = viewBinding.fragmentFormCreateQrCodeEpcNameInputEditText
        val name: String = nameInputEditText.text.toString()
        if(name.isBlank()){
            viewBinding.fragmentFormCreateQrCodeEpcNameErrorTextView.visibility = View.VISIBLE
            nameInputEditText.requestFocus()
            inputMethodManager.showSoftInput(nameInputEditText, InputMethodManager.SHOW_IMPLICIT)
            return ""
        }

        val ibanInputEditText = viewBinding.fragmentFormCreateQrCodeEpcIbanInputEditText
        val ibanText: String = ibanInputEditText.text.toString().uppercase()
        if(!iban.verify(ibanText)) {
            viewBinding.fragmentFormCreateQrCodeEpcIbanErrorTextView.visibility = View.VISIBLE
            ibanInputEditText.requestFocus()
            inputMethodManager.showSoftInput(ibanInputEditText, InputMethodManager.SHOW_IMPLICIT)
            return ""
        }

        stringBuilder.clear()
        stringBuilder.appendLine(SERVICE_TAG)
        stringBuilder.appendLine(VERSION)
        stringBuilder.appendLine(CHARACTER_SET)
        stringBuilder.appendLine(IDENTIFICATION)
        stringBuilder.appendLine(viewBinding.fragmentFormCreateQrCodeEpcBicInputEditText.text.toString())
        stringBuilder.appendLine(name)
        stringBuilder.appendLine(ibanText)
        stringBuilder.appendLine(viewBinding.fragmentFormCreateQrCodeEpcAmountInputEditText.text.toString())
        stringBuilder.appendLine(viewBinding.fragmentFormCreateQrCodeEpcPurposeInputEditText.text.toString())
        stringBuilder.appendLine(viewBinding.fragmentFormCreateQrCodeEpcRemittanceRefInputEditText.text.toString())
        stringBuilder.appendLine(viewBinding.fragmentFormCreateQrCodeEpcRemittanceTextInputEditText.text.toString())
        stringBuilder.appendLine(viewBinding.fragmentFormCreateQrCodeEpcInformationInputEditText.text.toString())

        return stringBuilder.toString().trim()
    }

    private fun configureInputEditTexts() {

        viewBinding.fragmentFormCreateQrCodeEpcIbanInputEditText.addTextChangedListener {
            val errorTextView = viewBinding.fragmentFormCreateQrCodeEpcIbanErrorTextView
            if(errorTextView.visibility == View.VISIBLE) {
                if (iban.verify(it.toString())) {
                    errorTextView.visibility = View.GONE
                }
            }
        }

        viewBinding.fragmentFormCreateQrCodeEpcNameInputEditText.addTextChangedListener {
            if(!it.isNullOrEmpty()){
                viewBinding.fragmentFormCreateQrCodeEpcNameErrorTextView.visibility = View.GONE
            }
        }

        // Si l'un des deux inputEditText suivant contient du texte, on enlève l'autre car seulement un des 2 doit contenir une valeur

        viewBinding.fragmentFormCreateQrCodeEpcRemittanceRefInputEditText.addTextChangedListener {
            viewBinding.fragmentFormCreateQrCodeEpcRemittanceTextInputEditText.apply {
                visibility = if(it.isNullOrEmpty())View.VISIBLE else View.GONE
            }
        }

        viewBinding.fragmentFormCreateQrCodeEpcRemittanceTextInputEditText.addTextChangedListener {
            viewBinding.fragmentFormCreateQrCodeEpcRemittanceRefInputEditText.apply {
                visibility = if(it.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
}