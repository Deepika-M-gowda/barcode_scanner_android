package com.atharok.barcodescanner.common.extentions

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

fun <T : Parcelable?> Bundle.getParcelableAppCompat(name: String?, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= 33) {
        this.getParcelable(name, clazz)
    } else {
        @Suppress("DEPRECATION")
        clazz.cast(this.getParcelable(name))
    }
}