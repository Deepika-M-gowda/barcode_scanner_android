package com.atharok.barcodescanner.common.extensions

import android.content.Intent
import com.google.zxing.ResultMetadataType

const val SCAN_RESULT = "SCAN_RESULT"
const val SCAN_RESULT_FORMAT = "SCAN_RESULT_FORMAT"
const val SCAN_RESULT_BYTES = "SCAN_RESULT_BYTES"
const val SCAN_RESULT_ORIENTATION = "SCAN_RESULT_ORIENTATION"
const val SCAN_RESULT_ERROR_CORRECTION_LEVEL = "SCAN_RESULT_ERROR_CORRECTION_LEVEL"
const val SCAN_RESULT_UPC_EAN_EXTENSION = "SCAN_RESULT_UPC_EAN_EXTENSION"
const val SCAN_RESULT_BYTE_SEGMENTS_ = "SCAN_RESULT_BYTE_SEGMENTS_"

fun com.google.zxing.Result.toIntent(): Intent {

    val result = this

    val intent = Intent()
        .putExtra(SCAN_RESULT, result.text)
        .putExtra(SCAN_RESULT_FORMAT, result.barcodeFormat.toString())

    if (result.rawBytes?.isNotEmpty() == true) {
        intent.putExtra(SCAN_RESULT_BYTES, result.rawBytes)
    }

    result.resultMetadata?.let { metadata ->
        metadata[ResultMetadataType.UPC_EAN_EXTENSION]?.let {
            intent.putExtra(SCAN_RESULT_ORIENTATION, it.toString())
        }

        metadata[ResultMetadataType.ERROR_CORRECTION_LEVEL]?.let {
            intent.putExtra(SCAN_RESULT_ERROR_CORRECTION_LEVEL, it.toString())
        }

        metadata[ResultMetadataType.UPC_EAN_EXTENSION]?.let {
            intent.putExtra(SCAN_RESULT_UPC_EAN_EXTENSION, it.toString())
        }

        metadata[ResultMetadataType.BYTE_SEGMENTS]?.let {
            @Suppress("UNCHECKED_CAST")
            for ((i, seg) in (it as Iterable<ByteArray>).withIndex()) {
                intent.putExtra("$SCAN_RESULT_BYTE_SEGMENTS_$i", seg)
            }
        }
    }

    return intent
}