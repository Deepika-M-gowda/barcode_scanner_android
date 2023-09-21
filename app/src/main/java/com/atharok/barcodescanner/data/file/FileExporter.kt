package com.atharok.barcodescanner.data.file

import android.content.Context
import android.net.Uri
import java.io.OutputStream

class FileExporter(private val context: Context) {
    fun export(uri: Uri, action: (OutputStream) -> Unit): Boolean {
        var successful = true
        try {
            context.contentResolver.openOutputStream(uri)?.let { outputStream ->
                action(outputStream)
                outputStream.flush()
                outputStream.close()
            }
        } catch (e: Exception) {
            successful = false
            e.printStackTrace()
        }
        return successful
    }
}