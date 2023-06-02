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
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentBarcodeFormCreatorQrWifiBinding
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType

/**
 * A simple [Fragment] subclass.
 */
class BarcodeFormCreatorQrWifiFragment : AbstractBarcodeFormCreatorQrFragment() {

    private var _binding: FragmentBarcodeFormCreatorQrWifiBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeFormCreatorQrWifiBinding.inflate(inflater, container, false)
        configureMenu()
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureWifiEncryptionAutoCompleteTextView()
    }

    override fun getBarcodeTextFromForm(): String {
        val ssid = viewBinding.fragmentBarcodeFormCreatorQrWifiSsidInputEditText.text.toString()
        val password = viewBinding.fragmentBarcodeFormCreatorQrWifiPasswordInputEditText.text.toString()
        val encryption = getEncryption()
        val hide = viewBinding.fragmentBarcodeFormCreatorQrWifiHideCheckBox.isChecked

        return "WIFI:T:$encryption;S:$ssid;P:$password;H:$hide;"
    }

    private fun configureWifiEncryptionAutoCompleteTextView(){
        val spinnerArray = arrayOf(
            getString(R.string.spinner_wifi_encryption_wep),
            getString(R.string.spinner_wifi_encryption_wpa),
            getString(R.string.spinner_wifi_encryption_none)
        )

        val spinnerAdapter = ArrayAdapter<String>(requireContext(), R.layout.template_spinner_item, spinnerArray)
        spinnerAdapter.setDropDownViewResource(R.layout.template_spinner_item)
        viewBinding.fragmentBarcodeFormCreatorQrWifiEncryptionAutoCompleteTextView.setAdapter(spinnerAdapter)
        viewBinding.fragmentBarcodeFormCreatorQrWifiEncryptionAutoCompleteTextView.setText(spinnerAdapter.getItem(0), false)
    }

    private fun getEncryption(): String = when(viewBinding.fragmentBarcodeFormCreatorQrWifiEncryptionAutoCompleteTextView.text.toString()) {
        getString(R.string.spinner_wifi_encryption_wep) -> "WEP"
        getString(R.string.spinner_wifi_encryption_wpa) -> "WPA"
        getString(R.string.spinner_wifi_encryption_none) -> "nopass"
        else -> "WEP"
    }

    override fun getBarcodeType(): BarcodeType = BarcodeType.WIFI
}