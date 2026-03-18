package com.unifor.comprovaentrega

import java.util.UUID

data class Delivery(
    val id: String = UUID.randomUUID().toString(),

    val codigoPedido: String,
    val destinatario: String,
    val observacao: String,

    val photoPath: String? = null,

    val latitude: Double? = null,
    val longitude: Double? = null,
    val gpsAccuracy: Float? = null,

    val createdAt: Long = System.currentTimeMillis()
)