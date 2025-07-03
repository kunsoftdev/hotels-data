package com.example.hotel.supplier;

import com.example.hotel.config.SupplierUrlConfig;
import com.example.hotel.model.*;
import com.example.hotel.dto.PatagoniaHotelDto;
import com.example.hotel.util.Utils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;
import static com.example.hotel.util.Utils.*;
import static com.example.hotel.util.Utils.safeDouble;

@Component
public class PatagoniaSupplierClient implements SupplierClient {

    private final RestTemplate restTemplate;
    private final String url;

    public PatagoniaSupplierClient(SupplierUrlConfig config, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.url = config.getPatagonia();
    }

    @Override
    public List<Hotel> fetchHotels() {
        if (isNullOrBlank(url)) return Collections.emptyList();

        PatagoniaHotelDto[] rawHotels = restTemplate.getForObject(url, PatagoniaHotelDto[].class);
        if (rawHotels == null) return Collections.emptyList();

        return Arrays.stream(rawHotels)
                .filter(dto -> !isNullOrBlank(dto.getId()))
                .map(this::mapToHotel)
                .collect(Collectors.toList());
    }

    private Hotel mapToHotel(PatagoniaHotelDto dto) {
        Hotel hotel = new Hotel();
        hotel.setId(dto.getId());
        hotel.setDestinationId(dto.getDestination());
        hotel.setName(clean(dto.getName()));
        hotel.setDescription(clean(dto.getInfo()));

        // Location
        Location location = new Location();
        location.setLat(safeDouble(dto.getLat()));
        location.setLng(safeDouble(dto.getLng()));
        location.setAddress(clean(dto.getAddress()));
        hotel.setLocation(location);

        // Amenities
        Amenities amenities = new Amenities();
        amenities.setRoom(mapAmenitiesToRoom(dto.getAmenities()));
        hotel.setAmenities(amenities);

        // Images
        Images images = new Images();
        images.setRooms(convertImageItems(dto.getImages() != null ? dto.getImages().getRooms() : null));
        images.setAmenities(convertImageItems(dto.getImages() != null ? dto.getImages().getAmenities() : null));
        hotel.setImages(images);

        return hotel;
    }

    private List<String> mapAmenitiesToRoom(List<String> amenities) {
        if (amenities == null) return Collections.emptyList();

        return amenities.stream()
                .map(Utils::humanize)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<ImageItem> convertImageItems(List<PatagoniaHotelDto.ImageItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream().map(i -> {
            ImageItem item = new ImageItem();
            item.setLink(i.getUrl());
            item.setDescription(i.getDescription());
            return item;
        }).collect(Collectors.toList());
    }
}
