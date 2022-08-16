package com.atharok.barcodescanner.common.extentions

import android.os.Build
import android.os.Bundle
import java.io.Serializable

fun <T : Serializable?> Bundle.getSerializableAppCompat(name: String?, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= 33) {
        this.getSerializable(name, clazz)
    } else {
        @Suppress("DEPRECATION")
        clazz.cast(this.getSerializable(name))
    }
}