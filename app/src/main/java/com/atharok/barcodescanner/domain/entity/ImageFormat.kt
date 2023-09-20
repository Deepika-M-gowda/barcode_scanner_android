package com.atharok.barcodescanner.domain.entity

enum class ImageFormat(val mimeType: String) {
    PNG("image/png"),
    JPG("image/jpeg"),
    SVG("image/svg+xml")
}