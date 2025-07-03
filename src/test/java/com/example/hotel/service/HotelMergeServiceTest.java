package com.example.hotel.service;

import com.example.hotel.config.SupplierUrlConfig;
import com.example.hotel.model.Hotel;
import com.example.hotel.supplier.AcmeSupplierClient;
import com.example.hotel.supplier.PaperfliesSupplierClient;
import com.example.hotel.supplier.PatagoniaSupplierClient;
import com.example.hotel.supplier.SupplierClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelMergeServiceTest {

    @Mock
    AcmeSupplierClient acmeClient;

    @Mock
    PatagoniaSupplierClient patagoniaClient;

    @Mock
    PaperfliesSupplierClient paperfliesClient;

    @InjectMocks
    HotelMergeService hotelMergeService;

    @Mock
    SupplierUrlConfig urlConfig;

    @BeforeEach
    void setUp() {
        List<SupplierClient> supplierClients = List.of(acmeClient, patagoniaClient, paperfliesClient);
        hotelMergeService = new HotelMergeService(supplierClients, urlConfig);

        // Set URL config to enable all suppliers
        when(urlConfig.getAcme()).thenReturn("abc");
        when(urlConfig.getPatagonia()).thenReturn("def");
        when(urlConfig.getPaperflies()).thenReturn("123");
    }

    @Test
    void mergeHotelsFromMultipleSuppliers() {
        Hotel h1 = new Hotel();
        h1.setId("h1");
        h1.setName("Hotel1");
        h1.setDescription("Short description");

        // Same Id hotel
        Hotel h11 = new Hotel();
        h11.setId("h1");
        h11.setName("Hotel1");
        h11.setDescription("Longer description for merging");

        Hotel h2 = new Hotel();
        h2.setId("h2");
        h2.setName("Hotel2");

        when(acmeClient.fetchHotels()).thenReturn(List.of(h1));
        when(patagoniaClient.fetchHotels()).thenReturn(List.of(h11));
        when(paperfliesClient.fetchHotels()).thenReturn(List.of(h2));

        List<Hotel> merged = hotelMergeService.getMergedHotels();

        assertEquals(2, merged.size());

        Hotel mergedH1 = merged.stream().filter(h -> h.getId().equals("h1")).findFirst().orElse(null);
        assertNotNull(mergedH1);
        assertEquals("Longer description for merging", mergedH1.getDescription());
    }

    @Test
    void shouldSkipSuppliersWithNullUrl() {
        when(urlConfig.getAcme()).thenReturn(null);
        when(urlConfig.getPatagonia()).thenReturn("");
        when(urlConfig.getPaperflies()).thenReturn("abc");
        Hotel hotel = new Hotel();
        hotel.setId("1");
        when(paperfliesClient.fetchHotels()).thenReturn(List.of(hotel));

        List<Hotel> result = hotelMergeService.getMergedHotels();

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    void shouldHandleSupplierFetchException() {
        when(acmeClient.fetchHotels()).thenThrow(new RuntimeException("Timeout"));
        Hotel hotel = new Hotel();
        hotel.setId("id1");
        when(patagoniaClient.fetchHotels()).thenReturn(List.of(hotel));
        when(paperfliesClient.fetchHotels()).thenReturn(Collections.emptyList());

        List<Hotel> result = hotelMergeService.getMergedHotels();
        assertEquals(1, result.size());
        assertEquals("id1", result.get(0).getId());
    }
}

