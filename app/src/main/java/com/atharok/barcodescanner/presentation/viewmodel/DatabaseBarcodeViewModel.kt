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

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atharok.barcodescanner.domain.entity.FileFormat
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.resources.Resource
import com.atharok.barcodescanner.domain.usecases.DatabaseBarcodeUseCase
import kotlinx.coroutines.launch

class DatabaseBarcodeViewModel(private val databaseBarcodeUseCase: DatabaseBarcodeUseCase): ViewModel() {

    val barcodeList: LiveData<List<Barcode>> = databaseBarcodeUseCase.barcodeList

    fun getBarcodeByDate(date: Long): LiveData<Barcode?> = databaseBarcodeUseCase.getBarcodeByDate(date)

    fun insertBarcode(barcode: Barcode) = viewModelScope.launch {
        databaseBarcodeUseCase.insertBarcode(barcode)
    }

    fun insertBarcodes(barcodes: List<Barcode>) = viewModelScope.launch {
        databaseBarcodeUseCase.insertBarcodes(barcodes)
    }

    fun update(date: Long, contents: String, barcodeType: BarcodeType, name: String) = viewModelScope.launch {
        databaseBarcodeUseCase.update(date, contents, barcodeType, name)
    }

    fun updateType(date: Long, barcodeType: BarcodeType) = viewModelScope.launch {
        databaseBarcodeUseCase.updateType(date, barcodeType)
    }

    fun updateTypeAndName(date: Long, barcodeType: BarcodeType, name: String) = viewModelScope.launch {
        databaseBarcodeUseCase.updateTypeAndName(date, barcodeType, name)
    }

    fun deleteBarcode(barcode: Barcode) = viewModelScope.launch {
        databaseBarcodeUseCase.deleteBarcode(barcode)
    }

    fun deleteBarcodes(barcodes: List<Barcode>) = viewModelScope.launch {
        databaseBarcodeUseCase.deleteBarcodes(barcodes)
    }

    fun deleteAll() = viewModelScope.launch {
        databaseBarcodeUseCase.deleteAll()
    }

    fun exportToFile(
        barcodes: List<Barcode>,
        format: FileFormat,
        uri: Uri
    ): LiveData<Resource<Boolean>> {
        return when(format) {
            FileFormat.CSV -> databaseBarcodeUseCase.exportToCsv(barcodes, uri)
            FileFormat.JSON -> databaseBarcodeUseCase.exportToJson(barcodes, uri)
        }
    }

    fun importFile(uri: Uri): LiveData<Resource<Boolean>> {
        return databaseBarcodeUseCase.importFromJson(uri)
    }
}