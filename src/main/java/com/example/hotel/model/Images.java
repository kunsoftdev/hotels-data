package com.example.hotel.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Images {
    private List<ImageItem> rooms = new ArrayList<>();
    private List<ImageItem> site = new ArrayList<>();
    private List<ImageItem> amenities = new ArrayList<>();
}
