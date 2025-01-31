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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.library.wifiSetup.WifiConnect
import com.atharok.barcodescanner.domain.library.wifiSetup.data.WifiSetupData
import com.atharok.barcodescanner.presentation.intent.createPickWifiNetworkIntent
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.WifiParsedResult

class WifiActionsFragment: AbstractParsedResultActionsFragment() {

    override fun configureActionItems(barcode: Barcode, parsedResult: ParsedResult) {
        if(parsedResult is WifiParsedResult) {
            addActionItem(configureWifiActionItem(parsedResult))
        }
        addActionItem(configureSearchOnWebActionItem(barcode))
        addActionItem(configureShareTextActionItem(barcode))
        addActionItem(configureCopyTextActionItem(barcode))
        addActionItem(configureModifyBarcodeActionItem(barcode))
        addActionItem(configureAssignANameToBarcodeActionItem(barcode))
    }

    private fun configureWifiActionItem(parsedResult: WifiParsedResult): ActionItem {
        return ActionItem(
            textRes = R.string.qr_code_type_name_wifi,
            imageRes = R.drawable.baseline_wifi_24,
            listener = showWifiAlertDialog(parsedResult)
        )
    }

    private fun showWifiAlertDialog(parsedResult: WifiParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val items = arrayOf<Triple<String, Int, ActionItem.OnActionItemListener>>(
                Triple(getString(R.string.action_wifi_connection_from_app), R.drawable.baseline_wifi_24, connectToWifiFromApp(parsedResult)),
                Triple(getString(R.string.action_wifi_connection_from_wifi_settings), R.drawable.baseline_settings_24, connectToWifiFromWifiSettings(parsedResult))
            )

            createAlertDialog(requireContext(), getString(R.string.qr_code_type_name_wifi), items).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private val wifiPreviewRequest: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){

                val data = result.data
                if (data?.hasExtra(Settings.EXTRA_WIFI_NETWORK_RESULT_LIST) == true) {

                    val wifiNetworkResultList: IntArray? =
                        data.getIntegerArrayListExtra(Settings.EXTRA_WIFI_NETWORK_RESULT_LIST)?.toIntArray()

                    wifiNetworkResultList?.forEach { code ->

                        val message = when (code) {
                            Settings.ADD_WIFI_RESULT_SUCCESS -> getString(R.string.action_wifi_add_network_successful)
                            Settings.ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED -> getString(R.string.action_wifi_add_network_failed)
                            Settings.ADD_WIFI_RESULT_ALREADY_EXISTS -> getString(R.string.action_wifi_add_network_already_exists)
                            else -> getString(R.string.action_wifi_add_network_unknown_error)
                        }
                        showSnackbar(message)
                    }
                }
            }else{
                showSnackbar(getString(R.string.action_wifi_add_network_refused))
            }
        }

    private fun connectToWifiFromApp(parsedResult: WifiParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val data: WifiSetupData = configureWifiSetupData(parsedResult)
            val wifiConnect = WifiConnect()

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> wifiConnect.connectWithApiR(data, wifiPreviewRequest)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> wifiConnect.connectWithApiQ(data) {
                    showSnackbar(getString(it))
                }
                else -> wifiConnect.connectWithApiOld(data) {
                    showSnackbar(getString(it))
                }
            }
        }
    }

    /**
     * Copie le mot de passe dans le presse papier et ouvre les paramètres Wifi.
     */
    private fun connectToWifiFromWifiSettings(parsedResult: WifiParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            copyToClipboard("password", parsedResult.password)
            showToastText(R.string.action_wifi_password_copy_label)

            val intent: Intent = createPickWifiNetworkIntent()
            startActivity(intent)
        }
    }

    private fun configureWifiSetupData(parsedResult: WifiParsedResult): WifiSetupData {
        return WifiSetupData(
            authType = parsedResult.networkEncryption ?: "",
            name = parsedResult.ssid ?: "",
            password = parsedResult.password ?: "",
            isHidden = parsedResult.isHidden,
            anonymousIdentity = "",
            identity = "",
            eapMethod = "",
            phase2Method = ""
        )
    }
}