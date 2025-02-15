package org.example.blps_lab.repositories

import org.example.blps_lab.models.Hotel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface HotelRepository : JpaRepository<Hotel, UUID> {
}