package com.example.hotel.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {
    private double lat;
    private double lng;
    private String address;
    private String city;
    private String country;
}
