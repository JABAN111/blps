package org.example.blps_lab1.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelId;

    @Column
    private String hotelName;

    @Column
    private Float price;

    @Column
    private Date dayOfArrival;

    @Column
    private Date dayOfDeparture;

    @Column
    private Integer guestsCount;

    @Column(unique = true)
    private String address;
}
