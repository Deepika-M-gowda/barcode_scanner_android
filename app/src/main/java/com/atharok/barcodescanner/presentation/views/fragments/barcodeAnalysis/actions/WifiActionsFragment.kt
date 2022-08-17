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

import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.utils.INTENT_PICK_WIFI_NETWORK
import com.atharok.barcodescanner.common.utils.INTENT_WIFI_ADD_NETWORKS
import com.atharok.barcodescanner.domain.entity.action.ActionEnum
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.library.wifiSetup.configuration.WifiSetupWithNewLibrary
import com.atharok.barcodescanner.domain.library.wifiSetup.configuration.WifiSetupWithOldLibrary
import com.atharok.barcodescanner.domain.library.wifiSetup.data.WifiSetupData
import com.atharok.barcodescanner.presentation.views.activities.BarcodeAnalysisActivity
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType
import com.google.zxing.client.result.WifiParsedResult
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class WifiActionsFragment: ActionsFragment() {

    private val wifiManager: WifiManager by inject()

    override fun start(barcode: Barcode, parsedResult: ParsedResult) {

        addSearchWithEngineActionFAB(barcode.contents)

        if(parsedResult.type == ParsedResultType.WIFI && parsedResult is WifiParsedResult) {
            viewBinding.fragmentBarcodeActionsFloatingActionMenu.addItem(ActionEnum.CONFIGURE_WIFI.drawableResource) {
                configureAlertDialog(parsedResult)
            }
        }
    }

    private fun configureAlertDialog(parsedResult: WifiParsedResult){

        val labelsList = arrayOf(
            getString(R.string.action_wifi_connection_from_app),
            getString(R.string.action_wifi_connection_from_wifi_settings)
        )

        val onClickListener = DialogInterface.OnClickListener { _, i ->
            when(i){
                0 -> connectToWifiFromApp(parsedResult)
                1 -> connectToWifiFromWifiSettings(parsedResult)
            }
        }

        AlertDialog.Builder(requireActivity()).apply {
            setTitle(R.string.action_wifi_connection_title_label)
            setNegativeButton(R.string.close_dialog_label) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            setItems(labelsList, onClickListener)
        }.create().show()
    }

    /**
     * Copie le mot de passe dans le presse papier et ouvre les paramètres Wifi.
     */
    private fun connectToWifiFromWifiSettings(parsedResult: WifiParsedResult){
        val clipboard: ClipboardManager = get()
        val clip = ClipData.newPlainText("password", parsedResult.password)
        clipboard.setPrimaryClip(clip)
        showToastText(R.string.action_wifi_password_copy_label)

        val intent: Intent = get(named(INTENT_PICK_WIFI_NETWORK))

        startActivity(intent)
    }

    // ---- Wifi Connection ----

    private fun connectToWifiFromApp(parsedResult: WifiParsedResult){

        val data: WifiSetupData = myScope.get { parametersOf(parsedResult) }

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> connectWithApiR(data)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> connectWithApiQ(data)
            else -> connectWithApiOld(data)
        }
    }

    // ---- API 30 ----

    @RequiresApi(Build.VERSION_CODES.R)
    private val previewRequest: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK){

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

    @RequiresApi(Build.VERSION_CODES.R)
    private fun connectWithApiR(data: WifiSetupData){
        val conf: WifiNetworkSuggestion? = get<WifiSetupWithNewLibrary>().configure(data)
        if(conf!=null) {

            val bundle = get<Bundle>().apply {
                putParcelableArrayList(Settings.EXTRA_WIFI_NETWORK_LIST, arrayListOf(conf))
            }

            val intent: Intent = get<Intent>(named(INTENT_WIFI_ADD_NETWORKS)).apply {
                putExtras(bundle)
            }

            previewRequest.launch(intent)
        }
    }

    // ---- API 29 ----

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectWithApiQ(data: WifiSetupData){
        val conf: WifiNetworkSuggestion? = get<WifiSetupWithNewLibrary>().configure(data)
        if(conf!=null) {
            val response = wifiManager.addNetworkSuggestions(listOf(conf))

            val message = when(response){
                WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS -> getString(R.string.action_wifi_add_network_successful)
                WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_INTERNAL -> getString(R.string.action_wifi_add_network_failed)
                WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED -> getString(R.string.action_wifi_add_network_refused)
                WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE -> getString(R.string.action_wifi_add_network_already_exists)
                WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_EXCEEDS_MAX_PER_APP -> getString(R.string.action_wifi_add_network_failed)
                WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_REMOVE_INVALID -> getString(R.string.action_wifi_add_network_failed)
                WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_NOT_ALLOWED -> getString(R.string.action_wifi_add_network_refused)
                WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_INVALID -> getString(R.string.action_wifi_add_network_failed)
                else -> getString(R.string.action_wifi_add_network_unknown_error)
            }

            showSnackbar(message)
        }
    }

    // ---- API 28 and less ----
    @Suppress("DEPRECATION")
    private fun connectWithApiOld(data: WifiSetupData){
        if(wifiManager.isWifiEnabled){

            val conf: android.net.wifi.WifiConfiguration? = get<WifiSetupWithOldLibrary>().configure(data)
            if(conf!=null) {
                if (!wifiManager.isWifiEnabled)
                    wifiManager.isWifiEnabled = true
                wifiManager.disconnect()
                wifiManager.enableNetwork(wifiManager.addNetwork(conf), true)

                val message = if(wifiManager.reconnect())
                    getString(R.string.action_wifi_add_network_successful)
                else
                    getString(R.string.action_wifi_add_network_failed)

                showSnackbar(message)
            }
        }else{
            showToastText(R.string.action_wifi_connection_error_wifi_not_activated)
        }
    }

    // ---- Snackbar ----
    private fun showSnackbar(text: String) {
        val activity = requireActivity()
        if(activity is BarcodeAnalysisActivity) {
            activity.showSnackbar(text)
        }
    }
    /*private fun showSnackbar(text: String) {
        Snackbar.make(viewBinding.root, text, Snackbar.LENGTH_SHORT).show()
    }*/
}