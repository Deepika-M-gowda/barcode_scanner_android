package com.atharok.barcodescanner.domain.entity.bank

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Informations utilent pour la génération d'un EPC QR Code.
 */
@Keep
@Entity
data class Bank(
    val name: String,
    val bic: String,
    @PrimaryKey val iban: String
): Serializable