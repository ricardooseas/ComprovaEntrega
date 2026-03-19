package com.unifor.comprovaentrega

import com.unifor.comprovaentrega.data.local.entity.Delivery

sealed class NavScreen {
    object Home : NavScreen()
    object NewDelivery : NavScreen()
    data class Details(val delivery: Delivery) : NavScreen()
}