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

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationListenerCompat
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentBarcodeFormCreatorQrLocalisationBinding
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.google.zxing.BarcodeFormat
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class BarcodeFormCreatorQrLocalisationFragment : AbstractBarcodeFormCreatorQrFragment() {

    private var _binding: FragmentBarcodeFormCreatorQrLocalisationBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeFormCreatorQrLocalisationBinding.inflate(inflater, container, false)
        configureMenu()
        updateSearchViews(false)
        configureFindLocalisation()
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopLocation()
        _binding=null
    }

    override fun onPause() {
        super.onPause()
        stopLocation()
    }

    override fun getBarcodeTextFromForm(): String {

        val latitude = viewBinding.fragmentBarcodeFormCreatorQrLocalisationLatitudeInputEditText.text.toString()
        val longitude = viewBinding.fragmentBarcodeFormCreatorQrLocalisationLongitudeInputEditText.text.toString()
        val request = viewBinding.fragmentBarcodeFormCreatorQrLocalisationRequestInputEditText.text.toString()

        return when {
            latitude.isNotBlank() && longitude.isNotBlank() && request.isNotBlank() -> "geo:$latitude,$longitude?q=$request"
            latitude.isNotBlank() && longitude.isNotBlank() && request.isBlank() -> "geo:$latitude,$longitude"
            else -> ""
        }
    }

    override fun generateBarcode() {
        val barcodeContents: String = getBarcodeTextFromForm()

        if (barcodeContents.isBlank()) {
            configureErrorMessage(getString(R.string.error_barcode_qr_localisation_missing_message))
            return
        }

        hideErrorMessage()
        startBarcodeDetailsActivity(barcodeContents, BarcodeFormat.QR_CODE, getQrCodeErrorCorrectionLevel())
    }

    // ---- Permissions ----

    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    /**
     * Gère le resultat de la demande de permission d'accès à la localisation.
     */
    private val requestPermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it) findLocalisation() else stopLocation()
        }


    private fun configureFindLocalisation() {
        viewBinding.fragmentBarcodeFormCreatorQrLocalisationSearchButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                findLocalisation()
            } else {
                requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        viewBinding.fragmentBarcodeFormCreatorQrLocalisationCancelButton.setOnClickListener {
            stopLocation()
        }
    }

    private fun findLocalisation() {
        updateSearchViews(true)

        val locationListener = object : LocationListenerCompat {
            override fun onLocationChanged(location: Location) {
                viewBinding.fragmentBarcodeFormCreatorQrLocalisationSearchInfoLayout.visibility = View.GONE
                viewBinding.fragmentBarcodeFormCreatorQrLocalisationLatitudeInputEditText.setText(location.latitude.toString())
                viewBinding.fragmentBarcodeFormCreatorQrLocalisationLongitudeInputEditText.setText(location.longitude.toString())
            }

            override fun onProviderEnabled(provider: String) {
                super.onProviderEnabled(provider)
                hideErrorMessage()
                viewBinding.fragmentBarcodeFormCreatorQrLocalisationSearchInfoLayout.visibility = View.VISIBLE
            }

            override fun onProviderDisabled(provider: String) {
                super.onProviderDisabled(provider)
                configureErrorMessage(getString(R.string.matrix_localisation_location_disabled_label))
                viewBinding.fragmentBarcodeFormCreatorQrLocalisationSearchInfoLayout.visibility = View.GONE
            }
        }

        val locationManager: LocationManager = get()
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

        this.locationManager = locationManager
        this.locationListener = locationListener
    }

    private fun stopLocation() {
        locationListener?.let {
            locationManager?.removeUpdates(it)
        }
        locationManager = null
        locationListener = null

        updateSearchViews(false)
        hideErrorMessage()
    }

    private fun updateSearchViews(isSearching: Boolean) {
        if(isSearching){
            viewBinding.fragmentBarcodeFormCreatorQrLocalisationSearchButton.visibility = View.GONE
            viewBinding.fragmentBarcodeFormCreatorQrLocalisationCancelButton.visibility = View.VISIBLE
            viewBinding.fragmentBarcodeFormCreatorQrLocalisationSearchInfoLayout.visibility = View.VISIBLE
        } else {
            viewBinding.fragmentBarcodeFormCreatorQrLocalisationSearchButton.visibility = View.VISIBLE
            viewBinding.fragmentBarcodeFormCreatorQrLocalisationCancelButton.visibility = View.GONE
            viewBinding.fragmentBarcodeFormCreatorQrLocalisationSearchInfoLayout.visibility = View.GONE
        }
    }

    override fun getBarcodeType(): BarcodeType = BarcodeType.LOCALISATION
}