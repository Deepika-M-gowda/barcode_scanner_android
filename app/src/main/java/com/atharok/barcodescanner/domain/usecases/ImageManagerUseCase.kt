package com.atharok.barcodescanner.domain.usecases

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.atharok.barcodescanner.common.extensions.is2DBarcode
import com.atharok.barcodescanner.common.utils.BARCODE_IMAGE_SIZE
import com.atharok.barcodescanner.domain.entity.barcode.QrCodeErrorCorrectionLevel
import com.atharok.barcodescanner.domain.repositories.ImageExportRepository
import com.atharok.barcodescanner.domain.repositories.ImageGeneratorRepository
import com.atharok.barcodescanner.domain.resources.Resource
import com.google.zxing.BarcodeFormat
import kotlinx.coroutines.Dispatchers

class ImageManagerUseCase(
    private val imageGeneratorRepository: ImageGeneratorRepository,
    private val imageExportRepository: ImageExportRepository
) {
    fun createBitmap(
        contents: String,
        barcodeFormat: BarcodeFormat,
        qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel
    ): LiveData<Resource<Bitmap?>> = liveData(Dispatchers.IO) {

        emit(Resource.loading())

        val bitmap = imageGeneratorRepository.createBitmap(
            text = contents,
            barcodeFormat = barcodeFormat,
            errorCorrectionLevel = qrCodeErrorCorrectionLevel.errorCorrectionLevel,
            width = BARCODE_IMAGE_SIZE,
            height = if(barcodeFormat.is2DBarcode()) BARCODE_IMAGE_SIZE else BARCODE_IMAGE_SIZE /2
        )

        emit(Resource.success(bitmap))
    }

    fun createSvg(
        contents: String?,
        barcodeFormat: BarcodeFormat,
        qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel
    ): LiveData<String?> = liveData(Dispatchers.IO) {

        contents?.let {
            try {
                val svg = imageGeneratorRepository.createSvg(
                    text = it,
                    barcodeFormat = barcodeFormat,
                    errorCorrectionLevel = qrCodeErrorCorrectionLevel.errorCorrectionLevel
                )
                emit(svg)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(null)
            }
        } ?: run {
            emit(null)
        }
    }

    fun exportToPng(bitmap: Bitmap?, uri: Uri): LiveData<Resource<Boolean>> {
        return export {
            bitmap?.let {
                imageExportRepository.exportToPng(bitmap, uri)
            } ?: run {
                false
            }
        }
    }

    fun exportToJpg(bitmap: Bitmap?, uri: Uri): LiveData<Resource<Boolean>> {
        return export {
            bitmap?.let {
                imageExportRepository.exportToJpg(it, uri)
            } ?: run {
                false
            }
        }
    }

    fun exportToSvg(svg: String?, uri: Uri): LiveData<Resource<Boolean>> {
        return export {
            svg?.let {
                imageExportRepository.exportToSvg(it, uri)
            } ?: run {
                false
            }
        }
    }

    fun shareBitmap(bitmap: Bitmap?): LiveData<Resource<Uri?>> = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        try {
            val uri: Uri? = bitmap?.let {
                imageExportRepository.shareBitmap(it)
            } ?: run {
                null
            }
            emit(Resource.success(uri))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.failure(e, null))
        }
    }

    private fun export(
        export: () -> Boolean
    ): LiveData<Resource<Boolean>> = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        try {
            val success = export()
            emit(Resource.success(success))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.failure(e, false))
        }
    }
}