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
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentFormCreateQrCodeWifiBinding

/**
 * A simple [Fragment] subclass.
 */
class FormCreateQrCodeWifiFragment : AbstractFormCreateBarcodeFragment() {

    private var _binding: FragmentFormCreateQrCodeWifiBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormCreateQrCodeWifiBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureSpinner()
    }

    override fun generateBarcodeTextFromForm(): String {
        val ssid = viewBinding.fragmentFormCreateQrCodeWifiSsidInputEditText.text.toString()
        val password = viewBinding.fragmentFormCreateQrCodeWifiPasswordInputEditText.text.toString()
        val encryption = getEncryption(viewBinding.fragmentFormCreateQrCodeWifiEncryptionSpinner.selectedItem as String)
        val hide = viewBinding.fragmentFormCreateQrCodeWifiHideCheckBox.isChecked

        return "WIFI:T:$encryption;S:$ssid;P:$password;H:$hide;"
    }

    private fun configureSpinner(){
        val spinnerArray = arrayOf(
            getString(R.string.spinner_wifi_encryption_wep),
            getString(R.string.spinner_wifi_encryption_wpa),
            getString(R.string.spinner_wifi_encryption_none))

        val spinnerAdapter = ArrayAdapter<String>(requireContext(), R.layout.template_spinner_item, spinnerArray)
        spinnerAdapter.setDropDownViewResource(R.layout.template_spinner_item)
        viewBinding.fragmentFormCreateQrCodeWifiEncryptionSpinner.adapter=spinnerAdapter
    }

    private fun getEncryption(selectedItem: String): String {
        return when(selectedItem) {
            getString(R.string.spinner_wifi_encryption_wep) -> "WEP"
            getString(R.string.spinner_wifi_encryption_wpa) -> "WPA"
            getString(R.string.spinner_wifi_encryption_none) -> "nopass"
            else -> "WEP"
        }
    }
}
