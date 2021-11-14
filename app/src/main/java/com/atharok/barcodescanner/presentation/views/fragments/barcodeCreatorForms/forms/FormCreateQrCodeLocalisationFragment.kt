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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeCreatorForms.forms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentFormCreateQrCodeLocalisationBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class FormCreateQrCodeLocalisationFragment : AbstractFormCreateBarcodeFragment() {

    private var _binding: FragmentFormCreateQrCodeLocalisationBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormCreateQrCodeLocalisationBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun generateBarcodeTextFromForm(): String {

        val latitude = viewBinding.fragmentFormCreateQrCodeLocalisationLatitudeInputEditText.text.toString()
        val longitude = viewBinding.fragmentFormCreateQrCodeLocalisationLongitudeInputEditText.text.toString()
        val request = viewBinding.fragmentFormCreateQrCodeLocalisationRequestInputEditText.text.toString()

        val qrText: String = if(latitude != "" && longitude != ""){

            if(request!="")
                "geo:$latitude,$longitude?q=$request"
            else
                "geo:$latitude,$longitude"

        }else{
            Snackbar.make(viewBinding.root,
                getString(R.string.snack_bar_message_error_localisation_missing),
                Snackbar.LENGTH_SHORT).show()
            ""
        }

        return qrText
    }
}
