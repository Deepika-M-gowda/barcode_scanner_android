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
import com.atharok.barcodescanner.databinding.FragmentBarcodeFormCreatorQrMailBinding
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.google.zxing.BarcodeFormat

/**
 * A simple [Fragment] subclass.
 */
class BarcodeFormCreatorQrMailFragment : AbstractBarcodeFormCreatorQrFragment() {

    private var _binding: FragmentBarcodeFormCreatorQrMailBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeFormCreatorQrMailBinding.inflate(inflater, container, false)
        configureMenu()
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun getBarcodeTextFromForm(): String {
        val email = viewBinding.fragmentBarcodeFormCreatorQrMailToInputEditText.text.toString()
        val subject = viewBinding.fragmentBarcodeFormCreatorQrMailSubjectInputEditText.text.toString()
        val message = viewBinding.fragmentBarcodeFormCreatorQrMailMessageInputEditText.text.toString()

        return if(email.isBlank() && subject.isBlank() && message.isBlank())
            "" else "MATMSG:TO:$email;SUB:$subject;BODY:$message;;"
    }

    override fun generateBarcode() {
        val barcodeContents: String = getBarcodeTextFromForm()

        if (barcodeContents.isBlank()) {
            configureErrorMessage(getString(R.string.error_barcode_qr_email_missing_message))
            return
        }

        hideErrorMessage()
        startBarcodeDetailsActivity(barcodeContents, BarcodeFormat.QR_CODE, getQrCodeErrorCorrectionLevel())
    }

    override fun getBarcodeType(): BarcodeType = BarcodeType.MAIL
}