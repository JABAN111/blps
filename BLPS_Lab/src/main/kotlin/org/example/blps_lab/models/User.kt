package org.example.blps_lab.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.util.UUID

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_app")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var userId: UUID? = null

    @Column(nullable = false)
    var firstName: String? = null

    @Column(nullable = false)
    var lastName: String? = null

    @Column(nullable = false, unique = true)
    var email: String? = null

    @Column(nullable = false)
    var password: String? = null
}