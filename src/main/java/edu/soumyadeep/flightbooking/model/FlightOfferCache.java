package edu.soumyadeep.flightbooking.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "flight_offers_cache")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FlightOfferCache {
    @Id
    private String offerId;   // use UUID as ID
    private String origin;
    private String destination;
    private Double price;
    private String currency;
    private String cabin;
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
    }
}