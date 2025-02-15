package org.example.blps_lab.dto

import java.math.BigDecimal
import java.util.*

data class HotelDto (
    val hotelName: String,
    val price: BigDecimal,
    val dayOfArrival: Date,
    val dayOfDeparture: Date,
    val guestCount: Int,
    val address: String
)