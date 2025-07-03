package com.example.hotel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AcmeHotelDto {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("DestinationId")
    private int destinationId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Latitude")
    private Double latitude;

    @JsonProperty("Longitude")
    private Double longitude;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("PostalCode")
    private String postalCode;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Facilities")
    private List<String> facilities = new ArrayList<>();
}

