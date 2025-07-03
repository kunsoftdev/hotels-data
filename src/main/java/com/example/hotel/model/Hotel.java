package com.example.hotel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Hotel {

    private String id;

    @JsonProperty("destination_id")
    private int destinationId;

    private String name;
    private Location location;
    private String description;
    private Amenities amenities;
    private Images images;

    @JsonProperty("booking_conditions")
    private List<String> bookingConditions = new ArrayList<>();
}

