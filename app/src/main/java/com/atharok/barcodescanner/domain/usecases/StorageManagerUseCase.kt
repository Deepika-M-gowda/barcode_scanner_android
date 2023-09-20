package com.atharok.barcodescanner.domain.usecases

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.repositories.FileExportRepository
import com.atharok.barcodescanner.domain.resources.Resource
import kotlinx.coroutines.Dispatchers

class StorageManagerUseCase(private val repository: FileExportRepository) {
    fun exportToCsv(
        barcodes: List<Barcode>,
        uri: Uri
    ): LiveData<Resource<Boolean>> {
        return export {
            repository.exportToCsv(barcodes, uri)
        }
    }

    fun exportToJson(
        barcodes: List<Barcode>,
        uri: Uri
    ): LiveData<Resource<Boolean>> {
        return export {
            repository.exportToJson(barcodes, uri)
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