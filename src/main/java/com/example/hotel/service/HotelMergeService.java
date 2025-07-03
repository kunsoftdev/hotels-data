package com.example.hotel.service;

import com.example.hotel.config.SupplierUrlConfig;
import com.example.hotel.model.*;
import com.example.hotel.supplier.AcmeSupplierClient;
import com.example.hotel.supplier.PaperfliesSupplierClient;
import com.example.hotel.supplier.PatagoniaSupplierClient;
import com.example.hotel.supplier.SupplierClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import static com.example.hotel.util.Utils.longerOrNonNull;

@Service
public class HotelMergeService {

    private final List<SupplierClient> suppliers;
    private final SupplierUrlConfig urlConfig;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Autowired
    public HotelMergeService(List<SupplierClient> suppliers, SupplierUrlConfig urlConfig) {
        this.suppliers = suppliers;
        this.urlConfig = urlConfig;
    }

    public List<Hotel> getMergedHotels() {
        Map<String, Hotel> mergedHotelsMap = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> tasks = getEnabledSuppliers().stream()
                .map(supplier -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return supplier.fetchHotels();
                    } catch (Exception e) {
                        System.err.println("Failed to fetch from " + supplier.getClass().getSimpleName() + ": " + e.getMessage());
                        return Collections.<Hotel>emptyList();
                    }
                }, executor).thenAccept(hotels -> {
                    for (Hotel hotel : hotels) {
                        if (hotel.getId() != null) {
                            mergedHotelsMap.merge(hotel.getId(), hotel, this::mergeHotels);
                        }
                    }
                }))
                .toList();

        // Wait for all tasks to complete
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        return new ArrayList<>(mergedHotelsMap.values());
    }

    private List<SupplierClient> getEnabledSuppliers() {
        return suppliers.stream()
                .filter(this::isEnabled)
                .collect(Collectors.toList());
    }

    private boolean isEnabled(SupplierClient supplier) {
        if (supplier instanceof AcmeSupplierClient) {
            return isNotBlank(urlConfig.getAcme());
        } else if (supplier instanceof PatagoniaSupplierClient) {
            return isNotBlank(urlConfig.getPatagonia());
        } else if (supplier instanceof PaperfliesSupplierClient) {
            return isNotBlank(urlConfig.getPaperflies());
        } else {
            return false;
        }
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.isBlank();
    }

    private Hotel mergeHotels(Hotel h1, Hotel h2) {
        Hotel result = new Hotel();
        result.setId(h1.getId());
        result.setDestinationId(h1.getDestinationId() != 0 ? h1.getDestinationId() : h2.getDestinationId());
        result.setName(Optional.ofNullable(h1.getName()).orElse(h2.getName()));
        result.setDescription(longerOrNonNull(h1.getDescription(), h2.getDescription()));

        result.setLocation(mergeLocation(h1.getLocation(), h2.getLocation()));
        result.setAmenities(mergeAmenities(h1.getAmenities(), h2.getAmenities()));
        result.setImages(mergeImages(h1.getImages(), h2.getImages()));
        result.setBookingConditions(mergeList(h1.getBookingConditions(), h2.getBookingConditions()));
        return result;
    }

    private Location mergeLocation(Location l1, Location l2) {
        if (l1 == null) return l2;
        if (l2 == null) return l1;

        Location merged = new Location();
        merged.setLat(l1.getLat() != 0 ? l1.getLat() : l2.getLat());
        merged.setLng(l1.getLng() != 0 ? l1.getLng() : l2.getLng());
        merged.setAddress(Optional.ofNullable(l1.getAddress()).orElse(l2.getAddress()));
        merged.setCity(Optional.ofNullable(l1.getCity()).orElse(l2.getCity()));
        merged.setCountry(longerOrNonNull(l1.getCountry(), l2.getCountry()));
        return merged;
    }

    private Amenities mergeAmenities(Amenities a1, Amenities a2) {
        if (a1 == null) return a2;
        if (a2 == null) return a1;

        Amenities merged = new Amenities();
        merged.setGeneral(mergeList(a1.getGeneral(), a2.getGeneral()));
        merged.setRoom(mergeList(a1.getRoom(), a2.getRoom()));
        return merged;
    }

    private Images mergeImages(Images i1, Images i2) {
        if (i1 == null) return i2;
        if (i2 == null) return i1;

        Images merged = new Images();
        merged.setSite(mergeList(i1.getSite(), i2.getSite()));
        merged.setRooms(mergeList(i1.getRooms(), i2.getRooms()));
        merged.setAmenities(mergeList(i1.getAmenities(), i2.getAmenities()));
        return merged;
    }

    private <T> List<T> mergeList(List<T> l1, List<T> l2) {
        Set<T> mergedSet = new LinkedHashSet<>();
        if (l1 != null) mergedSet.addAll(l1);
        if (l2 != null) mergedSet.addAll(l2);
        return new ArrayList<>(mergedSet);
    }
}
