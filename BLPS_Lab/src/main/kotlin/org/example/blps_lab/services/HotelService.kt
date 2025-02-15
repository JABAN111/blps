package org.example.blps_lab.services

import org.example.blps_lab.dto.HotelDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.example.blps_lab.models.Hotel
import org.example.blps_lab.repositories.HotelRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class HotelService(
    private val hotelRepository: HotelRepository
) {
    private val log: Logger = LoggerFactory.getLogger(HotelService::class.java)

    fun createHotel(hotel: Hotel) {
        hotelRepository.save(hotel);
        log.info("Hotel created: {}", hotel)
    }

    fun deleteHotel(hotelId: UUID) {
        val hotel = hotelRepository.findById(hotelId).orElseThrow(){
            log.warn("Hotel with id {} not found", hotelId)
            throw RuntimeException("Отель с таким id не существует")
        }
        hotelRepository.delete(hotel)
        log.info("Hotel with id {} deleted", hotelId)

    }

    fun getAllHotels(): List<Hotel>{
        val hotels = hotelRepository.findAll()
        log.info("All hotels retrieved: {}", hotels)
        return hotels
    }

    fun updateHotel(hotelId: UUID, hotelDto: HotelDto): Hotel{
        val hotelBefore = hotelRepository.findById(hotelId).orElseThrow(){
            log.warn("Hotel with id {} not found", hotelId)
            throw RuntimeException("Отель с таким id не существует")
        }

        hotelBefore.apply {
            hotelName = hotelDto.hotelName
            price = hotelDto.price
            dayOFArrival = hotelDto.dayOfArrival
            dayOfDeparture = hotelDto.dayOfDeparture
            guestsCount = hotelDto.guestCount
            address = hotelDto.address
        }

        val updatedHotel = hotelRepository.save(hotelBefore)
        log.info("Hotel with id {} updated", hotelId)
        return updatedHotel
    }
}