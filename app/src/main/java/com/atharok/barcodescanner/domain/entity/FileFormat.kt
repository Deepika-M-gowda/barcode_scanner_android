package com.atharok.barcodescanner.domain.entity

enum class FileFormat(val mimeType: String) {
    CSV("text/csv"),
    JSON("application/json")
}