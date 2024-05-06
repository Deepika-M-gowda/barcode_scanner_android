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

package com.atharok.barcodescanner.data.repositories

import androidx.lifecycle.LiveData
import com.atharok.barcodescanner.data.database.BarcodeDao
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.repositories.BarcodeRepository

class BarcodeRepositoryImpl(private val barcodeDao: BarcodeDao): BarcodeRepository {

    override fun getBarcodeList(): LiveData<List<Barcode>> = barcodeDao.getBarcodeList()

    override fun getBarcodeByDate(date: Long): LiveData<Barcode?> = barcodeDao.getBarcodeByDate(date)

    override suspend fun isExists(contents: String, format: String): Boolean = barcodeDao.isExists(contents, format)

    override suspend fun insertBarcode(barcode: Barcode): Long = barcodeDao.insert(barcode)

    override suspend fun insertBarcodes(barcodes: List<Barcode>) = barcodeDao.insert(barcodes)

    override suspend fun update(date: Long, contents: String, barcodeType: BarcodeType, name: String): Int = barcodeDao.update(date, contents, barcodeType.name, name)

    override suspend fun updateType(date: Long, barcodeType: BarcodeType): Int = barcodeDao.updateType(date, barcodeType.name)

    override suspend fun updateName(date: Long, name: String): Int = barcodeDao.updateName(date, name)

    override suspend fun updateTypeAndName(date: Long,
                                           barcodeType: BarcodeType,
                                           name: String
    ): Int = barcodeDao.updateTypeAndName(date, barcodeType.name, name)

    override suspend fun deleteAllBarcode(): Int = barcodeDao.deleteAll()

    override suspend fun deleteBarcodes(barcodes: List<Barcode>): Int = barcodeDao.deleteBarcodes(barcodes)

    override suspend fun deleteBarcode(barcode: Barcode): Int = barcodeDao.delete(barcode)
}