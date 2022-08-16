package com.atharok.barcodescanner.common.extentions

import android.content.Intent
import android.os.Parcelable
import java.io.Serializable

fun <T : Serializable?> Intent.getSerializableExtraAppCompat(name: String?, clazz: Class<T>): T? {
    return this.extras?.getSerializableAppCompat(name, clazz)
}

fun <T : Parcelable?> Intent.getParcelableExtraAppCompat(name: String?, clazz: Class<T>): T? {
    return this.extras?.getParcelableAppCompat(name, clazz)
}