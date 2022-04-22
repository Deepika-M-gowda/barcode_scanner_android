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

package com.atharok.barcodescanner.domain.library.wifiSetup.configuration

import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
open class WifiSetupWithNewLibrary: WifiSetup<WifiNetworkSuggestion> {

    override fun configureOpenNetwork(
        name: String,
        isHidden: Boolean
    ): WifiNetworkSuggestion = WifiNetworkSuggestion.Builder().apply {
        setSsid(name)
        setIsHiddenSsid(isHidden)
    }.build()

    override fun configureWpa2Network(
        name: String,
        password: String,
        isHidden: Boolean
    ): WifiNetworkSuggestion = WifiNetworkSuggestion.Builder().apply {
        setSsid(name)
        setWpa2Passphrase(password)
        setIsHiddenSsid(isHidden)
    }.build()

    override fun configureWpa2EapNetwork(
        name: String,
        password: String,
        isHidden: Boolean,
        anonymousIdentity: String,
        identity: String,
        eapMethod: Int?,
        phase2Method: Int?
    ): WifiNetworkSuggestion = WifiNetworkSuggestion.Builder().apply {
        setSsid(name)
        setWpa2Passphrase(password)
        setIsHiddenSsid(isHidden)
        setWpa2EnterpriseConfig(
            getWifiEnterpriseConfig(password, anonymousIdentity, identity, eapMethod, phase2Method)
        )
    }.build()

    override fun configureWpa3Network(
        name: String,
        password: String,
        isHidden: Boolean
    ): WifiNetworkSuggestion = WifiNetworkSuggestion.Builder().apply {
        setSsid(name)
        setWpa3Passphrase(password)
        setIsHiddenSsid(isHidden)
    }.build()

    override fun configureWpa3EapNetwork(
        name: String,
        password: String,
        isHidden: Boolean,
        anonymousIdentity: String,
        identity: String,
        eapMethod: Int?,
        phase2Method: Int?
    ): WifiNetworkSuggestion = WifiNetworkSuggestion.Builder().apply {
        setSsid(name)
        setWpa3Passphrase(password)
        setIsHiddenSsid(isHidden)

        val wifiEnterpriseConfig = getWifiEnterpriseConfig(password, anonymousIdentity, identity, eapMethod, phase2Method)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setWpa3EnterpriseStandardModeConfig(wifiEnterpriseConfig)
        }else {
            @Suppress("DEPRECATION")
            setWpa3EnterpriseConfig(wifiEnterpriseConfig)
        }
    }.build()

    override fun configureWepNetwork(
        name: String,
        password: String,
        isHidden: Boolean
    ): WifiNetworkSuggestion? = null

    private fun getWifiEnterpriseConfig(
        password: String,
        anonymousIdentity: String,
        identity: String,
        eapMethod: Int?,
        phase2Method: Int?
    ) = WifiEnterpriseConfig().also { config ->

        config.anonymousIdentity = anonymousIdentity
        config.identity = identity
        config.password = password

        eapMethod?.apply {
            config.eapMethod = this
        }

        phase2Method?.apply {
            config.phase2Method = this
        }
    }
}