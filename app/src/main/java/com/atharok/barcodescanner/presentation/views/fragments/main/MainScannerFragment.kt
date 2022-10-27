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
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.SCAN_RESULT
import com.atharok.barcodescanner.common.extensions.SCAN_RESULT_FORMAT
import com.atharok.barcodescanner.common.extensions.toIntent
import com.atharok.barcodescanner.databinding.FragmentMainScannerBinding
import com.atharok.barcodescanner.domain.library.SettingsManager
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.common.utils.BARCODE_KEY
import com.atharok.barcodescanner.common.utils.INTENT_START_ACTIVITY
import com.atharok.barcodescanner.domain.library.BeepManager
import com.atharok.barcodescanner.domain.library.VibratorAppCompat
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseViewModel
import com.atharok.barcodescanner.presentation.views.activities.BarcodeAnalysisActivity
import com.atharok.barcodescanner.presentation.views.activities.BarcodeScanFromImageGalleryActivity
import com.atharok.barcodescanner.presentation.views.activities.BaseActivity
import com.atharok.barcodescanner.presentation.views.activities.MainActivity
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment
import com.budiyev.android.codescanner.*
import com.google.zxing.Result
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

/**
 * A simple [Fragment] subclass.
 */
class MainScannerFragment : BaseFragment() {

    companion object {
        private const val ZXING_SCAN_INTENT_ACTION = "com.google.zxing.client.android.SCAN"
    }

    private var canEnableCamera = true

    private var codeScanner: CodeScanner? = null
    private val databaseViewModel: DatabaseViewModel by sharedViewModel()

