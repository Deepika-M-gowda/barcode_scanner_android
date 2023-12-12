/*
 * Barcode Scanner
 * Copyright (C) 2021  Atharok
 *
 * This file is part of Barcode Scanner.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.atharok.barcodescanner.domain.usecases

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.atharok.barcodescanner.domain.library.BarcodeImageGeneratorProperties
import com.atharok.barcodescanner.domain.repositories.ImageExportRepository
import com.atharok.barcodescanner.domain.repositories.ImageGeneratorRepository
import com.atharok.barcodescanner.domain.resources.Resource
import kotlinx.coroutines.Dispatchers

class ImageManagerUseCase(
    private val imageGeneratorRepository: ImageGeneratorRepository,
    private val imageExportRepository: ImageExportRepository
) {

    val bitmapObserver = MutableLiveData<Resource<Bitmap?>>()

    suspend fun createBitmap(properties: BarcodeImageGeneratorProperties) {
        bitmapObserver.value = Resource.loading()
        val bitmap = imageGeneratorRepository.createBitmap(properties)
        bitmapObserver.value = Resource.success(bitmap)
    }

    fun createSvg(properties: BarcodeImageGeneratorProperties): LiveData<String?> = liveData(Dispatchers.IO) {
        try {
            val svg = imageGeneratorRepository.createSvg(properties)
            emit(svg)
        } catch (e: Exception) {
            e.printStackTrace()
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