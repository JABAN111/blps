package org.example.blps_lab1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class HotelDto {
    private String hotelName;
    private Float price;
    private Date dayOfArrival;
    private Date dayOfDeparture;
    private Integer countOfGuests;
    private String address;
}
