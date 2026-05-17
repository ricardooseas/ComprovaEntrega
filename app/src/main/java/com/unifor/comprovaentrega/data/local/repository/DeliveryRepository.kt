package com.unifor.comprovaentrega.data.local.repository

import com.unifor.comprovaentrega.data.local.dao.DeliveryDao
import com.unifor.comprovaentrega.data.local.entity.Delivery
import kotlinx.coroutines.flow.Flow

class DeliveryRepository(private val dao: DeliveryDao) {

    fun getAllDeliveries(): Flow<List<Delivery>> = dao.getAllDeliveries()

    suspend fun insertDelivery(delivery: Delivery) = dao.insertDelivery(delivery)

    suspend fun deleteDelivery(delivery: Delivery) = dao.deleteDelivery(delivery)
}