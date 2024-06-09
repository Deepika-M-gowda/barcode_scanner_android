package com.atharok.barcodescanner.common.utils

import android.content.Context
import android.content.DialogInterface.OnClickListener
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showSimpleDialog(
    context: Context,
    @StringRes titleRes: Int,
    message: String,
    listener: OnClickListener? = null
): AlertDialog {
    return MaterialAlertDialogBuilder(context)
        .setTitle(titleRes)
        .setMessage(message)
        .setNegativeButton(android.R.string.ok, listener)
        .show()
}