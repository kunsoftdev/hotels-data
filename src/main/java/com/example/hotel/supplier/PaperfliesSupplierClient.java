package com.example.hotel.supplier;

import com.example.hotel.config.SupplierUrlConfig;
import com.example.hotel.dto.PaperfliesHotelDto;
import com.example.hotel.model.*;
import com.example.hotel.util.Utils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;
import static com.example.hotel.util.Utils.clean;
import static com.example.hotel.util.Utils.isNullOrBlank;

@Component
public class PaperfliesSupplierClient implements SupplierClient {

    private final RestTemplate restTemplate;
    private final String url;

    public PaperfliesSupplierClient(SupplierUrlConfig config, RestTemplate restTemplate) {
        this.url = config.getPaperflies();
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Hotel> fetchHotels() {
        if (url == null || url.isBlank()) return Collections.emptyList();

        PaperfliesHotelDto[] rawHotels = restTemplate.getForObject(url, PaperfliesHotelDto[].class);
        if (rawHotels == null) return Collections.emptyList();

        return Arrays.stream(rawHotels)
                .filter(dto -> !isNullOrBlank(dto.getId()))
                .map(this::mapToHotel)
                .collect(Collectors.toList());
    }

    private Hotel mapToHotel(PaperfliesHotelDto dto) {
        Hotel hotel = new Hotel();
        hotel.setId(dto.getId());
        hotel.setDestinationId(dto.getDestinationId());
        hotel.setName(clean(dto.getName()));
        hotel.setDescription(clean(dto.getDetails()));
        hotel.setBookingConditions(dto.getBookingConditions());

        // Location
        if (dto.getLocation() != null) {
            Location location = new Location();
            location.setAddress(clean(dto.getLocation().getAddress()));
            location.setCountry(clean(dto.getLocation().getCountry()));
            hotel.setLocation(location);
        }

        // Amenities
        if (dto.getAmenities() != null) {
            Amenities amenities = new Amenities();
            amenities.setGeneral(cleanList(dto.getAmenities().getGeneral()));
            amenities.setRoom(cleanList(dto.getAmenities().getRoom()));
            hotel.setAmenities(amenities);
        }

        // Images
        Images images = new Images();
        if (dto.getImages() != null) {
            images.setRooms(convertImages(dto.getImages().getRooms()));
            images.setSite(convertImages(dto.getImages().getSite()));
            images.setAmenities(convertImages(dto.getImages().getAmenities()));
        }
        hotel.setImages(images);

        return hotel;
    }

    private List<String> cleanList(List<String> rawList) {
        if (rawList == null) return Collections.emptyList();
        return rawList.stream()
                .map(Utils::humanize)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<ImageItem> convertImages(List<PaperfliesHotelDto.ImageItem> rawItems) {
        if (rawItems == null) return Collections.emptyList();
        return rawItems.stream().map(i -> {
            ImageItem item = new ImageItem();
            item.setLink(i.getLink());
            item.setDescription(i.getCaption());
            return item;
        }).collect(Collectors.toList());
    }
}
