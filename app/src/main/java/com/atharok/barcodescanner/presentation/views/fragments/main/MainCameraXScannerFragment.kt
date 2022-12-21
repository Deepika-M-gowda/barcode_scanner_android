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
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.AspectRatio.RATIO_4_3
import androidx.camera.core.FocusMeteringAction.FLAG_AF
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.SCAN_RESULT
import com.atharok.barcodescanner.common.extensions.SCAN_RESULT_FORMAT
import com.atharok.barcodescanner.common.extensions.afterMeasured
import com.atharok.barcodescanner.common.extensions.toIntent
import com.atharok.barcodescanner.common.utils.BARCODE_KEY
import com.atharok.barcodescanner.common.utils.INTENT_START_ACTIVITY
import com.atharok.barcodescanner.databinding.FragmentMainCameraXScannerBinding
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.library.BeepManager
import com.atharok.barcodescanner.domain.library.CameraXBarcodeAnalyzer
import com.atharok.barcodescanner.domain.library.SettingsManager
import com.atharok.barcodescanner.domain.library.VibratorAppCompat
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseViewModel
import com.atharok.barcodescanner.presentation.views.activities.BarcodeAnalysisActivity
import com.atharok.barcodescanner.presentation.views.activities.BarcodeScanFromImageGalleryActivity
import com.atharok.barcodescanner.presentation.views.activities.BaseActivity
import com.atharok.barcodescanner.presentation.views.activities.MainActivity
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment
import com.google.zxing.Result
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class MainCameraXScannerFragment : BaseFragment() {

    companion object {
        private const val ZXING_SCAN_INTENT_ACTION = "com.google.zxing.client.android.SCAN"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private var camera: Camera? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var flashEnabled = false
    private var barcodeFound = false

    private val databaseViewModel: DatabaseViewModel by activityViewModel()

    // ---- View ----
    private var _binding: FragmentMainCameraXScannerBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureResultBarcodeScanFromImageActivity()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainCameraXScannerBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureMenu()
        configurePermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        switchOffFlash()
        camera=null
        _binding=null
    }

    override fun onResume() {
        super.onResume()
        if(barcodeFound) {

            camera?.let {
                configureAutoFocus(it)
                configureZoom(it)
            }

            barcodeFound = false
        }
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

    // ---- Menu ----

    private fun configureMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_scanner, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when(menuItem.itemId){
                R.id.menu_scanner_flash -> {
                    switchFlash()
                    true
                }
                R.id.menu_scanner_scan_from_image -> {
                    startBarcodeScanFromImageActivity()
                    true
                }
                else -> false
            }

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)

                if(hasFlash() && allPermissionsGranted()) {
                    if (flashEnabled)
                        menu.getItem(0).icon =
                            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_flash_on_24)
                    else
                        menu.getItem(0).icon =
                            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_flash_off_24)

                }else{
                    menu.getItem(0).isVisible = false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // ---- Camera Permission ----

    private fun configurePermission() {
        if (!allPermissionsGranted()) {
            // Gère le resultat de la demande de permission d'accès à la caméra.
            val requestPermission: ActivityResultLauncher<String> =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                    if (it) {
                        doPermissionGranted()
                    } else doPermissionRefused()
                }
            requestPermission.launch(Manifest.permission.CAMERA)
        }else{
            doPermissionGranted()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun doPermissionGranted() {
        configureCamera()
        viewBinding.fragmentMainCameraXScannerCameraPermissionTextView.visibility = View.GONE
        viewBinding.fragmentMainCameraXScannerPreviewView.visibility = View.VISIBLE
        viewBinding.fragmentMainCameraXScannerScanOverlay.visibility = View.VISIBLE
        viewBinding.fragmentMainCameraXScannerSlider.visibility = View.VISIBLE
        viewBinding.fragmentMainCameraXScannerInformationTextView?.visibility = View.VISIBLE
    }

    private fun doPermissionRefused() {
        viewBinding.fragmentMainCameraXScannerCameraPermissionTextView.visibility = View.VISIBLE
        viewBinding.fragmentMainCameraXScannerPreviewView.visibility = View.GONE
        viewBinding.fragmentMainCameraXScannerScanOverlay.visibility = View.GONE
        viewBinding.fragmentMainCameraXScannerSlider.visibility = View.GONE
        viewBinding.fragmentMainCameraXScannerInformationTextView?.visibility = View.GONE
    }

    // ---- Camera ----

    private fun configureCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            camera = startCamera(cameraProvider)
            camera?.let {
                configureAutoFocus(it)
                configureZoom(it)
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun startCamera(cameraProvider: ProcessCameraProvider): Camera {
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val previewView = viewBinding.fragmentMainCameraXScannerPreviewView
        val scanOverlay = viewBinding.fragmentMainCameraXScannerScanOverlay

        val preview = Preview.Builder().apply {
            setTargetAspectRatio(RATIO_4_3)
        }.build()

        val imageAnalysis = ImageAnalysis.Builder().apply {
            setTargetAspectRatio(RATIO_4_3)
            setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build().also {
            it.setAnalyzer(cameraExecutor, CameraXBarcodeAnalyzer(previewView, scanOverlay) { result ->
                previewView.post {
                    onSuccessfulScanFromCamera(result)
                }
            })
        }

        cameraProvider.unbindAll()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        return cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageAnalysis)
    }

    private fun configureAutoFocus(camera: Camera) {
        val previewView = viewBinding.fragmentMainCameraXScannerPreviewView

        previewView.afterMeasured {

            val previewViewWidth = previewView.width.toFloat()
            val previewViewHeight = previewView.height.toFloat()

            val autoFocusPoint = SurfaceOrientedMeteringPointFactory(
                previewViewWidth, previewViewHeight
            ).createPoint(previewViewWidth / 2.0f, previewViewHeight / 2.0f)

            try {
                camera.cameraControl.startFocusAndMetering(
                    FocusMeteringAction
                        .Builder(autoFocusPoint, FLAG_AF)
                        .setAutoCancelDuration(2, TimeUnit.SECONDS)
                        .build()
                )
            } catch (e: CameraInfoUnavailableException) {
                Log.d("ERROR", "cannot access camera", e)
            }
        }
    }

    private fun configureZoom(camera: Camera) {
        val slider = viewBinding.fragmentMainCameraXScannerSlider

        camera.cameraControl.setLinearZoom(slider.value)
        slider.addOnChangeListener { _, value, _ ->
            camera.cameraControl.setLinearZoom(value)
        }
    }

    private fun hasFlash(): Boolean =
        requireContext().applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    private fun switchFlash(){
        camera?.let {
            flashEnabled = !flashEnabled
            it.cameraControl.enableTorch(flashEnabled)
            requireActivity().invalidateOptionsMenu()
        }
    }

    private fun switchOffFlash(){
        if(flashEnabled){
            switchFlash()
        }
    }

    // ---- Scan successful ----

    private fun onSuccessfulScan(contents: String?, formatName: String?, onResult: (barcode: Barcode) -> Unit) = requireActivity().runOnUiThread {

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

            switchOffFlash()

            val barcode: Barcode = get { parametersOf(contents, formatName) }

            // Insert les informations du code-barres dans la base de données (de manière asynchrone)
            databaseViewModel.insertBarcode(barcode)

            onResult(barcode)
        } else {
            showSnackbar(getString(R.string.scan_cancel_label))
        }
    }

    // ---- Scan from Camera ----

    private fun onSuccessfulScanFromCamera(result: Result) {
        if(!barcodeFound) {
            barcodeFound = true

            val contents = result.text
            val formatName = result.barcodeFormat?.name

            onSuccessfulScan(contents, formatName) { barcode ->
                // Si l'application a été ouverte via une application tierce
                if (requireActivity().intent?.action == ZXING_SCAN_INTENT_ACTION) {
                    sendResultToAppIntent(result.toIntent())
                }else{
                    startBarcodeAnalysisActivity(barcode)
                }
            }
        }
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

    // ---- Scan from Image ----

    private var resultBarcodeScanFromImageActivity: ActivityResultLauncher<Intent>? = null

    private fun configureResultBarcodeScanFromImageActivity(){
        resultBarcodeScanFromImageActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){

                it.data?.let { intentResult ->
                    onSuccessfulScanFromImage(intentResult)
                }
            }
        }
    }

    private fun onSuccessfulScanFromImage(intentResult: Intent) {

        if(!barcodeFound) {
            barcodeFound = true
            val contents = intentResult.getStringExtra(SCAN_RESULT)
            val formatName = intentResult.getStringExtra(SCAN_RESULT_FORMAT)

            onSuccessfulScan(contents, formatName) { barcode ->
                // Si l'application a été ouverte via une application tierce
                if (requireActivity().intent?.action == ZXING_SCAN_INTENT_ACTION) {
                    sendResultToAppIntent(intentResult)
                } else {
                    startBarcodeAnalysisActivity(barcode)
                }
            }
        }
    }

    private fun startBarcodeScanFromImageActivity(){
        switchOffFlash()
        resultBarcodeScanFromImageActivity?.let { result ->
            val intent = getBarcodeScanFromImageActivityIntent()
            result.launch(intent)
        }
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