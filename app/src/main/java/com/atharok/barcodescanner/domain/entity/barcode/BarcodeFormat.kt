package com.atharok.barcodescanner.domain.entity.barcode

import com.google.zxing.BarcodeFormat

fun BarcodeFormat.is1DProductBarcode(): Boolean {
    return when(this){
        BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.UPC_EAN_EXTENSION,
        BarcodeFormat.UPC_A, BarcodeFormat.UPC_E -> true
        else -> false
    }
}

fun BarcodeFormat.is1DIndustrialBarcode(): Boolean {
    return when(this){
        BarcodeFormat.CODE_128, BarcodeFormat.CODABAR,
        BarcodeFormat.CODE_39, BarcodeFormat.CODE_93, BarcodeFormat.ITF -> true
        else -> false
    }
}

fun BarcodeFormat.is2DBarcode(): Boolean {
    return when(this){
        BarcodeFormat.QR_CODE, BarcodeFormat.AZTEC, BarcodeFormat.DATA_MATRIX,
        BarcodeFormat.MAXICODE, BarcodeFormat.PDF_417,
        BarcodeFormat.RSS_14, BarcodeFormat.RSS_EXPANDED -> true
        else -> false
    }
}