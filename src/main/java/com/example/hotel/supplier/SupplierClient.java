package com.example.hotel.supplier;

import com.example.hotel.model.Hotel;

import java.util.List;

public interface SupplierClient {
    List<Hotel> fetchHotels();
}