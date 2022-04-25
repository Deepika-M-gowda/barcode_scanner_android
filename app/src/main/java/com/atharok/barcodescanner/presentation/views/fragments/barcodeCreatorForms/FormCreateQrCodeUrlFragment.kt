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
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentFormCreateQrCodeUrlBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class FormCreateQrCodeUrlFragment : AbstractFormCreateBarcodeFragment() {

    private var _binding: FragmentFormCreateQrCodeUrlBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormCreateQrCodeUrlBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun generateBarcodeTextFromForm(): String {

        closeVirtualKeyBoard(viewBinding.fragmentFormCreateQrCodeUrlInputEditText)

        val qrText: String = viewBinding.fragmentFormCreateQrCodeUrlInputEditText.text.toString()

        return if(qrText.startsWith("http://") || qrText.startsWith("https://")) {
            //activityCallback?.showQrCode(qrText)

            qrText
        }else {
            Snackbar.make(
                viewBinding.root,
                getString(R.string.snack_bar_message_error_url_format),
                Snackbar.LENGTH_SHORT
            ).show()
            ""
        }
    }
}
