package com.example.hotel.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Amenities {
    private List<String> general = new ArrayList<>();
    private List<String> room = new ArrayList<>();
}