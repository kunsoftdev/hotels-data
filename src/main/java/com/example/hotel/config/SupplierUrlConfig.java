package com.example.hotel.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "supplier.urls")
public class SupplierUrlConfig {
    private String acme;
    private String patagonia;
    private String paperflies;
}
