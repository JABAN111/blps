package org.example.blps_lab.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.math.BigDecimal
import java.util.Date
import java.util.UUID

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var hotelId: UUID? = null

    @Column(nullable = false)
    var hotelName: String? = null

    @Column(nullable = false)
    var price: BigDecimal? = null

    @Column
    var dayOFArrival: Date? = null

    @Column
    var dayOfDeparture: Date? = null

    @Column
    var guestsCount: Int? = null

    @Column(unique = true, nullable = false)
    var address: String? = null
}