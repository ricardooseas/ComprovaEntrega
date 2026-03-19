package com.unifor.comprovaentrega.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "deliveries")
data class Delivery(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),

    val codigoPedido: String,
    val destinatario: String,
    val observacao: String,

    val photoPath: String? = null,

    val latitude: Double? = null,
    val longitude: Double? = null,
    val gpsAccuracy: Float? = null,

    val createdAt: Long = System.currentTimeMillis()
)