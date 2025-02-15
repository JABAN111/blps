package org.example.blps_lab.controllers

import org.example.blps_lab.dto.HotelDto
import org.example.blps_lab.models.Hotel
import org.example.blps_lab.services.HotelService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v0/hotels")
class HotelController(
    val hotelService: HotelService
){
    @PostMapping("/create")
    fun createHotel(@RequestBody hotel: Hotel): ResponseEntity<Hotel>{
        hotelService.createHotel(hotel)
        return ResponseEntity.ok(hotel)
    }

    @GetMapping("/get-all")
    fun getAllHotels(): ResponseEntity<List<Hotel>>{
        val hotels =  hotelService.getAllHotels()
        return ResponseEntity.ok(hotels)
    }

    @DeleteMapping("/{hotelId}")
    fun deleteHotel(@RequestBody hotelId: UUID): ResponseEntity<String>{
        hotelService.deleteHotel(hotelId)
        return ResponseEntity.ok("Hotel with id $hotelId deleted")
    }

    @PutMapping("/{hotelId}")
    fun updateHotel(
        @PathVariable hotelId: UUID,
        @RequestBody hotelDto: HotelDto
    ) : ResponseEntity<Hotel>{
        val updateHotel = hotelService.updateHotel(hotelId, hotelDto)
        return ResponseEntity.ok(updateHotel)
    }
}