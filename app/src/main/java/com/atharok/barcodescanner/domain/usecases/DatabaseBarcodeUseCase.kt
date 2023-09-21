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

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.repositories.BarcodeRepository
import com.atharok.barcodescanner.domain.repositories.FileExportRepository
import com.atharok.barcodescanner.domain.resources.Resource
import kotlinx.coroutines.Dispatchers

class DatabaseBarcodeUseCase(
    private val barcodeRepository: BarcodeRepository,
    private val fileExportRepository: FileExportRepository
) {

    val barcodeList: LiveData<List<Barcode>> = barcodeRepository.getBarcodeList()

    fun getBarcodeByDate(date: Long): LiveData<Barcode?> = barcodeRepository.getBarcodeByDate(date)

    suspend fun insertBarcode(barcode: Barcode): Long = barcodeRepository.insertBarcode(barcode)

    suspend fun updateType(date: Long, barcodeType: BarcodeType): Int =
        barcodeRepository.updateType(date, barcodeType)

    suspend fun updateTypeAndName(date: Long, barcodeType: BarcodeType, name: String): Int =
        barcodeRepository.updateTypeAndName(date, barcodeType, name)

    suspend fun deleteBarcode(barcode: Barcode) = barcodeRepository.deleteBarcode(barcode)

    suspend fun deleteBarcodes(barcodes: List<Barcode>) = barcodeRepository.deleteBarcodes(barcodes)

    suspend fun deleteAll(): Int = barcodeRepository.deleteAllBarcode()

    fun exportToCsv(
        barcodes: List<Barcode>,
        uri: Uri
    ): LiveData<Resource<Boolean>> {
        return export {
            fileExportRepository.exportToCsv(barcodes, uri)
        }
    }

    fun exportToJson(
        barcodes: List<Barcode>,
        uri: Uri
    ): LiveData<Resource<Boolean>> {
        return export {
            fileExportRepository.exportToJson(barcodes, uri)
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
            emit(Resource.failure(e, false))
            e.printStackTrace()
        }
    }
}