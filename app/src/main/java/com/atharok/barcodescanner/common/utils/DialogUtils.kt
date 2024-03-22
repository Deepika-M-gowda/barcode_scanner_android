package com.atharok.barcodescanner.common.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog

fun showSimpleDialog(context: Context, @StringRes titleRes: Int, message: String): AlertDialog {
    return AlertDialog.Builder(context)
        .setTitle(titleRes)
        .setMessage(message)
        .setNegativeButton(android.R.string.ok, null)
        .show()
}