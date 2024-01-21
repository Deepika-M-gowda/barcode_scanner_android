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

package com.atharok.barcodescanner.domain.repositories

import androidx.lifecycle.LiveData
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType

/**
 * Repository permettant les interactions avec la BDD.
 */
interface BarcodeRepository {

    fun getBarcodeList(): LiveData<List<Barcode>>

    fun getBarcodeByDate(date: Long): LiveData<Barcode?>

    suspend fun insertBarcode(barcode: Barcode): Long

    suspend fun insertBarcodes(barcodes: List<Barcode>)

    suspend fun update(date: Long, contents: String, barcodeType: BarcodeType, name: String): Int

    suspend fun updateType(date: Long, barcodeType: BarcodeType): Int

    suspend fun updateName(date: Long, name: String): Int

    suspend fun updateTypeAndName(date: Long, barcodeType: BarcodeType, name: String): Int

    suspend fun deleteAllBarcode(): Int

    suspend fun deleteBarcodes(barcodes: List<Barcode>): Int

    suspend fun deleteBarcode(barcode: Barcode): Int
}