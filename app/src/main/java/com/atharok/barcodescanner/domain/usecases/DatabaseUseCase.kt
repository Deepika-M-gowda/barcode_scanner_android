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

import androidx.lifecycle.LiveData
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.repositories.BarcodeRepository

class DatabaseUseCase(private val barcodeRepository: BarcodeRepository) {

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
}