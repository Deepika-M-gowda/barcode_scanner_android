package com.atharok.barcodescanner.domain.library.camera

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.FloatRange
import kotlin.math.max
import kotlin.math.min

typealias OnZoomChangeListener = (zoom: Float) -> Unit

class CameraZoomGestureDetector(@FloatRange(from = 0.0, to = 1.0) defaultZoom: Float) :
    ScaleGestureDetector.SimpleOnScaleGestureListener(), View.OnTouchListener {

    companion object {

        /** Minimum time between calls to zoom listener. */
        private const val ZOOM_MINIMUM_WAIT_MILLIS: Long = 33L

        private const val ZOOM_LEVEL_STEP = 0.5f
        private const val MIN_ZOOM = 0f
        private const val MAX_ZOOM = 1f
    }

    /** Next time zoom change should be sent to listener. */
    private var delayZoomCallUntilMillis: Long = 0L

    private var currentZoom: Float = min(MAX_ZOOM, max(defaultZoom, MIN_ZOOM))

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector
    private lateinit var doubleTabGestureListener: DoubleTabGestureListener
    private lateinit var listener: OnZoomChangeListener

    fun attach(view: View, listener: OnZoomChangeListener) {
        val context = view.context
        this.scaleGestureDetector = ScaleGestureDetector(context, this)
        this.doubleTabGestureListener = DoubleTabGestureListener(this) {
            performListenerImmediately(it)
        }
        this.gestureDetector = GestureDetector(context, doubleTabGestureListener).apply {
            setOnDoubleTapListener(doubleTabGestureListener)
        }
        this.listener = listener
        view.setOnTouchListener(this)
    }

    private class DoubleTabGestureListener(
        val cameraZoom: CameraZoomGestureDetector,
        val listener: OnZoomChangeListener,
    ) : GestureDetector.SimpleOnGestureListener(), ValueAnimator.AnimatorUpdateListener {

        private val interpolator = LinearInterpolator()
        private var animator: ValueAnimator? = null

        fun stop() {
            val animator = this.animator
            if (animator != null) {
                animator.cancel()
                this.animator = null
            }
        }

        override fun onAnimationUpdate(animation: ValueAnimator) {
            val zoom = animation.animatedValue as Float
            listener.invoke(zoom)
        }

        private fun getNextLevelZoom(currentZoom: Float): Float {
            if (currentZoom >= MAX_ZOOM || currentZoom < MIN_ZOOM) {
                return MIN_ZOOM
            }
            var zoom = ((currentZoom / ZOOM_LEVEL_STEP) + 1) * ZOOM_LEVEL_STEP
            if (zoom > MAX_ZOOM) {
                zoom = MAX_ZOOM
            }
            return zoom
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            val oldZoom = cameraZoom.currentZoom
            val newZoom = getNextLevelZoom(oldZoom)
            stop()
            val animator = ValueAnimator.ofFloat(oldZoom, newZoom)
                .setDuration(300L)
            animator.interpolator = interpolator
            animator.addUpdateListener(this)
            animator.start()
            this.animator = animator
            return true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        scaleGestureDetector.onTouchEvent(event)
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        doubleTabGestureListener.stop()
        return super.onScaleBegin(detector)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val sf = detector.scaleFactor
        var zoom = (0.33f + currentZoom) * sf * sf - 0.33f
        zoom = min(MAX_ZOOM, max(zoom, MIN_ZOOM))
        performListener(zoom)
        return true
    }

    private fun performListener(zoom: Float) {
        // Refer to android Camera2, com.android.camera.ui.PreviewOverlay.ZoomProcessor#onScale
        // https://cs.android.com/android/platform/superproject/+/android-13.0.0_r8:packages/apps/Camera2/src/com/android/camera/ui/PreviewOverlay.java;l=364

        // Only call the listener with a certain frequency. This is
        // necessary because these listeners will make repeated
        // applySettings() calls into the portability layer, and doing this
        // too often can back up its handler and result in visible lag in
        // updating the zoom level and other controls.
        val now = SystemClock.uptimeMillis()
        if (now > delayZoomCallUntilMillis) {
            performListenerImmediately(zoom)
            delayZoomCallUntilMillis = now + ZOOM_MINIMUM_WAIT_MILLIS
        }
    }

    private fun performListenerImmediately(zoom: Float) {
        this.currentZoom = zoom
        listener.invoke(zoom)
    }

}
