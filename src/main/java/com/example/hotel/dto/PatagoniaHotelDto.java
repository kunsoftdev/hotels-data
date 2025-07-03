package com.example.hotel.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PatagoniaHotelDto {
    private String id;

    private int destination;

    private String name;

    private Double lat;

    private Double lng;

    private String address;

    private String info;

    private List<String> amenities = new ArrayList<>();

    private Images images;

    @Getter
    @Setter
    public static class Images {
        private List<ImageItem> rooms = new ArrayList<>();
        private List<ImageItem> amenities = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class ImageItem {
        private String url;
        private String description;
    }
}
