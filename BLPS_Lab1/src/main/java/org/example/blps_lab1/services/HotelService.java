package org.example.blps_lab1.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.models.Hotel;
import org.example.blps_lab1.repositories.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;

    public void createHotel(final Hotel hotel){
        hotelRepository.save(hotel);
        log.info("Created person: {}", hotel);
    }

    public void deleteHotel(final Long id){
        Optional<Hotel> deletingHotel = hotelRepository.findById(id);
        if(deletingHotel.isEmpty()){
            log.error("Hotel with id {} does not exist", id);
            throw new RuntimeException("Отель с таким id не существует");
        }
        hotelRepository.deleteById(id);
        log.info("Hotel deleted: {}", id);
    }

    public List<Hotel> getAllHotels(List<Hotel> hotel){
        var savedList = hotelRepository.saveAll(hotel);
        log.info("Saved {} hotels", savedList.size());
        return savedList;
    }
}
