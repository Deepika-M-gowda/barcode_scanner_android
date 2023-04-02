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
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentBarcodeFormCreatorQrSmsBinding
import com.google.zxing.BarcodeFormat

/**
 * A simple [Fragment] subclass.
 */
class BarcodeFormCreatorQrSmsFragment : AbstractBarcodeFormCreatorQrFragment() {

    private var _binding: FragmentBarcodeFormCreatorQrSmsBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeFormCreatorQrSmsBinding.inflate(inflater, container, false)
        configureMenu()
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun getBarcodeTextFromForm(): String {
        val number = viewBinding.fragmentBarcodeFormCreatorQrSmsPhoneNumberInputEditText.text.toString()
        val message = viewBinding.fragmentBarcodeFormCreatorQrSmsMessageInputEditText.text.toString()

        val qrText = when {
            number.isNotBlank() && message.isNotBlank() -> "smsto:$number:$message"
            number.isNotBlank() -> "tel:$number" // -> Si pas de message, on propose juste le numéro de téléphone
            else -> ""
        }

        return qrText
    }

    override fun generateBarcode() {
        val barcodeContents: String = getBarcodeTextFromForm()

        if (barcodeContents.isBlank()) {
            configureErrorMessage(getString(R.string.error_barcode_qr_phone_number_missing_message))
            return
        }

        hideErrorMessage()
        startBarcodeDetailsActivity(barcodeContents, BarcodeFormat.QR_CODE)
    }
}