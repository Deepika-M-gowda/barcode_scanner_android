package com.atharok.barcodescanner.domain.library.camera

import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.annotation.FloatRange
import kotlin.math.max
import kotlin.math.min

typealias OnZoomChangeListener = (zoom: Float) -> Unit

class CameraZoomGestureDetector(@FloatRange(from = 0.0, to = 1.0) defaultZoom: Float) :
    ScaleGestureDetector.SimpleOnScaleGestureListener(), View.OnTouchListener {

    companion object {

        /** Minimum time between calls to zoom listener. */
        private const val ZOOM_MINIMUM_WAIT_MILLIS: Long = 33L
    }

    /** Next time zoom change should be sent to listener. */
    private var delayZoomCallUntilMillis: Long = 0L

    private var currentZoom: Float = min(1f, max(defaultZoom, 0f))

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var listener: OnZoomChangeListener

    fun attach(view: View, listener: OnZoomChangeListener) {
        this.scaleGestureDetector = ScaleGestureDetector(view.context, this)
        this.listener = listener
        view.setOnTouchListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return scaleGestureDetector.onTouchEvent(event)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        // Refer to android Camera2, com.android.camera.ui.PreviewOverlay.ZoomProcessor#onScale
        // https://cs.android.com/android/platform/superproject/+/android-13.0.0_r8:packages/apps/Camera2/src/com/android/camera/ui/PreviewOverlay.java;l=364
        val sf = detector.scaleFactor
        val zoom = (0.33f + currentZoom) * sf * sf - 0.33f
        currentZoom = min(1f, max(zoom, 0f))

        // Only call the listener with a certain frequency. This is
        // necessary because these listeners will make repeated
        // applySettings() calls into the portability layer, and doing this
        // too often can back up its handler and result in visible lag in
        // updating the zoom level and other controls.
        val now = SystemClock.uptimeMillis()
        if (now > delayZoomCallUntilMillis) {
            listener.invoke(currentZoom)
            delayZoomCallUntilMillis = now + ZOOM_MINIMUM_WAIT_MILLIS
        }
        return true
    }

}
