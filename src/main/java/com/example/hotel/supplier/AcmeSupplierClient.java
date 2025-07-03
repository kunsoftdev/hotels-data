package com.example.hotel.supplier;

import com.example.hotel.config.SupplierUrlConfig;
import com.example.hotel.model.Amenities;
import com.example.hotel.model.Hotel;
import com.example.hotel.model.Location;
import com.example.hotel.dto.AcmeHotelDto;
import com.example.hotel.util.Utils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;
import static com.example.hotel.util.Utils.*;
import static com.example.hotel.util.Utils.clean;

@Component
public class AcmeSupplierClient implements SupplierClient {

    private final RestTemplate restTemplate;
    private final String url;

    public AcmeSupplierClient(SupplierUrlConfig config, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.url = config.getAcme();
    }

    @Override
    public List<Hotel> fetchHotels() {
        if (isNullOrBlank(url)) return Collections.emptyList();

        AcmeHotelDto[] rawHotels = restTemplate.getForObject(url, AcmeHotelDto[].class);
        if (rawHotels == null) return Collections.emptyList();

        return Arrays.stream(rawHotels)
                .filter(dto -> !isNullOrBlank(dto.getId()))
                .map(this::mapToHotel)
                .collect(Collectors.toList());
    }

    private Hotel mapToHotel(AcmeHotelDto dto) {
        Hotel hotel = new Hotel();
        hotel.setId(dto.getId());
        hotel.setDestinationId(dto.getDestinationId());
        hotel.setName(clean(dto.getName()));
        hotel.setDescription(clean(dto.getDescription()));

        // Location
        Location location = new Location();
        location.setLat(safeDouble(dto.getLatitude()));
        location.setLng(safeDouble(dto.getLongitude()));
        location.setAddress(clean(dto.getAddress()) + ", " + clean(dto.getPostalCode()));
        location.setCity(clean(dto.getCity()));
        location.setCountry(clean(dto.getCountry()));
        hotel.setLocation(location);

        // Amenities
        Amenities amenities = new Amenities();
        amenities.setGeneral(mapFacilitiesToGeneral(dto.getFacilities()));
        hotel.setAmenities(amenities);

        return hotel;
    }

    private List<String> mapFacilitiesToGeneral(List<String> facilities) {
        if (facilities == null) return Collections.emptyList();

        return facilities.stream()
                .map(Utils::humanize)
                .distinct()
                .collect(Collectors.toList());
    }
}

