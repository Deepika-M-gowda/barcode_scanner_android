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

package com.atharok.barcodescanner.presentation.views.fragments.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentMainScannerBinding
import com.atharok.barcodescanner.domain.library.SettingsManager
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.common.utils.BARCODE_KEY
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseViewModel
import com.atharok.barcodescanner.presentation.views.activities.BarcodeAnalysisActivity
import com.atharok.barcodescanner.presentation.views.activities.BarcodeScanFromImageActivity
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanOptions.ALL_CODE_TYPES
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

/**
 * A simple [Fragment] subclass.
 */
class MainScannerFragment : Fragment() {

    private val databaseViewModel: DatabaseViewModel by sharedViewModel()

    // ---- Camera Permission ----

    /**
     * Gère le resultat de la demande de permission d'accès à la caméra.
     */
    private val requestPermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it)
                configureScanner()
            else
                showSnackbar(getString(R.string.snack_bar_message_permission_refused))
        }

    // ---- View ----
    private var _binding: FragmentMainScannerBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainScannerBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermission.launch(Manifest.permission.CAMERA)

        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()

        if (checkCameraPermission())
            viewBinding.fragmentMainScannerZxingBarcodeScanner.resume()
    }

    override fun onPause() {
        super.onPause()

        if (checkCameraPermission())
            viewBinding.fragmentMainScannerZxingBarcodeScanner.pause()
    }

    private fun checkCameraPermission(): Boolean {
        val permission: Int = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_scanner, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId){
        R.id.menu_scanner_flash -> {
            switchTorchFlash()
            true
        }
        R.id.menu_scanner_scan_from_image -> {
            startBarcodeScanFromImageActivity()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        if(hasFlash()) {
            if (flashIsEnabled)
                menu.getItem(0).icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.baseline_flash_on_24)
            else
                menu.getItem(0).icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.baseline_flash_off_24)
        }else{
            menu.getItem(0).isVisible = false
        }
    }

    // ---- Flash ----

    private var flashIsEnabled = false

    private fun hasFlash(): Boolean =
        requireContext().applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    private fun configureFlash(){
        viewBinding.fragmentMainScannerZxingBarcodeScanner.setTorchOff()
        flashIsEnabled = false
    }

    private fun switchTorchFlash(){
        flashIsEnabled = !flashIsEnabled

        if(flashIsEnabled) {
            viewBinding.fragmentMainScannerZxingBarcodeScanner.setTorchOn()
        } else {
            viewBinding.fragmentMainScannerZxingBarcodeScanner.setTorchOff()
        }

        requireActivity().invalidateOptionsMenu()
    }

    // ---- Scan via Image ----

    private fun startBarcodeScanFromImageActivity(){
        if(flashIsEnabled)
            switchTorchFlash()

        val intent = Intent(requireContext(), BarcodeScanFromImageActivity::class.java)
        startActivity(intent)
    }

    // ---- Scan ----

    private fun getBeepManager(): BeepManager? {
        val settingsManager = get<SettingsManager>()
        return if(settingsManager.useBipScan || settingsManager.useVibrateScan){
            get<BeepManager> { parametersOf(requireActivity()) }
        } else null
    }

    private fun configureScanner(){

        configureFlash()

        val beepManager: BeepManager? = getBeepManager()

        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ALL_CODE_TYPES)
            setPrompt(getString(R.string.scan_information_label))
            setCameraId(0)
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
            setOrientationLocked(true)
        }
        val contract = ScanContract()

        val intent = contract.createIntent(requireContext(), options)

        viewBinding.fragmentMainScannerZxingBarcodeScanner.initializeFromIntent(intent)
        viewBinding.fragmentMainScannerZxingBarcodeScanner.decodeContinuous {
            beepManager?.playBeepSoundAndVibrate()
            doAfterScan(it)
        }
    }

    /*private fun configureScanner(){

        configureFlash()

        val beepManager: BeepManager? = getBeepManager()

        val scanner = IntentIntegrator.forSupportFragment(this).apply {
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setPrompt(getString(R.string.scan_information_label))
            setCameraId(0)
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
            setOrientationLocked(true)
        }

        viewBinding.fragmentMainScannerZxingBarcodeScanner.initializeFromIntent(scanner.createScanIntent())
        viewBinding.fragmentMainScannerZxingBarcodeScanner.decodeContinuous {
            beepManager?.playBeepSoundAndVibrate()
            doAfterScan(it)
        }
    }*/

    private fun doAfterScan(barcodeResult: BarcodeResult){
        val contents = barcodeResult.text
        val formatName = barcodeResult.barcodeFormat?.name

        if(contents != null && formatName != null){

            if(flashIsEnabled)
                switchTorchFlash()

            val barcode: Barcode = get { parametersOf(contents, formatName) }

            saveIntoDatabase(barcode)
            startBarcodeAnalysisActivity(barcode)
        }else{
            showSnackbar(getString(R.string.scan_cancel_label))
        }
    }

    /**
     * Enregistre dans la base de données, les informations du code-barres qui vient d'être scanné.
     */
    private fun saveIntoDatabase(barcode: Barcode){
        // Insert les informations du code bar dans la base de données (de manière asynchrone)
        databaseViewModel.insertBarcode(barcode)
    }

    /**
     * Démarre l'Activity: BarcodeAnalysisActivity.
     */
    private fun startBarcodeAnalysisActivity(barcode: Barcode){
        val intent = Intent(requireContext(), BarcodeAnalysisActivity::class.java)
        intent.putExtra(BARCODE_KEY, barcode)
        startActivity(intent)
    }

    // ---- Snackbar ----

    private fun showSnackbar(text: String) {
        Snackbar.make(viewBinding.root, text, Snackbar.LENGTH_SHORT).show()
    }
}