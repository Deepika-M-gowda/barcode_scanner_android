package com.atharok.barcodescanner.domain.library.camera

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.atharok.barcodescanner.common.extensions.afterMeasured
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class CameraConfig(private val context: Context) {

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var postZoom = -1f
    var flashEnabled = false
        private set

    private val resolution: Size by lazy {
        val orientation: Int = context.applicationContext.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            Size(960, 1280)
        else
            Size(1280, 960)
    }

    private val cameraSelector by lazy {
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    }

    private val preview by lazy {
        Preview.Builder().setTargetResolution(resolution).build()
    }

    private val imageAnalysis by lazy {
        ImageAnalysis.Builder().apply {
            setTargetResolution(resolution)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setOutputImageRotationEnabled(true)
            }
            setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build()
    }

    fun setAnalyzer(analyzer: ImageAnalysis.Analyzer) {
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)
    }

    fun startCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get().apply {

                try {
                    unbindAll()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    camera = bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis).apply {
                        configureAutoFocus(previewView, this)
                        if (postZoom != -1f) {
                            configureZoom(this, postZoom)
                            postZoom = -1f
                        }
                    }
                } catch(exc: Exception) {
                    Log.e("TAG", "Use case binding failed", exc)
                }
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun stopCamera() {
        switchOffFlash()
        cameraProvider?.let {
            it.unbindAll()
            camera = null
        }
    }

    fun isRunning(): Boolean = camera != null

    private fun configureAutoFocus(previewView: PreviewView, camera: Camera) {

        previewView.afterMeasured {

            val previewViewWidth = previewView.width.toFloat()
            val previewViewHeight = previewView.height.toFloat()

            val autoFocusPoint = SurfaceOrientedMeteringPointFactory(
                previewViewWidth, previewViewHeight
            ).createPoint(previewViewWidth / 2.0f, previewViewHeight / 2.0f)

            try {
                camera.cameraControl.startFocusAndMetering(
                    FocusMeteringAction
                        .Builder(autoFocusPoint, FocusMeteringAction.FLAG_AF)
                        .setAutoCancelDuration(2, TimeUnit.SECONDS)
                        .build()
                )
            } catch (e: CameraInfoUnavailableException) {
                Log.d("ERROR", "cannot access camera", e)
            }
        }
    }

    private fun configureZoom(camera: Camera, value: Float) {
        val safeZoom = max(0f, min(value, 1f))
        camera.cameraControl.setLinearZoom(safeZoom)
    }

    fun setLinearZoom(value: Float) {
        val camera = this.camera
        if (camera == null) {
            postZoom = value
            return
        }
        postZoom = -1f
        configureZoom(camera, value)
    }

    fun hasFlash(): Boolean =
        context.applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    fun switchFlash(){
        camera?.let {
            flashEnabled = !flashEnabled
            it.cameraControl.enableTorch(flashEnabled)
        }
    }

    private fun switchOffFlash(){
        if(flashEnabled){
            switchFlash()
        }
    }
}