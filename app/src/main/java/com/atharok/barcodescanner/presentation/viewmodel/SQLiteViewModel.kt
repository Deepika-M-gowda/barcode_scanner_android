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
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.models.local.database.repositories.BarcodeDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SQLiteViewModel(private val barcodeDataSource: BarcodeDataRepository): ViewModel() {

    val barcodeList: LiveData<List<Barcode>> = barcodeDataSource.getScanInformationList()
    private var barcodeLiveData: LiveData<Barcode>? = null

    fun getScanInformationById(id: Long): LiveData<Barcode> {
        if(barcodeLiveData==null)
            barcodeLiveData=barcodeDataSource.getScanInformationById(id)

        return barcodeLiveData!!
    }

    fun getScanInformationByDate(date: Long): LiveData<Barcode>{
        if(barcodeLiveData==null)
            barcodeLiveData=barcodeDataSource.getScanInformationByDate(date)

        return barcodeLiveData!!
    }

    fun insertScanInformation(barcode: Barcode) = viewModelScope.launch(Dispatchers.IO) {
        barcodeDataSource.insertScanInformation(barcode)
    }

    fun updateType(scanDate: Long, barcodeType: BarcodeType) = viewModelScope.launch(Dispatchers.IO) {
        barcodeDataSource.updateType(scanDate, barcodeType)
    }

    fun updateTypeAndName(scanDate: Long, barcodeType: BarcodeType, name: String) = viewModelScope.launch(Dispatchers.IO) {
        barcodeDataSource.updateTypeAndName(scanDate, barcodeType, name)
    }

    fun deleteScanInformation(barcode: Barcode) = viewModelScope.launch(Dispatchers.IO) {
        barcodeDataSource.deleteScanInformation(barcode)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        barcodeDataSource.deleteScanInformation()
    }
}*/