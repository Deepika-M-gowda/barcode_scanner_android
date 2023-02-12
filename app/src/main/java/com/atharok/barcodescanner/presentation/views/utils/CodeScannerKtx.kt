@file:JvmName("CodeScannerUtils")
@file:Suppress("DEPRECATION")

package com.atharok.barcodescanner.presentation.views.utils

import android.hardware.Camera
import android.view.SurfaceHolder
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecoderWrapperAccessor
import kotlinx.coroutines.*
import kotlin.coroutines.resume

/**
 * Get Camera maxZoom value, returns -1 on failure.
 *
 * @see [Camera.getParameters]
 * @see [Camera.Parameters.getMaxZoom]
 */
suspend fun CodeScanner.getMaxZoom(): Int {
    val camera: Camera = getCamera1() ?: return -1
    val parameters = try {
        camera.parameters
    } catch (e: RuntimeException) {
        return -1
    }
    return if (parameters.isZoomSupported) parameters.maxZoom else -1
}

suspend fun CodeScanner.getCamera1(): Camera? {
    var accessor: DecoderWrapperAccessor? = getDecoderWrapperAccessor()
    if (accessor != null) {
        return accessor.camera
    }
    accessor = awaitDecoderWrapperAccessor(getSurfaceHolder() ?: return null)
    return accessor.camera
}

private suspend fun CodeScanner.awaitDecoderWrapperAccessor(surfaceHolder: SurfaceHolder) =
    suspendCancellableCoroutine {
        // Unable to know the status of the asynchronous initialization camera.
        // Use the lifecycle of SurfaceHolder to get the camera instance.
        val callback = object : SurfaceHolder.Callback {

            private fun tryResume() {
                val accessor = getDecoderWrapperAccessor() ?: return
                surfaceHolder.removeCallback(this)
                it.resume(accessor)
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                tryResume()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int,
            ) {
                tryResume()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                surfaceHolder.removeCallback(this)
                it.cancel()
            }
        }
        surfaceHolder.addCallback(callback)

        it.invokeOnCancellation {
            surfaceHolder.removeCallback(callback)
        }
    }

private fun CodeScanner.getSurfaceHolder(): SurfaceHolder? {
    return try {
        val fieldSurfaceHolder = CodeScanner::class.java.getDeclaredField("mSurfaceHolder")
        fieldSurfaceHolder.isAccessible = true
        fieldSurfaceHolder.get(this) as SurfaceHolder
    } catch (e: Exception) {
        null
    }
}

private fun CodeScanner.getDecoderWrapperAccessor(): DecoderWrapperAccessor? {
    if (!isPreviewActive) return null
    val decoderWrapper: Any = try {
        // volatile modification, access in multiple threads.
        val fieldDecoderWrapper = CodeScanner::class.java.getDeclaredField("mDecoderWrapper")
        fieldDecoderWrapper.isAccessible = true
        fieldDecoderWrapper.get(this) ?: return null
    } catch (e: Exception) {
        return null
    }
    return DecoderWrapperAccessor(decoderWrapper)
}
