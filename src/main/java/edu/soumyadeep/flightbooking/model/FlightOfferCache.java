package edu.soumyadeep.flightbooking.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "flight_offers_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightOfferCache {

    @Id
    @Column(name = "offer_id", nullable = false, unique = true)
    private String offerId;   // Amadeus flight offer ID (string-based, e.g., "1", "2", ...)

    @Column(nullable = false, length = 10)
    private String origin; // e.g., "CCU"

    @Column(nullable = false, length = 10)
    private String destination; // e.g., "DEL"

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false, length = 5)
    private String currency; // e.g., "EUR", "USD"

    @Column(nullable = false, length = 20)
    private String cabin; // e.g., "ECONOMY", "BUSINESS"

    @Column(name = "trip_direction", nullable = false, length = 10)
    private String tripDirection; // OUTBOUND or RETURN

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
    }
}