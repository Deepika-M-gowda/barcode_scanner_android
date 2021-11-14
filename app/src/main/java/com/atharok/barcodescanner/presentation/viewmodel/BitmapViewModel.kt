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

package com.atharok.barcodescanner.presentation.viewmodel
/*
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.atharok.barcodescanner.models.local.barcode.repositories.BarcodeBitmapGeneratorRepository
import com.google.zxing.BarcodeFormat
import kotlinx.coroutines.Dispatchers

class BitmapViewModel(private val barcodeBitmapGeneratorRepository: BarcodeBitmapGeneratorRepository,
                      //private val shareRepository: BitmapShareRepository,
                      /*private val recordRepository: BitmapRecordRepository*/): ViewModel() {

    fun createBitmap(text: String, barcodeFormat: BarcodeFormat, width: Int, height: Int): LiveData<Bitmap?> = liveData(Dispatchers.IO) {
        emit(barcodeBitmapGeneratorRepository.create(text, barcodeFormat, width, height))
    }

    /*fun share(bitmap: Bitmap): LiveData<Uri?> = liveData(Dispatchers.IO) {
        emit(shareRepository.share(bitmap))
    }*/

    /*fun save(bitmap: Bitmap): LiveData<Boolean> = liveData(Dispatchers.IO) {
        emit(recordRepository.recordImage(bitmap))
    }*/
}*/