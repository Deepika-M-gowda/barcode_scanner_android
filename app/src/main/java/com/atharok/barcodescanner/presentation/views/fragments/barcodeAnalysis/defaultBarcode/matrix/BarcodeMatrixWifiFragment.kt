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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentBarcodeMatrixWifiBinding
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType
import com.google.zxing.client.result.WifiParsedResult

/**
 * A simple [Fragment] subclass.
 */
class BarcodeMatrixWifiFragment : AbstractBarcodeMatrixFragment() {
    private var _binding: FragmentBarcodeMatrixWifiBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeMatrixWifiBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: BarcodeAnalysis, parsedResult: ParsedResult) {

        if(parsedResult is WifiParsedResult && parsedResult.type == ParsedResultType.WIFI) {
            configureSSID(parsedResult.ssid)
            configurePassword(parsedResult.password)
            configureEncryption(parsedResult.networkEncryption)
            configureIsHidden(parsedResult.isHidden)

            /*configureAnonymousIdentity(parsedResult.anonymousIdentity)
            configureIdentity(parsedResult.identity)
            configureEapMethod(parsedResult.eapMethod)
            configurePhase2Method(parsedResult.phase2Method)*/

        }else{
            viewBinding.root.visibility = View.GONE
        }
    }

    private fun configureSSID(ssid: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixWifiSsidTextView,
        layout = viewBinding.fragmentBarcodeMatrixWifiSsidLayout,
        text = ssid
    )

    private fun configurePassword(password: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixWifiPasswordTextView,
        layout = viewBinding.fragmentBarcodeMatrixWifiPasswordLayout,
        text = password
    )

    private fun configureEncryption(encryption: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixWifiEncryptionTextView,
        layout = viewBinding.fragmentBarcodeMatrixWifiEncryptionLayout,
        text = encryption
    )

    private fun configureIsHidden(isHidden: Boolean?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixWifiIsHiddenTextView,
        layout = viewBinding.fragmentBarcodeMatrixWifiIsHiddenLayout,
        text = if(isHidden == true) getString(R.string.yes_label) else getString(R.string.no_label)
    )

    /*private fun configureAnonymousIdentity(anonymousIdentity: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixWifiAnonymousIdentityTextView,
        layout = viewBinding.fragmentBarcodeMatrixWifiAnonymousIdentityLayout,
        text = anonymousIdentity
    )

    private fun configureIdentity(identity: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixWifiIdentityTextView,
        layout = viewBinding.fragmentBarcodeMatrixWifiIdentityLayout,
        text = identity
    )

    private fun configureEapMethod(eapMethod: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixWifiEapMethodTextView,
        layout = viewBinding.fragmentBarcodeMatrixWifiEapMethodLayout,
        text = eapMethod
    )

    private fun configurePhase2Method(phase2Method: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixWifiPhase2MethodTextView,
        layout = viewBinding.fragmentBarcodeMatrixWifiPhase2MethodLayout,
        text = phase2Method
    )*/
}