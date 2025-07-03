package com.example.hotel.service;

import com.example.hotel.model.Hotel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class HotelService {
    @Autowired
    private HotelMergeService mergeService;
    private List<Hotel> cachedHotels = new ArrayList<>();
    private Map<String, Hotel> hotelById = new ConcurrentHashMap<>();
    private Map<Integer, List<Hotel>> hotelByDestination = new ConcurrentHashMap<>();

    // Refresh Cache Every 15 minutes
    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void refreshCache() {
        List<Hotel> hotels = mergeService.getMergedHotels();
        this.cachedHotels = hotels;

        // Rebuild indexes
        Map<String, Hotel> idIndex = new HashMap<>();
        Map<Integer, List<Hotel>> destIndex = new HashMap<>();

        for (Hotel hotel : hotels) {
            if (hotel.getId() != null) {
                idIndex.put(hotel.getId(), hotel);
            }

            int destId = hotel.getDestinationId();
            if (destId != 0) {
                destIndex.computeIfAbsent(destId, k -> new ArrayList<>()).add(hotel);
            }
        }

        this.hotelById = idIndex;
        this.hotelByDestination = destIndex;
    }

    public List<Hotel> getAllCachedHotels() {
        return cachedHotels;
    }

    public List<Hotel> getByHotelIds(List<String> ids) {
        return ids.stream()
                .map(hotelById::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<Hotel> getByDestinationId(int destinationId) {
        return hotelByDestination.getOrDefault(destinationId, Collections.emptyList());
    }
}
