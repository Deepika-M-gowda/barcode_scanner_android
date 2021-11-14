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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atharok.barcodescanner.models.local.barcode.repositories.BarcodeBitmapAnalysisRepository
import com.google.zxing.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BarcodeBitmapAnalysisViewModel(private val barcodeBitmapAnalysisRepository: BarcodeBitmapAnalysisRepository): ViewModel() {

    private val resultLiveData = MutableLiveData<Result?>()

    val zxingResult: LiveData<Result?> = resultLiveData

    fun findBarcodeInBitmap(bitmap: Bitmap) = viewModelScope.launch {

        val deferredResult = async(Dispatchers.IO) {
            barcodeBitmapAnalysisRepository.findBarcodeInBitmap(bitmap)
        }

        val result = deferredResult.await()

        resultLiveData.value = result
    }
}*/