    // ---- View ----
    private var _binding: FragmentMainScannerBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureResultBarcodeScanFromImageActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        resultBarcodeScanFromImageActivity = null
    }

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

        if(isCameraPermissionGranted()) configureScanner() else askCameraPermission()

        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()

        if (isCameraPermissionGranted() && canEnableCamera) {
            codeScanner?.startPreview()
        }
    }

    override fun onPause() {
        if (isCameraPermissionGranted()) {
            codeScanner?.isFlashEnabled = false
            codeScanner?.releaseResources()
            canEnableCamera=true
        }
        super.onPause()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity: Activity = requireActivity()
        if(activity is BaseActivity){
            if(activity.settingsManager.isAutoScreenRotationScanDisabled) {
                activity.lockDeviceRotation(true)
            }
        }
    }

    private fun askCameraPermission() {
        // Gère le resultat de la demande de permission d'accès à la caméra.
        val requestPermission: ActivityResultLauncher<String> =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    configureScanner()
                    codeScanner?.startPreview()
                } else {
                    viewBinding.fragmentMainScannerInformationTextView.setText(R.string.camera_permission_denied)
                }
            }

        requestPermission.launch(Manifest.permission.CAMERA)
    }

    private fun isCameraPermissionGranted(): Boolean {
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
            if (codeScanner?.isFlashEnabled == true)
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

    private fun hasFlash(): Boolean =
        requireContext().applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    private fun switchTorchFlash(){
        codeScanner?.isFlashEnabled = codeScanner?.isFlashEnabled == false
        requireActivity().invalidateOptionsMenu()
    }

    // ---- Scan via Image ----

    private var resultBarcodeScanFromImageActivity: ActivityResultLauncher<Intent>? = null

    private fun configureResultBarcodeScanFromImageActivity(){
        resultBarcodeScanFromImageActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){

                it.data?.let { intentResult ->
                    canEnableCamera = false // C'est appelé avant OnResume(), on empêche donc la réactivation de la caméra car elle n'est pas nécéssaire ici.
                    onSuccessfulScan(intentResult)
                }
            }
        }
    }

    private fun startBarcodeScanFromImageActivity(){
        if(codeScanner?.isFlashEnabled == true)
            switchTorchFlash()

        resultBarcodeScanFromImageActivity?.let { result ->
            val intent = getBarcodeScanFromImageActivityIntent()
            result.launch(intent)
        }
    }

    // ---- Scan ----

    private fun configureScanner(){

        codeScanner = CodeScanner(requireActivity(), viewBinding.fragmentMainScannerCodeScannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE

            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback(::onSuccessfulScan)
            errorCallback = ErrorCallback(::onErrorScan)
        }

        viewBinding.fragmentMainScannerInformationTextView.setText(R.string.scan_information_label)
    }

    private fun onSuccessfulScan(result: Result) = requireActivity().runOnUiThread {

        val contents = result.text
        val formatName = result.barcodeFormat?.name

        if(contents != null && formatName != null) {

            val settingsManager = get<SettingsManager>()

            if(settingsManager.shouldCopyBarcodeScan){
                copyToClipboard("contents", contents)
                showToastText(R.string.barcode_copied_label)
            }

            if(settingsManager.useBipScan)
                get<BeepManager>().playBeepSound(requireActivity())

            if(settingsManager.useVibrateScan)
                get<VibratorAppCompat>().vibrate()

            if(codeScanner?.isFlashEnabled == true)
                switchTorchFlash()

            val barcode: Barcode = get { parametersOf(contents, formatName) }

            saveIntoDatabase(barcode)

            // Si l'application a été ouverte via une application tierce
            if (requireActivity().intent?.action == ZXING_SCAN_INTENT_ACTION) {
                sendResultToAppIntent(result.toIntent())
            }else{
                startBarcodeAnalysisActivity(barcode)
            }
        } else {
            showSnackbar(getString(R.string.scan_cancel_label))
        }
    }

    private fun onSuccessfulScan(intentResult: Intent) = requireActivity().runOnUiThread {

        val contents = intentResult.getStringExtra(SCAN_RESULT)
        val formatName = intentResult.getStringExtra(SCAN_RESULT_FORMAT)

        if(contents != null && formatName != null) {

            val settingsManager = get<SettingsManager>()

            if(settingsManager.shouldCopyBarcodeScan){
                copyToClipboard("contents", contents)
                showToastText(R.string.barcode_copied_label)
            }

            if(settingsManager.useBipScan)
                get<BeepManager>().playBeepSound(requireActivity())

            if(settingsManager.useVibrateScan)
                get<VibratorAppCompat>().vibrate()

            if(codeScanner?.isFlashEnabled == true)
                switchTorchFlash()

            val barcode: Barcode = get { parametersOf(contents, formatName) }

            saveIntoDatabase(barcode)

            // Si l'application a été ouverte via une application tierce
            if (requireActivity().intent?.action == ZXING_SCAN_INTENT_ACTION) {
                sendResultToAppIntent(intentResult)
            }else{
                startBarcodeAnalysisActivity(barcode)
            }
        } else {
            showSnackbar(getString(R.string.scan_cancel_label))
        }
    }

    private fun onErrorScan(t: Throwable) = requireActivity().runOnUiThread {
        showSnackbar(getString(R.string.scan_error_exception_label, t.message))
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
        val intent = getBarcodeAnalysisActivityIntent().apply {
            putExtra(BARCODE_KEY, barcode)
        }
        startActivity(intent)
    }

    // ---- Snackbar ----

    private fun showSnackbar(text: String) {
        val activity = requireActivity()
        if(activity is MainActivity) {
            activity.showSnackbar(text)
        }
    }

    // ---- Intent ----

    private fun getBarcodeScanFromImageActivityIntent(): Intent =
        get(named(INTENT_START_ACTIVITY)) { parametersOf(BarcodeScanFromImageGalleryActivity::class) }

    private fun getBarcodeAnalysisActivityIntent(): Intent =
        get(named(INTENT_START_ACTIVITY)) { parametersOf(BarcodeAnalysisActivity::class) }


    private fun sendResultToAppIntent(intent: Intent) {

        requireActivity().apply {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}