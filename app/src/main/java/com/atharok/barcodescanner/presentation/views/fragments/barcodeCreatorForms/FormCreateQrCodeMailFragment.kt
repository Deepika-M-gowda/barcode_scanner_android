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
import com.atharok.barcodescanner.databinding.FragmentFormCreateQrCodeMailBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class FormCreateQrCodeMailFragment : AbstractFormCreateBarcodeFragment() {

    private var _binding: FragmentFormCreateQrCodeMailBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormCreateQrCodeMailBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun generateBarcodeTextFromForm(): String {
        val email = viewBinding.fragmentFormCreateQrCodeMailToInputEditText.text.toString()
        val subject = viewBinding.fragmentFormCreateQrCodeMailSubjectInputEditText.text.toString()
        val message = viewBinding.fragmentFormCreateQrCodeMailMessageInputEditText.text.toString()

        return if(email=="" && subject=="" && message==""){
            Snackbar.make(viewBinding.root,
                getString(R.string.snack_bar_message_error_email_missing),
                Snackbar.LENGTH_SHORT).show()
            ""
        } else {
            "MATMSG:TO:$email;SUB:$subject;BODY:$message;;"
        }
    }
}
