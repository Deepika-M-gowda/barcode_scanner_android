package com.budiyev.android.codescanner;

import android.hardware.Camera;

import androidx.annotation.NonNull;

/**
 * {@link DecoderWrapper} Accessor.
 *
 * @author shhu
 * @since 2023/2/9
 */
public class DecoderWrapperAccessor {

    private final DecoderWrapper mDecoderWrapper;

    public DecoderWrapperAccessor(@NonNull Object decoderWrapper) {
        mDecoderWrapper = (DecoderWrapper) decoderWrapper;
    }

    @SuppressWarnings("deprecation")
    @NonNull
    public Camera getCamera() {
        return mDecoderWrapper.getCamera();
    }
}
