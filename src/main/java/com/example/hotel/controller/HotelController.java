package com.example.hotel.controller;

import com.example.hotel.model.Hotel;
import com.example.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/hotels")
public class HotelController {
    @Autowired
    private HotelService hotelService;

    @GetMapping
    public List<Hotel> getHotels(@RequestParam(required = false) List<String> hotels, @RequestParam(required = false) Integer destination) {
        if (hotels != null && !hotels.isEmpty() && destination != null) {
            // Filter by both: intersection
            Set<String> hotelIdSet = new HashSet<>(hotels);
            return hotelService.getByDestinationId(destination).stream()
                    .filter(hotel -> hotelIdSet.contains(hotel.getId()))
                    .toList();
        }

        if (hotels != null && !hotels.isEmpty()) {
            return hotelService.getByHotelIds(hotels);
        }

        if (destination != null) {
            return hotelService.getByDestinationId(destination);
        }

        return hotelService.getAllCachedHotels();
    }
}

