package com.example.hotel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PaperfliesHotelDto {

    @JsonProperty("hotel_id")
    private String id;

    @JsonProperty("destination_id")
    private int destinationId;

    @JsonProperty("hotel_name")
    private String name;

    private String details;
    private Location location;
    private Amenities amenities;
    private Images images;

    @JsonProperty("booking_conditions")
    private List<String> bookingConditions = new ArrayList<>();

    @Getter
    @Setter
    public static class Location {
        private String address;
        private String country;
    }

    @Getter
    @Setter
    public static class Amenities {
        private List<String> general = new ArrayList<>();
        private List<String> room = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Images {
        private List<ImageItem> rooms = new ArrayList<>();
        private List<ImageItem> site = new ArrayList<>();
        private List<ImageItem> amenities = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class ImageItem {
        private String link;
        private String caption;
    }
}
